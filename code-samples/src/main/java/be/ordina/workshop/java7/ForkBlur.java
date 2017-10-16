package be.ordina.workshop.java7;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @author Yannick De Turck
 */
public class ForkBlur extends RecursiveAction {

    private int[] mSource;
    private int mStart;
    private int mLength;
    private int[] mDestination;
    private static int splitCount = 0; // Counter to keep track of the amount of splits
    private int mBlurWidth = 15; // Processing window size, should be odd.
    protected static int THRESHOLD = 10_000;

    public ForkBlur(int[] src, int start, int length, int[] dst) {
        mSource = src;
        mStart = start;
        mLength = length;
        mDestination = dst;
    }

    // Average pixels from source, write results into destination.
    protected void computeDirectly() {
        int sidePixels = (mBlurWidth - 1) / 2;
        for (int index = mStart; index < mStart + mLength; index++) {
            // Calculate average.
            float rt = 0, gt = 0, bt = 0;
            for (int mi = -sidePixels; mi <= sidePixels; mi++) {
                int mindex = Math.min(Math.max(mi + index, 0), mSource.length - 1);
                int pixel = mSource[mindex];
                rt += (float) ((pixel & 0x00ff0000) >> 16) / mBlurWidth;
                gt += (float) ((pixel & 0x0000ff00) >> 8) / mBlurWidth;
                bt += (float) ((pixel & 0x000000ff) >> 0) / mBlurWidth;
            }

            // Re-assemble destination pixel.
            int dpixel = (0xff000000)
                    | (((int) rt) << 16)
                    | (((int) gt) << 8)
                    | (((int) bt) << 0);
            mDestination[index] = dpixel;
        }
    }

    @Override
    protected void compute() {
        if (mLength < THRESHOLD) {
            computeDirectly();
            return;
        }

        int split = mLength / 2;
        splitCount++;

        invokeAll(new ForkBlur(mSource, mStart, split, mDestination),
                new ForkBlur(mSource, mStart + split, mLength - split, mDestination));
    }

    // Plumbing follows.
    public static void main(String[] args) throws Exception {
        String filename = "/Users/tomverelst/Desktop/duck.jpg";
        System.setProperty("java.awt.headless", "false");
        File file = new File(filename);
        BufferedImage image = ImageIO.read(file);

        new ImageFrame("ForkBlur - original", image);

        BufferedImage blurredImage = blur(image);

        new ImageFrame("ForkBlur - processed", blurredImage);
    }

    public static BufferedImage blur(BufferedImage srcImage) {
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();

        int[] src = srcImage.getRGB(0, 0, w, h, null, 0, w);
        int[] dst = new int[src.length];

        System.out.println("Array size is " + src.length);
        System.out.println("Threshold is " + THRESHOLD);

        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println(Integer.toString(processors) + " processor"
                + (processors != 1 ? "s are " : " is ")
                + "available");

        ForkBlur fb = new ForkBlur(src, 0, src.length, dst);

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        pool.invoke(fb);
        long endTime = System.currentTimeMillis();

        System.out.println("Image blur took " + (endTime - startTime) + " milliseconds.");
        System.out.println("Amount of splits: " + splitCount);

        BufferedImage dstImage =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, w, h, dst, 0, w);

        return dstImage;
    }
}

class ImageFrame extends JFrame {

    public ImageFrame(String title, BufferedImage image) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(image.getWidth(), image.getHeight());
        add(new ImagePanel(image));
        setLocationByPlatform(true);
        setVisible(true);
    }
}

class ImagePanel extends JPanel {

    BufferedImage mImage;

    public ImagePanel(BufferedImage image) {
        mImage = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int x = (getWidth() - mImage.getWidth()) / 2;
        int y = (getHeight() - mImage.getHeight()) / 2;
        g.drawImage(mImage, x, y, this);
    }
}
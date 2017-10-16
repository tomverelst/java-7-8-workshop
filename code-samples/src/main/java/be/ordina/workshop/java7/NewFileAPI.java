package be.ordina.workshop.java7;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yannick De Turck
 */
public class NewFileAPI {

    public static void main(String[] args) {
        // Reading a file in Java 6
        try {
            FileInputStream fstream = new FileInputStream("/Users/tomverelst/Desktop/test.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                System.out.println(strLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Reading a file in Java 7
        try {
            List<String> lines = Files.readAllLines(
                    FileSystems.getDefault().getPath("/Users/tomverelst/Desktop/test.txt"), StandardCharsets.UTF_8);
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * WatchService example
         * Watch a certain directory and log the  activities
         */
        final FileSystem fileSystem = FileSystems.getDefault();
        try (final WatchService watchService = fileSystem.newWatchService()) {
            final Map<WatchKey, Path> keyMap = new HashMap<>();
            final Path path = FileSystems.getDefault().getPath("/Users/tomverelst/Desktop");
            try {
                keyMap.put(path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE), path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            WatchKey watchKey;
            do {
                watchKey = watchService.take();
                final Path eventDir = keyMap.get(watchKey);
                for (final WatchEvent<?> event : watchKey.pollEvents()) {
                    final Kind kind = event.kind();
                    final Path eventPath = (Path) event.context();
                    System.out.println(eventDir + ": " + event.kind() + ": " + eventDir.resolve(eventPath));
                }
            } while (watchKey.reset());
        } catch (InterruptedException | IOException ex) {
            // Oops, something went wrong
        }
    }
}

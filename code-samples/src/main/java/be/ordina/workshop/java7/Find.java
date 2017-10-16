package be.ordina.workshop.java7;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Yannick De Turck
 */
public class Find extends SimpleFileVisitor<Path> { // Also check interface FileVisitor for a larger number of available methods

    public static void main(final String[] args) throws IOException {
        final FileVisitor<Path> fileVisitor = new Find();
        final Path root = Paths.get("/Users/tomverelst/Desktop");
        Files.walkFileTree(root, fileVisitor);
    }

    /*
     * Print the directories, ignore '.svn'-directories.
     */
    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) {
        if (".git".equals(dir.getFileName().toString())) {
            return FileVisitResult.SKIP_SUBTREE;
        }
        System.out.println("Directory found: " + dir);
        return FileVisitResult.CONTINUE;
    }

    /*
     * Print the files.
     */
    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
        System.out.println("File found: "+ file);
        return FileVisitResult.CONTINUE;
    }
}
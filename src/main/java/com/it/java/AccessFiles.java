package com.it.java;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;

public class AccessFiles implements FileVisitor<Path> {

    public FileVisitResult preVisitDirectory(Path pathToCurrentFolder, BasicFileAttributes attrs) throws IOException {
        delete(pathToCurrentFolder);
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFile(Path pathToFile, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()) {
            long lastAccessTime = attrs.lastAccessTime().toMillis();
            long timeDifference = System.currentTimeMillis() - lastAccessTime;
            long monthInMillis = 24 * 60 * 60 * 1000;

            if (timeDifference > 2 * monthInMillis) {
                delete(pathToFile);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path pathToCurrentFolder, IOException exc) throws IOException {
        delete(pathToCurrentFolder);
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println(exc.getMessage());
        return FileVisitResult.CONTINUE;
    }

    private void delete(Path pathToFile) throws IOException {
        if (Files.exists(pathToFile)) {
            DosFileAttributes dosFileAttributes = Files.readAttributes(pathToFile, DosFileAttributes.class);

            if (dosFileAttributes.isReadOnly()) {
                DosFileAttributeView dosFileAttributeView = Files.getFileAttributeView(pathToFile, DosFileAttributeView.class);
                dosFileAttributeView.setReadOnly(false);
            }

            if (Files.isRegularFile(pathToFile)) {
                Files.deleteIfExists(pathToFile);
            } else if (Files.isDirectory(pathToFile)) {
                DirectoryStream<Path> contentsOfCurrentDirectory = Files.newDirectoryStream(pathToFile);
                if (!contentsOfCurrentDirectory.iterator().hasNext()) {
                    Files.deleteIfExists(pathToFile);
                }
            }
        }
    }
}

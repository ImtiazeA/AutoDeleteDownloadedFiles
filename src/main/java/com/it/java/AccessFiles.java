package com.it.java;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;

public class AccessFiles extends SimpleFileVisitor<Path> {


    @Override
    public FileVisitResult preVisitDirectory(Path pathToCurrentFolder, BasicFileAttributes attrs) throws IOException {

        deleteFolder(pathToCurrentFolder);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path pathToFile, BasicFileAttributes attrs) throws IOException {

        if (attrs.isRegularFile()) {

            FileTime lastAccessTimeOfCurrentFile = attrs.lastAccessTime();
            long timeDifference = System.currentTimeMillis() - lastAccessTimeOfCurrentFile.toMillis();

            long monthInMillis = 24 * 60 * 60 * 1000;

            if (timeDifference > 2 * monthInMillis) {

                DosFileAttributes dosFileAttributes = Files.readAttributes(pathToFile, DosFileAttributes.class);

                if (!dosFileAttributes.isReadOnly()) {
                    Files.deleteIfExists(pathToFile);
                } else {
                    System.out.println("Could Not Delete: " + pathToFile.getRoot() + pathToFile.subpath(0, pathToFile.getNameCount()));
                }

            }

        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path pathToCurrentFolder, IOException exc) throws IOException {

        deleteFolder(pathToCurrentFolder);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println(exc.getMessage());
        return FileVisitResult.CONTINUE;
    }

    private void deleteFolder(Path pathToCurrentFolder) throws IOException {
        if (Files.exists(pathToCurrentFolder)) {
            DirectoryStream<Path> contentsOfCurrentDirectory = Files.newDirectoryStream(pathToCurrentFolder);

            if (!contentsOfCurrentDirectory.iterator().hasNext()) {
                Files.deleteIfExists(pathToCurrentFolder);
            }
        }
    }
}

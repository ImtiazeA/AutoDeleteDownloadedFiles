package com.it.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        Path pathToFolder = Paths.get("E:\\");

        Files.walkFileTree(pathToFolder, new AccessFiles());

    }

}

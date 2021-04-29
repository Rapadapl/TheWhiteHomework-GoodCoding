package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.company.Utils.getFileSize;

public class Main {

    public static void main(String[] args) {

        int scrArg = Arrays.asList(args).indexOf("-scr");
        String input = args[scrArg + 1];

        int destArg = Arrays.asList(args).indexOf("-dest");
        String output = args[destArg + 1];

        ArrayList<Ffile> files = new ArrayList<>();
        ArrayList<Dir> dirs = new ArrayList<>();

        File path = new File(input);
        File dest = new File(output);

        if (dest.exists()) deleteAllExceptNeed(dest, "!");
        dest.mkdirs();
        System.out.println("Исходная директория");
        printTree(path, "");


        //заводим коллекции с файлами и с папками
        for (File file : Objects.requireNonNull(path.listFiles())) {
            if (file.isDirectory()) dirs.add(new Dir(file));
            else files.add(new Ffile(file));
        }
        //сначала найдём файлы, которые точно нужно скопировать, заполнив сет (никаких проверок делать не нужно будет,
        //для этого перегружена equals.
        //аналогично для папок
        SortedSet<Ffile> difFiles = new TreeSet<>(files);
        Map<String, Integer> fileCount = new HashMap<>();


        SortedSet<Dir> difDirs = new TreeSet<>(dirs);
        Map<String, Integer> dirCount = new HashMap<>();
        //копируем файлы в нужное назначение и с нужным названием
        //в коллекции Map храним сколько раз встречался файл с таким названием для создания нужного названия
        //аналогично для папок, за исключением, что сначала создаём папку, потом копируем всё её содержимое
        for (var difFile : difFiles) {
            if (!fileCount.containsKey(difFile.getName())) {
                fileCount.put(difFile.getName(), 0);

                try {
                    var to = dest.getPath() + "\\" + difFile.getName() + "." + difFile.getType();
                    copyFile(new File(difFile.getPath()), new File(to));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                fileCount.put(difFile.getName(), fileCount.get(difFile.getName()) + 1);
                try {
                    copyFile(new File(difFile.getPath()),
                            new File(String.format("%s%s (%d)%s",
                                    dest.getPath() + "\\",
                                    difFile.getName(),
                                    fileCount.get(difFile.getName()),
                                    "." + difFile.getType())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        for (var difDir : difDirs) {
            if (!dirCount.containsKey(difDir.getName())) {
                dirCount.put(difDir.getName(), 0);
                var to = dest.getPath() + "\\" + difDir.getName();
                new File(to).mkdir();
                to += "\\";
                for (var fileInFolder : difDir.getFiles()) {
                    try {
                        copyFile(new File(fileInFolder.getPath()),
                                new File(to + fileInFolder.getName() + "." + fileInFolder.getType()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                dirCount.put(difDir.getName(), dirCount.get(difDir.getName()) + 1);
                var to = String.format("%s%s (%d)",
                        dest.getPath() + "\\",
                        difDir.getName(),
                        dirCount.get(difDir.getName()));
                new File(to).mkdir();
                to += "\\";
                for (var fileInFolder : difDir.getFiles()) {
                    try {
                        copyFile(new File(fileInFolder.getPath()),
                                new File(to + fileInFolder.getName() + "." + fileInFolder.getType()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        printTree(dest, path.getName() + ".zip");

        try {
            toZip(dest.getPath(), path.getName() + ".zip");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //функция для копирования файлов путём создания пустых файлов и папок и копирования значений
    static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) destFile.createNewFile();
        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }

    //функция для создания зип архива из файлов папке, после отработки удаляет все файлы из папки, кроме архива
    static void toZip(String dir, String name) throws IOException {

        Path zipPath = Files.createFile(Paths.get(dir + "\\" + name));
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Path dirContent = Paths.get(dir);
            Files.walk(dirContent)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> !path.getFileName().toString().endsWith(".zip"))
                    .forEach(path -> {

                        ZipEntry zipEntry = new ZipEntry(dirContent.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);//?
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        deleteAllExceptNeed(new File(dir), ".zip");
    }


    //метод для печати дерева директории (специализированно для 1 уровня вложенности + размер файла == содержимое)
    static void printTree(File dir, String name) {
        if (name.isEmpty())
            System.out.println(dir.getPath());
        else
            System.out.println(dir.getPath() + "\\" + name);

        ArrayList<File> dirs = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) dirs.add(file);
            else files.add(file);
        }

        for (var folder : dirs) {
            System.out.println("\t|- " + folder.getName() + "/");

            for (var fileInFolder : Objects.requireNonNull(folder.listFiles())) {
                int fileSize = getFileSize(fileInFolder);
                String data = String.format("%dKb", fileSize);
                System.out.println("\t\t|- " + fileInFolder.getName() + " " + data);
            }
        }

        for (var fileInRoot : files) {
            int fileSize = getFileSize(fileInRoot);
            String data = String.format("%dKb", fileSize);
            System.out.println("\t|- " + fileInRoot.getName() + " " + data);
        }
    }


    //удаление всех файлов, кроме с указанным расширением. Для удаления всех можно указать любой служебный символ,
//также специализирована на 1 уровень вложенности
    static void deleteAllExceptNeed(File dir, String end) {

        File[] files = dir.listFiles();
        for (var file : Objects.requireNonNull(files)) {

            if (file.isDirectory()) {
                for (var fileInFolder : Objects.requireNonNull(file.listFiles())) {
                    fileInFolder.delete();
                }
                file.delete();
            } else {
                if (!file.getName().endsWith(end)) {

                    file.delete();
                }
            }
        }

    }

}
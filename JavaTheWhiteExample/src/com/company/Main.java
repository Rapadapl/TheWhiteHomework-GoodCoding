package com.company;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) {

        var str = Arrays.asList(args).indexOf("-scr");
        String input = args[str + 1];
        str = Arrays.asList(args).indexOf("-dest");
        String output = args[str + 1];

        ArrayList<Ffile> files = new ArrayList<>();
        ArrayList<Dir> dirs = new ArrayList<>();
        File path = new File(input);
        File dest = new File(output);
        if (dest.exists()) deleteAllExceptNeed(dest, "!");
        dest.mkdirs();
        System.out.println("Исходная директория");
        tree(path, "");

        //Map<String, Set<Ffile>> equalFiles = new HashMap<>();
        //Map<String, Set<Dir>> equalDirs = new HashMap<>();
        //заводим коллекции с файлами и с папками
        for (File file : Objects.requireNonNull(path.listFiles())) {
            if (file.isDirectory()) dirs.add(new Dir(file));
            else files.add(new Ffile(file));
        }
        //сначала найдём файлы, которые точно нужно скопировать, заполнив сет (никаких проверок делать не нужно будет,
        //для этого перегружена equals.
        //аналогично для папок
        SortedSet<Ffile> difFiles = new TreeSet<>(files);
        //files = new ArrayList<>(Arrays.asList(difFiles.toArray(new Ffile[0])));
        Map<String, Integer> fileCount = new HashMap<>();


        SortedSet<Dir> difDirs = new TreeSet<>(dirs);
        Map<String, Integer> dirCount = new HashMap<>();
        //копируем файлы в нужное назначение и с нужным названием
        //в коллекции Map храним сколько раз встречался файл с таким названием для создания нужного названия
        //аналогично для папок, за исключением, что сначала создаём папку, потом копируем всё её содержимое
        for (var item : difFiles) {
            if (!fileCount.containsKey(item.getName())) {
                fileCount.put(item.getName(), 0);
                try {
                    var to = dest.getPath() + "\\" + item.getName() + "." + item.getType();
                    copyFile(new File(item.getPath()), new File(to));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                fileCount.put(item.getName(), fileCount.get(item.getName()) + 1);
                try {
                    copyFile(new File(item.getPath()),
                            new File(String.format("%s%s (%d)%s",
                                    dest.getPath() + "\\",
                                    item.getName(),
                                    fileCount.get(item.getName()),
                                    "." + item.getType())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        for (var item : difDirs) {
            if (!dirCount.containsKey(item.getName())) {
                dirCount.put(item.getName(), 0);
                var to = dest.getPath() + "\\" + item.getName();
                new File(to).mkdir();
                to += "\\";
                for (var file : item.getFiles()) {
                    try {
                        copyFile(new File(file.getPath()), new File(to + file.getName() + "." + file.getType()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                dirCount.put(item.getName(), dirCount.get(item.getName()) + 1);
                var to = String.format("%s%s (%d)",
                        dest.getPath() + "\\",
                        item.getName(),
                        dirCount.get(item.getName()));
                new File(to).mkdir();
                to += "\\";
                for (var file : item.getFiles()) {
                    try {
                        copyFile(new File(file.getPath()), new File(to + file.getName() + "." + file.getType()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        tree(dest, path.getName() + ".zip");

        try {
            toZip(dest.getPath(), path.getName() + ".zip");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //функция для копирования файлов путём создания пустых файлов и папок и копирования значений
    static void copyFile(File sourceFile, File destFile) throws IOException {
        //destFile.mkdirs();
        if (!destFile.exists()) destFile.createNewFile();


        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }

    //функция для создания зип архива из файлов папке, после отработки удаляет все файлы из папки, кроме архива
    static void toZip(String dir, String name) throws IOException {

        Path p = Files.createFile(Paths.get(dir + "\\" + name));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(dir);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> !path.getFileName().toString().endsWith(".zip"))
                    .forEach(path -> {

                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);//?
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
        deleteAllExceptNeed(new File(dir), ".zip");
    }


    //метод для печати дерева директории (специализированно для 1 уровня вложенности + размер файла == содержимое)
    static void tree(File dir, String name) {
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

        for (var item : dirs) {
            System.out.println("\t|- " + item.getName() + "/");

            for (var file : Objects.requireNonNull(item.listFiles())) {
                Scanner reader = null;
                String data = "";
                try {
                    reader = new Scanner(file);
                    data = reader.nextLine();
                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                System.out.println("\t\t|- " + file.getName() + " " + data);
            }
        }
        for (var file : files) {
            Scanner reader;
            String data = "";
            try {
                reader = new Scanner(file);
                data = reader.nextLine();
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("\t|- " + file.getName() + " " + data);
        }
    }


    //удаление всех файлов, кроме с указанным расширением. Для удаления всех можно указать любой служебный символ,
//также специализирована на 1 уровень вложенности
    static void deleteAllExceptNeed(File dir, String end) {

        File[] files = dir.listFiles();
        for (var f : files) {

            if (f.isDirectory()) {
                for (var ff : f.listFiles()) {
                    ff.delete();
                }
                f.delete();
            } else {
                if (!f.getName().endsWith(end)) {

                    f.delete();
                }
            }
        }

    }

}
package com.company;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Ffile implements Comparable<Ffile> {
    private String name;
    private String type;
    private String uuid;
    private Integer size;
    private String path;

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public Ffile(String name, String type, String uuid, Integer size, String path) {
        this.name = name;
        this.type = type;
        this.uuid = uuid;
        this.size = size;
        this.path = path;
    }

    //вытаскиваем из объекта io.File путь, имя, тип, uid и размер файла (размер файла хранится внутри файла)
    public Ffile(File file) {
        path = file.getPath();
        String fileName = file.getName();
        type = fileName.substring(fileName.lastIndexOf('.') + 1);
        Pattern uidPattern = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
        Matcher uidMatcher = uidPattern.matcher(fileName);

        if (uidMatcher.find()) {
            uuid = fileName.substring(uidMatcher.start(), uidMatcher.end());
            name = fileName.substring(0, uidMatcher.start() - 1);
        } else name = fileName.substring(0, fileName.lastIndexOf('.'));

        try {
            Scanner reader = new Scanner(file);
            String data = reader.nextLine();
            Pattern sizePattern = Pattern.compile("\\d+");
            Matcher sizeMatcher = sizePattern.matcher(data);
            if (sizeMatcher.find()) size = Integer.parseInt(data.substring(sizeMatcher.start(), sizeMatcher.end()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //перегружаем функцию equals, так, чтобы она сравнивала файлы с одинаковыми именами и типами и содержимым для заполнения сета
    @Override
    public boolean equals(Object o) {


        if (o == this) {
            return true;
        }

        if (!(o instanceof Ffile)) {
            return false;
        }

        // приведём типы чтобы можно было сравнить
        Ffile c = (Ffile) o;


        try {
            return CharSequence.compare(name, c.name) == 0 && CharSequence.compare(type, c.type) == 0 && compareTextFiles(c.path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Метод для сравнения текста внутри файлов
    public boolean compareTextFiles(String file2) throws Exception {
        BufferedReader r1 = new BufferedReader(new FileReader(path));
        BufferedReader r2 = new BufferedReader(new FileReader(file2));
        int c1, c2;
        while (true) {
            c1 = r1.read();
            c2 = r2.read();
            if (c1 == -1 && c2 == -1)
                return true;
            else if (c2 == -1 || c1 != c2) {
                return false;
            }
        }
    }

    //перегрузим метод для сравнения файлов по их размеру, чтобы можно было отсортировать
    @Override
    public int compareTo(Ffile ffile) {
        return -size.compareTo(ffile.size);
    }
}


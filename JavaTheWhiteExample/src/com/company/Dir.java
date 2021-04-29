package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dir implements Comparable<Dir> {
    private ArrayList<Ffile> files = new ArrayList<>();
    private String name;
    private String path;
    private String uuid;
    private Integer size = 0;

    public ArrayList<Ffile> getFiles() {
        return files;
    }

    public String getName() {
        return name;
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

    //вытаскиваем из объекта io.File путь, имя, uid папки.
    //также проходимся по всем вложенным файлам и добавляем их лист files, и расчитываем размер полученной папки
    public Dir(File file) {
        path = file.getPath();
        String dirName = file.getName();
        Pattern uidPattern = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
        Matcher uidMatcher = uidPattern.matcher(dirName);

        if (uidMatcher.find()) {
            uuid = dirName.substring(uidMatcher.start(), uidMatcher.end());
            name = dirName.substring(0, uidMatcher.start() - 1);
        }
        for (File f : Objects.requireNonNull(file.listFiles())) {
            files.add(new Ffile(f));
        }
        for (Ffile f : files) {
            size += f.getSize();
        }
    }

    public Dir(ArrayList<Ffile> files, String name, String path, String uuid, Integer size) {
        this.files = files;
        this.name = name;
        this.path = path;
        this.uuid = uuid;
        this.size = size;
    }

    //перегружаем функцию equals для соответсвия заданию
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Dir)) {
            return false;
        }

        // приведём типы чтобы можно было сравнить
        Dir c = (Dir) o;
        Set<Ffile> filesToCompare = new TreeSet<>(files);
        filesToCompare.addAll(c.files);
        // Сравниваем имена папок и содержимому файлов в папках
        return CharSequence.compare(name, c.name) == 0 && (size == c.size) && (files.size() == c.files.size())
                && (filesToCompare.size() == files.size());
    }


    //перегрузим метод для сравнения файлов по их размеру, чтобы можно было отсортировать
    @Override
    public int compareTo(Dir dir) {
        return -size.compareTo(dir.size);
    }
}

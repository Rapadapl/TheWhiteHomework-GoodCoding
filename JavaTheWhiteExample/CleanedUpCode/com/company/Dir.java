package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static com.company.Utils.getNameByUidRegexp;


public class Dir implements Comparable<Dir> {
    private final ArrayList<Ffile> files = new ArrayList<>();
    private final String name;
    private Integer size = 0;

    //вытаскиваем из объекта io.File путь, имя, uid папки.
    //также проходимся по всем вложенным файлам и добавляем их лист files, и расчитываем размер полученной папки
    public Dir(File file) {
        String dirName = file.getName();
        String uidPattern = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
        name = getNameByUidRegexp(uidPattern, dirName);

        for (File f : Objects.requireNonNull(file.listFiles())) {
            Ffile fileToAdd = new Ffile(f);
            files.add(fileToAdd);
            size += fileToAdd.getSize();
        }

    }

    public ArrayList<Ffile> getFiles() {
        return files;
    }

    public String getName() {
        return name;
    }

    //перегружаем функцию equals для соответсвия заданию
    @Override
    public boolean equals(Object equalsObj) {
        if (equalsObj == this) {
            return true;
        }

        if (!(equalsObj instanceof Dir)) {
            return false;
        }

        // приведём типы чтобы можно было сравнить
        Dir sameTypeObj = (Dir) equalsObj;
        Set<Ffile> filesToCompare = new TreeSet<>(files);
        filesToCompare.addAll(sameTypeObj.files);
        // Сравниваем имена папок и содержимому файлов в папках
        return CharSequence.compare(name, sameTypeObj.name) == 0 && (size == sameTypeObj.size) && (files.size() == sameTypeObj.files.size())
                && (filesToCompare.size() == files.size());
    }


    //перегрузим метод для сравнения файлов по их размеру, чтобы можно было отсортировать
    @Override
    public int compareTo(Dir dir) {
        return -size.compareTo(dir.size);
    }
}

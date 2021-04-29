package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static com.company.Utils.getFileSize;
import static com.company.Utils.getNameByUidRegexp;


public class Ffile implements Comparable<Ffile> {

    private final String type;
    private final String path;
    private final Integer size;
    private String name;


    //вытаскиваем из объекта io.File путь, имя, тип, uid и размер файла (размер файла хранится внутри файла)
    public Ffile(File file) {
        path = file.getPath();
        String fileName = file.getName();
        type = fileName.substring(fileName.lastIndexOf('.') + 1);
        String uidPattern = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
        name = getNameByUidRegexp(uidPattern, fileName);

        //если файл находится в директории, то у него нет uid, получаем имя другим путём
        if (name == null) name = fileName.substring(0, fileName.lastIndexOf('.'));

        size = getFileSize(file);
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public Integer getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    //перегружаем функцию equals, так, чтобы она сравнивала файлы с одинаковыми именами и типами и содержимым для заполнения сета
    @Override
    public boolean equals(Object equalsObj) {
        if (equalsObj == this) {
            return true;
        }

        if (!(equalsObj instanceof Ffile)) {
            return false;
        }

        // приведём типы чтобы можно было сравнить
        Ffile sameTypeObj = (Ffile) equalsObj;

        try {
            return CharSequence.compare(name, sameTypeObj.name) == 0 && CharSequence.compare(type, sameTypeObj.type) == 0 && compareTextFiles(sameTypeObj.path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Метод для сравнения текста внутри файлов
    private boolean compareTextFiles(String file2) throws Exception {
        BufferedReader reader1 = new BufferedReader(new FileReader(path));
        BufferedReader reader2 = new BufferedReader(new FileReader(file2));
        int char1, char2;
        while (true) {
            char1 = reader1.read();
            char2 = reader2.read();
            if (char1 == -1 && char2 == -1)
                return true;
            else if (char2 == -1 || char1 != char2)
                return false;
        }
    }


    //перегрузим метод для сравнения файлов по их размеру, чтобы можно было отсортировать
    @Override
    public int compareTo(Ffile ffile) {
        return -size.compareTo(ffile.size);
    }
}


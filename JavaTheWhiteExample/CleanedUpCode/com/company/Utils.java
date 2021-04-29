package com.company;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    public static String getNameByUidRegexp(String pattern, String fullName) {

        Pattern uidPattern = Pattern.compile(pattern);
        Matcher uidMatcher = uidPattern.matcher(fullName);

        if (uidMatcher.find()) {
            return fullName.substring(0, uidMatcher.start() - 1);
        }
        return null;
    }

    public static int getFileSize(File file) {
        try {
            Scanner reader = new Scanner(file);
            String data = reader.nextLine();
            Pattern sizePattern = Pattern.compile("\\d+");
            Matcher sizeMatcher = sizePattern.matcher(data);
            if (sizeMatcher.find()) return Integer.parseInt(data.substring(sizeMatcher.start(), sizeMatcher.end()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}

package com.ftkj.x3.client.util;

import com.ftkj.xxs.util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * @author luch
 */
public class NamesUtil {
    public static List<String> First_List = new ArrayList<>();
    public static List<String> Last_List = new ArrayList<>();

    public static void readNamesConfig(Locale locale) {
        Function<String, String> func1 = str -> str;
        First_List = FileUtils.readAllLines("classpath:sub/names/first_" + locale.toString() + ".txt", func1);
        Function<String, String> func = str -> str;
        Last_List = FileUtils.readAllLines("classpath:sub/names/last_" + locale.toString() + ".txt", func);
    }

    public static String randomNormalNames() {
        return randomNormalNames(ThreadLocalRandom.current());
    }

    public static String randomNormalNames(ThreadLocalRandom tlr) {
        String first = First_List.get(tlr.nextInt(First_List.size()));
        String last = Last_List.get(tlr.nextInt(Last_List.size()));

        return first + "·" + last;
    }

    public static String randomNames() {
        return randomNames(ThreadLocalRandom.current());
    }

    public static String randomNames(ThreadLocalRandom tlr) {
        String first = First_List.get(tlr.nextInt(First_List.size()));
        String last = Last_List.get(tlr.nextInt(Last_List.size()));

        return first + "r·" + last;
    }

    private static final char[] toBase64 = {
            //            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            //            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    public static void main(String[] args) {
        //        readNamesConfig("zh_CN");
        //        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        //        for (int i = 0; i < 10; i++) {
        //            System.out.println(randomNames(tlr));
        //        }
        StringBuilder sb = new StringBuilder();
        //        for (char c : toBase64) {
        //            sb.append(c).append("\n");
        //        }
        //        for (char c : toBase64) {
        //            sb.append(c).append("\n");
        //            for (char c1 : toBase64) {
        //                sb.append(c).append(c1).append("\n");
        //            }
        //        }
        int gap = 32;
        int count = 1;
        for (char c : toBase64) {
            //            sb.append(c).append("\n");
            for (char c1 : toBase64) {
                //                sb.append(c).append(c1).append("\n");
                for (char c2 : toBase64) {
                    if (c == c1 || c - gap == c1 || c1 - gap == c ||
                            c == c2 || c - gap == c2 || c2 - gap == c ||
                            c1 == c2 || c1 - gap == c2 || c2 - gap == c1) {
                        continue;
                    }
                    sb.append(c).append(c1).append(c2).append("\n");
                    count++;
                }
            }
        }
        System.out.println(sb);
        System.out.println(count);
        //        System.out.println((int) 'C');
    }
}

package com.ftkj.cfg.base;

import com.ftkj.util.excel.ColData;
import com.ftkj.util.excel.RowData;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractExcelBean extends ExcelBean {

    public final static Function<String, Integer> toInt = ParseListColumnUtil.toInt;
    public final static Function<String, Float> toFloat = ParseListColumnUtil.toFloat;
    public final static Function<String, String> toStr = ParseListColumnUtil.toStr;

    /** 分割字符串为 map[int, int]. 字符串格式: k1:v1,k2:v2,k3:v3, */
    public static Map<Integer, Integer> splitToIntMap(String str) {
        return splitToMap(str, toInt, toInt);
    }

    /** 分割字符串为 map[str, int]. 字符串格式: k1:v1,k2:v2,k3:v3, */
    public static Map<String, Integer> splitToStrIntMap(String str) {
        return splitToMap(str, toStr, toInt);
    }

    /** 分割字符串为 map[K, V]. 字符串格式: k1:v1,k2:v2,k3:v3, */
    public static <K, V> Map<K, V> splitToMap(String str, Function<String, K> keyMapper, Function<String, V> vMapper) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyMap();
        }
        String[] arr0 = str.split(",");
        Map<K, V> map = new LinkedHashMap<>();
        for (String s1 : arr0) {
            String[] arr1 = s1.split(":");
            map.put(keyMapper.apply(arr1[0]),
                    vMapper.apply(arr1[1]));
        }
        return map;
    }

    public static int getInt(RowData row, String colName) {
        ColData cd = row.getColData(colName);
        if (cd == null) {
            return 0;
        }
        return Integer.parseInt(cd.getValueStr());
    }

    public static String getStr(RowData row, String colName) {
        ColData cd = row.getColData(colName);
        if (cd == null) {
            return "";
        }
        return cd.getValueStr();
    }

    public static boolean getBoolean(RowData row, String colName) {
        int i = getInt(row, colName);
        return i != 0;
    }

}

package com.ftkj.util.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExcelConfigBean implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ExcelConfigBean.class);
    private static final long serialVersionUID = -2819392109848456073L;

    private String sheetName;
    private List<String> nameList;
    private List<String> descList;
    private List<String> typeList;
    //
    private List<RowData> dataList;

    public ExcelConfigBean() {
    }

    public ExcelConfigBean(String sheetName) {
        this.sheetName = sheetName;
        nameList = Lists.newArrayList();
        descList = Lists.newArrayList();
        typeList = Lists.newArrayList();
        dataList = Lists.newArrayList();
    }

    public void initHeader(List<String> nameList, List<String> descList, List<String> typeList) {
        this.nameList = nameList;
        this.descList = descList;
        this.typeList = typeList;
    }

    /**
     * 添加数据
     *
     * @param colName
     * @param value
     */
    public void addData(int row, String colName, String type, String value) {
        if (this.dataList.size() == row) {
            RowData rowData = new RowData();
            this.dataList.add(rowData);
        }
        try {
            this.dataList.get(row).addColData(colName, new ColData(type, value));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <V> List<V> converToBeanList(Class clazz) {
        if (clazz == null) {
            String className = this.sheetName.substring(0, 1).toUpperCase() + this.sheetName.substring(1);
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
        //
        List<V> list = Lists.newArrayList();
        try {
            // class 反射封装
            V bean = null;
            List<Field> fields = getDeclaredFields(clazz);
            Method ext = null;
            try {
                ext = clazz.getDeclaredMethod("initExec", RowData.class);
            } catch (NoSuchMethodException e) {
                ext = null;
            }
            for (RowData rowData : this.dataList) {
                bean = (V) clazz.newInstance();
                list.add(bean);
                //
                for (Field field : fields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if (!rowData.checkName(fieldName)) {
                        continue;
                    }
                    if (matchType(rowData.getColSimpleType(fieldName), field.getType())) {
                        field.set(bean, rowData.get(fieldName));
                    } else {
                        log.error(sheetName + "_" + fieldName + "[" + field.getType().getSimpleName() + " , " + rowData.getColSimpleType(fieldName) + "] 类型不一致");
                    }
                }
                try {
                    // 自己扩展解析,对进行标注的都进行调用
                    if (bean instanceof ExcelBean) {
                        ExcelBean eb = (ExcelBean) bean;
                        eb.initExec(rowData);
                    } else if (ext != null) {
                        ext.invoke(bean, rowData);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("read row " + rowData, e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(clazz.getSimpleName() + " 读取异常 " + e.getMessage(), e);
        }
        return list;
    }

    private List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> superClazz = clazz.getSuperclass();
        while (true) {
            if (superClazz == Object.class) {
                break;
            }
            log.trace("class {} super class {}", clazz.getCanonicalName(), superClazz.getCanonicalName());
            if (superClazz.isArray() || superClazz.isAnonymousClass() || superClazz.isAnnotation() ||
                    superClazz.isEnum() || superClazz.isInterface() || superClazz.isPrimitive()) {
                log.trace("class {} super class {} break", clazz.getCanonicalName(), superClazz.getCanonicalName());
                break;
            }
            fields.addAll(Arrays.asList(superClazz.getDeclaredFields()));
            superClazz = superClazz.getSuperclass();
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    /**
     * 转换成excel对应的类型来比较
     * long == int
     * double == float
     *
     * @param colType
     * @param type
     * @return
     */
    private boolean matchType(String colType, Class<?> type) {
        colType = colType.toLowerCase();
        switch (colType) {
            case "int": return type.isAssignableFrom(int.class) || type.isAssignableFrom(long.class) ||
                    type.isAssignableFrom(float.class) || type.isAssignableFrom(double.class);
            case "float": return type.isAssignableFrom(float.class) || type.isAssignableFrom(double.class);
        }
        return type.getSimpleName().toLowerCase().equals(colType);
    }

    /**
     * 直接取两个指定字段，返回map类型
     *
     * @param key  字段名做键
     * @param name 字段名做值
     * @return
     */
    public <T, V> Map<T, V> converToMap(String key, String name) {
        Map<T, V> map = Maps.newHashMap();
        // class 反射封装
        for (RowData rowData : this.dataList) {
            map.put(rowData.get(key), rowData.get(name));
        }
        return map;
    }

    /**
     * 取一个字段做键，转换成map形式，必须配置key键，type类型和value值这种表
     *
     * @param key
     * @return
     */
    public <K> Map<K, RowData> converToMap() {
        Map<K, RowData> map = Maps.newHashMap();
        // class 反射封装
        for (RowData rowData : this.dataList) {
            if (rowData.checkName("type")) {
                map.put(rowData.get("key"), rowData.converDataToMap());
            } else {
                map.put(rowData.get("key"), rowData);
            }
        }
        return map;
    }

    /**
     * 键值对配置表，取指定键名的value值，性能不高少用，一般用来取配置数据比较少的表里特殊字段
     *
     * @param key
     * @param value
     * @return
     */
    public <V> V getKeyValue(String key) {
        return this.dataList.stream().filter(row -> row.get("key").equals(key)).findFirst().get().get("value");
    }

    public String getSheetName() {
        return sheetName;
    }

    private <E> int size(List<E> l) {
        return l == null ? 0 : l.size();
    }

    @Override
    public String toString() {
        return "{" +
                "\"sheet\":\"" + sheetName + "\"" +
                ", \"name\":" + size(nameList) +
                ", \"desc\":" + size(descList) +
                ", \"type\":" + size(typeList) +
                ", \"data\":" + size(dataList) +
                '}';
    }
    //    @Override
    //    public String toString() {
    //        StringBuilder sb = new StringBuilder("ExcelConfigBean [sheetName=" + sheetName + "]\n");
    //        this.nameList.stream().forEach(str -> sb.append(str).append("\t"));
    //        sb.append("\n");
    //        this.descList.stream().forEach(str -> sb.append(str).append("\t"));
    //        sb.append("\n");
    //        this.typeList.stream().forEach(str -> sb.append(str).append("\t"));
    //        sb.append("\n");
    //        this.dataList.stream().forEach(str -> sb.append(str).append("\n"));
    //        return sb.toString();
    //    }
}

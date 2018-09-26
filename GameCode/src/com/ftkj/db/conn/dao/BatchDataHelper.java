package com.ftkj.db.conn.dao;

import com.ftkj.util.PathUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * @Author jiawang.gui
 * @Date 2018.7.17
 * 批量保存 辅助类
 */
public class BatchDataHelper {

    // key classname
    protected static Map<String, String> preSqlMap;
    protected static Map<String, String> parameterSqlMap;  //(?,?,?,?)
    protected static Map<String, String> afterSqlMap;

    public static void init() {
        Map<String, String> preSqlMap = Maps.newHashMap();
        Map<String, String> parameterSqlMap = Maps.newHashMap();
        Map<String, String> afterSqlMap = Maps.newHashMap();

        //log.debug("初始化包路径:[{}],项目路径:[{}]", servicePath, path);
        String path = "com.ftkj";
        List<String> batchDataNames = PathUtil.getAllName(path);
        //String replaceStr = InitServer.getReplaceStr(path);
        ClassLoader classLoader = BatchDataHelper.class.getClassLoader();
        for (String className : batchDataNames) {
            try {
                className = className.replace("/", ".");
                if(className.startsWith("com.ftkj.proto")){
                    continue;
                }
                Class<?> cla = classLoader.loadClass(className);
                if (!Modifier.isAbstract(cla.getModifiers()) && AsynchronousBatchDB.class.isAssignableFrom(cla)) {
                    Constructor<?>[] cs = cla.getConstructors();
                    AsynchronousBatchDB dataDB = (AsynchronousBatchDB) cla.newInstance();

                    initSql(dataDB, preSqlMap, parameterSqlMap, afterSqlMap);
                }
            } catch (Throwable e) {
                throw new RuntimeException("init class " + className, e);
            }
        }

        BatchDataHelper.preSqlMap = preSqlMap;
        BatchDataHelper.parameterSqlMap = parameterSqlMap;
        BatchDataHelper.afterSqlMap = afterSqlMap;
    }

    private static Object consCreateObject(Class<?> cla) {
        Constructor<?>[] consList = cla.getConstructors();

        Constructor<?> constructor = null;
        if (consList != null && consList.length > 0) {
            for (Constructor<?> cons : consList) {
                if (cons.getParameterCount() == 0) {
                    constructor = cons;
                    break;
                }
            }
            if (constructor == null) {
                constructor = consList[0];
            }
        }
        if (constructor.getParameterCount() == 0) {
            try {
                return constructor.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException("init class " + cla.getName(), e);
            }
        }
        Object[] parameters = new Object[constructor.getParameterCount()];
        constructor.getParameterTypes();
        //for()
        constructor.getParameterCount();
        return null;

        //AbstractBatchDataDB dataDB = (AbstractBatchDataDB) cla.newInstance();
    }
    private static void initSql(AsynchronousBatchDB dataDB, Map<String, String> preSqlMap, Map<String, String> parameterSqlMap, Map<String, String> afterSqlMap) {
        String tableName = dataDB.getTableName();
        String rowsString = dataDB.getRowNames();
        if (rowsString == null) {
            return;
        }
        String[] rows = rowsString.split(",");
        if (rows == null || rows.length == 0) {
            return;
        }
        List<String> rowList = Lists.newArrayListWithExpectedSize(rows.length);
        for (String row : rows) {
            rowList.add(row.trim());
        }
        //List<String> rowList = Arrays.asList(rows);
        //List<String> rowList = dataDB.getRowNameList();
        StringBuilder preBuilder = new StringBuilder();
        preBuilder.append("INSERT INTO ").append(tableName);
        preBuilder.append("(");

        StringBuilder parameterBuilder = new StringBuilder();
        parameterBuilder.append("(");

        StringBuilder afterBuilder = new StringBuilder();
        afterBuilder.append(" ON DUPLICATE KEY UPDATE ");

        boolean flag = false;
        for (String row : rowList) {
            if (flag) {
                preBuilder.append(",");
                parameterBuilder.append(",");
                afterBuilder.append(",");
            }
            if(row.startsWith("`")){
                preBuilder.append(row);
                afterBuilder.append(row).append("=VALUES(").append(row).append(")");
            } else {
                preBuilder.append("`").append(row).append("`");
                afterBuilder.append("`").append(row).append("`").append("=VALUES(").append("`").append(row).append("`").append(")");
            }
            //preBuilder.append(row);
            //preBuilder.append("`").append(row).append("`");
            parameterBuilder.append("?");
            //afterBuilder.append(row).append("=VALUES(").append(row).append(")");
            //afterBuilder.append("`").append(row).append("`").append("=VALUES(").append("`").append(row).append("`").append(")");

            flag = true;
        }
        preBuilder.append(")  VALUES");
        String preSql = preBuilder.toString();
        preSqlMap.put(dataDB.getClass().getName(), preSql);

        parameterBuilder.append(")");
        String parameterSql = parameterBuilder.toString();
        parameterSqlMap.put(dataDB.getClass().getName(), parameterSql);

        afterBuilder.append(";");
        String afterSql = afterBuilder.toString();
        afterSqlMap.put(dataDB.getClass().getName(), afterSql);

    }

    //func
    public static String findBatchDataPreSql(AsynchronousBatchDB dataDB) {
        if (preSqlMap == null) {
            return null;
        }
        return preSqlMap.get(dataDB.getClass().getName());
    }

    public static String findBatchDataParameterSql(AsynchronousBatchDB dataDB) {
        if (parameterSqlMap == null) {
            return null;
        }
        return parameterSqlMap.get(dataDB.getClass().getName());
    }

    public static String findBatchDataAfterSql(AsynchronousBatchDB dataDB) {
        if (afterSqlMap == null) {
            return null;
        }
        return afterSqlMap.get(dataDB.getClass().getName());
    }

}

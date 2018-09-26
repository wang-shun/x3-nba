package com.ftkj.util;

import org.joda.time.DateTime;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * 字符串工具
 *
 * @author tim.huang 2015年12月22日
 */
public class StringUtil {

    /**
     * ;
     */
    public static String DEFAULT_SP = "[;]";
    /**
     * ,
     */
    public static String DEFAULT_ST = "[,]";
    public static String ST = ",";
    /** 冒号 ":" */
    public static final String COLON = ":";
    /** 逗号',' */
    public static final String COMMA = ",";
    /** 分号';' */
    public static final String SEMICOLON = ";";
    /** 下划线'_' */
    public static final String UNDERLINE = "_";
    /**
     * =
     */
    public static String DEFAULT_EQ = "[=]";

    /**
     * :
     */
    public static String DEFAULT_FH = "[:]";

    public static int[] toIntArray(String str, String sp) {
        if ("".equals(str) || str == null) {
            return new int[0];
        }
        //填充数值参数
        String[] v = str.split(sp);
        int[] vInt = new int[v.length];
        for (int tv = 0; tv < v.length; tv++) {
            vInt[tv] = Integer.parseInt(v[tv]);
        }
        return vInt;
    }

    public static Integer[] toIntegerArray(String str) {
        return toIntegerArray(str, COMMA);
    }

    /** 分号';'分隔 */
    public static Integer[] toIntArrBySemicolon(String str) {
        return toIntegerArray(str, SEMICOLON);
    }

    public static Integer[] toIntegerArray(String str, String sp) {
        if ("".equals(str) || str == null) {
            return new Integer[0];
        }
        //填充数值参数
        String[] v = str.split(sp);
        Integer[] vInt = new Integer[v.length];
        for (int tv = 0; tv < v.length; tv++) {
            vInt[tv] = Integer.parseInt(v[tv]);
        }
        return vInt;
    }

    public static long[] toLongArray(String str, String sp) {
        if ("".equals(str) || str == null) {
            return new long[0];
        }
        //填充数值参数
        String[] v = str.split(sp);
        long[] vInt = new long[v.length];
        for (int tv = 0; tv < v.length; tv++) {
            vInt[tv] = Long.parseLong(v[tv]);
        }
        return vInt;
    }

    public static String[] toStringArray(String str, String sp) {
        if ("".equals(str) || str == null) {
            return new String[0];
        }
        //填充数值参数
        String[] v = str.split(sp);
        return v;
    }

    public static String formatString(String str, Object... vals) {
        StringBuffer sb = new StringBuffer();
        int start = 0, end = 0;
        for (Object val : vals) {
            end = str.indexOf("{}");
            if (start > str.length() || end < 0) {
                break;
            }
            sb.append(str.substring(start, end));
            if (val instanceof Date) {
                DateTime da = new DateTime(val);
                sb.append(da.toString("yyyy-MM-dd HH:mm:ss"));
            } else if (val instanceof Throwable) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                Throwable t = (Throwable) val;
                try {
                    t.printStackTrace(pw);
                    sb.append(sw.toString());
                } finally {
                    pw.close();
                }
            } else {
                sb.append(val.toString());
            }
            str = str.substring(end + 2);
        }
        sb.append(str);
        return sb.toString();
    }

    public static String formatSQL(Object... values) {
        StringBuilder sb = new StringBuilder();
        int length = values.length;
        int over = length - 1;
        for (int i = 0; i < length; i++) {
            appendValue(sb, values[i]);
            if (i == over) {
                sb.append("\n");
            } else {
                sb.append("\t");
            }
        }
        return sb.toString();
    }

    public static String formatSQL(List<Object[]> valueArrs) {
        StringBuilder sb = new StringBuilder();
        int oover = valueArrs.size() - 1;
        for (int j = 0; j < valueArrs.size(); j++) {
            Object[] values = valueArrs.get(j);
            int length = values.length;
            int over = length - 1;
            for (int i = 0; i < length; i++) {
                //TODO 对字符串进行转义 https://www.owasp.org/index.php/Preventing_SQL_Injection_in_Java#MySQL_Escaping
                //包括 \n \t
                appendValue(sb, values[i]);
                if (j == oover && i == over) {
                    sb.append("\n");
                } else {
                    sb.append("\t");
                }
            }
        }
        return sb.toString();
    }

    private static void appendValue(StringBuilder sb, Object value) {
        if (value instanceof Date) {
            DateTime da = new DateTime(value);
            sb.append(DateTimeUtil.getStringSql(da));
        } else if (value instanceof DateTime) {
            DateTime da = (DateTime) value;
            sb.append(DateTimeUtil.getStringSql(da));
        } else {
            appendValueToSql(sb, value);
        }
    }

    /**
     * android/database/DatabaseUtils.java
     * <p>
     * Appends an SQL string to the given StringBuilder, including the opening
     * and closing single quotes. Any single quotes internal to sqlString will
     * be escaped.
     * <p>
     * This method is deprecated because we want to encourage everyone
     * to use the "?" binding form.  However, when implementing a
     * ContentProvider, one may want to add WHERE clauses that were
     * not provided by the caller.  Since "?" is a positional form,
     * using it in this case could break the caller because the
     * indexes would be shifted to accomodate the ContentProvider's
     * internal bindings.  In that case, it may be necessary to
     * construct a WHERE clause manually.  This method is useful for
     * those cases.
     *
     * @param sb        the StringBuilder that the SQL string will be appended to
     * @param sqlString the raw string to be appended, which may contain single
     *                  quotes
     */
    public static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
        //        sb.append('\'');
        if (sqlString.indexOf('\'') != -1) {
            int length = sqlString.length();
            for (int i = 0; i < length; i++) {
                char c = sqlString.charAt(i);
                if (c == '\'') {
                    sb.append('\'');
                }
                sb.append(c);
            }
        } else {
            sb.append(sqlString);
        }
        //        sb.append('\'');
    }

    //load data infile default boundary \n  \t
    public static void appendLoadDataEscapedSQLString(StringBuilder sb, String sqlString) {
        String str = sqlString.replaceAll("\t","");
        str = str.replaceAll("\n","");
        sb.append(str);
    }

    /**
     * SQL-escape a string. android/database/DatabaseUtils.java
     */
    public static String sqlEscapeString(String value) {
        StringBuilder escaper = new StringBuilder();
        //appendEscapedSQLString(escaper, value);
        appendLoadDataEscapedSQLString(escaper, value);
        return escaper.toString();
    }

    /**
     * Appends an Object to an SQL string with the proper escaping, etc. android/database/DatabaseUtils.java
     */
    public static void appendValueToSql(StringBuilder sql, Object value) {
        if (value == null) {
            sql.append("NULL");
        }
        //        else if (value instanceof Boolean) {
        //            Boolean bool = (Boolean) value;
        //            if (bool) {
        //                sql.append('1');
        //            } else {
        //                sql.append('0');
        //            }
        //        }
        else if (value instanceof Number) {
            sql.append(value);
        } else {
            //appendEscapedSQLString(sql, value.toString());
            appendLoadDataEscapedSQLString(sql,value.toString());
        } // com.mysql.jdbc.EscapeTokenizer
    }

    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }

    public static String trimURI(String uri) {
        String trimmed = uri.substring(1);
        int splashIndex = trimmed.indexOf('/');

        return trimmed.substring(splashIndex);
    }

    public static String printlnStackTrace() {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement se : Thread.currentThread().getStackTrace()) {
            sb.append(se.getClassName()).append(".").append(se.getMethodName())
                .append("(")
                .append(se.getFileName()).append(":").append(se.getLineNumber())
                .append(")\n");
        }

        return sb.toString();
    }

}

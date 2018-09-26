package com.ftkj.tool.quartz;

/**
 * https://www.freeformatter.com/cron-expression-generator-quartz.html
 */
public final class Expression {
    /** 每天0点 */
    public static final String DAILY_MIDNIGHT = "0 0 0 * * ?";
    /** 每天0点1分 */
    public static final String DAILY_0001 = "0 1 0 ? * *";
    /** 每3小时 */
    public static final String _3HOUR = "0 0 0/3 * * ?";
    /** 每两小时的第1分钟, 偶数小时 */
    public static final String Every_Even_Hours = "0 1 0/2 ? * *";
    /** 每天0点
     "0 0 0 * * ?"</br>
     /** 每天3点
     "0 0 3 * * ?"</br>
     /** 每1秒钟
     "0/1 * * * * ?"</br>
     /** 每5秒钟
     "0/5 * * * * ?"</br>
     /** 每10秒钟
     "0/10 * * * * ?"</br>
     /** 每1分钟
     "0 0/1 * * * ?"</br>
     /** 每3分钟
     "0 0/3 * * * ?"</br>
     /** 每5分钟
     "0 0/5 * * * ?"</br>
     /** 每10分钟
     "0 0/10 * * * ?"</br>
     /** 每1小时
     "0 0 0/1 * * ?"</br>
     /** 每3小时
     "0 0 0/3 * * ?"</br>
     /** 每月最后一天23点59分
     "0 59 23 L * ?"</br>
     **/

    /** 每1小时     "0 0 0/1 * * ?" */
    public static final String Every_Hour = "0 0 * ? * * *";
    /** 每1分钟     "0 0/1 * * * ?" */
    public static final String Every_Minute = "0 * * ? * * *";
    /** 每10分钟    "0 0/10 * * * ?" */
    public static final String Every_10_Minute = "0 0/10 * ? * * *";
}

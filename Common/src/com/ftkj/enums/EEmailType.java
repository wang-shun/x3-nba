package com.ftkj.enums;

/**
 * 邮件类型
 *
 * @author Jay
 * @time:2017年4月24日 下午8:25:59
 */
public enum EEmailType {

    系统邮件(0),
    /** 系统公告 */
    Sys_B(1),
    /** 普通模版邮件 */
    Template(2),
    /** 有参数占位符的模版邮件 */
    Param_Template(3),    

    //
    ;

    private int type;

    private EEmailType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}

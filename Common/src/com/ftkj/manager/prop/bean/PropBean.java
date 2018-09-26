package com.ftkj.manager.prop.bean;

import com.ftkj.db.domain.bean.PropBeanVO;
import com.ftkj.enums.EPropType;
import com.ftkj.util.StringUtil;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2016年2月24日
 * 物品配置
 */
public abstract class PropBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private PropBeanVO prop;
    private EPropType type;
    /**
     * 时效性
     */
    private int hour;
    private int day;
    private int dayLimit;

    public PropBean(PropBeanVO prop) {
        super();
        this.prop = prop;
        this.hour = 0;
        this.day = -1;
        this.dayLimit = 0;
        this.type = EPropType.getEPropType(prop.getPropType());
        //		initAbility(this.prop.getConfig());
    }

    /**
     * 初始化子类属性
     */
    public final void initAbility(String config) {
        // gold=1;exp=120;diamonds=10
        String[] m = StringUtil.toStringArray(config, StringUtil.DEFAULT_ST);
        for (String temp : m) {
            if (temp.isEmpty()) {
                continue;
            }
            String[] s = StringUtil.toStringArray(temp, StringUtil.DEFAULT_FH);
            if (s == null || s.length < 2) {
                continue;
            }
            setAttr(s[0], s[1]);
        }
    }

    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "hour": {
                setHour(Integer.parseInt(valStr));
                break;
            }
            case "day": {
                setDay(Integer.parseInt(valStr));
                break;
            }
            case "limit": {
                setDayLimit(Integer.parseInt(valStr));
                break;
            }
            default: {
                break;
            }
        }
    }

    public int getTid() {
        return this.prop.getTid();
    }

    public int getGroupId() {
        return prop.getGroupId();
    }

    public int getPropId() {
        return this.prop.getPropId();
    }

    public int getUse() {
        return this.prop.getUse();
    }

    public int getPropType() {
        return this.prop.getPropType();
    }

    public String getName() {
        return this.prop.getName();
    }

    public String getConfig() {
        return this.prop.getConfig();
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    protected void setHour(int hour) {
        this.hour = hour;
    }

    public int getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(int dayLimit) {
        this.dayLimit = dayLimit;
    }

    public EPropType getType() {
        return type;
    }

    public int getSale() {
        return this.prop.getSale();
    }

    public int getDailyMaxNum() {
        return prop.getDailyMaxNum();
    }

    public PropBeanVO getProp() {
        return prop;
    }
}

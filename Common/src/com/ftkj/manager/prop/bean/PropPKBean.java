package com.ftkj.manager.prop.bean;

import com.ftkj.db.domain.bean.PropBeanVO;

/**
 * @author tim.huang 2017年3月14日
 * 比赛用水类道具
 */
public class PropPKBean extends PropBean {
    private static final long serialVersionUID = 1L;
    /** 作用范围 场上 1,场上. 2,场下. 3,场上场下所有 */
    public static final int Range_Match = 1;
    /** 作用范围 场下 1,场上. 2,场下. 3,场上场下所有 */
    public static final int Range_Bench = 2;
    /** 作用范围 场上场下所有 1,场上. 2,场下. 3,场上场下所有 */
    public static final int Range_All = 3;

    /** 效果范围 全体 0全体 1单个 */
    public static final int Count_All = 0;
    /** 效果范围 单个 0全体 1单个 */
    public static final int Count_Single = 1;
    /**
     * 己方效果
     */
    private int home;
    /**
     * 对方效果
     */
    private int away;

    /**
     * 作用范围 1,场上. 2,场下. 3,场上场下所有
     */
    private int range;
    /**
     * 效果范围 0全体 1单个
     */
    private int count;
    /**
     * 细分类型
     * 1:饮料
     * 2:战术
     */
    private int child;

    public PropPKBean(PropBeanVO prop) {
        super(prop);
        this.home = 0;
        this.away = 0;
        this.range = 1;
        this.count = 1;
        this.child = 0;
    }

    public int getHome() {
        return home;
    }

    public int getAway() {
        return away;
    }

    public int getRange() {
        return range;
    }

    public int getCount() {
        return count;
    }

    public int getChild() {
        return child;
    }

    @Override
    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "child": {
                this.child = Integer.parseInt(valStr);
                break;
            }
            case "home": {
                this.home = Integer.parseInt(valStr);
                break;
            }
            case "away": {
                this.away = Integer.parseInt(valStr);
                break;
            }
            case "range": {
                this.range = Integer.parseInt(valStr);
                break;
            }
            case "count": {
                this.count = Integer.parseInt(valStr);
                break;
            }
            default:
                super.setAttr(nameStr, valStr);
        }
    }

}

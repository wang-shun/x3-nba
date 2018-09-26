package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.enums.EMoneyType;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author tim.huang
 * 2017年3月3日
 */
public class MoneyPO extends AsynchronousBatchDB {
    private long teamId;
    /** 球卷*/
    private int money;
    /** 绑定球卷*/
    private int bdMoney;
    /** 金币*/
    private int gold;
    /** 经验*/
    private int exp;
    /** 建设费*/
    private int jsf;

    public MoneyPO() {
        super();
    }

    public MoneyPO(long teamId) {
        super();
        this.teamId = teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMoney() {
        return money;
    }

    public int getGold() {
        return gold;
    }

    public int getExp() {
        return exp;
    }

    public int getJsf() {
        return jsf;
    }

    public void setJsf(int jsf) {
        this.jsf = jsf;
    }

    public int getBdMoney() {
        return bdMoney;
    }

    public void setBdMoney(int bdMoney) {
        this.bdMoney = bdMoney;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.teamId, this.money, this.gold, this.exp, this.jsf, this.bdMoney);
    }

    @Override
    public String getRowNames() {
        return "team_id,money,gold,exp,jsf,bd_money";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.money, this.gold, this.exp, this.jsf, this.bdMoney);
    }

    @Override
    public String getTableName() {
        return "t_u_money";
    }

    @Override
    public void del() {

    }

    /**
     * 不保存，链式调用
     *
     * @param money
     * @param value
     * @return
     */
    public void updateMoney(EMoneyType money, int value) {
        if (value == 0) {
            return;
        }
        switch (money) {
            case Money:
                setMoney(getMoney() + value);
                break;
            case Bind_Money:
                setBdMoney(getBdMoney() + value);
                break;
            case Gold:
                setGold(getGold() + value);
                break;
            case Exp:
                setExp(getExp() + value);
                break;
            case Build_Money:
                setJsf(getJsf() + value);
                break;
            default:
                break;
        }
    }

    public int getMoney(EMoneyType money) {
        switch (money) {
            case Money: return getMoney();
            case Bind_Money: return getBdMoney();
            case Gold: return getGold();
            case Exp: return getExp();
            case Build_Money: return getJsf();
            default: return 0;
        }
    }

    /**
     * 检查货币类型是否有足够的数量
     *
     * @param money
     * @param value
     * @return true 有
     */
    public boolean hasMoney(EMoneyType money, int value) {
        return getMoney(money) >= value;
    }


}

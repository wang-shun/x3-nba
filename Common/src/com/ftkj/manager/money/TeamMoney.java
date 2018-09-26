package com.ftkj.manager.money;

import com.ftkj.db.domain.MoneyPO;
import com.ftkj.enums.EMoneyType;

/**
 * @author tim.huang
 * 2017年3月3日
 * 球队货币
 */
public class TeamMoney {
    private MoneyPO money;

    public TeamMoney(MoneyPO money) {
        super();
        this.money = money;
        this.money.save();
    }

    /**
     * 不保存，链式调用
     */
    public TeamMoney updateMoney(EMoneyType money, int value) {
        this.money.updateMoney(money, value);
        return this;
    }

    public int getMoney(EMoneyType money) {
        return this.money.getMoney(money);
    }

    /**
     * 检查货币类型是否有足够的数量
     *
     * @return true 有
     */
    public boolean hasMoney(EMoneyType money, int value) {
        return getMoney(money) >= value;
    }

    public void save() {
        this.money.save();
    }

    public long getTeamId() {
        return this.money.getTeamId();
    }

    public int getMoney() {
        return this.money.getMoney();
    }

    public int getGold() {
        return this.money.getGold();
    }

    public int getExp() {
        return this.money.getExp();
    }

    public int getJsf() {
        return this.money.getJsf();
    }

    public int getBdMoney() {
        return this.money.getBdMoney();
    }

}

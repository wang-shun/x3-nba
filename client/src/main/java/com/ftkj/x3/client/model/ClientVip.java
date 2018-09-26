package com.ftkj.x3.client.model;

import com.ftkj.db.domain.VipPO;

import java.util.List;

/**
 * @author luch
 */
public class ClientVip extends VipPO {
    private List<Integer> buyStatus;

    public List<Integer> getBuyStatusList() {
        return buyStatus;
    }

    public void setBuyStatus(List<Integer> buyStatus) {
        this.buyStatus = buyStatus;
    }

    @Override
    public void save() {
    }

    @Override
    public void del() {
    }

    public boolean isVip() {
        return getLevel() > 0;
    }
}

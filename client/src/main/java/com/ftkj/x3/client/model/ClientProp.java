package com.ftkj.x3.client.model;

import com.ftkj.db.domain.PropPO;

/**
 * @author luch
 */
public class ClientProp extends PropPO {
    private int endSec;

    public int getEndSec() {
        return endSec;
    }

    public void setEndSec(int endSec) {
        this.endSec = endSec;
    }

    @Override
    public void save() {
    }

    @Override
    public void del() {
    }

}

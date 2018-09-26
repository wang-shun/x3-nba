package com.ftkj.x3.client.model;

import com.ftkj.db.domain.BuffPO;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author luch
 */
public class ClientBuff extends BuffPO {

    private List<Integer> params;

    public ClientBuff() {
    }

    public ClientBuff(int id, List<Integer> params, DateTime endTime) {
        super();
        setId(id);
        setParams(params);
        setEndTime(endTime);
    }

    public List<Integer> getParam() {
        return params;
    }

    public void setParams(List<Integer> params) {
        this.params = params;
    }

    @Override
    public void save() {
    }

    @Override
    public void del() {
    }

    public int getParamSum() {
        return params == null ? 0 : params.stream().mapToInt(e -> e).sum();
    }
}

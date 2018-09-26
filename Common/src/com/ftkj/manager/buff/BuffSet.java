package com.ftkj.manager.buff;

import java.io.Serializable;
import java.util.List;

public class BuffSet implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<TeamBuff> list;

    public BuffSet(List<TeamBuff> list) {
        this.list = list;
    }

    public List<TeamBuff> getList() {
        return list;
    }

    public void setList(List<TeamBuff> list) {
        this.list = list;
    }

    /**
     * 取value的第一个值聚合
     *
     * @return
     */
    public int getValueSum() {
        return list.stream().mapToInt(b -> b.getValues().getValue(0)).sum();
    }

    public int getValueSum(int index) {
        return list.stream().mapToInt(b -> b.getValues().getValue(index)).sum();
    }

    /**
     * 取第几个参数的第几个值
     * 一般用于，%X概率返还X%的情况，概率叠加，返还比例不叠加
     *
     * @param num
     * @param index
     * @return
     */
    public int getValue(int num, int index) {
        return list.get(num).getValues().getValue(index);
    }

}

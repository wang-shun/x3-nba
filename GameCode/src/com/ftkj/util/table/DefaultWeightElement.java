package com.ftkj.util.table;

import com.ftkj.util.IntervalInt;

import java.io.Serializable;

/** 权重子元素. 格式: id,权重; */
public class DefaultWeightElement implements IdentifyWeightElement, Serializable {
    private static final long serialVersionUID = 1739124117808464503L;
    /** id. 例如:组id, 球员品质, 道具组id, 道具id, 球员组id, 球员id 等等 */
    private final int id;
    /** 权重 */
    private final IntervalInt weight;

    public DefaultWeightElement(int id, IntervalInt weight) {
        this.id = id;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public IntervalInt getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"weight\":" + weight +
                '}';
    }
}

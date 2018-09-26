package com.ftkj.util.table;

/** 带有标识的权重子元素. */
public interface IdentifyWeightElement extends WeightElement {
    /** id. 例如:组id, 球员品质, 道具组id, 道具id, 球员组id, 球员id 等等 */
    int getId();
}

package com.ftkj.manager.prop;

import java.io.Serializable;

/**
 * 道具随机项
 * @author Jay
 * @time:2018年2月6日 上午11:28:57
 */
public class PropRandomHit implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 命中下标
     */
    private int index;
    private PropRandom item;
    
	public PropRandomHit(int index, PropRandom item) {
		this.index = index;
		this.item = item;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public PropRandom getItem() {
		return item;
	}
	public void setItem(PropRandom item) {
		this.item = item;
	}

}

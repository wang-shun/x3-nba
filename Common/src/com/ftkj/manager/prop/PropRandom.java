package com.ftkj.manager.prop;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 道具随机项
 * @author Jay
 * @time:2018年2月6日 上午11:28:57
 */
public class PropRandom extends PropAwardConfig implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int propId;
    private int num;
    private int rate;
    
	public PropRandom(int propId, int num, int rate) {
		super();
		this.propId = propId;
		this.num = num;
		this.rate = rate;
	}
	
	/**
	 * 拷贝数据创建新的对象
	 * @return
	 */
	public PropRandomHit copyNew(int index) {
		return new PropRandomHit(index, new PropRandom(this.propId, this.num, this.rate));
	}

	public int getPropId() {
		return propId;
	}

	public void setPropId(int propId) {
		this.propId = propId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	@Override
	public List<PropSimple> getPropSimpleList() {
		return Lists.newArrayList(new PropSimple(this.propId, this.num));
	}
}

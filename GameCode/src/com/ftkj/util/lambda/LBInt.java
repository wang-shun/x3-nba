package com.ftkj.util.lambda;

/**
 * @author tim.huang
 * 2017年2月28日
 * 在做聚合计算的时候可以使用
 */
public class LBInt {
	private int val;
	
	
	public LBInt(int val) {
		super();
		this.val = val;
	}
	
	
	public int increaseAndGet(){
		this.val++;
		return this.val;
	}

	public LBInt() {
		super();
	}

	public void sum(int v){
		this.val+=v;
	}
	
	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}
	
}

package com.ftkj.manager.tactics;

import com.ftkj.enums.TacticId;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年3月14日
 *
 */
public class TacticsSimple implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private TacticId tid;
	private int level;
	
	public TacticsSimple(TacticId tid, int level) {
		super();
		this.tid = tid;
		this.level = level;
	}

	public TacticId getTid() {
		return tid;
	}

	public int getLevel() {
		return level;
	}
	
	
}

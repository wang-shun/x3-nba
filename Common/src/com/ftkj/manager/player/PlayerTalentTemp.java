package com.ftkj.manager.player;

/**
 * @author tim.huang
 * 2018年2月28日
 * 临时处理天赋对象
 */ 
public class PlayerTalentTemp extends PlayerTalent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean dflock;
	private boolean zglock;
	private boolean lblock;
	private boolean qdlock;
	private boolean gmlock;
	private boolean tlmzlock;
	private boolean fqmzlock;
	private boolean sfmzlock;
	
	public boolean isDflock() {
		return dflock;
	}
	public void setDflock(boolean dflock) {
		this.dflock = dflock;
	}
	public boolean isZglock() {
		return zglock;
	}
	public void setZglock(boolean zglock) {
		this.zglock = zglock;
	}
	public boolean isLblock() {
		return lblock;
	}
	public void setLblock(boolean lblock) {
		this.lblock = lblock;
	}
	public boolean isQdlock() {
		return qdlock;
	}
	public void setQdlock(boolean qdlock) {
		this.qdlock = qdlock;
	}
	public boolean isGmlock() {
		return gmlock;
	}
	public void setGmlock(boolean gmlock) {
		this.gmlock = gmlock;
	}
	public boolean isTlmzlock() {
		return tlmzlock;
	}
	public void setTlmzlock(boolean tlmzlock) {
		this.tlmzlock = tlmzlock;
	}
	public boolean isFqmzlock() {
		return fqmzlock;
	}
	public void setFqmzlock(boolean fqmzlock) {
		this.fqmzlock = fqmzlock;
	}
	public boolean isSfmzlock() {
		return sfmzlock;
	}
	public void setSfmzlock(boolean sfmzlock) {
		this.sfmzlock = sfmzlock;
	}
	
	
}

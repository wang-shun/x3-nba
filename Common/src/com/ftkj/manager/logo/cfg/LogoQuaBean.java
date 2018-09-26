package com.ftkj.manager.logo.cfg;

/**
 * 头像品质合成分解现相关的配置
 * @author Jay
 * @time:2017年3月15日 下午8:03:47
 */
public class LogoQuaBean {

	/**
	 * 合成品质
	 */
	private int quality;
	/**
	 * 攻防
	 */
	private int cap;
	/**
	 * 碎片合成概率
	 */
	private float combRate;
	
	/**
	 * 分解成碎片的数量
	 */
	private int debris;
	
	/**
	 * 进阶需要同等级数量，（四合一）
	 */
	private int forward;
	
	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getCap() {
		return cap;
	}

	public void setCap(int cap) {
		this.cap = cap;
	}

	public float getCombRate() {
		return combRate;
	}

	public void setCombRate(float combRate) {
		this.combRate = combRate;
	}

	public int getDebris() {
		return debris;
	}

	public void setDebris(int debris) {
		this.debris = debris;
	}

	public int getForward() {
		return forward;
	}

	public void setForward(int forward) {
		this.forward = forward;
	}

}

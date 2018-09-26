package com.ftkj.cfg;

/**
 * 捐献勋章配置
 * @author tim.huang
 * 2017年6月26日
 */
public class LeagueAppointBean {
				
	/**勋章道具ID*/
	private int propId;
	/**贡献*/
	private int score;
	/**功勋*/
	private int feats;
	/**荣誉*/
	private int honor;
	/**可获得联盟升级经验*/
	private int exp;
	
	public int getPropId() {
		return propId;
	}
	public void setPropId(int propId) {
		this.propId = propId;
	}
	
	/**获取, 贡献值
	 * @return
	 */
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	/**
	 * 获取,功勋值
	 * @return
	 */
	public int getFeats() {
		return feats;
	}
	
	public void setFeats(int feats) {
		this.feats = feats;
	}
	public int getHonor() {
		return honor;
	}
	public void setHonor(int honor) {
		this.honor = honor;
	}
	
	/**可获得联盟升级经验*/
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	
}

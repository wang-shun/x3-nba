package com.ftkj.cfg;

/**
 * 联盟每日贡献累计奖励配置
 * @author mr.lei
 * 2018年9月15日11:23:03
 */
public class LeagueScoreRewardBean {
	/**需求贡献*/
	private int score;
	/**礼包奖励*/
	private String reward;

	public LeagueScoreRewardBean() {
		super();
	}

	/**需求贡献*/
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	/**礼包奖励*/
	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

}

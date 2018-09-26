package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

/**
 * 降薪的新秀球员配置数据.
 * @author mr.lei
 * 2018-8-6 10:08:21
 *
 */
public class PlayerCutsBean extends ExcelBean {
	/**可降薪的球员ID*/
	private int playerId;
	
	/**最低可降到的工资*/
	private int minPrice;
	
	/**可降薪的品质*/
	private String grade;
	
	@Override
	public void initExec(RowData row) {
		

	}

	/**获取, 可降薪的球员ID*/
	public int getPlayerId() {
		return playerId;
	}

	/**设置, 可降薪的球员ID*/
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	/**获取, 最低可降到的工资*/
	public int getMinPrice() {
		return minPrice;
	}

	/**设置, 最低可降到的工资*/
	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	/**获取, 可降薪的品质*/
	public String getGrade() {
		return grade;
	}

	/**设置, 可降薪的品质*/
	public void setGrade(String grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return "PlayerCutsBean [playerId=" + playerId + ", minPrice="
				+ minPrice + ", grade=" + grade + "]";
	}
	
}

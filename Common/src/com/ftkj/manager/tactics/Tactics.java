package com.ftkj.manager.tactics;

import org.joda.time.DateTime;

import com.ftkj.db.domain.TacticsPO;
import com.ftkj.enums.TacticId;

/**
 * @author tim.huang
 * 2017年3月2日
 * 战术数据
 */
public class Tactics {
	private TacticsPO tacticsInfo;
	
	public Tactics(TacticsPO tacticsInfo) {
		super();
		this.tacticsInfo = tacticsInfo;
	}
	
	public void save(){
		this.tacticsInfo.save();
	}

	public TacticId getTactics(){
		return TacticId.convert(tacticsInfo.getTid());
	}

	
	/**
	 * 取等级，如果等级10，且突破状态buffTime大于当前时间，返回11级		
	 * @return
	 */
	public int getLevel() {
		int lv = this.tacticsInfo.getLevel();
		if(lv == 10 && this.tacticsInfo.getBuffTime().isAfter(DateTime.now())) {
			return 11;
		}
		return lv;
	}
	
	public void setLevel(int level) {
		this.tacticsInfo.setLevel(level);
		
	}
	
	public void setBuffTime(DateTime date) {
		this.tacticsInfo.setBuffTime(date);
	}
	
	public DateTime getBuffTime() {
		return this.tacticsInfo.getBuffTime();
	}
}

package com.ftkj.manager.active.cardAward;

import org.joda.time.DateTime;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.util.DateTimeUtil;

/**
 * 周卡月卡类
 * @author Jay
 * @time:2018年2月7日 上午10:30:34
 */
public class AtvCardAward extends ActiveBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ActiveDataField(fieldName = "sData1", size=31)
	private DBList status;
	
	public AtvCardAward(ActiveBasePO po) {
		super(po);
	}
	
	/**
	 * 重置对象
	 */
	public void clear() {
		this.getPo().setCreateTime(DateTime.now());
		this.setDayCount(0);
		this.setiData2(0);
		this.status = new DBList(this.status.getSize());
		this.save();
	}
	
	/**
	 * 判断是否已购买
	 * true 时， 可以购买， false 是购买过，不能购买
	 * @return
	 */
	public boolean isNull() {
		int days = DateTimeUtil.getDaysBetweenNum(getCreateTime(), DateTime.now(), 0);
		int hasCount = getDayCount() - days - 1;
		int status = hasCount >= 0 ? getStatus().getValue(days) : 1;
		if(hasCount < 0 || (hasCount==0 && status == 1)) {
			return true;
		}
		return false;
	}
	
	public void setDayCount(int day) {
		this.setiData1(day);
	}
	
	public int getDayCount() {
		return this.getiData1();
	}
	
	public DBList getStatus() {
		return this.status;
	}
	
	public DateTime getCreateTime() {
		return this.getPo().getCreateTime();
	}
}

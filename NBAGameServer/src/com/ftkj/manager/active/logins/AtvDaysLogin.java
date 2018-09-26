package com.ftkj.manager.active.logins;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.DBList;

/**
 * 累计登录通用
 * @author Jay
 * @time:2017年9月8日 上午10:50:33
 */
public class AtvDaysLogin extends ActiveBase {

	private static final long serialVersionUID = 1L;

	public AtvDaysLogin(ActiveBasePO po) {
		super(po);
	}
	public DBList getLoginStatus() {
		return this.getFinishStatus();
	}
	public void addLoginDay(int day) {
		this.setiData1(getiData1() + day);
	}
	public int getLoginDay() {
		return this.getiData1();
	}
	
}

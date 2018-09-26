package com.ftkj.manager.active.longtime;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;

import java.util.List;

/**
 * 用户屏蔽入口
 * @author Jay
 * @time:2017年9月8日 下午5:42:51
 */
@ActiveAnno(redType=ERedType.活动, atv = EAtv.活动入口屏蔽, clazz=AtvTeamCloseManager.AtvCloseWindow.class)
public class AtvTeamCloseManager extends ActiveBaseManager {


	@Override
	public void instanceAfter() {
		super.instanceAfter();
	}

	/**
	 * 取用户屏蔽入口列表
	 * @param teamId
	 * @return
	 */
	public List<Integer> getCloseList(long teamId) {
		AtvCloseWindow atvObj = getTeamData(teamId);
		return atvObj.items.getList();
	}

	/**
	 * 添加用户屏蔽入口
	 * @param teamId
	 * @param atvId
	 */
	public void addTeamCloseAtv(long teamId, int atvId) {
		AtvCloseWindow atvObj = getTeamData(teamId);
		if(atvObj.items.containsValue(atvId)) return;
		atvObj.items.addValue(atvId);
		atvObj.save();
	}

	public static class AtvCloseWindow extends ActiveBase {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public AtvCloseWindow(ActiveBasePO po) {
			super(po);
		}

		@ActiveDataField(fieldName = "sData1")
		private DBList items;

		public DBList getItems() {
			return items;
		}
	}

}

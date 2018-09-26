package com.ftkj.manager.active.starload;

import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.server.ServiceCode;

/**
 * 巨星之路的接口定义处
 * @author Jay
 * @time:2017年9月8日 下午5:42:51
 */
public class AtvStarLoadManager extends BaseManager {

	
	@Override
	public void instanceAfter() {

	}
	
	/**
	 * 主界面
	 * @param atvId
	 */
	@ClientMethod(code = ServiceCode.AtvStarLoadManager_showView)
	public void showView(int atvId) {
		IStarLoadManager manager = (IStarLoadManager) ActiveBaseManager.getManager(atvId);
		if(manager != null) {
			manager.showView();
		}
	}
	
	/**
	 * 领奖
	 * @param atvId
	 * @param id
	 */
	@ClientMethod(code = ServiceCode.AtvStarLoadManager_getAward)
	public void getAward(int atvId, int id) {
		IStarLoadManager manager = (IStarLoadManager) ActiveBaseManager.getManager(atvId);
		if(manager != null) {
			manager.getAward(id);
		}
	}

	/**
	 * 领排名奖励
	 * @param atvId
	 * @param id
	 */
	@ClientMethod(code = ServiceCode.AtvStarLoadManager_getRankAward)
	public void getRankAward(int atvId) {
		IStarLoadManager manager = (IStarLoadManager) ActiveBaseManager.getManager(atvId);
		if(manager != null) {
			manager.getRankAward();
		}
	}
}

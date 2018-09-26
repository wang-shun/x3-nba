package com.ftkj.db.ao.pk.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.pk.IPKCustomAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.pk.PKCustomDAO;
import com.ftkj.manager.custom.CustomPVPRoom;

/**
 * @author tim.huang
 * 2017年8月10日
 *
 */
public class PKCustomAOImpl extends BaseAO implements IPKCustomAO {

	@IOC
	private PKCustomDAO customDAO;
	
	@Override
	public List<CustomPVPRoom> getCustomPVPRoomList() {
		return customDAO.getCustomPVPRoomList();
	}

}

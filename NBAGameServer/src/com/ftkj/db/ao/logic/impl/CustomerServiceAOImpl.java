package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ICustomerServiceAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.CustomerServiceDAO;
import com.ftkj.db.domain.CustomerServicePO;

/**
 * @author mr.lei
 * 2018年8月31日14:29:02
 */
public class CustomerServiceAOImpl extends BaseAO implements ICustomerServiceAO {

	@IOC
	private CustomerServiceDAO dao;
	
	@Override
	public List<CustomerServicePO> getAllCustomerService() {
		return dao.getAllCustomerService();
	}

	@Override
	public List<CustomerServicePO> getPlayerCustomerServiceData(long teamId) {
		return dao.getPlayerCustomerServiceData(teamId);
	}

	@Override
	public CustomerServicePO getCustomerServiceDataByCsId(long csId) {
		return dao.getCustomerServiceDataByCsId(csId);
	}

	@Override
	public int getRowCount() {
		return dao.getRowCount();
	}
    
	
}

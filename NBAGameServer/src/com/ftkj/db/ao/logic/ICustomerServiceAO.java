package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.CustomerServicePO;

/**
 * 客服工单AO
 * @author mr.lei
 * @since 2018年8月31日12:29:48
 */
public interface ICustomerServiceAO {
	/**查询所有客服工单数据*/
	public List<CustomerServicePO> getAllCustomerService();
	
	/**查询玩家提的客服工单数据*/
	public List<CustomerServicePO> getPlayerCustomerServiceData(long teamId);
	
	/**根据csId查询玩家提的客服工单数据*/
	public CustomerServicePO getCustomerServiceDataByCsId(long csId);
	
	/**获取表中表记录行数*/
	public int getRowCount();
}

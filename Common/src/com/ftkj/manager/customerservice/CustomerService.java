package com.ftkj.manager.customerservice;

import com.ftkj.db.domain.CustomerServicePO;

/**
 * 客服工单
 * @author mr.lei
 *
 */
public class CustomerService {
	
	private CustomerServicePO po;

	public CustomerService(CustomerServicePO po) {
		super();
		this.po = po;
	}
	
	/**
	 * 生成一条客服工单数据,并保存到数据库.
	 * @param csId
	 * @param areaName
	 * @param teamId
	 * @param vipLevel
	 * @param playerName
	 * @param telphone
	 * @param problem
	 * @param response
	 * @param occurTime
	 * @param updateTime
	 * @return
	 */
	public static CustomerService createCustomerService(long csId, String areaName, long teamId,
			int vipLevel, String playerName, String telphone, String qq, String problem,
			String response,String respStatus, String occurTime){
		CustomerServicePO po = new CustomerServicePO(csId, areaName, teamId, vipLevel, playerName, 
				telphone, qq, problem, response,respStatus, occurTime);
		po.save();
		return new CustomerService(po);
	}
	
	public void save(){
		this.po.save();
	}
	
	public void del(){
		this.po.del();
	}

	public CustomerServicePO getPo() {
		return po;
	}
	
}

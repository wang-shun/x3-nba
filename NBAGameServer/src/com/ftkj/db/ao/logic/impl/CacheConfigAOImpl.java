package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ICachceConfigAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.CacheConfigDAO;
import com.ftkj.db.domain.bean.ConfigBeanVO;

/**
 * @author tim.huang
 * 2017年3月7日
 * 缓存配置AO
 */
@Deprecated
public class CacheConfigAOImpl extends BaseAO implements ICachceConfigAO {
	
	@IOC
	private CacheConfigDAO cacheConfigDAO;	

	@Override
	public List<ConfigBeanVO> getAllConfigBean() {
		return cacheConfigDAO.getAllConfigBean();
	}
	

}

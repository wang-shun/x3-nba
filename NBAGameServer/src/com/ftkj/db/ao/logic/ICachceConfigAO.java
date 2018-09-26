package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.bean.ConfigBeanVO;

/**
 * @author tim.huang
 * 2017年3月7日
 * 缓存配置AO
 */
public interface ICachceConfigAO {
	
	public List<ConfigBeanVO> getAllConfigBean();
	
}

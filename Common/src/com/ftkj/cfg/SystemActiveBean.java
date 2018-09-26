package com.ftkj.cfg;

import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.console.GameConsole;
import com.ftkj.db.domain.active.base.SystemActivePO;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

/**
 * 活动配置
 * @author Jay
 * @time:2017年9月6日 下午2:48:39
 */
public class SystemActiveBean extends ExcelBean {

	private int atvId;
	private String name;
	private int noTimeLimit;
	/**
	 * 活动配置
	 */
	private Map<String, String> configMap;
	/**
	 * 配置
	 */
	private Map<Integer, SystemActiveCfgBean> configList;
	
	/**
	 * 活动配置
	 */
	private SystemActivePO active;
	
	@Override
	public void initExec(RowData row) {
		String config = row.get("config");
		if(config!=null && !config.trim().equals("")) {
			String[] s = config.trim().split(",");
			this.configMap = Maps.newHashMap();
			for(String c : s) {
				String[] k = c.split("=");
				if(k.length < 2){
				    continue;
                }
				this.configMap.put(k[0], k[1]);
			}
		}
	}

	public int getAtvId() {
		return atvId;
	}

	public void setAtvId(int atvId) {
		this.atvId = atvId;
	}

	public DateTime getStartDateTime() {
		return this.active == null ? GameConsole.Min_Date : this.active.getStartTime();
	}

	public void setStartDateTime(DateTime startDateTime) {
		if(this.active == null) {
			return;
		}
		this.active.setStartTime(startDateTime);
	}

	/**
	 * 不受时间限制的活动默认返回最大时间
	 * @return
	 */
	public DateTime getEndDateTime() {
		if(this.getNoTimeLimit() == 1) {
			return GameConsole.Max_Date;
		}
		if(this.active != null) {
			return this.active.getEndTime();
		}
		return GameConsole.Min_Date;
	}

	public void setEndDateTime(DateTime endDateTime) {
		if(this.active == null) {
			return;
		}
		this.active.setEndTime(endDateTime);
	}

	public Map<String, String> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map<String, String> configMap) {
		this.configMap = configMap;
	}

	public Map<Integer, SystemActiveCfgBean> getAwardConfigList() {
		return configList;
	}

	public void setConfigList(Map<Integer, SystemActiveCfgBean> configList) {
		this.configList = configList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Integer, SystemActiveCfgBean> getConfigList() {
		return configList;
	}

	/**
	 * 对活动配置做特殊处理 <BR/>
	 * 自定义配置会覆盖excel里面的配置
	 * @param active
	 */
	public void setActive(SystemActivePO active) {
		this.active = active;
		String userConfig = this.active.getJsonConfig();
		if(userConfig == null || userConfig.trim().equals("")) {
			return;
		}
		//|| 
		String[] cfgs = userConfig.split(",");
		if(cfgs.length < 1 || cfgs[0].equals("")) {
			return;
		}
		for(String cfg : cfgs) {
			if(userConfig.indexOf("=") == -1) {
				continue;
			}
			String[] c = cfg.split("=");
			this.configMap.put(c[0], c[1]);
		}
	}
	
	public SystemActivePO getActive() {
		return active;
	}

	public int getNoTimeLimit() {
		return noTimeLimit;
	}

	public void setNoTimeLimit(int noTimeLimit) {
		this.noTimeLimit = noTimeLimit;
	}

	@Override
	public String toString() {
		return "SystemActiveBean [atvId=" + atvId + ", name=" + name + ", startDateTime=" + DateTimeUtil.getStringSql(getStartDateTime())
				+ ", endDateTime=" + DateTimeUtil.getStringSql(getEndDateTime()) + ", status=" + this.active.getStatus() + "]";
	}
	
	
}

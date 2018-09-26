package com.ftkj.cfg;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.excel.RowData;

public class ServerShardBean extends ExcelBean {

	/**
	 * 区号
	 */
	private int shardId;
	/**
	 * 开服时间
	 */
	private DateTime serverStartTime;
	public int getShardId() {
		return shardId;
	}
	public void setShardId(int shardId) {
		this.shardId = shardId;
	}
	public DateTime getServerStartTime() {
		return serverStartTime;
	}
	public void setServerStartTime(DateTime serverStartTime) {
		this.serverStartTime = serverStartTime;
	}
	@Override
	public void initExec(RowData row) {
		this.serverStartTime = DateTimeUtil.getDateTime(row.get("startTime"));
	}

}

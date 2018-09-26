package com.ftkj.enums.log;

/**
 * @author tim.huang
 * 2017年11月27日
 * 阿里日志版本控制
 */
public enum ELogVersion {
	
	道具统计(2.7f,"syslog_team_prop_tag"),
	货币统计(2.4f,"syslog_team_money_tag"),
	新手引导(2.5f,"syslog_help_step_tag"),
	在线时长(2.6f,"syslog_team_oline_tag"),
	会员(2.8f,"syslog_team_vip_tag"),
	键值对(3.0f,"syslog_key_val_tag"),
	登录(3.1f,"syslog_log_login_tag"),
	//
	身价变动(3.2f,"syslog_log_price_tag"),
	待签球员(3.3f,"syslog_log_besign_tag"),
	球队管理(3.4f,"syslog_log_player_tag"),
	// 聊天独立
	聊天记录(3.5f,"syslog_log_chat_tag"),
	
	;
	
	private String logVersion;
	private String logSyslog;
	
	ELogVersion(float logVersion,String logSyslog) {
		this.logVersion = ""+logVersion;
		this.logSyslog = logSyslog;
	}

	public String getLogVersion() {
		return logVersion;
	}

	public String getLogSyslog() {
		return logSyslog;
	}
	
}

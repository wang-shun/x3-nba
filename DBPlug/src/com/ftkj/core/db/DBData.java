package com.ftkj.core.db;


/**
 * @author tim.huang
 * 2016年1月11日
 * 数据库数据
 */
public class DBData {
	private String userName;
	private String passWord;
	private String ip;
	private String port;
	private String dbName;
	
	public DBData(String userName, String passWord, String ip, String port,
			String dbName) {
		super();
		this.userName = userName;
		this.passWord = passWord;
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
	}
	
	public String getUserName() {
		return userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public String getIp() {
		return ip;
	}
	public String getPort() {
		return port;
	}
	public String getDbName() {
		return dbName;
	}
	
	
}

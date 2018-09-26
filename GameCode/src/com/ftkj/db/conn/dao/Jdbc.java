package com.ftkj.db.conn.dao;

public class Jdbc {
	String username;
	String password;
	String driver;
	String url;
	
	//每个分区最小连接数量
	int minConnectionsPerPartition;
	//每个分区最大连接数量
	int maxConnectionsPerPartition;
	//连接耗尽时,一次性创建的连接数
	int acquireIncrement;
	//分区数量
	int partitionCount;
	//设置测试空闲连接测试时间
	int idleConnectionTestPeriodInSeconds;
	//设置statement缓存大小
	int statementsCacheSize;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getMinConnectionsPerPartition() {
		return minConnectionsPerPartition;
	}
	public void setMinConnectionsPerPartition(int minConnectionsPerPartition) {
		this.minConnectionsPerPartition = minConnectionsPerPartition;
	}
	public int getMaxConnectionsPerPartition() {
		return maxConnectionsPerPartition;
	}
	public void setMaxConnectionsPerPartition(int maxConnectionsPerPartition) {
		this.maxConnectionsPerPartition = maxConnectionsPerPartition;
	}
	public int getAcquireIncrement() {
		return acquireIncrement;
	}
	public void setAcquireIncrement(int acquireIncrement) {
		this.acquireIncrement = acquireIncrement;
	}
	public int getPartitionCount() {
		return partitionCount;
	}
	public void setPartitionCount(int partitionCount) {
		this.partitionCount = partitionCount;
	}
	public int getIdleConnectionTestPeriodInSeconds() {
		return idleConnectionTestPeriodInSeconds;
	}
	public void setIdleConnectionTestPeriodInSeconds(
			int idleConnectionTestPeriodInSeconds) {
		this.idleConnectionTestPeriodInSeconds = idleConnectionTestPeriodInSeconds;
	}
	public int getStatementsCacheSize() {
		return statementsCacheSize;
	}
	public void setStatementsCacheSize(int statementsCacheSize) {
		this.statementsCacheSize = statementsCacheSize;
	}
	
	
	
}

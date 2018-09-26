package com.ftkj.invoker;

public class Jdbc {
	String username;
	String password;
	String driver;
	String url;
	
	int maxPoolSize;
	int minPoolSize;
	int checkoutTimeout;
	int idleConnectionTestPeriod;
	int maxIdleTime;
	int maxStatements;
		
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
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	public int getMinPoolSize() {
		return minPoolSize;
	}
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public int getCheckoutTimeout() {
		return checkoutTimeout;
	}
	public void setCheckoutTimeout(int checkoutTimeout) {
		this.checkoutTimeout = checkoutTimeout;
	}
	public int getIdleConnectionTestPeriod() {
		return idleConnectionTestPeriod;
	}
	public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
		this.idleConnectionTestPeriod = idleConnectionTestPeriod;
	}
	public int getMaxIdleTime() {
		return maxIdleTime;
	}
	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}
	public int getMaxStatements() {
		return maxStatements;
	}
	public void setMaxStatements(int maxStatements) {
		this.maxStatements = maxStatements;
	}
	@Override
	public String toString() {
		return "Jdbc [checkoutTimeout=" + checkoutTimeout + ", driver="
				+ driver + ", idleConnectionTestPeriod="
				+ idleConnectionTestPeriod + ", maxIdleTime=" + maxIdleTime
				+ ", maxPoolSize=" + maxPoolSize + ", maxStatements="
				+ maxStatements + ", minPoolSize=" + minPoolSize
				+ ", password=" + password + ", url=" + url + ", username="
				+ username + "]";
	}
	
	
}

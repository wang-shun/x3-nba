package com.ftkj.jredis;

public class Jredis {	
	String host;
	int port;
	String password;
	int database;
	int connectionCount;	
	
	public Jredis(){}
	
	public static Jredis init(){
		Jredis j = new Jredis();
		j.setHost("58.251.128.79");//58.251.128.79,192.168.0.176
        j.setPort(6379);
        j.setDatabase(0);
        j.setPassword(null);
        j.setConnectionCount(5);
		return j;
	}
	
	public Jredis(String host,int port,int database,int connectionCount,String password){
		this.host = host;
		this.port = port;
		this.database = database;
		this.connectionCount = connectionCount;
		this.password = password;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getDatabase() {
		return database;
	}
	public void setDatabase(int database) {
		this.database = database;
	}
	public int getConnectionCount() {
		return connectionCount;
	}
	public void setConnectionCount(int connectionCount) {
		this.connectionCount = connectionCount;
	}
	
}

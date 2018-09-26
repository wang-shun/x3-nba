package com.ftkj.invoker;

public class Server {
	ServerType serverName;
	int port;
	String ip;
	public Server(){}
	public Server(ServerType serverName,String ip,int port){
		this.serverName = serverName;
		this.ip = ip;
		this.port = port;
	}
	
	public ServerType getServerName() {
		return serverName;
	}
	public void setServerName(ServerType serverName) {
		this.serverName = serverName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getId(){
		return ip+":"+port;
	}
	@Override
	public String toString() {
		return "Server [ip=" + ip + ", port=" + port + ", serverName="+ serverName + "]";
	}
	
}

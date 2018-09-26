package com.ftkj.client;

import com.ftkj.client.robot.BaseRobot;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

public class BaseClient{

	private GameClient client; 
	
	
	public void conn(BaseRobot robot,String ip,int port){
		client = new GameClient();
//		client.conn("121.10.118.25", 8038, new ClientHandler(robot));
		client.conn(ip, port, new ClientHandler(robot));
	}
	
	public void conn(BaseRobot robot) throws Exception {
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			ip = "127.0.0.1";
		}
		client = new GameClient();
		client.conn(ip, 8038, new ClientHandler(robot));
	}
	
	public void sendData(ClientData data){
		String da = data.getValues().stream().collect(Collectors.joining(GameClient.S));
		ClientRequest req = new ClientRequest(data.getRid(), data.getServiceCode(),da);
		client.send(req);
	}
	
	public boolean isConn(){
		return client!=null && client.isConn();
	}
	
	
}

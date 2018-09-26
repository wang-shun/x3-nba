package com.ftkj.client;

import java.util.stream.Collectors;

public class BaseClient{

	private GameClient client; 
	
	
	public void conn(String ip,int port){
		client = new GameClient();
//		client.conn("121.10.118.25", 8038, new ClientHandler(robot));
		client.conn(ip, port, new ClientHandler());
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

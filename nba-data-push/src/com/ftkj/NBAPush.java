package com.ftkj;

import com.ftkj.client.BaseClient;
import com.ftkj.client.ClientData;
import com.ftkj.tool.log.LoggerManager;

import java.net.ConnectException;
import java.util.Arrays;

public class NBAPush {
	
	private static final NBAPush obj = new NBAPush();
	
	private BaseClient bc;
	public static NBAPush get(){
		return obj;
	}
	
	public void initNBAPush(String ip,int port){
		bc = getClient(ip, port);
		LoggerManager.info("连接到服务器.ip={}, port={}, isConn={}", ip, port, bc.isConn());
	}
	
	private BaseClient client = new BaseClient();
	
	
	public void reloadNBAData() throws Exception{
		if(bc.isConn()) {
			LoggerManager.info("发生身价更新包={}", 110001001);
			bc.sendData(genClientData(110001001,"All"));
		}
		else {
			throw new ConnectException("连接不成功");
		}
	}
	
	
	private BaseClient getClient(String ip,int port){
		if(!client.isConn()){
			client.conn(ip, port);
		}
		return client;
	}
	
	public void reloadNBAPKData() throws Exception{
		if(bc.isConn()) {
			LoggerManager.info("发送刷新今日赛程包={}", 110001002);
			bc.sendData(genClientData(110001002));
		}
		else {
			throw new ConnectException("连接不成功");
		}
	}
	
	
	public static void main(String[] args) throws Exception{
//		NBAPush.get().reloadNBAData("192.168.12.116", 2222);
//		NBAPush.get().reloadNBAPKData("192.168.12.116", 2222);
	}
	
	public static ClientData genClientData(int serviceCode, Object...args) {
		ClientData data = new ClientData(serviceCode);
		if(args!=null && args.length>0){
			Arrays.stream(args).forEach(arg->data.appendValues(arg.toString()));
		}
		return data;
	}
	
}

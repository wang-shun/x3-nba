package com.ftkj.route;

import java.io.File;

import org.apache.zookeeper.data.Stat;

import com.ftkj.tool.zookeep.ZookeepConfig;
import com.ftkj.tool.zookeep.ZookeepServer;
import com.ftkj.util.ByteUtil;

public class ConfigServer {
	public static void main(String[] args) throws Throwable{
		ZookeepConfig config = new ZookeepConfig();
//		121.10.118.38 120.78.133.186 192.168.10.181  169.55.68.219
		config.setIpPort("120.78.133.186:2181");
		config.setUser("tim");
		config.setPwd("zgame2016");
		ZookeepServer zk = new ZookeepServer(config,(event)->System.err.println(event.getPath()));
		File file = new File("E:/Z项目文档/游戏配置文档");
//		Thread.sleep(10000);
		for(File f : file.listFiles()){
			System.err.println(f.getName());
			if(!f.getName().endsWith(".xlsx")) continue;
			String path = ZookeepServer.getConfigPath()+"/"+f.getName().substring(0, f.getName().indexOf(".xlsx"));
			if(f.getName().endsWith(".xlsx")){
				Stat stat = zk.exists(path);
				if(stat!=null){//走set
					zk.del(path);
				}
//				else{
					zk.create(path, ByteUtil.toByteArray(f));
//				}
			}
		}
//		zk.create(ZookeepServer.getLogicPath(), "zgame path");
//		zk.create(ZookeepServer.getMasterPath(), "zgame path");
//		zk.create(ZookeepServer.getConfigPath(), "zgame path");
//		zk.create(ZookeepServer.getRoutePath(), "zgame path");
		
		
		zk.set(ZookeepServer.getConfigPath(), ""+System.currentTimeMillis());
//		zk.create("/zgame/config/物品", ByteUtil.toByteArray(file));
//		
//		Thread.sleep(6666000);
//		byte[] bt = zk.getBytes("/zgame/config/物品");
//		InputStream in = new ByteArrayInputStream(bt);
//		Workbook wb = new XSSFWorkbook(in);
//		System.err.println(wb.getSheetName(0));
//		zk.create(node, msg);
		
		
		
	}
}

package com.ftkj;

import java.io.File;

import org.apache.zookeeper.data.Stat;

import com.ftkj.tool.zookeep.ZookeepConfig;
import com.ftkj.tool.zookeep.ZookeepServer;
import com.ftkj.util.ByteUtil;

public class ConfigServer {
	public static void main(String[] args) throws Throwable{
		ZookeepConfig config = new ZookeepConfig();
//		config.setIpPort("192.168.10.70:2181");
//		config.setIpPort("120.78.133.186:2181");
//		config.setIpPort("172.18.145.95:2181"); //精英测试服
		config.setIpPort("169.55.68.219:2181"); //ios审核
		config.setUser("tim");
		config.setPwd("xgame2016");
		ZookeepServer zk = new ZookeepServer(config,(event)->System.err.println(event.getPath()));
//		File file = new File("E:/FTXNBA/Server/NBAGameServer/excel");
		File file = new File("E:/FTXNBA/FTXNBADataSystem/游戏配置文档");
		for(File f : file.listFiles()){
			if(f.getName().indexOf("xlsx") == -1) {
				continue;
			}
			System.err.println(f.getName());
			String path = ZookeepServer.getConfigPath()+"/"+f.getName().substring(0, f.getName().indexOf(".xlsx"));
			if(f.getName().endsWith(".xlsx")){
				zk.del(path);
				zk.create(path, ByteUtil.toByteArray(f));
//				Stat stat = zk.exists(path);
//				if(stat!=null){//走set
//					zk.set(path, ByteUtil.toByteArray(f));
//				}else{
//					zk.create(path, ByteUtil.toByteArray(f));
//				}
			}
		}
		
		zk.set(ZookeepServer.getConfigPath(), ""+System.currentTimeMillis());
//		zk.create("/zgame/config/物品", ByteUtil.toByteArray(file));
//		
//		Thread.sleep(6000);
//		byte[] bt = zk.getBytes("/zgame/config/物品");
//		InputStream in = new ByteArrayInputStream(bt);
//		Workbook wb = new XSSFWorkbook(in);
//		System.err.println(wb.getSheetName(0));
//		zk.create(node, msg);
		
		
		
	}
}

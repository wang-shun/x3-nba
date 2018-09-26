package com.ftkj;

import java.io.File;

import com.ftkj.tool.zookeep.ZookeepConfig;
import com.ftkj.tool.zookeep.ZookeepServer;
import com.ftkj.util.ByteUtil;

public class ConfigUpdate {
	public static void main(String[] args) throws Throwable{
		ZookeepConfig config = new ZookeepConfig();
		config.setIpPort("192.168.10.70:2181");
		config.setUser("tim");
		config.setPwd("xgame2016");
		ZookeepServer zk = new ZookeepServer(config,(event)->System.err.println(event.getPath()));
		File file = new File(ConfigUpdate.class.getResource("").getPath());
		System.err.println(file);
		for(File f : file.listFiles()){
			if(f.getName().indexOf("xlsx") == -1) {
				continue;
			}
			System.err.println(f.getName());
			String path = ZookeepServer.getConfigPath()+"/"+f.getName().substring(0, f.getName().indexOf(".xlsx"));
			if(f.getName().endsWith(".xlsx")){
				zk.del(path);
				zk.create(path, ByteUtil.toByteArray(f));
			}
		}
		zk.set(ZookeepServer.getConfigPath(), ""+System.currentTimeMillis());
	}
}

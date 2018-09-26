

import java.io.File;


public class ConfigUpdate {
	public static void main(String[] args) throws Throwable{
		ZookeepConfig config = new ZookeepConfig();
		String ip = "192.168.10.70";
		if(args.length>0) {
			ip = args[0];
		}
		config.setIpPort(ip+":2181");
		config.setUser("tim");
		config.setPwd("xgame2016");
		ZookeepServer zk = new ZookeepServer(config,(event)->System.err.println(event.getPath()));
		String excelDir = ConfigUpdate.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		File file = new File(excelDir);
		System.err.println("excel目录：" + file.getAbsolutePath());
		for(File f : file.listFiles()){
			if(f.getName().indexOf("xlsx") == -1) {
				continue;
			}
			System.err.println(f.getName());
			String path = "/zgame/config/"+f.getName().substring(0, f.getName().indexOf(".xlsx"));
			if(f.getName().endsWith(".xlsx")){
				zk.del(path);
				zk.create(path, ByteUtil.toByteArray(f));
			}
		}
		zk.set("/zgame/config", ""+System.currentTimeMillis());
	}
}

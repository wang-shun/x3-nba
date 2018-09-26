package com.ftkj;

import com.ftkj.server.GameSource;
import com.ftkj.server.socket.GameSocketServer;
import com.ftkj.server.socket.SocketServerConfig;
import com.ftkj.util.IPUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

public class GMServerStartup {
	
	public static void main(String[] args) throws Throwable{
//		System.out.println(System.getProperty("java.class.path"));
		final int port;
		final String ip;
		final String jsPath;
		int net = 1;
		//关闭打印
		GameSource.statJob = false;
		String classPath = GMServerStartup.class.getResource("/").getPath();
		System.out.println("***"+classPath);
//		PropertyConfigurator.configure(classPath+"log4j.properties"); 
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
	    File conFile = new File(classPath+"log4j2.xml");
	    logContext.setConfigLocation(conFile.toURI());
	    logContext.reconfigure();
		SocketServerConfig config = null;
		
		if(args.length>=3){
			ip = args[0];
			port = Integer.parseInt(args[1]);
			jsPath = args[2];
			if(args.length>=4){
				net = Integer.parseInt(args[3]);
			}
			config = new SocketServerConfig(){
				
				@Override
				public String getServicePackgePath() {
					return "";
				}
				
				
				@Override
				public String getActiveManagerPackgePath() {
					return "";
				}


				@Override
				public int getPort() {
					return port;
				}
				
				@Override
				public String getPath() {
					return GMServerStartup.class.getResource("/").getPath();
				}
				
				@Override
				public String getIP() {
					return ip;
				}

				@Override
				public String getManagerPackgePath() {
					return "com.ftkj.manager.gm";
				}

				@Override
				public String getCommonPackgePath() {
					return "com.ftkj";
				}
				
				@Override
				public String getAOPackgePath() {
					return "";
				}

				@Override
				public String getDAOPackgePath() {
					return "";
				}
				

				@Override
				public String getJobPackgePath() {
					return "";
				}

				@Override
				public String getJSScriptPath() {
					return jsPath;
				}

				@Override
				public ClassLoader getClassLoader() {
					return GMServerStartup.class.getClassLoader();
				}
				
			};
			GameSource.net = net==1;
		}else{
			config = new SocketServerConfig(){
				@Override
				public String getServicePackgePath() {
					return "";
				}
				
				
				@Override
				public String getActiveManagerPackgePath() {
					return "";
				}


				@Override
				public int getPort() {
					return 2222;
				}
				
				@Override
				public String getPath() {
					return GMServerStartup.class.getResource("/").getPath();
				}
				
				@Override
				public String getIP() {
					return IPUtil.getLocalIp();
				}

				@Override
				public String getManagerPackgePath() {
					return "com.ftkj.manager.gm";
				}
				
				@Override
				public String getCommonPackgePath() {
					return "com.ftkj";
				}

				@Override
				public String getAOPackgePath() {
					return "";
				}

				@Override
				public String getDAOPackgePath() {
					return "";
				}

				@Override
				public String getJobPackgePath() {
					return "";
				}

				@Override
				public String getJSScriptPath() {
					return System.getProperty("user.dir")+File.separatorChar+"config"+File.separatorChar+"config.js";
				}

				@Override
				public ClassLoader getClassLoader() {
					return GMServerStartup.class.getClassLoader();
				}
			};
		}
		GameSocketServer g = new GameSocketServer(config);
		g.startNode();
	}
}

package com.ftkj.core.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.ftkj.core.util.StringUtil;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class DBConsole {
//	DataSource db;
	private BoneCP db;
	public DBConsole(DBData j){
		try{
			//System.out.println("===jdbc:"+j.toString());
			Class.forName("com.mysql.jdbc.Driver");
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(StringUtil.formatString("jdbc:mysql://{}:{}/{}?useUnicode=true&characterEncoding=utf8", 
					j.getIp(),j.getPort()+"",j.getDbName())); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
			config.setUsername(j.getUserName()); 
			config.setPassword(j.getPassWord());
			//每个分区最小连接数量
			config.setMinConnectionsPerPartition(10);
			//每个分区最大连接数量
			config.setMaxConnectionsPerPartition(30);
			//连接耗尽时,一次性创建的连接数
			config.setAcquireIncrement(10);
			//分区数量
			config.setPartitionCount(3);
			//设置测试空闲连接测试时间
			config.setIdleConnectionTestPeriodInSeconds(60*5);
			
			config.setStatementsCacheSize(400);
			
			db = new BoneCP(config); // setup the connection pool
//			HashMap<String,Object> map = new HashMap<String,Object>();
//			p.setProperty("user", j.getUserName());
//			p.setProperty("password", j.getPassWord());			
//			p.setProperty("driverClass", "com.mysql.jdbc.Driver");
//			Class.forName("org.hsqldb.jdbcDriver");
//			map.put("maxPoolSize", 10);
//			map.put("minPoolSize", 5);
//			map.put("checkoutTimeout", 1000*10);
//			map.put("idleConnectionTestPeriod", 60*10);
//			map.put("maxIdleTime", 60*3);
//			map.put("maxStatements", 1000);
			
//			db = DataSources.pooledDataSource(DataSources.unpooledDataSource(
//					StringUtil.formatString("jdbc:mysql://{}:{}/{}?useUnicode=true&characterEncoding=utf8", 
//							j.getIp(),j.getPort()+"",j.getDbName()), p), map);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Connection getConnection()throws Exception{
		try {
			Connection connection = db.getConnection();
			return connection;
		} catch (SQLException e) {
			throw new Exception(e.toString());
		}
	}
	
	
}

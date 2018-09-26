package com.ftkj.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.ftkj.core.db.DBData;


/**
 * @author tim.huang
 * 2016年1月8日
 * File控制
 */
public class FileConsole {
	
	public static File[] getFile()throws Exception{
		File fileRoot = new File(getFilePath());
		File[] fileChilds  = fileRoot.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile() && file.getName().indexOf("xlsx")>0;
			}
		});
		return fileChilds;
	}
	
	private static String getFilePath()throws Exception{
		Properties properties = new Properties();
		File file = new File("dbconfig.properties");
		FileInputStream fis = new FileInputStream(file);
		properties.load(new InputStreamReader(fis, "UTF-8"));
		fis.close();
		return properties.getProperty("path");
	}
	
	public static DBData getDBSource()throws Exception{
		Properties properties = new Properties();
		File file = new File("dbconfig.properties");
		FileInputStream fis = new FileInputStream(file);
		properties.load(new InputStreamReader(fis, "UTF-8"));
		fis.close();
		return new DBData(properties.getProperty("userName"), properties.getProperty("passWord")
				,properties.getProperty("ip"),properties.getProperty("port"),properties.getProperty("dbName"));
	}
	
}

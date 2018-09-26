package com.ftkj.core;

import java.io.File;
import java.util.Scanner;

import com.ftkj.core.project.XGameExcel;
import com.ftkj.core.project.ZGameExcel;

/**
 * @author tim.huang
 * 2016年1月11日
 * 插件启动
 */
public class PlugStartup {
	
	public static void main(String[] args){
		try {
			ExcelConsole ec  = new ExcelConsole(new ZGameExcel());
			File[] files = FileConsole.getFile();
			for(File file : files){
//				System.out.print("--save excel file :["+file.getName()+"]");
				System.out.println("--save excel file :["+file.getName()+"] result num:["+ec.run(file)+"]");
			}
			Scanner in=new Scanner(System.in);
			String a = in.nextLine();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}

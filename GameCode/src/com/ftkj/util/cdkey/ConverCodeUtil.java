package com.ftkj.util.cdkey;

import com.ftkj.util.MD5Util;
import com.ftkj.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tim.huang
 * 2016年12月20日
 * 激活码生成工具
 */
public class ConverCodeUtil {
	private static final Logger log = LoggerFactory.getLogger(ConverCodeUtil.class);
	private static String ZM = "QWERTYUIOP1A2S3D4F5G6H7J8K9L0ZXCVBNM";
	
	public static List<String> instantCode(String id,String plat,int num){
		int length = ZM.length();
		List<String> codes = Stream.generate(()->""+ZM.charAt(RandomUtil.randInt(length))
				+ZM.charAt(RandomUtil.randInt(length))+ZM.charAt(RandomUtil.randInt(length)))
		.distinct().limit(num).map(code->instantCode(id,plat,code)).collect(Collectors.toList());
//		codes.stream().forEach(code->System.err.println(code));
		return codes;
	}
	
	public static String instantCode(String id,String plat,String code){
		return id+plat+MD5Util.encodeMd516BitConverCode(id, plat, code).substring(0,7)+code;
	}
	
	private static void ss(List<String> st,String name){
		FileWriter fw = null;
		try {
			// 如果文件存在，则追加内容；如果文件不存在，则创建文件
			File f = new File("E:\\"+name+".txt");
			fw = new FileWriter(f, true);
		} catch (IOException e) {
            log.error(e.getMessage(), e);
		}
		PrintWriter pw = new PrintWriter(fw);
		st.forEach(s -> pw.println(s));
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException e) {
            log.error(e.getMessage(), e);
		}
		System.err.println("写入成功");
	}
	
	public static void main(String[] args) {
		String id = "001";
		List<String> ls = ConverCodeUtil.instantCode(id, "01", 20);
		ss(ls,id);
//		A01UC94F775D2F7769945UP8
//		System.err.println(ConverCodeUtil.instantCode("A01", "UC", "UP8").equals("A01UC94F775D2F7769945UP8"));
	}
}

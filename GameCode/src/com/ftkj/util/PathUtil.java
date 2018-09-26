package com.ftkj.util;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路径工具类
 * @author tim.huang
 * 2015年11月27日
 */
public class PathUtil {
	private static final Logger log = LoggerFactory.getLogger(PathUtil.class);
	public static final String FILESEPARATOR = System.getProperty("file.separator");
	public static final String POINT = ".";
	/**
	 * 递归获得当前路径下所有.java文件
	 * @param path
	 * @return
	 */
	public static final List<String> getAllName(String path) {
		try {
			long time = System.currentTimeMillis();
			ClassGetter scan = new ClassGetter(path);
			List<String>  list = scan.getFullyQualifiedClassNameList();
//			for(String val : list)
//				System.out.println(val);
			list = list.stream().distinct().collect(Collectors.toList());
			if(log.isDebugEnabled()) {
				log.debug("{}个类,耗时:{}", list.size(), System.currentTimeMillis() - time);
			}
			return list;
		} catch (IOException e) {
			return Lists.newArrayList();
		}
        
		
//		File root = new File(path);
//		List<String> result = new ArrayList<String>();
//		File[] fList = root.listFiles();
//		if (fList == null || fList.length <= 0){
//			return result;
//		}
////		log.info("file array[{}]",fList);
//		for (File child : fList) {
//			if (child.isDirectory()) {
//				result.addAll(getAllName(child.getPath()));
//			} else {
//				if(child.getPath().indexOf("$")<0&&(child.getPath().endsWith(".class") 
//						|| child.getPath().endsWith(".java"))){
//					
//					String url = child.getPath().replace(".class", "").replace(".java", "");
//					result.add(url.replace("\\", "/"));
//				}
//			}
//		}
//		return result;
		
	}
	
	public static void main(String[] args) {
		List<String> list = PathUtil.getAllName(PathUtil.class.getResource("/").getPath());
		for(String s : list){
			System.err.println(s);
		}
		System.out.println(PathUtil.FILESEPARATOR);
	}
}

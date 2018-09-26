package com.ftkj.core.util;

/**
 * 字符串工具
 * 
 * @author tim.huang 2015年12月22日
 */
public class StringUtil {
	
	/**
	 *  ;  
	 */
	public static String DEFAULT_SP = "[;]";
	/**
	 *  ,
	 */
	public static String DEFAULT_ST = "[,]";
	
	
	public static int[] toIntArray(String str,String sp) {
		if("".equals(str) || str == null){
			return new int[0];
		}
		//填充数值参数
		String [] v = str.split(sp);
		int [] vInt = new int[v.length];
		for(int tv = 0 ; tv < v.length; tv++){
			vInt[tv] = Integer.parseInt(v[tv]);
		}
		return vInt;
	}
	
	public static String [] toStringArray(String str,String sp){
		if("".equals(str) || str == null){
			return new String[0];
		}
		//填充数值参数
		String [] v = str.split(sp);
		return v;
	}
	
//	public static String formatString(String str,String... vals){
//		for(String val : vals){
//			str = str.replaceFirst("[8]", val);
//		}
//		return str;
//	}  

	public static String formatString(String str,String... vals){
		StringBuffer sb = new StringBuffer();
		int start = 0,end = 0;
		for(String val : vals){
			end = str.indexOf("{}");
			if(start>str.length() || end<0){
				break;
			}
			sb.append(str.substring(start,end));
			sb.append(val);
			str = str.substring(end+2);
		}
		sb.append(str);
		return sb.toString();
	}

}

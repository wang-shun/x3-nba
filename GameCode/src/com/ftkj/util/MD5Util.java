package com.ftkj.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	
	private final static String KEY = "WILLusSFC8tAsC3459v4mPgOvdfIktuBcwnDcjl8";
	private final static String ConverCodeKey = "B40021FasAC09kasFhga9B519869h71234s342k";
	//
	public static String encodeMD5(String input){
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance( "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md5.update(input.getBytes());
		StringBuilder sb=new StringBuilder();
		for(byte b:md5.digest()){
			sb.append(String.format("%02X",b));
		}
		return sb.toString();
	}
	
	public static String encodeMD516Bit(String input){
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance( "MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md5.update(input.getBytes());
		StringBuilder sb=new StringBuilder();
		for(byte b:md5.digest()){
			sb.append(String.format("%02X",b));
		}
		return sb.toString().substring(8, 24);
	}
	
	public static String encodeMd516BitConverCode(String id,String plat,String code){
		String input = id + ConverCodeKey + plat + ConverCodeKey + code;
		return encodeMD516Bit(input);
	}
	
	
	
	
	/**
	 * 2个字符串 跟密钥合并成字符串进行加密
	 * @param key1
	 * @param key2
	 * @return
	 */
	public static String encodeMD5(Object key1,Object key2){
		String key = key1.toString() +KEY + key2.toString() +KEY;
		return encodeMD5(key);
	}
	
	//92f9e7b6a0ce76e04dac829efef03639
	public static void main(String[] args) {
		String md5= "1agwefgd465af4sd6vfa68e65qwr4vq41324et8";
		String md6= "102102041529244UlGus9tAsCv4mxJPgOvIktuBwnDcjl8txGNuoYibCOCU10";
		
		System.out.println(MD5Util.encodeMd516BitConverCode("asdf","123","asdghh"));
	}
}

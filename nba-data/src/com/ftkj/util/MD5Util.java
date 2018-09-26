package com.ftkj.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	//
	public static String encodeMD5(String input){
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance( "MD5" );
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
	
	
	public static void main(String[] args) {	
		String md5= "102102041529244UlGus9tAsCv4mxJPgOvIktuBwnDcjl8txGNuoYibCOCU10";
		String md6= "102102041529244UlGus9tAsCv4mxJPgOvIktuBwnDcjl8txGNuoYibCOCU10";
		System.out.println(MD5Util.encodeMD5(md5).toLowerCase());
	}
}

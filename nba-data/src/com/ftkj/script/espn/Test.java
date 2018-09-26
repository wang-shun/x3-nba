package com.ftkj.script.espn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.ftkj.script.JSONUtil;

public class Test {

	public static String readFile(String path) throws IOException{
		File file=new File(path);
		if(!file.exists()||file.isDirectory())throw new FileNotFoundException();
		BufferedReader br=new BufferedReader(new FileReader(file));
		String temp=null;
		StringBuffer sb=new StringBuffer();
		temp=br.readLine();
		while(temp!=null){
			sb.append(temp+" ");
			temp=br.readLine();
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception{
		String json = readFile("d:/1.txt");



		//System.out.println(JSONUtil.toJson(new A(new leagues("A","B","C"),new events("D","E"))));

		A a = JSONUtil.fromJson(json, A.class);
		List<Events> evs = a.getEvents();
		for(Events t:evs){
			//System.out.println("//"+t.competitions.get(0).competitors.size());
			System.out.println("//"+t.competitions.get(0).status.type.detail);
			System.out.println("//"+t.competitions.get(0).status.type.detail);
		}
	}
}

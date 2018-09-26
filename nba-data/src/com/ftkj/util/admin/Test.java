package com.ftkj.util.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {

	static String file_fix = ".java";
	
	private List<String> readfile(List<String>list,String filepath){
		try {
			File file = new File(filepath);
			String path = "";
			if (!file.isDirectory()) {
				path = file.getPath();
				if(path.substring(path.length()-5, path.length()).equals(file_fix)){
					//System.out.println("--path=" + file.getPath() +"//"+path.substring(path.length()-5, path.length()));
					list.add(file.getPath());
				}
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						path = readfile.getPath();
						if(path.substring(path.length()-5, path.length()).equals(file_fix)){
							//System.out.println("--path=" + readfile.getPath());
							if(path.indexOf("\\proto\\mt\\")!=-1)continue;
							if(path.indexOf("\\manager\\pk\\")!=-1)continue;
							if(path.indexOf("\\manager\\sysAtv\\")!=-1)continue;
							if(path.indexOf("\\manager\\bean\\")!=-1)continue;
							if(path.indexOf("\\action\\")!=-1)continue;							
							if(path.indexOf("GMService.java")!=-1)continue;
							if(path.indexOf("ProtoType.java")!=-1)continue;
							if(path.indexOf("BaseService.java")!=-1)continue;
							if(path.indexOf("StadiumManager.java")!=-1)continue;
							if(path.indexOf("PKAnalogyManager.java")!=-1)continue;
							if(path.indexOf("PKAuctionManager.java")!=-1)continue;
							if(path.indexOf("PKBackPeakManager.java")!=-1)continue;
							if(path.indexOf("PKQualifyManager.java")!=-1)continue;
							if(path.indexOf("PKMazeManager.java")!=-1)continue;
							if(path.indexOf("CrossManager.java")!=-1)continue;
							if(path.indexOf("DraftManager.java")!=-1)continue;
							if(path.indexOf("HonorManager.java")!=-1)continue;
							if(path.indexOf("MissionManager.java")!=-1)continue;
							
							list.add(readfile.getPath());
						}
					} else if (readfile.isDirectory()) {
						readfile(list,filepath + "\\" + filelist[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	private int readFileLinesNum(String fileName) {
		File file = new File(fileName);
		int line = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;			
			while ((tempString = reader.readLine()) != null) {
				if(!tempString.replace(" ", "").replace("	", "").equals("") && tempString.indexOf("import")==-1){
					//System.out.println("line " + line + ": " + tempString);
					System.out.println(tempString.replace("	", ""));
					line++;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return line;
	}

	public static void main(String[] args){
		Test T = new Test();
		/*
		List<String> paths = new ArrayList<String>();
		paths = T.readfile(paths,"E:\\workspace\\GameServiceServer\\src\\");
		//paths = T.readfile(paths,"E:\\workspace\\DaoServer\\src\\");
		int count = 0;
		for(String a:paths){
			count+=T.readFileLinesNum(a);
		}
		System.out.println("///"+count);
		*/
		
		T.readFileLinesNum("E:\\workspace\\GameServiceServer\\src\\com\\ftkj\\manager\\pk\\SQLUtil.java");
	}
}

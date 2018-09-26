package com.ftkj.util.excel;

import java.io.File;

public class ExcelTest {

	public static void main(String[] args) throws Exception {
		ExcelRead read = new ExcelRead();
		String path = "E:/FTXNBA/Server/NBAGameServer/excel/装备.xlsx";
        File file = new File(path);
		read.readFile(file);
	}
}

package com.ftkj.core.project;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ftkj.core.PlugResult;
import com.ftkj.core.util.StringUtil;
import com.google.common.collect.Lists;

public class ZGameExcel extends BaseExcel{

	@SuppressWarnings("deprecation")
	@Override
	public PlugResult ExcelToDB(File file) throws Exception {
		@SuppressWarnings("resource")
		Workbook wb = new XSSFWorkbook(file);
		
		Sheet sheet = wb.getSheetAt(0);
		Row row = null;
		int maxRow = sheet.getLastRowNum();
		int maxCel = 0;
		//表字段名称定义
		String columName = "";
		//表名
		String tableName = "";
		List<String> columType = Lists.newArrayList();
		Cell cel = null;
		//记录有数据的列下表
		List<Integer> columList = new ArrayList<Integer>();
		//临时字符串存放
		String msg = "",msg2 = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <= maxRow; i++) {
			row = sheet.getRow(i);
			if(row == null) continue;
			if (i == 0) {//第一行，取到配置的表名
				cel = row.getCell(0);
				tableName = getMsg(cel);
				continue;
			} else if (i == 1) {//第二行,取到对应的列名,屏蔽废弃表字段
				maxCel = row.getLastCellNum();
				for (int c = 0; c < maxCel; c++) {
					cel = row.getCell(c);
					if(cel == null || cel.getCellType() == XSSFCell.CELL_TYPE_BLANK){
						continue;
					}
					msg = getMsg(cel);
					if(msg.startsWith("#")){
						continue;
					}
					if(msg.startsWith("*")){
						msg = msg.substring(1);
					}
					columName =columName + "`"+msg+"`" + ",";
					columList.add(c);
				}
			}else if(i == 2){//废弃第3
				continue;
			}else if(i == 3) {// 4字段类型 
				maxCel = row.getLastCellNum();
				for (int index : columList) {
					cel = row.getCell(index);
					columType.add(cel.getStringCellValue());
				}
			} else{//从第4行开始读取数据
				maxCel = row.getLastCellNum();
				for (int index : columList) {
					cel = row.getCell(index);
//					if(cel == null || cel.getCellType() == XSSFCell.CELL_TYPE_BLANK){
//						break;
//					}
					msg = getMsg(cel, columType.get(index));
					if(msg.startsWith("#")){ 
						msg2 =  "";
						break;
					}
					msg2 = msg2 + msg + "\t";
				}
//				System.err.println(msg2);
				if(!"".equals(msg2)){
					sb.append(msg2);
					sb.append("\n");
				}
				msg2 = "";
			}
		}
		// 打印表数据
//		System.err.println();
//		System.err.println(sb.toString());
		//
		byte[] bytes = sb.toString().getBytes();
		InputStream is = new ByteArrayInputStream(bytes);
		columName = columName.substring(0, columName.length()-1);
		String sql = StringUtil.formatString(
				"LOAD DATA LOCAL INFILE '{}.csv' REPLACE INTO TABLE {} ({})",
				file.getName(), tableName, columName);
		return new PlugResult(sql, tableName, is);
	}

	
	/**
	 * 根据类型来取
	 * @param cel
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected  String getMsg(Cell cel, String type) throws Exception{
		String strVal = cel.toString();
		boolean isNull = "".equals(strVal.trim());
		if(int.class.getSimpleName().equals(type)) {
			return isNull ? "0" : Float.valueOf(String.valueOf(cel.getNumericCellValue())).intValue()+"";
		}
		else if(float.class.getSimpleName().equals(type)) {
			return isNull ? "0" : Float.valueOf(String.valueOf(cel.getNumericCellValue())).toString();
		}
		return new String(strVal.getBytes(),"UTF-8");
		
	}
	
}

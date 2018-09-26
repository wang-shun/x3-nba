package com.ftkj.core.project;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ftkj.core.PlugResult;
import com.ftkj.core.util.StringUtil;
import com.google.common.collect.Lists;

public class XGameExcel  extends BaseExcel{
	
	public List<Map<String, String>> dataList;
	
	public XGameExcel() {
		dataList = Lists.newArrayList();
	}

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
		Cell cel = null;
		//记录有数据的列下表
		List<Integer> columList = new ArrayList<Integer>();
		//临时字符串存放
		String msg = "",msg2 = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <= maxRow; i++) {
			row = sheet.getRow(i);
			if(row == null) continue;
			if (i == 0) {//第二行,取到对应的列名,屏蔽废弃表字段
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
					columName =columName + msg +",";
					columList.add(c);
				}
			}else if(i < 4){//废弃第3行数据 
				continue;
			}else{//从第4行开始读取数据
				maxCel = row.getLastCellNum();
				for (int index : columList) {
					cel = row.getCell(index);
					if(cel == null || cel.getCellType() == XSSFCell.CELL_TYPE_BLANK){
						break;
					}
					msg = getMsg(cel);
					if(msg.startsWith("#")){ 
						msg2 =  "";
						break;
					}
					msg2 = msg2 + msg + "\t";
					// 扩展处理
					if(index == 0) {
						dataList.add(getMapData(i, columName.split(",")[index], msg));
					}else {
						dataList.set(i-4, getMapData(i, columName.split(",")[index], msg));
					}
				}
//				System.err.println(msg2);
				if(!"".equals(msg2)){
					sb.append(msg2);
					sb.append("\n");
				}
				msg2 = "";
			}
		}
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

	
	public Map<String, String> getMapData(int row, String colName, String value) {
		int idx = row -4;
		Map<String, String> map = null;
		
		if(dataList.size() > idx) {
			map = dataList.get(idx);
		}else {
			map = new HashMap<String,String>();
		}
		map.put(colName, value);
		return map;
	}

	public List<Map<String, String>> getDataList() {
		return dataList;
	}
	
	
}

package com.ftkj.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ftkj.enums.ErrorCode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 获取ErrorCode.java里定义的错误码，重新生成到Error.xlsx文件，
 * 原旧存在的错误码依旧使用老的，新增的错误码则添加进去，默认错误码排序从
 * 小到大。
 * @author mr.lei
 *
 */
public class ErrorExcelConsole {
	
	/**
	 * 执行前先配置好args参数的值：错误码文件所在路径。
	 * @param args
	 */
	public static void main(String[] args) {
		//表单sheet名称
		String sheetName = "Code";
		//错误码文件路径，例如：E:/work_place/Error.xlsx
		String errorFilePath = null;
		
		if (args.length == 0) {
			System.err.println("请在执行前输入参数args的值！");
			return;
		}
		
		if (args[0] != null) {
			errorFilePath = args[0];
		}
		
		File  file = new File(errorFilePath);
		if (file == null || !file.isFile()) {
			System.err.println("指定路径的文件不存在:" + errorFilePath);
			return;
		}
		
		//*******读取原来Excel中Code表单中的数据，从第5行开始读取，一行读取4个单元格数据******
		XSSFWorkbook wb = null;
		FileOutputStream out = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
			reWriteSheetData(wb.getSheet(sheetName), 4);
			//保存
			out = new FileOutputStream(errorFilePath);  
			wb.write(out);
			System.out.println("ErrorCode更新到Error.xlsx文件成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (out != null) {
					out.close();
				}
				
				if (wb != null) {
					wb.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * 重新写入错误码数据，原旧数据存在则写入原旧数据，否则写入最新数据。
	 * 最新数据存ErrorCode.java中定义的枚举值获取。第二列单元格设置为空。
	 * @param sheet				指定写入的表单
	 * @param rowStartIndex		起始写入的行
	 */
	private static void reWriteSheetData(XSSFSheet sheet, int rowStartIndex){
		Map<Integer, ErrorCode> codeMap = ErrorCode.getCodeMap();
		if (codeMap == null || codeMap.size() == 0) {
			System.err.println("错误码为空，请检查ErrorCode枚举是否有添加枚举值！");
			return;
		}
		
		List<ErrorCode> errorCodes = new ArrayList<ErrorCode>(codeMap.values());
		//根据code值得大小从小到大排序
		Collections.sort(errorCodes, (o1, o2) -> o1.getCode() - o2.getCode());
		
		Map<Integer, ErrorCode> errorCodeMap = new LinkedHashMap<Integer, ErrorCode>();
		//按排序顺序保存到有序Map中
		errorCodes.forEach(ecode -> errorCodeMap.put(ecode.getCode(), ecode));
		
		// 读取指定的表单数据数据从指定的第到指定集合，key对应的协议号，value=单元格的值.硬编码从第5行开始读取
		Map<Integer, List<Object>> sourceSheetDataMap = readSheet(sheet, rowStartIndex);
		// 清空表单被读取的数据
		clearSheetRowData(sheet, rowStartIndex);
		int i = rowStartIndex;
		
		//重新写入
		for (ErrorCode errorCode : errorCodes) {
			List<Object> list = sourceSheetDataMap.get(errorCode.getCode());
			XSSFRow newRow = sheet.createRow(i);
			if (list != null) {
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j) == null) {
						newRow.createCell(j, CellType.STRING);
					}else if (list.get(j) != null 
						&& Integer.class.getSimpleName().equals(list.get(j).getClass().getSimpleName())) {
						newRow.createCell(j, CellType.NUMERIC).setCellValue((int)list.get(j));
					}else {
						newRow.createCell(j, CellType.STRING).setCellValue((String)list.get(j));
					}
				}
			}else {
				//编号也就是错误码
				newRow.createCell(0, CellType.NUMERIC).setCellValue((int)errorCode.getCode());
				//名称,默认为空
				newRow.createCell(1, CellType.STRING);
				//说明
				newRow.createCell(2, CellType.STRING).setCellValue(errorCode.getTip());
				//弹窗类型，默认为1
				newRow.createCell(3, CellType.NUMERIC).setCellValue(1);
			}
			
			i++;
		}
	}
	
	/**
	 * 清空起始行开始以后所有行的数据。
	 * @param sheet			指定清除的表单
	 * @param rowStartIndex	清除的起始行
	 */
	private static void clearSheetRowData(XSSFSheet sheet, int rowStartIndex){
		int maxRow = sheet.getLastRowNum();
		for (int i = rowStartIndex; i <= maxRow; i++) {
			if (sheet.getRow(i) == null) {
				continue;
			}
			sheet.removeRow(sheet.getRow(i));
		}
	}

	
	/**
	 * 读取指定表单行开始读取所有数据。
	 * @param sheet				指定的表单
	 * @param rowStartIndex		读取数据开始的起始行
	 * @return	返回读取的数据，key是指定的错误码协议号，value是单元格的数值。
	 */
	@SuppressWarnings("deprecation")
	private static Map<Integer, List<Object>> readSheet(XSSFSheet sheet, int rowStartIndex) {
		String sheetName = sheet.getSheetName();
        int maxRow = sheet.getLastRowNum();
        XSSFRow firstRow = sheet.getRow(0);
        // 过滤非配置表
        if (sheetName.startsWith("#") || maxRow == 0 || firstRow == null) {
            return null;
        }
        
        int maxCel = firstRow.getLastCellNum();
        Map<Integer, List<Object>> sheetDataMap = Maps.newHashMap();
        
        //默认从第5行开始读取
        for (int i = rowStartIndex; i <= maxRow; i++) {
        	XSSFRow row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			
			List<Object> rowCellValueList = Lists.newArrayList();
			for (int j = 0; j < maxCel; j++) {
				if (j == 1) {
					rowCellValueList.add(null);
					continue;
				}
				
				XSSFCell cell = row.getCell(j);
				
				if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					rowCellValueList.add((int)cell.getNumericCellValue());
				}else {
					rowCellValueList.add(cell.getStringCellValue());
				}
			}
			
			sheetDataMap.put((int)rowCellValueList.get(0), rowCellValueList);
		}
		
        return sheetDataMap;
	}

}

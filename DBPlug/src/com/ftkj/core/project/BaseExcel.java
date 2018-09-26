package com.ftkj.core.project;

import com.ftkj.core.IExcel;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.text.SimpleDateFormat;

public abstract class BaseExcel implements IExcel{
	@SuppressWarnings("deprecation")
	protected  String getMsg(Cell cel) throws Exception{
		switch (cel.getCellType()) {// 根据cell中的类型来输出数据
		case XSSFCell.CELL_TYPE_NUMERIC:
			if(HSSFDateUtil.isCellDateFormatted(cel)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.format(HSSFDateUtil.getJavaDate(cel.getNumericCellValue())).toString();
			}
			else if(cel.getCellStyle().getDataFormat()==1 || cel.getCellStyle().getDataFormat()==176
					|| cel.getCellStyle().getDataFormat()==0 || cel.getCellStyle().getDataFormat()==49){//整数
				return ""+(int)cel.getNumericCellValue();
			}
			return "" + cel.getNumericCellValue();
		case XSSFCell.CELL_TYPE_STRING:
			return new String(cel.getStringCellValue().getBytes(),"UTF-8");
		case XSSFCell.CELL_TYPE_BOOLEAN:
			return "" + cel.getBooleanCellValue();
		case XSSFCell.CELL_TYPE_FORMULA:
			return "" + (int)cel.getNumericCellValue();
		default:
			System.exit(0);
		}
		return "";
	}
}

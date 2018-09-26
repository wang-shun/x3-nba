package com.ftkj.util.excel;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExcelRead {

    public final static Logger log = LogManager.getLogger(ExcelRead.class);

    @SuppressWarnings("resource")
    public static List<ExcelConfigBean> readFile(File file) throws Exception {
        List<ExcelConfigBean> list = Lists.newArrayList();
        Workbook wb = new XSSFWorkbook(file);

        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            ExcelConfigBean config = readSheet(wb.getSheetAt(i), file.getName().split("[.]")[0] + "_");
            if (config != null) {
                list.add(config);
            }
        }
        return list;
    }

    @SuppressWarnings("resource")
    public static ExcelConfigBean readFile(byte[] fileBytes, String sheet, String name) throws Exception {
        //		List<ExcelConfigBean> list = Lists.newArrayList();
        if (fileBytes == null || fileBytes.length == 0) {
            return null;
        }
        log.debug("文档数据大小{}--[{}]", name, fileBytes.length);
        InputStream in = new ByteArrayInputStream(fileBytes);
        Workbook wb = new XSSFWorkbook(in);

        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            if (sheet.equals(wb.getSheetAt(i).getSheetName())) {
                return readSheet(wb.getSheetAt(i), name + "_");
            }
            //			ExcelConfigBean config = readSheet(wb.getSheetAt(i), file.getName().split("[.]")[0]+"_");
        }
        return null;
    }

    /**
     * 读配置表
     *
     * @param sheet
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ExcelConfigBean readSheet(Sheet sheet, String fileName) throws Exception {
        String sheetName = sheet.getSheetName();
        int maxRow = sheet.getLastRowNum();
        Row firstRow = sheet.getRow(0);
        // 过滤非配置表
        if (sheetName.startsWith("#") || maxRow == 0 || firstRow == null) {
            return null;
        }
        int maxCel = firstRow.getLastCellNum();
        //
        ExcelConfigBean config = new ExcelConfigBean(fileName + "" + sheetName);
        // 解析
        Row row = null;
        List<String> nameList = Lists.newArrayList();
        List<String> descList = Lists.newArrayList();
        List<String> typeList = Lists.newArrayList();
        List[] list = {nameList, descList, typeList};
        // 空白行，连续空白三行，不往后读，空行直接跳过读取
        int blankRowNum = 0;
        for (int i = 0; i <= maxRow; i++) {
            row = sheet.getRow(i);
            if (row == null) {
                log.warn("配置数据出现空行 " + config.getSheetName() + " row=" + (i + 1));
                if (++blankRowNum > 2) {
                    break;
                } else {
                    continue;
                }
                //throw new  Exception("配置数据出现空行" + config.getSheetName() + "row=" + i);
            }
            if (i < 3) {
                int c = 0;
                try {
                    for (; c < maxCel; c++) {
                        Cell cel = row.getCell(c);
                        list[i].add(getMsg(cel, typeList));
                    }
                    if (i == 2) {
                        config.initHeader(nameList, descList, typeList);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(String.format("%s 配置数据出现表头和数据不一致列数 sheet %s, row %s col %s", e.getMessage(), config.getSheetName(), i, c), e);
                }
            } else if (i > 3) {//开始读取数据
                for (int c = 0; c < maxCel; c++) {
                    // 读到空行跳过
                    if (row == null) {
                        break;
                    }
                    // 判断第一列是否是空行，跑出异常
                    Cell cel = row.getCell(c);
                    // 行数，数据
                    if (cel == null) {
                        cel = row.createCell(c);
                    }
                    // 跳过第一列为空的
                    if (c == 0 && cel.toString().trim().equals("")) {
                        break;
                        //throw new  Exception("空字段数据["+ config.getSheetName() +", "+ row.getRowNum()+1 +"]");
                    }
                    config.addData(i - 4, nameList.get(c), typeList.get(c), getMsg(cel, typeList));
                }
            }
        }
        return config;
    }

    @SuppressWarnings("deprecation")
    private static String getMsg(Cell cel, List<String> typeList) throws Exception {
        try {
            switch (cel.getCellType()) {// 根据cell中的类型来输出数据
                case XSSFCell.CELL_TYPE_NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cel)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        return sdf.format(HSSFDateUtil.getJavaDate(cel.getNumericCellValue()));
                    }
                    return getDefaultByType(cel, typeList);
                case XSSFCell.CELL_TYPE_STRING:
                    return new String(cel.toString().getBytes(), "UTF-8");
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    return "" + cel.getBooleanCellValue();
                case XSSFCell.CELL_TYPE_FORMULA:
                    return "" + (int) cel.getNumericCellValue();
                default:
                    return getDefaultByType(cel, typeList);
            }
        } catch (Exception e) {
            if (cel == null) {
                throw new RuntimeException("cell null", e);
            } else {
                throw new RuntimeException(String.format("sheet %s rowidx %s columnidx %s val %s",
                        cel.getSheet().getSheetName(), cel.getRowIndex(), cel.getColumnIndex(), cel.getStringCellValue()), e);
            }
        }
    }

    /**
     * excel 数据类型解析扩展
     *
     * @param cel
     * @param typeListcel
     * @return
     */
    @SuppressWarnings("deprecation")
    private static String getDefaultByType(Cell cel, List<String> typeListcel) {
        String type = typeListcel.get(cel.getColumnIndex());
        String value = "".equals(cel.toString()) ? "0" : cel.toString();
        if ("int".equals(type.toLowerCase())) {
            return Float.valueOf(value).intValue() + "";
        }
        if ("float".equals(type.toLowerCase())) {
            return Float.valueOf(value).floatValue() + "";
        }
        cel.setCellType(XSSFCell.CELL_TYPE_STRING);
        return cel.toString();
    }

}

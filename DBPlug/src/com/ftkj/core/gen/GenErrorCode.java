package com.ftkj.core.gen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.ftkj.core.project.XGameExcel;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author Jay
 * @time:2017年3月20日 上午11:59:30
 */
public class GenErrorCode {

	public static void main(String[] args) {
		gen();
		System.err.println("生成完成。。。");
	}
	
	public static void gen() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		try {
			// 取模板
			cfg.setDirectoryForTemplateLoading(new File("E:/FTXNBA/Server/DBPlug/src/template"));
			cfg.setDefaultEncoding("UTF-8");
	        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	        Template temp = cfg.getTemplate("ErrorCode.ftl");
	        
	        // 生成文件，固定的路径
	        String path = "E:/FTXNBA/Server/Common/src/com/ftkj/enums/ErrorCode.java";
	        File file = new File(path);
	        
	        Writer out = new OutputStreamWriter(new FileOutputStream(file));
	        temp.process(genData(), out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}   
	}
	
	public static Map<String, Object> genData() {
		//
        List<String> list = new ArrayList<String>();
        list.add("\n");
        XGameExcel excel = new XGameExcel();
        try {
        	File file = new File("excel/Error.xlsx");
        	System.err.println(file.getAbsolutePath().toString());
			excel.ExcelToDB(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
        for(Map<String,String> map : excel.dataList) {
        	String val = "/** " + map.get("desc") + " */ \n\t" + map.get("name")+ "(" + map.get("id") + "),";
        	list.add(val);
        }
        //
        Map<String, Object> root = Maps.newHashMap();
        root.put("items", list);
        return root;
	}
	

}

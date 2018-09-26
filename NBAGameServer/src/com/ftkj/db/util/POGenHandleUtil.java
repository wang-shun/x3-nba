//package com.ftkj.db.util;
//import java.io.File;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
//import java.lang.reflect.Field;
//import java.sql.Date;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.joda.time.DateTime;
//
//import com.ftkj.db.domain.PlayerExchangePO;
//import com.ftkj.db.domain.active.base.SystemActivePO;
//
//import freemarker.template.Configuration;
//import freemarker.template.Template;
//import freemarker.template.TemplateException;
//import freemarker.template.TemplateExceptionHandler;
//
///**
// * 生成PO的Handle工具
// * @author lin.lin
// *
// */
//public class POGenHandleUtil {
//
//	
//	public static void main(String[] args) throws IOException, TemplateException {
//		// 先生成PO
//		//excel名称
//		// 再生成RowH
//        gen(PlayerExchangePO.class);
//	}
//	
//	public static void gen(@SuppressWarnings("rawtypes") Class clazz) {
//		Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
//		try {
//			cfg.setDirectoryForTemplateLoading(new File("E:/FTXNBA/Server/DBPlug/src/template"));
//			cfg.setDefaultEncoding("UTF-8");
//	        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//	        Template temp = cfg.getTemplate("RowH.ftl");
//	        
//	        Writer out = new OutputStreamWriter(System.err);
//	        temp.process(genData(clazz), out);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (TemplateException e) {
//			e.printStackTrace();
//		}   
//	}
//	
//	public static Map<String, Object> genData(@SuppressWarnings("rawtypes") Class clazz) {
//		//
//        List<String> attr_list = new ArrayList<String>();
//        for(Field field : clazz.getDeclaredFields()) {
//        	if(!"serialVersionUID".equals(field.getName())) {
//        		String name = field.getName();
//        		String fUpName = name.substring(0, 1).toUpperCase() + name.substring(1);
//        		String row = "po.set" + fUpName + getType(field);
////        		System.err.println(row);
//        		attr_list.add(row);
//        	}
//        }
//        //
//        Map<String, Object> root = new HashMap<String, Object>();
//        root.put("className", clazz.getSimpleName());
//        root.put("attrs", attr_list);
//        return root;
//	}
//
//	@SuppressWarnings("rawtypes")
//	public static Map<Class, String> typeMap = new HashMap<Class, String>();
//	
//	static {
//		typeMap.put(int.class, "Int");
//		typeMap.put(float.class, "Float");
//		typeMap.put(long.class, "Long");
//		typeMap.put(String.class, "String");
//		typeMap.put(Date.class, "Timestamp");
//		typeMap.put(DateTime.class, "Timestamp");
//	}
//	
//	private static String getType(Field field) {
//		if(field.getType() == DateTime.class) {
//			return "(new DateTime(row.get"+typeMap.get(field.getType())+"(\""+getDBFieldName(field.getName())+"\")));";
//		}
//		return "(row.get"+typeMap.get(field.getType())+"(\""+getDBFieldName(field.getName())+"\"));";
//	} 
//	
//	private static String getDBFieldName(String name) {
//		char[] nameChar = name.toCharArray();
//		StringBuilder sb = new StringBuilder("");
//		for(char c : nameChar) {
//			if(Character.isUpperCase(c)){
//				sb.append("_" + (c +"").toLowerCase());
//				continue;
//			}
//			sb.append(c);
//		}
//		return sb.toString();
//	}
//
//}

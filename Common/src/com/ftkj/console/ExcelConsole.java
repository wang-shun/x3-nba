package com.ftkj.console;

@Deprecated
public class ExcelConsole {

//	// excel 目录
//	private final static String excelPath;
//	// 工具
//	private static ExcelRead excelRead;
//	/**
//	 * 所有的excel表数据， key=文件名_sheetName,   value=数据封装
//	 */
//	private static Map<String, ExcelConfigBean> excelBeanMap;
//	
//	public static Logger logger = Logger.getLogger(ExcelConsole.class);
//	
//	//  后面可以考虑重新读入的方案
//	static {
//		excelPath = GameStartup.class.getResource("/").getPath() + "../excel";
//		excelRead = new ExcelRead();
//		excelBeanMap = Maps.newHashMap();
//	}
//	
//	/**
//	 * 初始化读取excel
//	 * @throws Exception
//	 */
//	public static void init() {
//		File dir = new File(excelPath); 
//		if(dir == null || !dir.isDirectory()) {
//			logger.info("excel目录不存在");
//			return;
//		}
//		List<ExcelConfigBean> list =  Lists.newArrayList();
//		logger.info("读取excel配置\n--------------");
//		logger.info("==============================================================");
//		try {
//			for(File file : dir.listFiles()) {
//				List<ExcelConfigBean> li = excelRead.readFile(file);
//				list.addAll(li);
//				// 打印
//				StringBuilder sb = new StringBuilder("读取：" + file.getName() + "     \n");
//				li.stream().forEach(c-> sb.append(c.getSheetName()+"\n"));
//				logger.info(sb.toString());
//			}
//		} catch (Exception e) {
//		}
//		logger.info("==============================================================");
//		list.stream().forEach(file-> {
//			excelBeanMap.put(file.getSheetName(), file);
//		});
//	}
//	
//	/**
//	 * 从新加载配置，所有的Console类都应该重新读取配置
//	 */
//	public static void reloadAllConsole() {
//		// 用注解方式把所有的Console注册进来，方便管理
//		
//	}
//	@Deprecated
//	public static ExcelConfigBean getExcelBean(String name) {
//		return excelBeanMap.get(name);
//	}
//	
//	public static void print(String sheetName) {
//		System.out.println(excelBeanMap.get(sheetName).toString());
//	}
//	
//	public static void printAll() {
//		excelBeanMap.values().forEach(bean-> {
//			System.out.println(bean.toString());
//		});
//	}
//	
//	public static void clear() {
//		excelBeanMap.clear();
//	}
//	
//	/*
//	 * 测试
//	 */
//	public static void main(String[] args) throws Exception {
//		init();
//		printAll();
//	}

}

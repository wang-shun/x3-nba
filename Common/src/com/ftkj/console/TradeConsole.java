package com.ftkj.console;

import com.ftkj.enums.EConfigKey;

public class TradeConsole {

	public static final int Query_Page_Size = 16;
	/** 交易许可证 */ 
	public static int Trade_Prop_ID = 1115;
	/** 交易费率 */ 
	public static float Trade_Money_Rate = 0.05f;
	/** 每天可出售上限 */ 
	public static int Trade_Max_EveryDay = 5;
	/** 检查交易过期筛选时间 */ 
	public static int Check_Past_Min = 5;
	/**
	 * 周最大购买额度
	 */
	public static int Week_Max_Buy_Money = 20000;
	
	public static void init() {
		Trade_Money_Rate = ConfigConsole.getIntVal(EConfigKey.Trade_Money_Rate) / 100.0f;
		Week_Max_Buy_Money = ConfigConsole.getIntVal(EConfigKey.Trade_Max_Money_week);
		if(Trade_Money_Rate >= 1 || Trade_Money_Rate < 0) {
			try {
				throw new Exception("交易费率配置异常:" + Trade_Money_Rate );
			} catch (Exception e) {
				Trade_Money_Rate = 0.05f;
			}
		}
		Trade_Max_EveryDay = ConfigConsole.getIntVal(EConfigKey.Trade_Max_EveryDay);
		Trade_Prop_ID = ConfigConsole.getIntVal(EConfigKey.Trade_Prop_ID);
	}
	
}

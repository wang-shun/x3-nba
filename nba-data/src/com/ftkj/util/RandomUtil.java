package com.ftkj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomUtil {
    private static final Logger logger = LoggerFactory.getLogger(RandomUtil.class);
	public static RandomUtil randomManager  = null;

	public static RandomUtil getInstance(){
		if (randomManager == null){
			//logger.error("Init RandomManager.");
			randomManager = new RandomUtil();
		}
		return randomManager;
	}
	Map <String, RandomCtlCfg> mapRandomCfgs = new HashMap<String, RandomCtlCfg>();
	
	public void addRandomValue(String strType, int iRateValue, Object obj){
		RandomCtlCfg randomCtlCfg ;
		
		randomCtlCfg = mapRandomCfgs.get(strType);
		if (randomCtlCfg == null){
			randomCtlCfg = new RandomCtlCfg();
			//logger.error("Create Random Control("+strType+")");
			mapRandomCfgs.put(strType, randomCtlCfg);
		}
		//logger.error("Create Random Item("+strType+":"+iRateValue+")");
		randomCtlCfg.addItem(iRateValue, obj);
	}
	
	public Object getRandom(String strType){
		RandomCtlCfg randomCtlCfg ;
		
		randomCtlCfg = mapRandomCfgs.get(strType);
		if (randomCtlCfg == null) return null;
		
		return randomCtlCfg.getRandomObj() ;
	}
	
	public int getTotalValue(String strType){
		RandomCtlCfg randomCtlCfg ;
		
		randomCtlCfg = mapRandomCfgs.get(strType);
		if (randomCtlCfg == null) return 0;
		
		return randomCtlCfg.getTotalValue() ;
	}
	
	class RandomCtlCfg{
		int iTotalValue =0;
		List<RandomItem> items = new ArrayList<RandomItem>();
		public int addItem(int iRateValue, Object obj){
			RandomItem item ;

			item = new RandomItem(iRateValue, obj);
			iTotalValue += iRateValue ;
			items.add(item);
			return items.size();
		}
		
		public Object getRandomObj(){
			int iRandom ;
			RandomItem item = null;
			
			if (items.size()<1) return null ;
			iRandom = (int) (Math.random()*iTotalValue);
			for(int i=0;i<items.size();i++){
				item = items.get(i);
				if (iRandom<item.iRateValue)
					break ;
				iRandom -= item.iRateValue ;
			}
			return item.obj ;
		}
		
		public int getTotalValue(){
			return iTotalValue;
		}
	}

	class RandomItem{
		public int iRateValue ;
		public Object obj ;
		RandomItem(int iRateValue, Object obj){
			this.iRateValue = iRateValue ;
			this.obj = obj ;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] argv){
		
		int max = 10;
		for(int i=1;i<=max;i+=2){
			System.out.println("--"+(i-1)+" - "+(max-i));			
		}
		for(int i=1;i<=max;i+=2){
			System.out.println("=="+(i-1)+" - "+i);			
		}
		/*
		
		RandomUtil.getInstance().addRandomValue("inn-normal", 1, new Integer(9634));
		RandomUtil.getInstance().addRandomValue("inn-normal", 1, new Integer(355));
		RandomUtil.getInstance().addRandomValue("inn-normal", 1, new Integer(11));
		RandomUtil.getInstance().addRandomValue("inn-normal", 1, new Integer(1));
		RandomUtil.getInstance().addRandomValue("inn-normal", 1, new Integer(2));
		
		Map mapStat= new HashMap();
		for(int i=0;i<10000000;i++){
			Integer intRandomValue = null ;
			intRandomValue = (Integer)(RandomUtil.getInstance().getRandom("inn-normal"));
			String strKey ;
			strKey = "key-"+intRandomValue.intValue() ;
			Integer intValue = (Integer) mapStat.get(strKey);
			if (intValue == null){
				intValue = new Integer(3777);
				intValue = 0 ;
				System.out.println("Crate Key:"+strKey);
				mapStat.put(strKey, intValue);
			}
			intValue += 1 ;
			mapStat.remove(strKey);
			mapStat.put(strKey,intValue);
//			System.out.println(strKey+":"+intValue);
		}
		for(Iterator<String> it= mapStat.keySet().iterator();it.hasNext();){
			String strKey = it.next();
			Integer intValue ;
			intValue = (Integer)mapStat.get(strKey);
			System.out.println(strKey+":"+intValue);
		}*/
		return ;
	}
}

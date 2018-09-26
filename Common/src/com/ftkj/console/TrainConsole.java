package com.ftkj.console;

import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.manager.train.LeagueTrainBean;
import com.ftkj.manager.train.TrainArenaBean;
import com.ftkj.manager.train.TrainBean;
import com.ftkj.manager.train.TrainNpcBean;
import com.ftkj.manager.train.TrainTypeBean;

/**
 * 训练馆数据初始化
 * @author qin.jiang
 */
public class TrainConsole {
	
    /** 训练馆等级配置*/
    private static Map<Integer, TrainBean> trainBeanMap;
    /** 训练类型配置*/
    private static Map<Integer, TrainTypeBean> trainTypeBeanMap; 
    /** 训练馆单独配的NPC配置*/
    private static Map<Integer, TrainNpcBean> trainNpcBeanMap; 
    /** 联盟训练馆配置*/
    private static Map<Integer, LeagueTrainBean> leagueTrainBeanMap; 
    /** 联盟球馆配置*/
    private static Map<Integer, TrainArenaBean> trainArenaBeanMap; 
    
	public static void init(){	
	    trainBeanMap = CM.trainBeanList.stream().collect(Collectors.toMap(TrainBean::getLevel, trainBean -> trainBean));
	    trainTypeBeanMap = CM.trainTypeBeanList.stream().collect(Collectors.toMap(TrainTypeBean::getType, trainTypeBean -> trainTypeBean));
	    trainNpcBeanMap = CM.trainNpcBeanList.stream().collect(Collectors.toMap(TrainNpcBean::getId, trainNpcBean -> trainNpcBean));
	    leagueTrainBeanMap = CM.leagueTrainBeanList.stream().collect(Collectors.toMap(LeagueTrainBean::getLeaTrainId, leagueTrainBean -> leagueTrainBean));
	    trainArenaBeanMap = CM.trainAreanBeanList.stream().collect(Collectors.toMap(TrainArenaBean::getId, trainArenaBean -> trainArenaBean));
	}

    public static Map<Integer, TrainBean> getTrainBeanMap() {
        return trainBeanMap;
    }

    public static void setTrainBeanMap(Map<Integer, TrainBean> trainBeanMap) {
        TrainConsole.trainBeanMap = trainBeanMap;
    }

    public static Map<Integer, TrainTypeBean> getTrainTypeBeanMap() {
        return trainTypeBeanMap;
    }

    public static void setTrainTypeBeanMap(Map<Integer, TrainTypeBean> trainTypeBeanMap) {
        TrainConsole.trainTypeBeanMap = trainTypeBeanMap;
    }
    
    public static Map<Integer, TrainNpcBean> getTrainNpcBeanMap() {
		return trainNpcBeanMap;
	}

	public static Map<Integer, LeagueTrainBean> getLeagueTrainBeanMap() {
        return leagueTrainBeanMap;
    }

    public static void setLeagueTrainBeanMap(Map<Integer, LeagueTrainBean> leagueTrainBeanMap) {
        TrainConsole.leagueTrainBeanMap = leagueTrainBeanMap;
    }

    public static Map<Integer, TrainArenaBean> getTrainArenaBeanMap() {
        return trainArenaBeanMap;
    }

    public static void setTrainArenaBeanMap(Map<Integer, TrainArenaBean> trainArenaBeanMap) {
        TrainConsole.trainArenaBeanMap = trainArenaBeanMap;
    }

}

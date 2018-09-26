package com.ftkj.manager.logic.taskpool;

import java.util.HashMap;
import java.util.Map;

import com.ftkj.enums.ETaskCondition;

/**
 * 任务池	
 * @author ken
 * @date 2017-2-21
 */
public class TaskPool {

	private static Map<Integer, Class<? extends AbstractTask>> taskMap = null;
	
	static {
		
		taskMap = new HashMap<Integer, Class<? extends AbstractTask>>();
//	    taskMap.put(ETaskCondition.点击界面.getType(), Type1.class);
//		taskMap.put(ETaskCondition.好友.getType(), Type1.class);
//		taskMap.put(ETaskCondition.球探签约.getType(), Type2.class);
//		taskMap.put(ETaskCondition.即时PK.getType(), Type3.class);
//		taskMap.put(ETaskCondition.商城购买.getType(), Type4.class);
		taskMap.put(ETaskCondition.打比赛.getType(), Type5.class);
//		taskMap.put(ETaskCondition.解雇球员.getType(), Type6.class);
//		taskMap.put(ETaskCondition.球队等级.getType(), Type7.class);
//		taskMap.put(ETaskCondition.多人赛.getType(), Type8.class);		
//		taskMap.put(ETaskCondition.强化装备.getType(), Type9.class);
//		taskMap.put(ETaskCondition.进阶装备.getType(), Type10.class);		
//		taskMap.put(ETaskCondition.升级装备.getType(), Type11.class);
//		taskMap.put(ETaskCondition.充值RMB.getType(), Type12.class);
//		taskMap.put(ETaskCondition.获得球员.getType(), Type13.class);
//		taskMap.put(ETaskCondition.联盟.getType(), Type14.class);
//		taskMap.put(ETaskCondition.联盟聊天.getType(), Type15.class);
//		taskMap.put(ETaskCondition.联盟捐献经费.getType(), Type16.class);
//		taskMap.put(ETaskCondition.联盟捐献勋章.getType(), Type17.class);
//		taskMap.put(ETaskCondition.球星卡全套卡制作.getType(), Type18.class);		
//		taskMap.put(ETaskCondition.球星卡升级.getType(), Type19.class);
//		taskMap.put(ETaskCondition.合成荣誉头像.getType(), Type20.class);		
//		taskMap.put(ETaskCondition.分解荣誉头像.getType(), Type21.class);
//		taskMap.put(ETaskCondition.累计胜场.getType(), Type22.class);
//		taskMap.put(ETaskCondition.球馆转盘.getType(), Type23.class);
//		taskMap.put(ETaskCondition.招募球员.getType(), Type24.class);
//		taskMap.put(ETaskCondition.任命教练.getType(), Type25.class);
//		taskMap.put(ETaskCondition.球员升星.getType(), Type26.class);
//		taskMap.put(ETaskCondition.球员升级.getType(), Type27.class);
//		taskMap.put(ETaskCondition.球员训练.getType(), Type28.class);
//		taskMap.put(ETaskCondition.球员技能.getType(), Type29.class);
//		taskMap.put(ETaskCondition.球馆建筑升级.getType(), Type30.class);
//		taskMap.put(ETaskCondition.多人赛总冠军.getType(), Type31.class);
//		taskMap.put(ETaskCondition.选秀.getType(), Type32.class);
//		taskMap.put(ETaskCondition.主线赛程胜利.getType(), Type33.class);
//		taskMap.put(ETaskCondition.合成球衣.getType(), Type34.class);
//		taskMap.put(ETaskCondition.学习战术.getType(), Type35.class);		
//        taskMap.put(ETaskCondition.升级战术.getType(), Type36.class);
//        taskMap.put(ETaskCondition.解锁教练.getType(), Type37.class);
//        taskMap.put(ETaskCondition.球馆球员上阵.getType(), Type38.class);
//        taskMap.put(ETaskCondition.球馆升级.getType(), Type39.class);        
//        taskMap.put(ETaskCondition.选秀选人.getType(), Type40.class);
//        taskMap.put(ETaskCondition.街球赛.getType(), Type41.class);
//        taskMap.put(ETaskCondition.Main_Match.getType(), Type42.class);       
//        taskMap.put(ETaskCondition.完成一场比赛.getType(), Type43.class);
//        taskMap.put(ETaskCondition.刷新球队天赋.getType(), Type44.class);        
//        taskMap.put(ETaskCondition.多人赛名次.getType(), Type45.class);
//        taskMap.put(ETaskCondition.训练馆占领成功.getType(), Type46.class);
//        taskMap.put(ETaskCondition.比赛胜利次数.getType(), Type47.class);
      
        }
	
	public static Class<? extends AbstractTask> getTask(int conditionType) {		
		
		return taskMap.get(conditionType);
	}
	
}

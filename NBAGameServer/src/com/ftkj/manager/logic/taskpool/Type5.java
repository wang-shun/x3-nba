package com.ftkj.manager.logic.taskpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.manager.arena.Arena;
import com.ftkj.manager.logic.ArenaManager;
import com.ftkj.manager.logic.TaskManager;
import com.ftkj.manager.task.TaskCondition;
import com.ftkj.manager.task.TaskConditionBean;

public class Type5 extends AbstractTask {

	private static final long serialVersionUID = -6137146531197423207L;
	
	private static final Logger log = LoggerFactory.getLogger(TaskManager.class);

    @Override
    public boolean executeTask(long teamId, TaskConditionBean taskConditionBean, TaskCondition taskCondition) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean acceptTask(long teamId, TaskConditionBean taskConditionBean, TaskCondition taskCondition) {
        ArenaManager arenaManager = getManager(ArenaManager.class);
        Arena arena = arenaManager.getArena(teamId);
        switch(taskConditionBean.getValString()) {
            case "Main_Match":
                break;
            case "Arena_Match":
//                int matchCount = arena == null ? 0 : (int)arena.getTotalMatchCount();
//                taskCondition.getCondition().setValInt((matchCount));
                if(taskCondition.getValInt() >= taskConditionBean.getValInt()) return true;
                break;
            case "LeagueGroup_Match":
                break;
            default:
                log.error("acceptTask 无效类型--{}", taskConditionBean.getValString());  
        }            
       
        return false;
    }
}

package com.ftkj.manager.train;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

/**
 * 球队训练馆数据
 * @author qin.jiang
 *
 */
public class TrainArenaBean extends ExcelBean{

	/** 球馆id */
    private int id;
    /** 球馆名称*/
    private int teamId;   
    /** 对应球队id */
    private String name;
    /** 对应球队名称 */
    private String teamName;
 
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTeamId() {
        return teamId;
    }
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public void initExec(RowData row) {
        // TODO Auto-generated method stub
        
    }   
    
}

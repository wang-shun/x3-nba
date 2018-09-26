package com.ftkj.manager.starlet;

import java.util.List;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * 新秀排位赛阵容数据
 * @author qin.jiang
 */
public class StarletPlayer  extends AsynchronousBatchDB{

    private static final long serialVersionUID = -2799175686612636029L;
    
    /** 球队ID */
    private long teamId;  
    /** 策划配置id */
    private Integer playerRid;  

    /** 阵容位置 */
    private String lineupPosition;
    /** 战力值(新秀临时战力值) */
    private int cap;    
    
    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public long getTeamId() {
        return teamId;
    }

    public String getLineupPosition() {
        return lineupPosition;
    }

    public void setLineupPosition(String lineupPosition) {
        this.lineupPosition = lineupPosition;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public Integer getPlayerRid() {
        return playerRid;
    }

    public void setPlayerRid(Integer playerRid) {
        this.playerRid = playerRid;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.teamId, this.playerRid, this.lineupPosition, this.cap);
    }

    @Override
    public String getRowNames() {
        return "team_id, player_rid, lineup_position, cap";
    }

    @Override
    public String getTableName() {
        return "t_u_starlet_player";
    }

    @Override
    public void del() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,
                this.playerRid,
                this.lineupPosition,
                this.cap);
    }

}

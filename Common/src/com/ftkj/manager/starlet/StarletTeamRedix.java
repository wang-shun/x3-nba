package com.ftkj.manager.starlet;

import java.util.List;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * 新秀对抗赛数据
 * @author qin.jiang
 */
public class StarletTeamRedix extends AsynchronousBatchDB {

    private static final long serialVersionUID = -2799175686612636029L;

    /** 球队ID */
    private long teamId;
    
    /** 卡组类型 */
    private int cardType;
    
    /** 获胜总基数 */
    private int redixNum;    
  
    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getRedixNum() {
        return redixNum;
    }

    public void setRedixNum(int redixNum) {
        this.redixNum = redixNum;
    }    

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.teamId, this.cardType, this.redixNum);
    }

    @Override
    public String getRowNames() {
        return "team_id, card_type, redix_num";
    }

    @Override
    public String getTableName() {
        return "t_u_starlet_team_redix";
    }

    @Override
    public void del() {
        // TODO Auto-generated method stub
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,
                this.cardType,             
                this.redixNum);
    }
}

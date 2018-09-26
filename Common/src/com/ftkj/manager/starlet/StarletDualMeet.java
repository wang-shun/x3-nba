package com.ftkj.manager.starlet;

import java.util.List;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.google.common.collect.Lists;

/**
 * 新秀对抗赛数据
 * @author qin.jiang
 */
public class StarletDualMeet extends AsynchronousBatchDB {
    private static final long serialVersionUID = -6860712945677016085L;
 
    /** 球队ID */
    private long teamId;    
    /** 策划配置id */
    private int playerRid; 
    /** 得分*/
    private int pts;
    /** 篮板*/
    private int reb;
    /** 助攻*/
    private int ast;
    /** 抢断*/
    private int stl;
    /** 盖帽*/
    private int blk;
    /** 失误*/
    private int to;
    /** 犯规*/
    private int pf; 
    /** 总次数*/
    private int total; 

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getPlayerRid() {
        return playerRid;
    }

    public void setPlayerRid(int playerRid) {
        this.playerRid = playerRid;
    }

    public int getPts() {
        return pts;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public int getReb() {
        return reb;
    }

    public void setReb(int reb) {
        this.reb = reb;
    }

    public int getAst() {
        return ast;
    }

    public void setAst(int ast) {
        this.ast = ast;
    }

    public int getStl() {
        return stl;
    }

    public void setStl(int stl) {
        this.stl = stl;
    }

    public int getBlk() {
        return blk;
    }

    public void setBlk(int blk) {
        this.blk = blk;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getPf() {
        return pf;
    }

    public void setPf(int pf) {
        this.pf = pf;
    }
    
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
    @Override
    public String getRowNames() {
        return "team_id, player_rid, pts, reb, ast, stl, blk, `to`, pf, total";
    }
    
    @Override
    public String getTableName() {
        return "t_u_starlet_dual_meet";
    }

    @Override
    public void del() {
        // TODO Auto-generated method stub
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(
                this.teamId,               
                this.playerRid,
                this.pts,
                this.reb,
                this.ast,
                this.stl,
                this.blk,
                this.to,
                this.pf,
                this.total);
    }
    
    @Override
    public synchronized void save() {
        super.save();
    }
    
    @Override
    public String getSource() {
        // TODO Auto-generated method stub
        return null;
    }
}

package com.ftkj.manager.player;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.ftkj.console.PlayerConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

/**
 * @author tim.huang
 * 2018年2月28日
 * 球员天赋
 */
public class PlayerTalent extends AsynchronousBatchDB implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private long teamId;
    private int id;
    private int playerId;
    //8维波动值
    private int df;
    private int zg;
    private int lb;
    private int qd;
    private int gm;
    private int tlmz;
    private int fqmz;
    private int sfmz;
    private List<PropSimple> propList;
    public static PlayerTalent Empty = new PlayerTalent();

    public void refreshTalent(PlayerTalent tmp) {
        this.df = tmp.getDf();
        this.zg = tmp.getZg();
        this.lb = tmp.getLb();
        this.qd = tmp.getQd();
        this.gm = tmp.getGm();
        this.tlmz = tmp.getTlmz();
        this.fqmz = tmp.getFqmz();
        this.sfmz = tmp.getSfmz();
        this.save();
    }
    
    /**
     * 天赋的攻防计算，整值 = 当前天赋攻防 / 满天赋攻防
     * @return 天赋比例数值
     */
    public int getTalentValue() {
    	PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
    	if(pb == null) {
    		return 0;
    	}
    	Map<EActionType, Float> map = pb.getAbility();
    	float a1 = map.get(EActionType.pts) * getAbility(EActionType.pts) * 1.5f
    			+ map.get(EActionType.ftm) * getAbility(EActionType.ftm) * 0.2f
		    	+ map.get(EActionType.fgm) * getAbility(EActionType._2pm) * 0.3f
		    	+ map.get(EActionType._3pm) * getAbility(EActionType._3pm) * 0.25f
		    	+ map.get(EActionType.oreb) * getAbility(EActionType.reb) * 0.4f
		    	+ map.get(EActionType.ast) * getAbility(EActionType.ast) * 0.6f
		    	+ map.get(EActionType.dreb) * getAbility(EActionType.reb) * 0.3f
		    	+ map.get(EActionType.stl) * getAbility(EActionType.stl) * 0.8f
		    	+ map.get(EActionType.blk) * getAbility(EActionType.blk) * 0.6f;
    	//
    	float a2 = map.get(EActionType.pts) * 2500 * 1.5f
    			+ map.get(EActionType.ftm) * 2500 * 0.2f
		    	+ map.get(EActionType.fgm) * 2500 * 0.3f
		    	+ map.get(EActionType._3pm) * 2500 * 0.25f
		    	+ map.get(EActionType.oreb) * 2500 * 0.4f
		    	+ map.get(EActionType.ast) * 2500 * 0.6f
		    	+ map.get(EActionType.dreb) * 2500 * 0.3f
		    	+ map.get(EActionType.stl) * 2500 * 0.8f
		    	+ map.get(EActionType.blk) * 2500 * 0.6f;
    	return (int) Math.floor(a1 / a2 * 100);
    }

    public static PlayerTalent createPlayerTalent(String config) {
        int[] array = StringUtil.toIntArray(config, StringUtil.DEFAULT_ST);
        PlayerTalent ptTemp = new PlayerTalent();
        ptTemp.setDf(array[0]);
        ptTemp.setZg(array[1]);
        ptTemp.setLb(array[2]);
        ptTemp.setQd(array[3]);
        ptTemp.setGm(array[4]);
        ptTemp.setTlmz(array[5]);
        ptTemp.setFqmz(array[6]);
        ptTemp.setSfmz(array[7]);
        return ptTemp;
    }

    public static PlayerTalent createPlayerTalent(long teamId, int playerRid, int id, DropBean drop, boolean save) {
        PlayerTalent ptTemp = new PlayerTalent();
        ptTemp.setTeamId(teamId);
        ptTemp.setPlayerId(playerRid);
        ptTemp.setId(id);
        ptTemp.setDf(drop.roll().get(0).getNum());
        ptTemp.setZg(drop.roll().get(0).getNum());
        ptTemp.setLb(drop.roll().get(0).getNum());
        ptTemp.setQd(drop.roll().get(0).getNum());
        ptTemp.setGm(drop.roll().get(0).getNum());
        ptTemp.setTlmz(drop.roll().get(0).getNum());
        ptTemp.setFqmz(drop.roll().get(0).getNum());
        ptTemp.setSfmz(drop.roll().get(0).getNum());
        if (save) {
            ptTemp.save();
        }
        return ptTemp;
    }

    public String getTalentScore() {
        return this.df + "," + this.zg + "," + this.lb + "," + this.qd + "," + this.gm + "," + this.tlmz + "," + this.fqmz + "," + this.sfmz;
    }

    public float getAbility(EActionType type) {
        if (type == EActionType.pts) {
            return df;
        } else if (type == EActionType.ast) {
            return zg;
        } else if (type == EActionType.reb) {
            return lb;
        } else if (type == EActionType._2pm) {
            return tlmz;
        } else if (type == EActionType.ftm) {
            return fqmz;
        } else if (type == EActionType._3pm) {
            return sfmz;
        } else if (type == EActionType.stl) {
            return qd;
        } else if (type == EActionType.blk) {
            return gm;
        }
        return 0f;
    }

    public List<PropSimple> getPropList() {
        return propList;
    }

    public void setPropList(List<PropSimple> propList) {
        this.propList = propList;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public int getZg() {
        return zg;
    }

    public void setZg(int zg) {
        this.zg = zg;
    }

    public int getLb() {
        return lb;
    }

    public void setLb(int lb) {
        this.lb = lb;
    }

    public int getQd() {
        return qd;
    }

    public void setQd(int qd) {
        this.qd = qd;
    }

    public int getGm() {
        return gm;
    }

    public void setGm(int gm) {
        this.gm = gm;
    }

    public int getTlmz() {
        return tlmz;
    }

    public void setTlmz(int tlmz) {
        this.tlmz = tlmz;
    }

    public int getFqmz() {
        return fqmz;
    }

    public void setFqmz(int fqmz) {
        this.fqmz = fqmz;
    }

    public int getSfmz() {
        return sfmz;
    }

    public void setSfmz(int sfmz) {
        this.sfmz = sfmz;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.teamId, this.id, this.playerId, this.df, this.zg, this.lb, this.qd, this.gm, this.tlmz, this.fqmz, this.sfmz);
    }

    @Override
    public String getRowNames() {
        return "team_id, id, player_id, df, zg, lb, qd, gm, tlmz, fqmz, sfmz";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.id, this.playerId, this.df, this.zg, this.lb, this.qd, this.gm, this.tlmz, this.fqmz, this.sfmz);
    }

    @Override
    public String getTableName() {
        return "t_u_player_talent";
    }

    @Override
    public void del() {
        this.playerId = -1;
        this.save();
    }

    @Override
    public synchronized void save() {
        super.save();
    }

    @Override
    public PlayerTalent clone() throws CloneNotSupportedException {
        return (PlayerTalent) super.clone();
    }

    @Override
    public String toString() {
        return "{" +
                "\"teamId\":" + teamId +
                ", \"id\":" + id +
                ", \"playerId\":" + playerId +
                ", \"df\":" + df +
                ", \"zg\":" + zg +
                ", \"lb\":" + lb +
                ", \"qd\":" + qd +
                ", \"gm\":" + gm +
                ", \"tlmz\":" + tlmz +
                ", \"fqmz\":" + fqmz +
                ", \"sfmz\":" + sfmz +
                '}';
    }


}

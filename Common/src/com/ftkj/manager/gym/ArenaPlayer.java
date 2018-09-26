package com.ftkj.manager.gym;

import org.joda.time.DateTime;

import com.ftkj.console.PlayerConsole;
import com.ftkj.db.domain.ArenaPlayerPO;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.manager.player.PlayerBean;

/**
 * @author tim.huang
 * 2017年7月5日
 *
 */
public class ArenaPlayer {
	private ArenaPlayerPO po;
	
	public static ArenaPlayer createArenaPlayer(long teamId,int pid,int playerId){
		ArenaPlayerPO po = new ArenaPlayerPO();
		PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
		po.setCreateTime(DateTime.now());
		po.setGrade(pb.getGrade().ordinal());
		po.setPlayerId(playerId);
		po.setPosition(pb.getPosition()[0].getId());
		po.setPid(pid);
		po.setTeamId(teamId);
		po.setTid(pb.getTeam().getTid());
		po.save();
		ArenaPlayer ap = new ArenaPlayer(po);
		return ap;
	}
	
	public static ArenaPlayer createArenaPlayer(long teamId,int pid,int playerId,int grade,int position,int tid){
		ArenaPlayerPO po = new ArenaPlayerPO();
		po.setCreateTime(DateTime.now());
		po.setGrade(grade);
		po.setPlayerId(playerId);
		po.setPosition(position);
		po.setPid(pid);
		po.setTeamId(teamId);
		po.setTid(tid);
		po.save();
		ArenaPlayer ap = new ArenaPlayer(po);
		return ap;
	}
	

	public ArenaPlayer(ArenaPlayerPO po) {
		this.po = po;
	}

	public void del(){
		this.po.del();
	}
	

	public int getPosition() {
		return this.po.getPosition();
	}
	
	public int getPid() {
		return this.po.getPid();
	}

	public int getPlayerId() {
		return this.po.getPlayerId();
	}

	public int getTid() {
		return this.po.getTid();
	}

	public EPlayerGrade getGrade() {
		return EPlayerGrade.values()[this.po.getGrade()];
	}
	
}

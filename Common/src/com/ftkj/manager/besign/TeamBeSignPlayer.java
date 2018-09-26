package com.ftkj.manager.besign;

import com.ftkj.db.domain.BeSignPlayerPO;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TeamBeSignPlayer {

	private long teamId;
	private List<BeSignPlayer> beSignList;
	private AtomicInteger sid;
	
	public TeamBeSignPlayer(long teamId, List<BeSignPlayerPO> list) {
		this.teamId = teamId;
		//
		int maxId = list.stream().mapToInt(e->e.getId()).max().orElse(0);
		sid = new AtomicInteger(maxId);
		//
		List<BeSignPlayer> newlist = Lists.newArrayList();
		for(BeSignPlayerPO po : list) {
			newlist.add(new BeSignPlayer(po));
		}
		this.beSignList = newlist;
	}
	
	public BeSignPlayer addBeSignPlayer(int playerId, int price,int tid, boolean bind) {
		BeSignPlayerPO po = new BeSignPlayerPO(sid.incrementAndGet(), teamId, playerId, price, tid, DateTime.now().plusDays(1));
		po.setBind(bind);
		BeSignPlayer beSign = new BeSignPlayer(po);
		beSign.save();
		this.beSignList.add(beSign);
		return beSign;
	}
	
	public List<BeSignPlayer> getBeSignList() {
		return beSignList;
	}
	
	/**
	 * 移除待签，或者签约时调用
	 * 注意：调用后，playerId会设置-1
	 * @param id
	 * @return 
	 */
	public BeSignPlayer removeBeSign(int id) {
		BeSignPlayer beSign = getBeSignPlayer(id);
		beSign.del();
		beSignList.remove(beSign);
		return beSign;
	}
	
	/**
	 * 移除待签，或者签约时调用
	 * @param status
	 * @return 
	 */
	public List<BeSignPlayer> removeBeSignList(int[] ids) {
		List<BeSignPlayer> removeList = Lists.newArrayList();
		for(int id : ids) {
			removeList.add(removeBeSign(id));
		}
		return removeList;
	}
	
	/**
	 * 取待签球员
	 * @param id
	 * @return
	 */
	public BeSignPlayer getBeSignPlayer(int id) {
		return this.beSignList.stream().filter(beSign-> beSign.getId()==id).findFirst().get();
	}
	
	/**
	 * 判断是否有该待签
	 * @param id
	 * @return
	 */
	public boolean checkBeSignPlayer(int id) {
		return this.beSignList.stream().anyMatch(beSign-> beSign.getId()==id);
	}

}

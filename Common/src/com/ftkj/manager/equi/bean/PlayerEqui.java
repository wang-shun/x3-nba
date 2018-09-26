package com.ftkj.manager.equi.bean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.console.PlayerConsole;
import com.ftkj.manager.player.PlayerBean;
import com.google.common.collect.Maps;

/**
 * @Description:套装，某个球员的装备
 * @author Jay
 * @time:2017年3月15日 下午2:35:50
 */
public class PlayerEqui {

	/**
	 * 球员
	 */
	private int playerId;
	/**
	 * 装备列表，部位：装备
	 */
	private Map<Integer, Equi> equiMap;
	
	
	
	/**
	 * 一般加载用
	 * @param id
	 * @param PlayerId
	 * @param pid
	 */
	public PlayerEqui(int PlayerId) {
		this.playerId = PlayerId;
		this.equiMap = Maps.newConcurrentMap();
	}
	
	/**
	 * 构造，默认不穿戴
	 * @param id 套装
	 * @param PlayerId 套装类型
	 * @param list 装备列表
	 */
	public PlayerEqui(int PlayerId, List<Equi> list) {
		this.playerId = PlayerId;
		this.equiMap = list.stream().collect(Collectors.toMap(Equi::getType, (e) -> e));
	}
	
	/**
	 * 设置装备所属球员
	 * @param playerId
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
		this.equiMap.values().forEach(equi-> equi.setPlayerId(playerId));
	}
	
	/**
	 * 重置球衣归属
	 */
	public void resetEquiTeam() {
		PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
		for(Equi e: this.equiMap.values()) {
			int equiTeam = e.getType() !=3 ? 0 : pb.getTeamId();
			e.setEquiTeam(equiTeam);
		}
	}
	
	public Collection<Equi> getPlayerEqui() {
		return this.equiMap.values();
	}
	
	public void save() {
		this.equiMap.values().forEach(equi-> equi.save());
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void put(int type, Equi equi) {
		this.equiMap.put(type, equi);
	}
	
	public Equi getPlayerEquiByType(int type) {
		return this.equiMap.get(type);
	}

}

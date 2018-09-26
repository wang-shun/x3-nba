package com.ftkj.manager.gym;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tim.huang
 * 2017年8月14日
 *
 */
public class ArenaPlayerTeam {
	private List<ArenaPlayer> players;
	
	private AtomicInteger ids;
	
	
	public ArenaPlayerTeam(List<ArenaPlayer> players){
		this.players = players;
		int maxId = this.players.stream().mapToInt(player->player.getPid()).max().orElse(0);
		this.ids = new AtomicInteger(maxId);
	}
	
	public void addPlayer(ArenaPlayer player){
		this.players.add(player);
	}
	
	public List<ArenaPlayer> getPlayers() {
		return players;
	}

	public int getId() {
		return ids.incrementAndGet();
	}
	
	
	
	
	
	
}

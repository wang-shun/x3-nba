package com.ftkj.manager.player;

import com.ftkj.enums.EPlayerPosition;

public class PlayerSimple {
	private int playerId;
	private EPlayerPosition position;
	private EPlayerPosition playerPosition;
	private int level;
	private int price;
	
	private PlayerSimple(int playerId, EPlayerPosition position,
			EPlayerPosition playerPosition, int level, int price) {
		super();
		this.playerId = playerId;
		this.position = position;
		this.playerPosition = playerPosition;
		this.level = level;
		this.price = price;
	}

	public static PlayerSimple newPlayerSimple(int playerId,EPlayerPosition position,EPlayerPosition playerPosition,
			int level,int price){
		PlayerSimple ps = new PlayerSimple(playerId, position, playerPosition, level, price);
		return ps;
	}
	
	public int getPlayerId() {
		return playerId;
	}
	public EPlayerPosition getPosition() {
		return position;
	}
	public EPlayerPosition getPlayerPosition() {
		return playerPosition;
	}
	public int getLevel() {
		return level;
	}
	public int getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return "PlayerSimple [playerId=" + playerId + ", position=" + position + ", playerPosition=" + playerPosition
				+ ", level=" + level + ", price=" + price + "]";
	}
	
}

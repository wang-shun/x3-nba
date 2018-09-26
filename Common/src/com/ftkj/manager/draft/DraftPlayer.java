package com.ftkj.manager.draft;

import com.ftkj.manager.player.PlayerTalent;

/**
 * @author tim.huang
 * 2017年5月4日
 *
 */
public class DraftPlayer {
	private int playerId;
	private int price;
	private PlayerTalent pt;
	private String signTeamName;
	private boolean bind;//是否绑定
	public DraftPlayer(int playerId, int price,PlayerTalent pt) {
		super();
		this.playerId = playerId;
		this.price = price;
		this.signTeamName = "";
		this.pt = pt;
	}
	public PlayerTalent getPt() {
		return pt;
	}
	public void setPt(PlayerTalent pt) {
		this.pt = pt;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getSignTeamName() {
		return signTeamName;
	}
	public void setSignTeamName(String signTeamName) {
		this.signTeamName = signTeamName;
	}

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }
}

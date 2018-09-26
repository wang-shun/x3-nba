package com.ftkj.manager.besign;

import com.ftkj.db.domain.BeSignPlayerPO;

import org.joda.time.DateTime;

public class BeSignPlayer {

	private BeSignPlayerPO playerPO;
	
	public BeSignPlayer() {
	}

	public BeSignPlayer(BeSignPlayerPO playerPO) {
		super();
		this.playerPO = playerPO;	
	}
	
	
	public int getTid(){
		return this.playerPO.getTid();
	}
	public void save() {
		this.playerPO.save();
	}
	public void del() {
		this.playerPO.setPlayerId(-1);
		this.save();
	}
	public int getId() {
		return this.playerPO.getId();
	}

	public int getPlayerId() {
		return this.playerPO.getPlayerId();
	}
	
	public int getPrice() {
		return this.playerPO.getPrice();
	}
	
	public DateTime getEndTime() {
		return this.playerPO.getEndTime();
	}

    public boolean isBind() {
        return playerPO.isBind();
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((playerPO == null) ? 0 : playerPO.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BeSignPlayer other = (BeSignPlayer) obj;
		if (playerPO == null) {
			if (other.playerPO != null) {
				return false;
			}
		} else if (playerPO.getId() != other.playerPO.getId()) {
			return false;
		}
		return true;
	}
	
	
	
	
	
}

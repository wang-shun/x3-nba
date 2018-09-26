package com.ftkj.manager.active.cardAward;

import com.ftkj.proto.AtvCardAwardPB;

public interface ICardWardManager {
	
	public void getCardAward();

	public AtvCardAwardPB.AtvCardData getTeamActiveData(long teamId);
}

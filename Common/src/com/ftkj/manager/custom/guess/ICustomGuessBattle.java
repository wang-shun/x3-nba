package com.ftkj.manager.custom.guess;

import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.custom.CustomGuessResult;

public interface ICustomGuessBattle {
	public CustomGuessResult getCustomGuessResult(BattleSource bs);
}

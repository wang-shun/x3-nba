package com.ftkj.manager.logic.common;

import com.ftkj.annotation.IOC;
import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.logic.PlayerManager;
import com.ftkj.manager.logic.TaskManager;
import com.ftkj.manager.player.Player;
import com.google.common.eventbus.Subscribe;

public class SignPlayerManager extends BaseManager {
	
	@IOC
	private TaskManager taskManager;
	@IOC
	private PlayerManager playerManager;

	@Override
	public void instanceAfter() {
		EventBusManager.register(EEventType.得到球员, this);
	}
	
	/**
	 * 得到球员
	 * @param player
	 */
	@Subscribe
	public void signPlayer(Player player) {
		long teamId = player.getTeamId();
		int playerId = player.getPlayerRid();
		int price = player.getPrice();
		String grade = PlayerConsole.getPlayerGrade(playerId);
		boolean isBasePrice = playerManager.isMinPricePlayer(playerId, price);
		taskManager.updateTask(teamId, ETaskCondition.获得球员, 1, grade + ","
                + price + "," + (isBasePrice ? EStatus.True.getId() : EStatus.False.getId()));
	}

}

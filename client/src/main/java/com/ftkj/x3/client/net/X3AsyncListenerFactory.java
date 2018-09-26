package com.ftkj.x3.client.net;

import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.task.helper.PropHelper;
import com.ftkj.x3.client.task.logic.player.PlayerClient;
import com.ftkj.x3.client.task.logic.sys.AllStarClient;
import com.ftkj.x3.client.task.logic.sys.ArenaClient;
import com.ftkj.x3.client.task.logic.sys.BattleClient;
import com.ftkj.x3.client.task.logic.sys.MainMatchClient;
import com.ftkj.x3.client.task.logic.sys.RankedClient;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.xxs.client.net.AsyncListenerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 异步消息处理.
 *
 * @author luch
 */
@Component
public class X3AsyncListenerFactory implements AsyncListenerFactory {
    private static final Logger log = LoggerFactory.getLogger(X3AsyncListenerFactory.class);
    private Map<Integer, X3AsyncListener> asyncListenerMap = new HashMap<>();

    private X3AsyncListener ignored = (uc, user, msg) ->
            log.debug("Ignored server push msg. code {} tid {}", msg.code(), user.tid());

    @Autowired
    private MainMatchClient mmc;
    @Autowired
    private RankedClient rc;
    @Autowired
    private BattleClient battle;
    @Autowired
    private AllStarClient allStarClient;
    @Autowired
    private ArenaClient arenaClient;

    /** 初始化异步消息处理器 */
    public void init() {
        asyncListenerMap.put(ServiceCode.Push_Battle_Coach, ignored);
        asyncListenerMap.put(ServiceCode.Push_Battle_Skill, ignored);
        asyncListenerMap.put(ServiceCode.Push_Window_All, ignored);
        asyncListenerMap.put(ServiceCode.Match_Topic, ignored);

        asyncListenerMap.put(ServiceCode.Battle_All_Push, battle::mainPush);
        asyncListenerMap.put(ServiceCode.Battle_Round_Push, battle::roundPush);
        asyncListenerMap.put(ServiceCode.Battle_PK_Stage_Round_Main_End, battle::roundEndPush);
        asyncListenerMap.put(ServiceCode.Battle_Start_Push, battle::startPush);
        asyncListenerMap.put(ServiceCode.Push_Battle_End, battle::endPush);
        asyncListenerMap.put(ServiceCode.Push_Arena_Defend_Tip, ignored);
        asyncListenerMap.put(ServiceCode.RedPointManager_push_Change, ignored);
        asyncListenerMap.put(ServiceCode.Push_Money, TeamClient::moneyChange);
        asyncListenerMap.put(ServiceCode.Team_Up_Level, TeamClient::pushTeamLev);
        asyncListenerMap.put(ServiceCode.Push_Player, PlayerClient::playerChange);
        asyncListenerMap.put(ServiceCode.Player_Storage_Change_Push, PlayerClient::storageChange);
        asyncListenerMap.put(ServiceCode.Prop_Change, PropHelper::propChange);
        asyncListenerMap.put(ServiceCode.Push_Player_Star, PlayerClient::updateStar);
        asyncListenerMap.put(ServiceCode.GameManager_topicTarget, ignored);

        asyncListenerMap.put(ServiceCode.MMatch_Info_Push, mmc::infoPush);
        asyncListenerMap.put(ServiceCode.MMatch_Championship_Info_Push, mmc::championshipInfoPush);
        asyncListenerMap.put(ServiceCode.MMatch_Buy_Match_Num_Push, mmc::basePush);
        asyncListenerMap.put(ServiceCode.MMatch_Quick_Match_Push, mmc::quickMatchPush);
        asyncListenerMap.put(ServiceCode.MMatch_Equip_Exp_Push, mmc::equipExpPush);
        asyncListenerMap.put(ServiceCode.MMatch_Match_Win_Push, ignored);

        asyncListenerMap.put(ServiceCode.RMatch_Info_Push, rc::infoPush);
        asyncListenerMap.put(ServiceCode.RMatch_Rank_Push, rc::rankListPush);

        asyncListenerMap.put(ServiceCode.Push_Task_Day_Finish, ignored);
        asyncListenerMap.put(ServiceCode.AllStar_Info_Push, allStarClient::infoPush);
        asyncListenerMap.put(ServiceCode.AllStar_End_Match_Push, ignored);

        asyncListenerMap.put(ServiceCode.Arena_Info_Push, arenaClient::infoPush);
        asyncListenerMap.put(ServiceCode.Arena_Target_Change_Push, arenaClient::targetChangePush);
        asyncListenerMap.put(ServiceCode.Arena_End_Match_Push, arenaClient::matchEndPush);
        asyncListenerMap.put(ServiceCode.Arena_Buy_Match_Num_Push, arenaClient::buyNumPush);
        asyncListenerMap.put(ServiceCode.Arena_Refresh_Target_Push, arenaClient::refreshTargetPush);

    }

    @Override
    public X3AsyncListener getAsyncListener(int code) {
        return asyncListenerMap.get(code);
    }
}

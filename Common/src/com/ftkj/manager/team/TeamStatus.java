package com.ftkj.manager.team;

import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.battle.EBattleType;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * @author tim.huang
 * 2017年5月8日
 * 玩家状态
 */
public class TeamStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    //    private int draftRoomId;//选秀房间ID
    private volatile List<TeamBattleStatus> states;
    private DateTime worldChat;

    public TeamStatus() {
        super();
        states = Lists.newArrayList();
    }

    //	public void putBattle(EBattleType type,long battleId,String node){
    //		TeamBattleStatus bt = getBattle(type);
    //		if(bt !=null){
    //			bt.setBattleId(battleId);
    //			bt.setNode(node);
    //		}else{
    //			bt = new TeamBattleStatus(type,battleId,node);
    //			battleStatus.add(bt);
    //		}
    //	}
    public void putBattle(EBattleType type, long battleId, String nodeIp, String nodeName, EBattleRoomStatus status) {
        TeamBattleStatus bt = getBattle(type);
        if (bt != null) {
            bt.setBattleId(battleId);
            bt.setNodeIp(nodeIp);
        } else {
            bt = new TeamBattleStatus(type, battleId, nodeIp);
            states.add(bt);
        }
        bt.setNodeName(nodeName);
        bt.updateStatus(status);
    }

    public TeamBattleStatus getBattle(EBattleType type) {
        return states.stream()
            .filter(bat -> bat.getType() == type)
            .findFirst()
            .orElse(null);
    }

    public List<TeamBattleStatus> getStates() {
        return states;
    }

    public DateTime getWorldChat() {
        return worldChat;
    }

    public void setWorldChat(DateTime worldChat) {
        this.worldChat = worldChat;
    }

    /** 球队是否在此类型比赛中. true 是 */
    public static boolean inBattle(TeamStatus ts, EBattleType... types) {
        return ts != null && ts.inBattle(types);
    }

    /** 球队是否在此类型比赛中. true 是 */
    public boolean inBattle(EBattleType... types) {
        for (EBattleType type : types) {
            for (TeamBattleStatus bs : states) {
                if (bs == null || bs.getType() != type) {
                    continue;
                }
                if (bs.getBattleId() != 0 && !bs.isTimeOut()) {
                    return true;
                }
            }
        }
        return false;
    }
}

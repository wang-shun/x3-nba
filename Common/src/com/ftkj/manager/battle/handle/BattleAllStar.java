package com.ftkj.manager.battle.handle;

import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Set;

/**
 * 全明星.
 */
public class BattleAllStar extends BattleCommon {
    private static final Logger log = LoggerFactory.getLogger(BattleAllStar.class);

    public BattleAllStar(BattleSource bs) {
        super(bs);
    }

    public BattleAllStar(BattleSource bs, BattleEnd end, BattleRoundReport round) {
        super(bs, end, round);
    }

    @Override
    protected void initPre() {
        BattleSource bs = getBattleSource();
        BattleAttr ba = bs.getOrCreateAttributeMap(bs.getHome().getTeamId()).getVal(EBattleAttribute.All_Star_Battle_Attr);
        if (ba == null || (ba.tjCapRate == 0f && ba.jiliCapRate == 0f)) {
            return;
        }
        BattleTeam home = bs.getHome();
        
        for(BattlePlayer pr : home.getPlayers()) {
          float rate = 0;
          if(ba.playerRid.contains(pr.getPlayerId())) {
            rate +=  ba.tjCapRate;
          }
          if(ba.jiliCapRate > 0) {
            rate +=  ba.jiliCapRate;
          }
          if(rate > 0f) {
            addCap(bs, ba, home, pr,rate);
          }
        }
    }

    private void addCap(BattleSource bs, BattleAttr ba, BattleTeam home, BattlePlayer pr,float rate) {
        PlayerAbility pa = new PlayerAbility(AbilityType.Npc_Buff, pr.getPlayerId());
        float ocap = pr.getAbility().getAttrData(EActionType.ocap);
        float dcap = pr.getAbility().getAttrData(EActionType.dcap);//比赛开始前的总防守战力
        float addocap = ocap * rate;
        pa.setAttr(EActionType.ocap, addocap);
        float adddcap = dcap * rate;
        pa.setAttr(EActionType.dcap, adddcap);
        pr.addPlayerAbility(pa);
        log.debug("btallstar init. bid {} tid {} prid {} rate {} ocap {} -> {} dcap {} -> {}", bs.getId(),
            home.getTeamId(), pr.getRid(), ba.tjCapRate, ocap, addocap, dcap, adddcap);
    }

    /** 主线赛程. 锦标赛的防守方替身数据 */
    public static final class BattleAttr implements Serializable {
        private static final long serialVersionUID = -2476716022935316138L;
        /** 推荐球员 攻防比率加成 */
        private final float tjCapRate;
        /** 增加推荐球员的攻防, 配置id. 为空的时候表示所有上阵球员 */
        private final Set<Integer> playerRid;
        /** 激励攻防加成*/
        private final float jiliCapRate;
        
        public BattleAttr(float jiliCapRate,float tjCapRate, Set<Integer> playerRid) {
            this.jiliCapRate = jiliCapRate;
            this.tjCapRate = tjCapRate;
            this.playerRid = playerRid;
        }
    }

}

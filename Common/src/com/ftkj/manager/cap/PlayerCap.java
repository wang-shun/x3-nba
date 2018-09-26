package com.ftkj.manager.cap;

import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ECapModule;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 球员裸攻
 *
 * @author Jay
 */
public class PlayerCap extends CapModule {
    private PlayerBean pb;
    private PlayerTalent pt;
    /**
     * 原始的攻防系数，都是1
     */
    private static Map<EActionType, Float> srcCapMap;
    /** 保底攻防 */
    public static final int Lowest_Cap = 20;

    static {
        srcCapMap = Maps.newHashMap();
        // 和攻防计算相关的三围都要放进来
        srcCapMap.put(EActionType.fgm, 1f);
        srcCapMap.put(EActionType.ftm, 1f);
        srcCapMap.put(EActionType.pts, 1f);
        srcCapMap.put(EActionType._2pm, 1f);
        srcCapMap.put(EActionType._3pm, 1f);
        srcCapMap.put(EActionType._3pa, 1f);
        srcCapMap.put(EActionType.oreb, 1f);
        srcCapMap.put(EActionType.reb, 1f);
        srcCapMap.put(EActionType.ast, 1f);
        //
        srcCapMap.put(EActionType.to, 1f);
        srcCapMap.put(EActionType.min, 1f);
        srcCapMap.put(EActionType.pf, 1f);
        //
        srcCapMap.put(EActionType.stl, 1f);
        srcCapMap.put(EActionType.blk, 1f);
        srcCapMap.put(EActionType.dreb, 1f);
    }

    public PlayerCap(int playerId, PlayerTalent pt) {
        super(AbilityType.Player_Base);
        this.playerId = playerId;
        this.pb = PlayerConsole.getPlayerBean(playerId);
        this.pt = pt;
        // 初始化攻防计算
        initCap();

        pb.getAbility().forEach((key, val) -> {
            if (key == EActionType.ocap || EActionType.dcap == key) {
                return;
            }
            addModuleActionType(ECapModule.球员裸数据, key, val);
        });
    }

    @Override
    public void initConfig() {
        rootNode = new CapNode(ECapModule.球员裸数据, true);
    }

    @Override
    public float[] analysis(ECapModule module) {
        float jg = 0;
        float fs = 0;
        if (module == ECapModule.球员裸数据) {
            // 原始裸值，系数是1
            jg = getJg(pb, srcCapMap, this.pt);
            fs = getFs(pb, srcCapMap, this.pt);
            //			jg = pb.getAbility(EActionType.attack_cap);
            //			fs = pb.getAbility(EActionType.guard_cap);
        }
        return new float[]{Math.max(jg, Lowest_Cap), Math.max(fs, Lowest_Cap)};
    }

}

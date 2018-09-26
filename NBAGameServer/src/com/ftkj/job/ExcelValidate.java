package com.ftkj.job;

import com.ftkj.cfg.MMatchLevBean;
import com.ftkj.cfg.MMatchLevBean.SpecialHandleMatch;
import com.ftkj.cfg.MMatchLevBean.Star;
import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.cfg.battle.BattleCustomBean.CustomBean;
import com.ftkj.console.ArenaConsole;
import com.ftkj.console.BattleConsole;
import com.ftkj.console.BattleCustomConsole;
import com.ftkj.console.CM;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.MainMatchConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.RankedMatchConsole;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.common.CacheManager;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 */
public class ExcelValidate {
    private static final Logger log = LoggerFactory.getLogger(ExcelValidate.class);

    public static void main(String[] args) {
        String excelPath = getExcelPath();
        CM.debugExcelPath = excelPath;
        log.info("client confg {}", excelPath);
        CM.init(false);
        CacheManager.initCacheFromClient();
        CacheManager.validateCache();

        validateOther();
    }

    private static String getExcelPath() {
        return System.getProperty("x3.excel");
    }

    private static void validateOther() {
        long curr = System.currentTimeMillis();
        MMatchLevBean levb = MainMatchConsole.getLevBean(1101);
        SpecialHandleMatch shm = levb.getSpecialHandle(1);
        Star star = levb.getStar(3);
        //        log.info("star cfg {} shm {}", star, shm);
        log.info("fa cfg {}", RankedMatchConsole.getFirstAward(1));
        log.info("drop cfg {}", DropConsole.getAndRoll(43015));
        //        log.info("rmatch seasons {}", RankedMatchConsole.getSeasonArrays());
        //        log.info("rmatch curr {} season {} currornext {}", curr, RankedMatchConsole.getCurrSeason(curr),
        //                RankedMatchConsole.getCurrOrNextSeason(curr));
        //        log.info("next tier {}", RankedMatchConsole.getHigherTier(5, 1556));
        //        log.info("next tier {}", RankedMatchConsole.getLowerTier(5, 1415));
        //        log.info("next tier {}", RankedMatchConsole.getLowerTier(5, 1100));
        BattleBean bb = BattleConsole.getBattle(EBattleType.Main_Match_Normal);
        //        log.info("bb {}", bb);
        for (int i = 97; i < 101; i++) {
            log.info("round {} step {}", i, bb.getBaseBean().getSteps().getStepByRound(i));
        }
        CustomBean cb = BattleCustomConsole.getBean(1);
        //        log.info("custom bean {}", cb);
        //        AIRuleGroupBean ai = AIConsole.getBean(10001);
        //        log.info("ai size {} cfg {}", ai.getRules().size(), ai);
        int tdid = ConfigConsole.getIntVal(EConfigKey.Build_Refresh_Talent_Drop);
        DropBean drop = DropConsole.getDrop(tdid);
        log.info("talent drop id {} {}", tdid, drop);
        if (drop != null) {
            for (int i = 0; i < 8; i++) {
                log.info("talent random {}", drop.roll().get(0).getNum());
            }
        }
        Map<Integer, PropSimple> props = ArenaConsole.getChangeRankReward(5001, 3812, 1411);
        log.info("arena max rank props {}", props);
        NPCBean npc = NPCConsole.getNPC(50001);
        log.info("npc {} name {} {}", npc.getNpcId(), npc.getNpcName(), npc);
    }
}

package com.ftkj.manager.common;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.console.AIConsole;
import com.ftkj.console.AllStarConsole;
import com.ftkj.console.AnswerQuestionConsole;
import com.ftkj.console.ArchiveConsole;
import com.ftkj.console.ArenaConsole;
import com.ftkj.console.BattleConsole;
import com.ftkj.console.BattleCustomConsole;
import com.ftkj.console.CDKeyConsole;
import com.ftkj.console.CM;
import com.ftkj.console.CoachConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DraftConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.EmailConsole;
import com.ftkj.console.EquiConsole;
import com.ftkj.console.GameConsole;
import com.ftkj.console.GradeConsole;
import com.ftkj.console.GymConsole;
import com.ftkj.console.HappySevenDayConsole;
import com.ftkj.console.HonorMatchConsole;
import com.ftkj.console.KnockoutMatchConsole;
import com.ftkj.console.LeagueArenaConsole;
import com.ftkj.console.LeagueConsole;
import com.ftkj.console.LeagueGroupWarConsole;
import com.ftkj.console.LogoConsole;
import com.ftkj.console.MainMatchConsole;
import com.ftkj.console.ModuleConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.PlayerCardConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PlayerCutsConsole;
import com.ftkj.console.PlayerExchangeConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.console.RankedMatchConsole;
import com.ftkj.console.ShopConsole;
import com.ftkj.console.SignConsole;
import com.ftkj.console.SkillConsole;
import com.ftkj.console.StageConsole;
import com.ftkj.console.StarletConsole;
import com.ftkj.console.StreetBallConsole;
import com.ftkj.console.SystemActiveConsole;
import com.ftkj.console.TacticsConsole;
import com.ftkj.console.TaskConsole;
import com.ftkj.console.TeamConsole;
import com.ftkj.console.TradeConsole;
import com.ftkj.console.TrainConsole;
import com.ftkj.console.VipConsole;
import com.ftkj.db.ao.common.IActiveAO;
import com.ftkj.db.ao.common.INBADataAO;
import com.ftkj.db.domain.active.base.SystemActivePO;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.db.domain.bean.PlayerMoneyBeanPO;
import com.ftkj.manager.BaseManager;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

/**
 * @author tim.huang
 * 2016年1月13日
 * 缓存配置管理
 */
public class CacheManager extends BaseManager {
    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);
    @IOC
    private INBADataAO nbaDataAO;
    @IOC
    private IActiveAO activeAO;

    public void resetCache() {
        if (GameSource.pool.equals("gm")) {
            return;
        }
        initCache();
        validateCache();
    }

    public void initCache() {
        List<SystemActivePO> activePoList = null;
        List<PlayerBeanVO> playerBeanList = nbaDataAO.getAllPlayerBean();
        List<PlayerMoneyBeanPO> playerMoneyBeanPOList = nbaDataAO.getAllPlayerMoneyBean();
        List<PlayerBeanVO> avgPlayerBeanList = nbaDataAO.getAllPlayerAvgBean();

        // 逻辑服才加载活动 
        if (GameSource.pool.equals("logic")) {
            // // 把活动表的创建放在这里
            if (GameSource.getServerStartDay() == 1) {
                log.warn("创建活动数据表：{}", GameSource.shardId);
                activeAO.createActiveDataTable();
            }
            activePoList = activeAO.getSystemActiveList(GameSource.shardId);
        } else {
            activePoList = Lists.newArrayList();
        }
        log.info("cache init. pool {} shardId {} act {}", GameSource.pool, GameSource.shardId, activePoList.size());
        initCache0(playerBeanList, playerMoneyBeanPOList, avgPlayerBeanList, activePoList);
        log.info("cache init done.");
    }

    /** 客户端需要一个静态方法初始化策划配置. 客户端只需要策划配置裸数据, 不需要 IOC 相关类 */
    public static void initCacheFromClient() {
        initCache0(CM.playerBeanList, CM.playerMoneyBeanPOList, Lists.newArrayList(), Collections.emptyList());
        //        initCache0(Collections.emptyList(), Collections.emptyList());
    }

    public static void initCache0(List<PlayerBeanVO> playerBeanList
        , List<PlayerMoneyBeanPO> playerMoneyBeanPOList, List<PlayerBeanVO> avgPlayerBeanList, List<SystemActivePO> activePoList) {

        /**
         * 注意在配置加载的时候，假如需要调用到其他配置的数据，
         * 请在其他配置加载完毕后再执行当前配置的加载。避免配置为空的情况
         *
         * */

        //初始化全局键值配置
        ConfigConsole.init(CM.GLOBAL_CONFIGS);

        //装备
        EquiConsole.init(CM.EQUI_LIST, CM.EQUI_UP_STRLV_LIST, CM.EQUI_UP_LV_LIST, CM.EQUI_UP_QUA_LIST, CM.EQUI_CLOTHES_QUA_LIST, CM.EQUI_Refresh_List);

        //头像
        LogoConsole.init(CM.logoLvList, CM.logoQuaList, CM.playerMap);

        //主线赛程
        StageConsole.init(CM.stageList);

        //初始化球员配置
        PlayerConsole.init(playerBeanList, playerMoneyBeanPOList, avgPlayerBeanList);

        // 道具，依赖球员
        PropConsole.init(CM.propList, PlayerConsole.getPlayerBeanList(), CM.itemDrops, CM.itemConerts);

        // 球星卡
        PlayerCardConsole.init(CM.Player_Card_Group, CM.Player_Card_Composite, CM.Player_Card_StarLvExp, CM.Player_Card_Grade_Cap);

        //战术
        TacticsConsole.init();

        //NPC
        NPCConsole.init();

        //掉落
        DropConsole.init(CM.DROP);

        //经验
        GradeConsole.init(CM.teamExpList);
        TeamConsole.init();
        //
        PropConsole.initAbility();

        DraftConsole.init(CM.draftList);

        KnockoutMatchConsole.init(CM.matchList, CM.matchRankAwardList);
        BattleConsole.init();
        BattleCustomConsole.init();
        LeagueConsole.init();

        LeagueArenaConsole.init();

        ShopConsole.init();

        GradeConsole.init(CM.teamExpList);

        TaskConsole.init();

        EmailConsole.init(CM.emailViewBeanList);

        StreetBallConsole.init(CM.streetBallList);

        TrainConsole.init();

        GymConsole.init();

        TradeConsole.init();

        SignConsole.init();

        SkillConsole.init();

        CoachConsole.init();

        VipConsole.init();

        GameConsole.init();

        ArchiveConsole.init();
        MainMatchConsole.init();

        HonorMatchConsole.init();
        
        HappySevenDayConsole.init();

        RankedMatchConsole.init();
        CDKeyConsole.init();

        LeagueGroupWarConsole.init();

        PlayerExchangeConsole.init(CM.playerExchangeCfg);
        AllStarConsole.init();
        ArenaConsole.init();
        ModuleConsole.init();
        // 保存活动数据到数据库，注意这里只执行一次即可
        SystemActiveConsole.init(activePoList, CM.systemActiveList, CM.systemActiveCfgList);
        AIConsole.init();
        
        StarletConsole.init();
        
        //降薪球员配置数据
        PlayerCutsConsole.init(CM.playerCutsBeanList);
        
        //答题活动
        AnswerQuestionConsole.init(CM.answerQuestionCfgList);
        
        //---------------------------------------------------
        //InstanceFactory.get().resetConfig();
        // 清理CM对象释放内存
        //CM.clear();
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Cache.getOrder();
    }

    @Override
    public void instanceAfter() {
        /**
         * 加载配置
         */
        CM.setJedisUtil(redis);
        if (GameSource.pool.equals("gm")) {
            log.info("==================GM节点不读Excel===================");
            return;
        }
        CM.reload();
        initCache();
        validateCache();
    }

    public static void validateCache() {
        try {
            Class<CM> cmclz = CM.class;
            ImmutableSet<ClassInfo> classes = ClassPath.from(cmclz.getClassLoader())
                .getTopLevelClasses(cmclz.getPackage().getName());
            for (ClassInfo ci : classes) {
                Class<?> clz = ci.load();
                boolean canValidate = false;
                for (Class<?> dclz : clz.getInterfaces()) {
                    if (dclz.isAssignableFrom(ValidateBean.class)) {
                        canValidate = true;
                        break;
                    }
                }
                if (!canValidate) {
                    continue;
                }
                log.info("validate excel console {}", clz.getName());
                ValidateBean vb = (ValidateBean) clz.newInstance();
                vb.validate();

            }
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        log.info("validate excel done.");
    }

}

package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.console.ConfigConsole;
import com.ftkj.console.GradeConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EStatus;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.ScoutPB.ScoutMain;
import com.ftkj.proto.ScoutPB.ScoutPlayerData;
import com.ftkj.proto.ScoutPB.ScoutRollData;
import com.ftkj.proto.ScoutPB.ScoutRollMain;
import com.ftkj.proto.TeamBeSignPB.BeSignPlayer;
import com.ftkj.proto.TeamBeSignPB.TeamBeSignData;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.ParamTestContext;
import com.ftkj.x3.client.task.helper.GmHelper;
import com.ftkj.x3.client.task.helper.PropHelper;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.x3.client.task.logic.player.PlayerClient;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.xxs.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 球探.
 *
 * @author luch
 */
@Component
public class ScoutClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(ScoutClient.class);
    @Autowired
    private PropHelper propHelper;
    @Autowired
    private GmHelper gm;
    @Autowired
    private PlayerClient playerClient;
    @Autowired
    private TeamClient teamClient;

    public static void main(String[] args) {
        new ScoutClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        ClientUser cu = uc.user();
        ScoutTestContext tc = module();
        //        ScoutTestContext tc = new ScoutTestContext(ScoutTestContext.robotParam());
        tc.getBase().star();
        Ret ret = moduleTest(uc, cu, tc);
        tc.getBase().end();
        log.debug("test stats {}", tc.getBase().stats());
        return ret;
    }

    private Ret moduleTest(UserClient uc, ClientUser cu, ScoutTestContext context) {
        teamClient.upgradeLev(uc, cu, GradeConsole.getMaxLv());

        ScoutRollMain srm = showRollPlayer(uc);
        log.info("tid {} rollPlayer ScoutRollMain {}", cu.tid(), shortDebug(srm));
        for (ScoutRollData srd : srm.getRollListList()) {
            rollPlayer(uc, cu, srd.getType(), 10);
        }
        Ret ret = signRandomPlayer(uc, cu, context);
        if (ret.isErr()) {
            return ret;
        }
        return ret;
    }

    /** 机器人测试功能 */
    public Ret robotTest(UserClient uc, ClientUser cu, ScoutTestContext context) {
        return signRandomPlayer(uc, cu, context);
    }

    /** 随即签约球员 */
    private Ret signRandomPlayer(UserClient uc, ClientUser cu, ScoutTestContext context) {
        gm.gmAddVip(uc, cu, 15);
        ScoutMain sm = showScoutMain(uc);
        if (hasSign(sm)) {
            Resp<ScoutMain> resp = refreshScout(uc, cu);
            context.sleep();
            sm = resp.getObj();
        }
        log.info("tid {} showScoutMain {}", cu.tid(), shortDebug(sm));
        //sign player (fire player)
        TeamBeSignData beSignData = beSignList(uc);
        final int psize = cu.getPlayers().getStoragePlayerSize();
        int storegeSizeCfg = playerClient.getStorageMaxSize(cu);
        log.info("tid {} storege size {}/{}", cu.tid(), psize, storegeSizeCfg);
        if (storegeSizeCfg - psize < beSignData.getBeSignListCount()) {
            Ret ret = playerClient.firePlayerWithNum(uc, cu, beSignData.getBeSignListCount(), context.getBase());
            if (ret.isErr()) {
                return ret;
            }
            context.sleep();
        }
        final int srcpsize = cu.getPlayers().getStoragePlayerSize();
        int signNum = Math.min(storegeSizeCfg, beSignData.getBeSignListCount());
        for (int i = 0; i < signNum; i++) {
            BeSignPlayer bsp = beSignData.getBeSignList(i);
            log.info("tid {} star signPlayer {}", cu.tid(), shortDebug(bsp));
            Ret ret = signPlayer(uc, cu, bsp);
            if (ret.isErr()) {
                return ret;
            }
            context.sleep();
        }
        log.info("tid {} scout. signPlayer done. storege size {} -> {}", cu.tid(), srcpsize, cu.getPlayers().getStoragePlayerSize());
        //make card
        int makeCardNum = context.getBase().getTlr().nextInt(1, beSignData.getBeSignListCount());
        for (int i = signNum; i < makeCardNum; i++) {
            BeSignPlayer bsp = beSignData.getBeSignList(i);
            log.info("tid {} star makeCard {}", cu.tid(), shortDebug(bsp));
            Ret ret = makeCard(uc, cu, bsp);
            if (ret.isErr()) {
                return ret;
            }
            context.sleep();
        }
        log.info("tid {} scout. makecard done. num {}", cu.tid(), makeCardNum);
        return succ();
    }

    private boolean hasSign(ScoutMain sm) {
        for (ScoutPlayerData spd : sm.getPlayerListList()) {
            if (spd.getStatus() == EStatus.ScoutSign.getId()) {
                return true;
            }
        }
        return false;
    }

    public ScoutRollMain showRollPlayer(UserClient uc) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.ScoutManager_showRollPlayer));
        return parseFrom(ScoutRollMain.getDefaultInstance(), msg);
    }

    public ScoutMain showScoutMain(UserClient uc) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.ScoutManager_showScoutMain));
        return parseFrom(ScoutMain.getDefaultInstance(), msg);
    }

    public Ret rollPlayer(UserClient uc, ClientUser cu, int type, int count) {
        int propId = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Prop_C);
        if (type == roll_A) {
            propId = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Prop_A);
        } else if (type == roll_B) {
            propId = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Prop_B);
        } else if (type == roll_C) {
        }

        if (!cu.hasProp(propId, count)) {
            propHelper.gmAddProp(uc, propId, count);
        }
        Message msg = uc.writeAndGet(createReq(ServiceCode.ScoutManager_rollPlayer, type, count));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} roll player type {} count {} fail. ret {}", uc.tid(), type, count, ret(resp));
            return ret(resp);
        }
        log.info("tid {} roll player type {} count {} succ", uc.tid(), type, count);
        return succ();
    }

    public TeamBeSignData beSignList(UserClient uc) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.BeSignManager_List));
        TeamBeSignData resp = parseFrom(TeamBeSignData.getDefaultInstance(), msg);
        log.info("tid {} beSignList count {} succ", uc.tid(), resp.getBeSignListCount());
        return resp;
    }

    public Ret signPlayer(UserClient uc, ClientUser cu, BeSignPlayer bsp) {
        if (cu.getMoney().getGold() < bsp.getPrice() * 2) {
            uc.writeAndGet(uc.gmAddMoney(0, bsp.getPrice() * 2, 0));
        }
        Message msg = uc.writeAndGet(createReq(ServiceCode.BeSignManager_Sign, bsp.getId()));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} signPlayer {} fail. ret {}", uc.tid(), bsp.getId(), ret(resp));
            return ret(resp);
        }
        log.info("tid {} signPlayer {} {} succ", uc.tid(), bsp.getId(), bsp.getPlayerId());
        return succ();
    }

    public Ret makeCard(UserClient uc, ClientUser cu, BeSignPlayer bsp) {
        if (cu.getMoney().getGold() < bsp.getPrice() * 2) {
            uc.writeAndGet(uc.gmAddMoney(0, bsp.getPrice() * 2, 0));
        }
        Message msg = uc.writeAndGet(createReq(ServiceCode.BeSignManager_oneKeyMakeCard, bsp.getId()));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} makeCard {} fail. ret {}", uc.tid(), bsp.getId(), ret(resp));
            return ret(resp);
        }
        log.info("tid {} makeCard {} {} succ", uc.tid(), bsp.getId(), bsp.getPlayerId());
        return succ();
    }

    //    public Ret oldSignPlayer(UserClient uc, ClientUser cu, ScoutPlayerData spd) {
    //        Message msg = uc.writeAndGet(createReq(ServiceCode.ScoutManager_signPlayer, spd.getIndex()));
    //        DefaultData resp = parseFrom(msg);
    //        if (uc.isError(resp)) {
    //            log.warn("tid {} signPlayer index {} fail. ret {}", uc.tid(), spd.getIndex(), ret(resp));
    //            return ret(resp);
    //        }
    //        log.info("tid {} signPlayer index {} succ", uc.tid(), spd.getIndex());
    //        return succ();
    //    }

    /** 刷新球员 */
    public Resp<ScoutMain> refreshScout(UserClient uc, ClientUser cu) {
        PropBean pb = PropConsole.getProp(ConfigConsole.getIntVal(EConfigKey.SCOUT_NUM));
        if (!cu.hasProp(pb.getPropId(), 1)) {
            propHelper.gmAddProp(uc, pb.getPropId(), 1);
        }
        Message msg = uc.writeAndGet(createReq(ServiceCode.ScoutManager_refreshScout));
        ScoutMain sm = parseFrom(ScoutMain.getDefaultInstance(), msg);
        log.info("tid {} refreshScout succ", cu.getTid());
        return succ(sm);
    }

    private static final int roll_A = 1;
    private static final int roll_B = 2;
    private static final int roll_C = 3;

    public static ScoutTestContext module() {
        return new ScoutTestContext(ScoutTestContext.moduleParam());
    }

    public static final class ScoutTestContext extends ParamTestContext {
        public ScoutTestContext(TestParams base) {
            super(base);
        }

    }
}

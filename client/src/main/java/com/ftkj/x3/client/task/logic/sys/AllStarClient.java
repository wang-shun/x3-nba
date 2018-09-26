package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.cfg.AllStarBean;
import com.ftkj.console.AllStarConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.match.SysAllStar;
import com.ftkj.proto.AllStarPb.AllStarAllResp;
import com.ftkj.proto.AllStarPb.AllStarNpcResp;
import com.ftkj.proto.AllStarPb.AllStarTeamRankResp;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientTeamAllStar;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * 挑战全明星 client.
 *
 * @author luch
 */
@Component
public class AllStarClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(AllStarClient.class);
    @Autowired
    private TeamClient teamClient;

    public static void main(String[] args) {
        new AllStarClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        ClientUser cu = uc.user();
        return moduleTest(uc, cu);
    }

    public Ret moduleTest(UserClient uc, ClientUser cu) {
        return succ();
    }

    public AllStarBean getEnable() {
        LocalTime now = LocalTime.now();
//        for (AllStarBean asb : AllStarConsole.getAllStars().values()) {
//            if (asb.canMatch(now)) {
//                return asb;
//            }
//        }
        return null;
    }

    /** 获取信息 */
    public Ret info(UserClient uc, ClientUser cu, int rid) {
        AllStarBean asb = AllStarConsole.getBean(rid);
        teamClient.upgradeLev(uc, cu, asb.getTeamLev());
        return uc.reqCommon(uc, cu, ServiceCode.AllStar_Info, ServiceCode.AllStar_Info_Push, rid);
    }

    /** 开始比赛 */
    public Ret startMatch(UserClient uc, ClientUser cu, int rid) {
        return uc.reqCommon(uc, cu, ServiceCode.AllStar_Start_Match, 0, rid);
    }

    public void infoPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        AllStarAllResp resp = parseFrom(AllStarAllResp.getDefaultInstance(), msg);
        if (isSimpleLog()) {
            log.info("tid {} allstar info push. {}", uc.tid(), shortDebug(resp.getTeam()));
        } else {
            log.info("tid {} allstar info push. {}", uc.tid(), shortDebug(resp));
        }
        cu.setSysAllStar(createSys(resp.getNpc()));
        cu.setAllStar(createTeam(resp.getTeam()));
    }

    private ClientTeamAllStar createTeam(AllStarTeamRankResp resp) {
        ClientTeamAllStar as = new ClientTeamAllStar();
        as.setRank(resp.getRank());
        as.setHp(resp.getHp());
        return as;
    }

    private SysAllStar createSys(AllStarNpcResp resp) {
        SysAllStar sas = new SysAllStar();
        sas.setId(resp.getId());
//        sas.setAct(EActionType.convertByType(resp.getAct()));
        sas.setNpcLev(resp.getLev());
        sas.setHp(resp.getHp());
        return sas;
    }

}

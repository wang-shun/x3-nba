package com.ftkj.x3.client.task.logic.team;

import com.ftkj.cfg.TeamExpBean;
import com.ftkj.cfg.TeamNumBean;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.console.GradeConsole;
import com.ftkj.console.TeamConsole;
import com.ftkj.console.VipConsole;
import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.TeamDayNumType;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.TeamPB.TeamInfoData;
import com.ftkj.proto.TeamPB.TeamMoneyData;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientBuff;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.ClientPbUtil;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.helper.PropHelper;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.xxs.net.Message;
import com.ftkj.xxs.util.WrapInt;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author luch
 */
@Component
public class TeamClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(TeamClient.class);
    @Autowired
    private PropHelper propHelper;

    public static void main(String[] args) {
        new TeamClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        int prid = propHelper.randomProp(uc, 9999);
        propHelper.gmAddProp(uc, prid, 23);
        return succ();
    }

    public void upgradeExp(UserClient uc, ClientUser cu, int exp) {
        uc.writeAndGet(uc.gmAddMoney(0, 0, exp));
    }

    public void upgradeLev(UserClient uc, ClientUser cu, int finalLev) {
        if (cu.getTeam().getLevel() >= finalLev) {
            return;
        }
        TeamExpBean teb = GradeConsole.getTeamExpBean(finalLev);
        int addExp = teb.getTotal() - cu.getMoney().getExp();
        uc.writeAndGet(uc.gmAddMoney(0, 0, addExp));
    }

    public Ret gmSetDayNum(UserClient uc, ClientUser cu, TeamDayNumType type, int finalNum) {
        Message msg = uc.writeAndGet(createGM(GmCommand.Team_Day_Num_Set, type.getType(), finalNum));
        return ret(msg);
    }

    public Ret gmSetBuyNum(UserClient uc, ClientUser cu, TeamNumType type, int finalNum) {
        Message msg = uc.writeAndGet(createGM(GmCommand.Team_Num_Set, type.getType(), finalNum));
        return ret(msg);
    }

    public interface GmSetSucc {
        void apply(TeamNumBean tnb);
    }

    /** gm命令重置购买次数, 并且增加下一次购买需要的货币 */
    public Ret gmSetBuyNumAndAddCurrency(UserClient uc, ClientUser cu, TeamNumType type, int currNum, int finalNum,
                                         GmSetSucc gmSetSucc) {
        TeamNumBean tnb = TeamConsole.getNums(type);
        int nextNum = currNum + 1;
        if (currNum >= tnb.getMaxNum()) {
            Message msg = uc.writeAndGet(createGM(GmCommand.Team_Num_Set, type.getType(), finalNum));
            if (uc.isError(msg)) {
                return ret(msg);
            }
            nextNum = 1;
            gmSetSucc.apply(tnb);
        }
        Integer numMoney = tnb.getCurrency(nextNum);
        if (!cu.getMoney().hasMoney(tnb.getCurrencyType(), numMoney)) {
            Message msg = uc.writeAndGet(uc.gmCurrency(tnb.getCurrencyType(), numMoney));
            if (uc.isError(msg)) {
                return ret(msg);
            }
        }
        return succ();
    }

    public static void moneyChange(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        TeamMoneyData tmd = parseFrom(TeamMoneyData.getDefaultInstance(), msg);
        if (log.isDebugEnabled()) {
            log.debug("tid {} money change {}", cu.getTid(), shortDebug(tmd));
        }
        ClientPbUtil.updateMoney(tmd, cu.getMoney());
    }

    public static void pushTeamLev(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        DefaultData resp = parseFrom(msg);
        if (log.isDebugEnabled()) {
            log.debug("tid {} lev change {}", cu.tid(), shortDebug(resp));
        }
        if (resp.getBigNum() > 0) {
            cu.getTeam().setLevel((int) resp.getBigNum());
        }
    }

    public int getBuffSum(ClientUser cu, EBuffType buffType) {
        WrapInt sum = new WrapInt();
        getBuffs(cu, buffType, buff -> sum.add(buff.getParamSum()));
        return sum.get();
    }

    public void getBuffs(ClientUser cu, EBuffType buffType, Consumer<ClientBuff> consumer) {
        for (EBuffKey buffKey : EBuffKey.values()) {
            ClientBuff buff = null;
            if (buffKey == EBuffKey.VIP加成) {
                buff = getVipBuff(cu, buffType);
            } else if (cu.getBuffs().containsKey(buffKey.getStartID() + buffType.getId())) {
                buff = cu.getBuffs().get(buffKey.getStartID() + buffType.getId());
            }
            if (buff != null && buff.getEndTime().isAfterNow()) {
                consumer.accept(buff);
            }
        }
    }

    private ClientBuff getVipBuff(ClientUser cu, EBuffType type) {
        if (!cu.getVip().isVip()) {
            return null;
        }
        int value = VipConsole.getVipLevelBean(cu.getVip().getLevel()).getBuffMap().get(type.getId()).get("v");
        // 加成值
        return new ClientBuff(type.getId() + EBuffKey.VIP加成.getStartID(), Collections.singletonList(value), DateTime.now().plusDays(1));
    }

    /** 查看指定球队的战力和阵容 */
    public TeamInfoData viewTeamInfoAndCaps(UserClient uc, ClientUser cu, long teamId) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.TeamManger_viewTeamInfo, teamId));
        TeamInfoData resp = parseFrom(TeamInfoData.getDefaultInstance(), msg);
        log.info("viewinfo tid {} ttid {} cap {}", uc.tid(), resp.getTeamInfo().getTeamId(), resp.getTotalCap());
        if (!isSimpleLog()) {
            log.debug("viewinfo tid {} tid {} team caps {} ", uc.tid(), resp.getTeamInfo().getTeamId(), shortDebug(resp.getCapListList()));
        }
        return resp;
    }
}

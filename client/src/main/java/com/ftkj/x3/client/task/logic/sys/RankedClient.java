package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.cfg.RankedMatchTierBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.RankedMatchConsole;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.match.RankedMatch;
import com.ftkj.manager.match.RankedMatch.Season;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.RankedMatchPb.RMatchAllResp;
import com.ftkj.proto.RankedMatchPb.RMatchMedalRankResp;
import com.ftkj.proto.RankedMatchPb.RMatchSeasonResp;
import com.ftkj.proto.RankedMatchPb.RMatchTeamResp;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.BitUtil;
import com.ftkj.x3.client.model.ClientRankedMatch;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.xxs.client.net.XxsUserClient.AsyncWaitLatch;
import com.ftkj.xxs.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 跨服天梯赛 client.
 *
 * @author luch
 */
@Component
public class RankedClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(RankedClient.class);

    public static void main(String[] args) {
        new RankedClient().run();
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

    public Ret info(UserClient uc, ClientUser cu) {
        AsyncWaitLatch awl = uc.createAysnWaitLatch(code(ServiceCode.RMatch_Info_Push));
        Message msg = uc.writeAndGet(createReq(ServiceCode.RMatch_Info));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} rmatch info. fail {}", cu.tid(), ret(resp));
            return ret(resp);
        }
        uc.waitAysnLatchRelease(awl);
        log.info("tid {} rmatch info succ", uc.tid());
        return succ();
    }

    public Ret rankList(UserClient uc, ClientUser cu) {
        RankedMatch rm = cu.getRankedMatch();
        int rating = rm.getCurrSeason() != null ? rm.getCurrSeason().getRating() :
                ConfigConsole.global().rMatchInitRating;
        RankedMatchTierBean tb = RankedMatchConsole.getTierByRating(rating);
        log.info("rating {} tier {}", rating, tb);
        if (tb == null) {
            return Ret.convert(ErrorCode.RMatch_Tier_Bean.code);
        }
        return rankList(uc, cu, tb.getMedalId());
    }

    public Ret rankList(UserClient uc, ClientUser cu, int medalId) {
        return uc.reqCommon(uc, cu, ServiceCode.RMatch_Rank, ServiceCode.RMatch_Rank_Push, medalId);
    }

    private RankedMatch createRM(long tid, RMatchAllResp resp) {
        ClientRankedMatch rm = new ClientRankedMatch();
        rm.setTeamId(tid);
        RMatchTeamResp tr = resp.getTeam();
        for (Integer faid : tr.getFirstAwardList()) {
            rm.setFirstAward(BitUtil.addBit(rm.getFirstAward(), 1 << faid));
        }
        rm.setTotalMatchCount(tr.getTotalMatchCount());
        rm.setTotalWinCount(tr.getTotalWinCount());
        rm.setWinningStreak(tr.getWinningStreak());
        rm.setLastMatchTime(tr.getLastMatchTime());
        rm.setDailyReward(tr.getDailyReward());
        rm.setTempInPool(tr.getInPool());

        rm.setCurrSeason(createSeason(tr.getCurrSeason()));
        rm.setCurrSeason(createSeason(tr.getPreSeason()));
        return rm;
    }

    private Season createSeason(RMatchSeasonResp sr) {
        Season s = new Season();
        s.setId(sr.getId());
        s.setTierId(sr.getTierId());
        s.setRank(sr.getRank());
        s.setRating(sr.getRating());
        s.setMatchCount(sr.getMatchCount());
        s.setWinCount(sr.getWinCount());
        s.setWinningStreakMax(sr.getWinningStreakMax());
        return s;
    }

    public Ret joinPool(UserClient uc, ClientUser cu) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.RMatch_Join_Pool));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} joinPool. fail {}", uc.tid(), ret(resp));
            return ret(resp);
        }
        log.info("tid {} joinPool succ", uc.tid());
        return succ();
    }

    public void infoPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        RMatchAllResp resp = parseFrom(RMatchAllResp.getDefaultInstance(), msg);
        if (isSimpleLog()) {
            RMatchTeamResp tresp = resp.getTeam();
            log.info("tid {} rmatch info push. {}", uc.tid(), shortDebug(tresp));
        } else {
            log.info("tid {} rmatch info push. {}", uc.tid(), shortDebug(resp));
        }
        RankedMatch rm = createRM(cu.tid(), resp);
        cu.setRankedMatch(rm);
    }

    public void rankListPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        RMatchMedalRankResp resp = parseFrom(RMatchMedalRankResp.getDefaultInstance(), msg);
        if (isSimpleLog()) {
            log.info("tid {} rankListPush. {}", uc.tid(), resp.getTeamsCount());
        } else {
            log.info("tid {} rankListPush. {}", uc.tid(), shortDebug(resp));
        }
    }
}

package com.ftkj.x3.client.task.logic.player;

import com.ftkj.cfg.PlayerGradeBean;
import com.ftkj.console.GradeConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EPlayerStorage;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.prop.bean.PropSimpleBean;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.PlayerPB.PlayerSimpleData;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.console.ClientConsole;
import com.ftkj.x3.client.model.ClientPlayer;
import com.ftkj.x3.client.model.ClientPlayerGrade;
import com.ftkj.x3.client.model.ClientTeamPlayer;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.ClientPbUtil;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.ParamTestContext;
import com.ftkj.x3.client.task.TestContext.TestParams;
import com.ftkj.x3.client.task.helper.PropHelper;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.xxs.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 模板client.
 *
 * @author luch
 */
@Component
public class PlayerClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(PlayerClient.class);
    @Autowired
    private ClientConsole console;
    @Autowired
    private PropHelper propHelper;
    @Autowired
    private TeamClient teamClient;

    public static final int MAX_NUM = 998;

    public static void main(String[] args) {
        new PlayerClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        ClientUser cu = uc.user();
        return moduleTest(uc, cu);
    }

    public Ret moduleTest(UserClient uc, ClientUser cu) {
        PlayerTestContext context = module();
        for (int i = 0; i < 10; i++) {
            log.info("========== loop {} ===========", i);
            List<Integer> pids = getLineupShufflePids(cu);
            Ret ret = randomTest(uc, cu, pids, context);
            if (ret.isErr()) {
                return ret;
            }
        }
        return succ();
    }

    /** 机器人测试功能 */
    public Ret robotPlayerTest(UserClient uc, ClientUser cu, PlayerTestContext context) {
        List<Integer> pids = getLineupShufflePids(cu);
        return randomTest(uc, cu, pids, context);
    }

    /** 机器人测试功能 */
    public Ret randomTest(UserClient uc, ClientUser cu, List<Integer> lineupPids, PlayerTestContext context) {
        ClientPlayer p = cu.getPlayers().getLineup(lineupPids.get(0));
        Ret ret = addLev(uc, cu, p.getPlayerRid(), context.getTlr().nextInt(context.addLevMin, context.addLevMax));
        if (ret.isErr()) {
            return ret;
        }
        context.sleep();
        ret = randomUpdateLineup(uc, cu, context.getTlr(), lineupPids);
        if (ret.isErr()) {
            return ret;
        }
        return succ();
    }

    /** 随机交换球员 */
    public Ret randomUpdateLineup(UserClient uc, ClientUser cu, ThreadLocalRandom tlr, List<Integer> lineupPids) {
        if (lineupPids.size() < 2) {
            return succ();
        }
        int pid1 = lineupPids.get(0);
        int pid2 = lineupPids.get(1);
        ClientPlayer p1 = cu.getPlayers().getLineup(pid1);
        ClientPlayer p2 = cu.getPlayers().getLineup(pid2);

        if (p1.getLineupPos() == EPlayerPosition.NULL || p2.getLineupPos() == EPlayerPosition.NULL) {
            boolean storage = tlr.nextBoolean();
            if (storage && !cu.getPlayers().getStoragePlayers().isEmpty()) {
                ClientPlayer lpPlayer = p1.getLineupPos() == EPlayerPosition.NULL ? p1 : p2;
                int storagePlayerId = cu.getPlayers().getStoragePlayers().lastKey();
                return changeStore(uc, cu, lpPlayer.getId(), storagePlayerId);
            }
        }
        return updateLineup(uc, cu, pid1, pid2);
    }

    public List<Integer> getLineupShufflePids(ClientUser cu) {
        List<Integer> pids = new ArrayList<>(cu.getPlayers().getLineupSize());
        for (ClientPlayer p : cu.getPlayers().getLineups().values()) {
            if (p.getLineupPos() == EPlayerPosition.NULL) {
                pids.add(p.getId());
            }
        }
        Collections.shuffle(pids);
        return pids;
    }

    /** 交换球员位置 */
    public Ret updateLineup(UserClient uc, ClientUser cu, int pid1, int pid2) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.Player_Tran_Position, pid1, pid2));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} 交换球员位置, pid1 {} pid2 {} fail. ret {}", cu.tid(), pid1, pid2, ret(resp));
            return ret(resp);
        }
        ClientPlayer p1 = cu.getPlayers().getLineup(pid1);
        ClientPlayer p2 = cu.getPlayers().getLineup(pid2);
        EPlayerPosition p1pos = p1.getLineupPos();
        EPlayerPosition p2pos = p2.getLineupPos();
        log.info("tid {} 交换球员位置, p1 {} {} pos {} p2 {} {} pos {} succ", cu.tid(), p1.getPlayerRid(), pid1, p1pos, p2.getPlayerRid(), pid2, p2pos);
        p1.setLineupPos(p2pos);
        p2.setLineupPos(p1pos);
        return succ();
    }

    /**
     * 仓库和阵容位置交替.
     * <pre>
     * 当p1_id为-1时，是从仓库取出到阵容
     * 当p2_id为-1时，是从阵容放入到仓库
     * 当他两都不为-1时，是仓库和阵容球员的替换
     * </pre>
     *
     * @param pid1        阵容的球员ID，唯一ID, 不是配置id
     * @param storagePid2 仓库的球员ID，唯一ID, 不是配置id
     */
    public Ret changeStore(UserClient uc, ClientUser cu, int pid1, int storagePid2) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.Player_Tran_Store, pid1, storagePid2));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} 球员阵容和仓库位置交替, pid1 {} storagePid2 {} fail. ret {}", cu.tid(), pid1, storagePid2, ret(resp));
            return ret(resp);
        }
        ClientPlayer p1 = cu.getPlayers().getLineup(pid1);
        ClientPlayer p2 = cu.getPlayers().getStoragePlayer(storagePid2);
        log.info("tid {} 球员阵容和仓库位置交替, pid1 {} pos {} storagePid2 {} succ", cu.tid(), pid1, p1 != null ? p1.getLineupPos() : "", storagePid2);
        if (p1 != null && p2 != null) {// 交换位置
            EPlayerPosition tranPos = p1.getLineupPos();
            lineupToStorage(cu, pid1);
            storageToLineup(cu, storagePid2, tranPos);
        } else if (p1 != null) {  // 放入仓库
            lineupToStorage(cu, pid1);
        } else if (p2 != null) {// 放入阵容
            storageToLineup(cu, storagePid2, EPlayerPosition.NULL);
        }
        return succ();
    }

    private void storageToLineup(ClientUser cu, int storagePid2, EPlayerPosition tranPos) {
        ClientPlayer p2 = cu.getPlayers().getStoragePlayers().remove(storagePid2);
        p2.setStorage(EPlayerStorage.阵容.getType());
        p2.setLineupPos(tranPos);
        cu.getPlayers().addLineups(p2);
    }

    private void lineupToStorage(ClientUser cu, int pid1) {
        ClientPlayer p1 = cu.getPlayers().getLineups().remove(pid1);
        p1.setStorage(EPlayerStorage.仓库.getType());
        p1.setLineupPos(EPlayerPosition.NULL);
        cu.getPlayers().addStoragePlayers(p1);
    }

    /**
     * @param playerRid {@link ClientPlayer#getPlayerRid()}
     */
    public Ret addLev(UserClient uc, ClientUser cu, int playerRid, int addLev) {
        PlayerBean pb = PlayerConsole.getPlayerBean(playerRid);
        if (pb == null) {
            return Ret.clientError("球员配置 %s 不存在", playerRid);
        }
        ClientPlayerGrade pg = cu.getPlayers().getGradeOrDefault(playerRid, prid -> new ClientPlayerGrade());
        log.info("tid {} player {} add lev {}, old lev {}", cu.tid(), playerRid, addLev, pg.getGrade());
        return upgradeLev(uc, cu, playerRid, Math.min(pg.getGrade() + addLev, console.getPlayer().getMaxGrade()));
    }

    /**
     * 升级阵容中的球员等级
     */
    public Ret upgradeLineupLev(UserClient uc, ClientUser cu, int finalLev) {
        for (ClientPlayer pr : cu.getPlayers().getLineups().values()) {
            Ret ret = upgradeLev(uc, cu, pr.getPlayerRid(), finalLev);
            if (ret.isErr()) {
                return ret;
            }
        }
        return succ();
    }

    public Ret fillLineupPlayer(UserClient uc, ClientUser cu) {
        return fillLineupPlayer(uc, cu, cu.getTeam().getLineupCount() + 5);
    }

    public Ret fillLineupPlayer(UserClient uc, ClientUser cu, int maxNum) {
        //        final int maxNum = ConfigConsole.getIntVal(EConfigKey.Max_Player_Count);
        ClientTeamPlayer tp = cu.getPlayers();
        final int lpsize = tp.getLineupSize();
        if (lpsize >= maxNum) {
            return succ();
        }
        final int fillNum = maxNum - lpsize;
        log.info("fill lineup. tid {} num {}", cu.tid(), fillNum);
        int num = 0;
        for (ClientPlayer pr : tp.getStoragePlayers().values()) {//从仓库移动到阵容
            if (tp.getLineupByPrid(pr.getPlayerRid()) != null) {
                continue;
            }
            Ret ret = changeStore(uc, cu, -1, pr.getId());
            if (ret.isErr()) {
                return ret;
            }
            num++;
            if (num >= fillNum) {
                break;
            }
        }
        if (num > 0) {
            log.info("fill lineup. tid {} move from {} num store player to lineup", cu.tid(), num);
        }
        if (num >= fillNum) {
            return succ();
        }
        final int numss = num;
        for (Integer prid : console.getPlayer().infiniteRandomAllPids()) {//gm命令添加新球员到阵容
            if (tp.getLineupByPrid(prid) != null) {
                continue;
            }
            Ret ret = gmAddPlayer(uc, cu, prid);
            if (ret.isErr()) {
                return ret;
            }
            ClientPlayer cp = tp.getLineupByPrid(prid);
            if (cp == null) {//不在阵容中,
                cp = tp.getStoragePlayerByPrid(prid);
                if (cp != null) {
                    ret = changeStore(uc, cu, -1, cp.getId());
                    if (ret.isErr()) {
                        return ret;
                    }
                } else {
                    log.warn("fill lineup. tid {} player rid {} pos error", cu.tid(), prid);
                }
            }
            num++;
            if (num >= fillNum) {
                break;
            }
        }
        log.info("fill lineup. tid {} add {} num player by gm", cu.tid(), num - numss);
        return succ();
    }

    private Ret gmAddPlayer(UserClient uc, ClientUser cu, int pid) {
        int price = 0;
        Message msg = uc.writeAndGet(createReq(ServiceCode.GMManager_addPlayer, cu.tid(), pid, price));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} gmAddPlayer {} price {} fail. ret {}", cu.tid(), pid, price, ret(resp));
            return ret(resp);
        }
        log.info("tid {} gmAddPlayer {} price {} succ", cu.tid(), pid, price);
        return succ();
    }

    /**
     * @param playerRid {@link ClientPlayer#getPlayerRid()}
     */
    public Ret upgradeLev(UserClient uc, ClientUser cu, int playerRid, int finalLev) {
        PlayerBean pb = PlayerConsole.getPlayerBean(playerRid);
        if (pb == null) {
            return Ret.clientError("球员配置 %s 不存在", playerRid);
        }
        final ClientPlayerGrade pg = cu.getPlayers().getGradeOrDefault(playerRid, prid -> new ClientPlayerGrade());
        if (pg.getGrade() >= finalLev) {
            return succ();
        }
        teamClient.upgradeLev(uc, cu, finalLev);

        int addexp = 0;
        for (int lev = finalLev; lev >= pg.getGrade(); lev--) {
            PlayerGradeBean pgb = PlayerConsole.getPlayerGradeBean(lev);
            if (pgb == null) {
                return ret("球员 %s 升级 %s 配置不存在", playerRid, lev);
            }
            if (lev == pg.getGrade()) {
                addexp += pgb.getNeedExp() - pg.getExp();
            } else {
                addexp += pgb.getNeedExp();
            }
        }
        log.info("tid {} player {} upgrade to lev {}, old lev {}", cu.tid(), playerRid, finalLev, pg.getGrade());
        return upgradeExp(uc, cu, playerRid, addexp);
    }

    /**
     * @param playerRid {@link ClientPlayer#getPlayerRid()}
     */
    public Ret upgradeExp(UserClient uc, ClientUser cu, int playerRid, int exp) {
        if (exp <= 0) {
            return succ();
        }
        PlayerBean pb = PlayerConsole.getPlayerBean(playerRid);
        if (pb == null) {
            return Ret.clientError("球员配置 %s 不存在", playerRid);
        }
        PropSimpleBean psb = console.getProp().getMaxPlayerExpProp(exp);
        if (psb == null) {
            return ret("球员升级道具配置 %s 不存在");
        }
        int propId = psb.getPropId();
        ClientPlayerGrade pg = cu.getPlayers().getGrade(playerRid);
        if (pg == null) {
            pg = new ClientPlayerGrade();
            cu.getPlayers().addGrade(pg);
        }
        int num = divideAndRemainder(exp, psb.getPlayerExp());
        propHelper.gmAddProp(uc, propId, num);
        log.info("tid {} update playerRid {} curr exp {} add exp {} propid {} num {}",
                cu.tid(), playerRid, pg.getExp(), exp, propId, num);
        final int useCount = num < MAX_NUM ? 1 : divideAndRemainder(num, MAX_NUM);
        for (int i = 0; i < useCount; i++) {
            int useNum = Math.min(num, MAX_NUM);
            Ret ret = levelUpByProp(uc, cu, playerRid, propId, useNum);
            if (ret.isErr()) {
                return ret;
            }
            if (useCount > 1) {
                num = -MAX_NUM;
            }
        }
        return succ();
    }

    public Ret levelUpByProp(UserClient uc, ClientUser cu, int playerRid, int propId, int num) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.PlayerGradeManager_levelUP, playerRid, propId, num));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} player {} levelup fail, propId {} num {}. ret {}", cu.tid(), playerRid, propId, num, ret(resp));
            return ret(resp);
        }
        log.info("tid {} player {} levelup succ, propId {} num {}", cu.tid(), playerRid, propId, num);
        return succ();
    }

    public static int divideAndRemainder(int divisor, int dividend) {
        int num = divisor / dividend;
        if (divisor % dividend > 0) {
            return num + 1;
        }
        return num;
    }

    public Ret firePlayer(UserClient uc, ClientUser cu, TestParams tp) {
        return firePlayerWithNum(uc, cu, cu.getPlayers().getStoragePlayerSize(), tp);
    }

    public Ret firePlayerWithNum(UserClient uc, ClientUser cu, int num, TestParams tp) {
        int storageSize = cu.getPlayers().getStoragePlayerSize();
        int playerSize = cu.getPlayers().getLineupSize();
        if (storageSize <= 0) {
            return Ret.success();
        }
        log.info("tid {} fireplayer num {}/({}+{})", cu.tid(), num, storageSize, playerSize);
        int count = 0;
        for (Integer playerId : cu.getPlayers().getStoragePlayers().keySet()) {
            Ret ret = firePlayer(uc, cu, playerId);
            if (ret.isErr()) {
                return ret;
            }
            count++;
            if (count >= num) {
                break;
            }
            tp.sleep();
        }
        for (ClientPlayer player : cu.getPlayers().getLineups().values()) {
            if (player.getLineupPos() != null && !player.getLineupPos().equals(EPlayerPosition.NULL)) {
                continue;
            }
            Ret ret = changeStore(uc, cu, player.getId(), -1);
            if (ret.isErr()) {
                return ret;
            }
            ret = firePlayer(uc, cu, player.getId());
            if (ret.isErr()) {
                return ret;
            }
            count++;
            if (count >= num) {
                break;
            }
            tp.sleep();
        }
        log.info("tid {} fireplayer num {}/({}+{}) succ {}", cu.tid(), num, storageSize, playerSize, count);
        return Ret.success();

    }

    public Ret firePlayer(UserClient uc, ClientUser cu, int pid) {
        ClientPlayer p = cu.getPlayers().getPlayer(pid);
        if (p == null) {
            log.warn("tid {} fireplayer {} fail, player null. ret", cu.getTid(), pid);
            return succ();
        }
        Message msg = uc.writeAndGet(createReq(ServiceCode.PlayerManager_Fire, pid));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} fireplayer {} {} fail. ret {}", cu.getTid(), pid, p.getPlayerRid(), ret(resp));
            return ret(resp);
        }
        log.info("tid {} fireplayer id {} rid {} succ", cu.getTid(), pid, p.getPlayerRid());
        cu.getPlayers().removePlayer(pid);
        return succ();
    }

    public int getStorageMaxSize(ClientUser cu) {
        int lvSize = GradeConsole.getTeamExpBean(cu.getTeam().getLevel()).getStorage();
        int vipSize = teamClient.getBuffSum(cu, EBuffType.球员仓库上限增加);
        return PlayerConsole.MAX_STORAGE_SIZE + lvSize + vipSize;
    }

    public static void updateStar(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        DefaultData resp = parseFrom(msg);
        ClientPlayerGrade pg = cu.getPlayers().getGrade(resp.getCode());
        if (pg == null) {
            pg = new ClientPlayerGrade();
            cu.getPlayers().addGrade(pg);
        }
        if (log.isDebugEnabled()) {
            log.debug("tid {} player {} updateStar {}", cu.getTid(), resp.getCode(), pg.getGrade() + 1);
        }
        pg.updateStar(1);
    }

    public static void playerChange(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        playerChange0(uc, cu, "main", cu.getPlayers().getLineups(), msg);
    }

    public static void storageChange(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        playerChange0(uc, cu, "storage", cu.getPlayers().getStoragePlayers(), msg);
    }

    public static void playerChange0(UserClient uc, ClientUser cu,
                                     String pos,
                                     Map<Integer, ClientPlayer> players,
                                     ClientRespMessage msg) {
        PlayerSimpleData psd = parseFrom(PlayerSimpleData.getDefaultInstance(), msg);
        if (log.isDebugEnabled()) {
            log.debug("tid {} player {} change {}", cu.getTid(), pos, shortDebug(psd));
        }
        ClientPlayer p = ClientPbUtil.createPlayer(psd);
        ClientPlayer oldp = players.get(p.getId());
        if (oldp != null) {
            log.warn("tid {} old player {} {} != null", cu.getTid(), oldp.getId(), oldp.getPlayerRid());
        }
        cu.getPlayers().addPlayer(players, p);
    }

    public static PlayerTestContext module() {
        return new PlayerTestContext(PlayerTestContext.moduleParam(), 1, 10);
    }

    public static final class PlayerTestContext extends ParamTestContext {
        private final int addLevMin;
        private final int addLevMax;

        public PlayerTestContext(TestParams base, int addLevMin, int addLevMax) {
            super(base);
            this.addLevMin = addLevMin;
            this.addLevMax = addLevMax;
        }
    }
}

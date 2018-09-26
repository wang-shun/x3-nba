package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PlayerExchangeConsole;
import com.ftkj.db.ao.logic.IBeSignPlayerAO;
import com.ftkj.db.domain.BeSignPlayerPO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.besign.BeSignPlayer;
import com.ftkj.manager.besign.TeamBeSignPlayer;
import com.ftkj.manager.logic.log.GameBesignLogManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.scout.ScoutPlayer;
import com.ftkj.manager.system.CheckAPI;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerCardPB.CollectData.Builder;
import com.ftkj.proto.TeamBeSignPB;
import com.ftkj.proto.TeamBeSignPB.TeamBeSignData;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * 待签
 *
 * @author Jay
 */
public class BeSignManager extends BaseManager implements OfflineOperation {
    private Map<Long, TeamBeSignPlayer> beSignMap;
    @IOC
    private IBeSignPlayerAO beSignAO;
    @IOC
    private ScoutManager scountManager;
    @IOC
    private PlayerCardManager playerCardManager;
    @IOC
    private GymManager localArenaManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private PropManager propManager;
    @IOC
    private PlayerManager playerManager;

    @Override
    public void instanceAfter() {
        beSignMap = Maps.newConcurrentMap();
    }

    TeamBeSignPlayer getTeamBeSignPlayer(long teamId) {
        TeamBeSignPlayer teamBeSign = beSignMap.get(teamId);
        if (teamBeSign == null) {
            List<BeSignPlayerPO> list = beSignAO.getBeSignPlayerList(teamId);
            if (list == null) {
                list = Lists.newArrayList();
            }
            teamBeSign = new TeamBeSignPlayer(teamId, list);
            beSignMap.put(teamId, teamBeSign);
            GameSource.checkGcData(teamId);
        }
        return teamBeSign;
    }

    /**
     * 下线回收
     */
    @Override
    public void offline(long teamId) {
        beSignMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        beSignMap.remove(teamId);
    }

    /**
     * 添加待签
     *
     * @param tid 天赋ID
     */
    public void addBeSignPlayer(long teamId, int playerId, int price, int tid, ModuleLog moduleLog) {
        addBeSignPlayer(teamId, playerId, price, tid, false, moduleLog);
    }

    /**
     * 添加待签
     *
     * @param tid  天赋ID
     * @param bind 是否绑定
     */
    public void addBeSignPlayer(long teamId, int playerId, int price, int tid, boolean bind, ModuleLog moduleLog) {
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        BeSignPlayer besign = teamBeSign.addBeSignPlayer(playerId, price, tid, bind);

        RedPointParam param = new RedPointParam(teamId, ERedPoint.待签球员.getId(), 1);
        EventBusManager.post(EEventType.奖励提示, param);
        GameBesignLogManager.Log(teamId, besign.getId(), besign.getPlayerId(), besign.getPrice(), 1, moduleLog);
    }

    public void addBeSignPlayers(long teamId, List<ScoutPlayer> list, ModuleLog moduleLog) {
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        list.forEach(player -> {
            BeSignPlayer besign = teamBeSign.addBeSignPlayer(player.getPlayerId(), player.getPrice(), player.getTalent().getId(), player.isBind());
            GameBesignLogManager.Log(teamId, besign.getId(), besign.getPlayerId(), besign.getPrice(), 1, moduleLog);
        });
        RedPointParam param = new RedPointParam(teamId, ERedPoint.待签球员.getId(), 1);
        EventBusManager.post(EEventType.奖励提示, param);
    }

    /**
     * 待签列表
     */
    @ClientMethod(code = ServiceCode.BeSignManager_List)
    public void getTeamBeSignList() {
        long teamId = getTeamId();
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        TeamBeSignData data = converData(teamId, getTeamBeSignPlayer(teamId), tp);
        sendMessage(data);
    }

    /**
     * 签约
     */
    @ClientMethod(code = ServiceCode.BeSignManager_Sign)
    public void signPlayer(int id) {
        long teamId = getTeamId();
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        if (!teamBeSign.checkBeSignPlayer(id)) {
            log.debug("待签球员不存在{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Besign_1.code).build());
            return;
        }
        //调用签约接口
        BeSignPlayer bs = teamBeSign.getBeSignPlayer(id);
        if (bs.getEndTime().isBeforeNow()) {
            log.debug("待签球员已过期{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Besign_2.code).build());
            return;
        }
        ModuleLog module = ModuleLog.getModuleLog(EModuleCode.待签, "签约");
        ErrorCode ret = scountManager.signPlayer(teamId, bs.getPlayerId(), bs.getPrice(), bs.getTid(), bs.isBind(), module);
        if (ret != ErrorCode.Success) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ret.code).build());
            return;
        }
        taskManager.updateTask(teamId, ETaskCondition.球探签约, 1, PlayerConsole.getPlayerBean(bs.getPlayerId()).getGrade().getGrade());
        teamBeSign.removeBeSign(id);
        GameBesignLogManager.Log(teamId, bs.getId(), bs.getPlayerId(), bs.getPrice(), -1, module);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ret.code).build());
    }

    @ClientMethod(code = ServiceCode.BeSignManager_makeArenaPlayer)
    public void makeArenaPlayer(int id) {
        long teamId = getTeamId();
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        if (!teamBeSign.checkBeSignPlayer(id)) {
            log.debug("待签球员不存在{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Besign_1.code).build());
            return;
        }
        //调用签约接口
        BeSignPlayer beSign = teamBeSign.getBeSignPlayer(id);
        if (beSign.getEndTime().isBeforeNow()) {
            log.debug("待签球员已过期{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Besign_2.code).build());
            return;
        }
        if (!teamMoneyManager.updateTeamMoney(teamId, 0, -beSign.getPrice(), 0, 0, true, ModuleLog.getModuleLog(EModuleCode.待签, ""))) {
            log.debug("经费不足{}", beSign.getPrice());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Besign_2.code).build());
            return;
        }
        int pid = localArenaManager.addPlayerLineup(teamId, beSign.getPlayerId());
        teamBeSign.removeBeSign(id);
        GameBesignLogManager.Log(teamId, beSign.getId(), beSign.getPlayerId(), beSign.getPrice(), -1, ModuleLog.getModuleLog(EModuleCode.待签, "制作Arena球员"));
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(pid + "").build());
    }

    /**
     * 球星卡
     * 一键收集
     */
    @ClientMethod(code = ServiceCode.BeSignManager_oneKeyMakeCard)
    public void oneKeyMakeCard() {
        long teamId = getTeamId();
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        List<Integer> playerIds = Lists.newArrayList();
        if (teamBeSign.getBeSignList().size() < 1) {
            return;
        }
        for (BeSignPlayer beSign : teamBeSign.getBeSignList()) {
            if (beSign.getEndTime().isBeforeNow()) {
                continue;
            }
            playerIds.add(beSign.getPlayerId());
        }
        List<BeSignPlayer> list = teamBeSign.removeBeSignList(teamBeSign.getBeSignList().stream().mapToInt(i -> i.getId()).toArray());
        Builder builder = playerCardManager.markCard(teamId, playerIds.stream().mapToInt(s -> s).toArray());
        for (BeSignPlayer beSign : list) {
            GameBesignLogManager.Log(teamId, beSign.getId(), beSign.getPlayerId(), beSign.getPrice(), -1, ModuleLog.getModuleLog(EModuleCode.待签, "制卡"));
        }
        sendMessage(builder.setCode(ErrorCode.Success.code).build());
    }

    //
    @ClientMethod(code = ServiceCode.BeSignManager_makeCard)
    public void makeCard(int id) {
        long teamId = getTeamId();
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        if (!teamBeSign.checkBeSignPlayer(id)) {
            log.debug("待签球员不存在!{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        BeSignPlayer beSign = teamBeSign.getBeSignPlayer(id);
        int playerId = beSign.getPlayerId();
        teamBeSign.removeBeSign(id);
        Builder builder = playerCardManager.markCard(teamId, new int[]{playerId});
        GameBesignLogManager.Log(teamId, beSign.getId(), beSign.getPlayerId(), beSign.getPrice(), -1, ModuleLog.getModuleLog(EModuleCode.待签, "制卡"));
        sendMessage(builder.setCode(ErrorCode.Success.code).build());
    }

    /**
     * 批量制卡
     */
    @ClientMethod(code = ServiceCode.BeSignManager_makeCardBatch)
    public void makeCardBatch(String ids) {
        long teamId = getTeamId();
        int[] id = CheckAPI.converStringIds(ids);
        if (id.length < 1) {
            log.debug("参数不合法{}", ids);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        //
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        List<Integer> playerIds = Lists.newArrayList();
        for (int i : id) {
            if (!teamBeSign.checkBeSignPlayer(i)) {
                log.debug("待签球员不存在!{}", ids);
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Besign_1.code).build());
                return;
            }
            playerIds.add(teamBeSign.getBeSignPlayer(i).getPlayerId());
        }
        //
        List<BeSignPlayer> list = teamBeSign.removeBeSignList(id);
        Builder builder = playerCardManager.markCard(teamId, playerIds.stream().mapToInt(pid -> pid).toArray());
        for (BeSignPlayer beSign : list) {
            GameBesignLogManager.Log(teamId, beSign.getId(), beSign.getPlayerId(), beSign.getPrice(), -1, ModuleLog.getModuleLog(EModuleCode.待签, "制卡"));
        }
        sendMessage(builder.setCode(ErrorCode.Success.code).build());
    }

    /**
     * 批量回收待签
     */
    @ClientMethod(code = ServiceCode.BeSignManager_recycle)
    public void recycleBeSign(String ids) {
        long teamId = getTeamId();
        int[] id = CheckAPI.converStringIds(ids);
        if (id.length < 1) {
            log.debug("参数不合法{}", ids);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        //
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        int propNum = 0;
        for (int i : id) {
            if (!teamBeSign.checkBeSignPlayer(i)) {
                log.debug("待签球员不存在!{}", ids);
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Besign_1.code).build());
                return;
            }
            propNum += PlayerExchangeConsole.getRecoveryNum(PlayerConsole.getPlayerBean(teamBeSign.getBeSignPlayer(i).getPlayerId()).getGrade().getGrade());
        }
        //
        List<BeSignPlayer> list = teamBeSign.removeBeSignList(id);
        propManager.addProp(teamId, new PropSimple(PlayerExchangeConsole.Recycling_PROP_ID, propNum), true, ModuleLog.getModuleLog(EModuleCode.球员回收, "待签回收"));
        for (BeSignPlayer beSign : list) {
            GameBesignLogManager.Log(teamId, beSign.getId(), beSign.getPlayerId(), beSign.getPrice(), -1, ModuleLog.getModuleLog(EModuleCode.待签, "制卡"));
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(propNum).build());
    }

    /**
     * 一键回收
     */
    @ClientMethod(code = ServiceCode.BeSignManager_recycleAll)
    public void recycleAll() {
        long teamId = getTeamId();
        TeamBeSignPlayer teamBeSign = getTeamBeSignPlayer(teamId);
        // 这里的回收数量要公式计算
        int propNum = 0;
        if (teamBeSign.getBeSignList().size() < 1) {
            log.debug("待签球员不存在!");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        //
        int[] ids = new int[teamBeSign.getBeSignList().size()];
        for (int i = 0; i < teamBeSign.getBeSignList().size(); i++) {
            BeSignPlayer bp = teamBeSign.getBeSignList().get(i);
            ids[i] = bp.getId();
            int num = PlayerExchangeConsole.getRecoveryNum(PlayerConsole.getPlayerBean(bp.getPlayerId()).getGrade().getGrade());
            propNum += num;
        }
        ModuleLog module = ModuleLog.getModuleLog(EModuleCode.球员回收, "待签回收");
        List<BeSignPlayer> list = teamBeSign.removeBeSignList(ids);
        propManager.addProp(teamId, new PropSimple(PlayerExchangeConsole.Recycling_PROP_ID, propNum), true, module);
        for (BeSignPlayer beSign : list) {
            GameBesignLogManager.Log(teamId, beSign.getId(), beSign.getPlayerId(), beSign.getPrice(), -1, module);
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(propNum).build());
    }

    // 回包协议
    private TeamBeSignPB.TeamBeSignData converData(long teamId, TeamBeSignPlayer teamBeSign, TeamPlayer tp) {
        List<TeamBeSignPB.BeSignPlayer> dataList = Lists.newArrayList();
        for (BeSignPlayer beSign : teamBeSign.getBeSignList()) {
            TeamBeSignPB.BeSignPlayer t = TeamBeSignPB.BeSignPlayer.newBuilder()
                .setId(beSign.getId())
                .setPlayerId(beSign.getPlayerId())
                .setPrice(beSign.getPrice())
                .setEndTime(beSign.getEndTime().getMillis())
                .setTalent(PlayerManager.getPlayerTalentData(tp.getPlayerTalent(beSign.getTid())))
                .setBind(beSign.isBind())
                .build();
            dataList.add(t);
        }
        return TeamBeSignPB.TeamBeSignData.newBuilder()
            .setTeamId(teamId)
            .addAllBeSignList(dataList)
            .build();
    }

}

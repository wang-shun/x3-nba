package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.LogoConsole;
import com.ftkj.db.ao.logic.IPlayerLogoAO;
import com.ftkj.db.domain.LogoPO;
import com.ftkj.db.domain.PlayerLogoPO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.logo.PlayerLogoMT;
import com.ftkj.manager.logo.TeamPlayerLogo;
import com.ftkj.manager.logo.bean.Logo;
import com.ftkj.manager.logo.bean.PlayerLogo;
import com.ftkj.manager.logo.cfg.LogoLvBean;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.system.CheckAPI;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author Jay
 * @Description:荣誉头像
 * @time:2017年3月15日 下午7:34:22
 */
public class PlayerLogoManager extends BaseManager implements OfflineOperation {

    /** 球队头像 */
    private Map<Long, TeamPlayerLogo> playerLogoMap;
    @IOC
    private IPlayerLogoAO playerLogoAO;
    @IOC
    private PlayerManager playerManamger;
    @IOC
    private PropManager propManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private BuffManager buffManager;

    @Override
    public void instanceAfter() {
        playerLogoMap = Maps.newConcurrentMap();
    }

    /**
     * 取玩家头像
     *
     * @return
     */
    public TeamPlayerLogo getTeamPlayerLogo(long teamId) {
        TeamPlayerLogo teamLogo = playerLogoMap.get(teamId);
        if (teamLogo == null) {
            List<PlayerLogoPO> list1 = playerLogoAO.getPlayerLogoPOList(teamId);
            List<LogoPO> list2 = playerLogoAO.getLogoPOList(teamId);
            teamLogo = new TeamPlayerLogo(teamId, list1, list2);
            playerLogoMap.put(teamId, teamLogo);
            GameSource.checkGcData(teamId);
        }
        return teamLogo;
    }

    /**
     * 下线操作，清理数据
     */
    @Override
    public void offline(long teamId) {
        playerLogoMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        playerLogoMap.remove(teamId);
    }

    /**
     * 球员的头像列表
     */
    @ClientMethod(code = ServiceCode.Player_Logo_List)
    public void showLogoList() {
        long teamId = getTeamId();
        sendMessage(PlayerLogoMT.getPlayerLogoData(getTeamPlayerLogo(teamId), getLuckyValue(teamId)));
    }

    /**
     * 更换头像
     *
     * @param playerId
     * @param logoId   0 是默认空头像，DB不存此数据
     */
    @ClientMethod(code = ServiceCode.Player_Logo_Change)
    public void changeLogo(int playerId, int logoId) {
        long teamId = getTeamId();
        // 确认球员和头像存在且非使用中，就更换
        TeamPlayerLogo playerLogo = getTeamPlayerLogo(teamId);
        TeamPlayer teamPlayer = playerManamger.getTeamPlayer(teamId);
        //
        if (logoId != 0 && !playerLogo.checkPlayerLogoId(playerId, logoId)) {
            // 没有该球员头像
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Logo_0.code).build());
            log.debug("没有该球员头像");
            return;
        }
        if (logoId != 0 && playerLogo.checkUseLogo(logoId)) {
            // 头像在使用
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Logo_1.code).build());
            log.debug("头像在使用");
            return;
        }
        if (!teamPlayer.existPlayer(playerId)) {
            // 阵容没有该球员
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            log.debug("阵容没有该球员");
            return;
        }
        playerLogo.changeLogo(teamPlayer.getPlayer(playerId), logoId);
        // 更换头像
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 进阶点亮
     *
     * @param playerId
     */
    @ClientMethod(code = ServiceCode.Player_Logo_Farword)
    public void logoFarwordStep(int playerId) {
        long teamId = getTeamId();
        // 如果还没有进阶过，则会生成数据
        TeamPlayerLogo playerLogo = getTeamPlayerLogo(teamId);
        PlayerLogo pl = playerLogo.getPlayerLogo(playerId);
        LogoLvBean logoLv = LogoConsole.getLogoLv(pl.getLv());
        if (pl.getLv() + 1 > LogoConsole.MAX_LV && pl.getStarLv() == logoLv.getStat()) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_3.code).build());
            log.debug("等级已满");
            return;
        }
        TeamPlayer teamPlayer = playerManamger.getTeamPlayer(teamId);
        TeamProp teamProp = propManager.getTeamProp(teamId);
        //
        if (!teamProp.checkPropNum(LogoConsole.QuaProp, logoLv.getJsf())) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_4.code).build());
            log.debug("荣耀点不足");
            return;
        }
        propManager.delProp(teamId, LogoConsole.QuaProp, logoLv.getJsf(), true, false);
        // 进阶
        boolean suc = playerLogo.forward(teamPlayer.getPlayer(playerId));
        //
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(suc ? 1 : 0).build());
    }

    /**
     * 头像荣誉的
     * 等级转移
     */
    @ClientMethod(code = ServiceCode.Player_Logo_Tran)
    public void logoHonorTran(int player1, int player2) {
        long teamId = getTeamId();
        // 互转，所需转移卡取最高等级来算
        TeamPlayerLogo playerLogo = getTeamPlayerLogo(teamId);
        PlayerLogo p1 = playerLogo.getPlayerLogo(player1);
        PlayerLogo p2 = playerLogo.getPlayerLogo(player2);
        // 这样取出来是不会为空的，默认存在0级
        if ((p1.getLv() == 0 && p1.getStep() == 0)
                || (p1.getLv() == 0 && p1.getStep() == 0)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Logo_2.code).build());
            return;
        }
        // 转移卡数量
        int num = playerLogo.getTranCardNum(p1, p2);
        TeamProp teamProp = propManager.getTeamProp(teamId);
        if (!teamProp.checkPropNum(LogoConsole.TRAN_TID, num)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            log.debug("道具不足");
            return;
        }
        propManager.delProp(teamId, LogoConsole.TRAN_TID, num, true, false);
        // 转移
        Player pla1 = playerManamger.getTeamPlayer(teamId).getPlayer(player1);
        Player pla2 = playerManamger.getTeamPlayer(teamId).getPlayer(player2);
        playerLogo.tranLogoHonor(pla1, pla2);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 碎片合成
     */
    @ClientMethod(code = ServiceCode.Player_Logo_Comb)
    public void combination(int playerId) {
        long teamId = getTeamId();
        // 看头像球员是否存在
        if (!LogoConsole.checkPlayer(playerId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Logo_0.code).build());
            log.debug("看头像球员不存在");
            return;
        }
        TeamProp teamProp = propManager.getTeamProp(teamId);
        boolean hasNum = teamProp.checkPropNum(LogoConsole.DEBRIS_TID, LogoConsole.DEBRIS_NUM);
        if (!hasNum) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            log.debug("道具不足");
            return;
        }
        // 扣道具
        propManager.delProp(teamId, LogoConsole.DEBRIS_TID, LogoConsole.DEBRIS_NUM, true, false);
        // 随机合成品质
        int qua = randQuality(teamId, getLuckyValue(teamId));
        if (qua < LogoConsole.LUCKY_QUALITY) {
            addLuckyValue(teamId);
        } else {
            clearLuckyValue(teamId);
        }
        // 合成
        Logo logo = getTeamPlayerLogo(teamId).addLogo(playerId, qua);
        taskManager.updateTask(teamId, ETaskCondition.合成荣誉头像, 1, EModuleCode.荣誉头像.getName());
        // 推送logo
        sendMessage(teamId, PlayerLogoMT.getLogoData(logo), ServiceCode.Player_Logo_Add_Push);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(getLuckyValue(teamId)).build());
    }

    /**
     * 随机合成品质
     *
     * @param teamId
     * @param luckyValue
     * @return
     */
    private int randQuality(long teamId, int luckyValue) {
        if (luckyValue == 100) {
            return LogoConsole.LUCKY_QUALITY;
        }
        // VIP品质加成
        Map<Integer, Integer> adjustMap = Maps.newHashMap();
        //		adjustMap.put(LogoConsole.LUCKY_QUALITY, buffManager.getBuffSet(teamId, EBuffType.合成橙色头像的概率提高).getValueSum().intValue());
        return LogoConsole.randQuality(adjustMap);
    }

    /**
     * 头像分解，最多可同时分解5个头像
     *
     * @param logId1
     * @param logId2
     * @param logId3
     * @param logId4
     * @param logId5
     */
    @ClientMethod(code = ServiceCode.Player_Logo_Resolve)
    public void resolveLogo(String logIds) {
        if (logIds == null || logIds.trim().equals("")) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            log.debug("参数错误");
            return; // 重复参数
        }
        long teamId = getTeamId();
        // 检查是否在用，分解
        int[] ids = CheckAPI.converStringIds(logIds);
        Map<Integer, Integer> props = CheckAPI.converParamToMap(ids);
        if (props.values().stream().anyMatch(size -> size > 1)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            log.debug("重复参数");
            return; // 重复参数
        }
        //
        TeamPlayerLogo playerLogo = getTeamPlayerLogo(teamId);
        //
        boolean noHas = props.keySet().stream().anyMatch(logoId -> !playerLogo.checkLogoId(logoId));
        if (noHas) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Logo_0.code).build());
            log.debug("不存在头像");
            return; // 不存在头像
        }
        // 在使用的不能分解
        boolean isUse = props.keySet().stream().anyMatch(logoId -> playerLogo.checkUseLogo(logoId));
        // 金色不可以分解
        boolean notResolve = props.keySet().stream().anyMatch(logoId -> !playerLogo.isCanResolveLogo(logoId));
        if (isUse || notResolve) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Logo_1.code).build());
            log.debug("有在使用的头像");
            return; // 有在使用的头像
        }
        // 分解并得到碎片数量
        int num = playerLogo.resolve(props.keySet());
        // 添加碎片
        propManager.addProp(teamId, new PropSimple(LogoConsole.DEBRIS_TID, num), false, ModuleLog.getModuleLog(EModuleCode.荣誉头像, ""));
        taskManager.updateTask(teamId, ETaskCondition.分解荣誉头像, 1, EModuleCode.荣誉头像.getName());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 四合一，升阶,
     * 可能前台传指定id过来更加方便点
     *
     * @param playerId 球员ID
     * @param quality  品质
     */
    @ClientMethod(code = ServiceCode.Player_Logo_Quality)
    public void upQuality(int playerId, int quality) {
        long teamId = getTeamId();
        TeamPlayerLogo playerLogo = getTeamPlayerLogo(teamId);
        if (!playerLogo.hasLogoNum(playerId, quality)) {
            // 数量不够
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_6.code).build());
            log.debug("数量不够");
            return;
        }
        if (quality == LogoConsole.MAX_QUA) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_3.code).build());
            log.debug("最高品质");
            return;
        }
        // 合成
        if (!playerLogo.upQuality(playerId, quality)) {
            log.debug("头像不足");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_6.code).build());
            return;
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        // 推送荣誉头像的最新包
        sendMessage(teamId, PlayerLogoMT.getLogoHonorData(playerLogo.getPlayerLogo(playerId), playerLogo.getPlayerLogoList(playerId)), ServiceCode.Player_Logo_Hero_Update);

    }

    /**
     * 取幸运值
     *
     * @param teamId
     * @return
     */
    public int getLuckyValue(long teamId) {
        String key = RedisKey.getKey(teamId, RedisKey.Player_Logo_Lucky_Value);
        int value = redis.getIntNullIsZero(key);
        return value;
    }

    /**
     * 保存幸运值
     *
     * @param teamId
     * @param value
     */
    public void saveLuckyValue(long teamId, int value) {
        String key = RedisKey.getKey(teamId, RedisKey.Player_Logo_Lucky_Value);
        redis.set(key, value + "");
    }

    /**
     * 累加幸运值
     *
     * @param teamId
     */
    public void addLuckyValue(long teamId) {
        saveLuckyValue(teamId, getLuckyValue(teamId) + LogoConsole.LUCKY_EACH);
    }

    /**
     * 清空幸运值
     *
     * @param teamId
     */
    public void clearLuckyValue(long teamId) {
        redis.del(RedisKey.Player_Logo_Lucky_Value + teamId);
    }

}

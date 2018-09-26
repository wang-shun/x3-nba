package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.EquiConsole;
import com.ftkj.db.ao.logic.ITeamEquiAO;
import com.ftkj.db.domain.EquiPO;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.equi.EEquiType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.EquiParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.equi.TeamEqui;
import com.ftkj.manager.equi.bean.Equi;
import com.ftkj.manager.equi.bean.PlayerEqui;
import com.ftkj.manager.equi.cfg.EquiBean;
import com.ftkj.manager.equi.cfg.EquiPropsBean;
import com.ftkj.manager.equi.cfg.EquiRefreshBean;
import com.ftkj.manager.equi.cfg.EquiUpQuaBean;
import com.ftkj.manager.equi.cfg.EquiUpStrBean;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.system.CheckAPI;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.TeamEquiPB;
import com.ftkj.proto.TeamEquiPB.PlayerEquiData;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Jay
 * @Description:球队装备
 * @time:2017年3月17日 上午10:17:47
 */
public class EquiManager extends BaseManager implements OfflineOperation {

    private static Map<Long, TeamEqui> teamEquiMap;

    @IOC
    private ITeamEquiAO teamEquiAO;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TeamMoneyManager moneyManager;
    @IOC
    private TaskManager taskManager;
    //	@IOC
    //	private EquiUpQuality equiUpQuality;
    //	@IOC
    //	private EquiUpStrong equiUpStrong;
    @IOC
    private BuffManager buffManager;
    @IOC
    private ChatManager chatManager;
    @IOC
    private TeamManager teamManager;

    // 强化等级跑马灯配置
    private Map<Integer, EGameTip> upStrTipMap;
    // 升星提示
    private Map<Integer, EGameTip> upStarTipMap;
    // 等阶跑马灯配置
    private Map<Integer, EGameTip> upQuaTipMap;

    @Override
    public void instanceAfter() {
        teamEquiMap = Maps.newConcurrentMap();
     
        upStrTipMap = Maps.newHashMap();
         
        upStrTipMap.put(31, EGameTip.装备强化4星10_12);
        upStrTipMap.put(32, EGameTip.装备强化4星10_12);
        upStrTipMap.put(33, EGameTip.装备强化4星10_12);     
        upStrTipMap.put(38, EGameTip.装备强化5星10_12);
        upStrTipMap.put(39, EGameTip.装备强化5星10_12);
        upStrTipMap.put(40, EGameTip.装备强化5星10_12);
        //
        upStarTipMap = Maps.newHashMap();
        upStarTipMap.put(13, EGameTip.装备强化2星);
        upStarTipMap.put(20, EGameTip.装备强化3星);
        upStarTipMap.put(27, EGameTip.装备强化4星);
        upStarTipMap.put(34, EGameTip.装备强化5星);
        //
        upQuaTipMap = Maps.newHashMap();
        upQuaTipMap.put(3, EGameTip.装备染色紫色);
        upQuaTipMap.put(4, EGameTip.装备染色橙色);
        upQuaTipMap.put(5, EGameTip.装备染色红色);

        //
        EventBusManager.register(EEventType.装备进化, this);
        EventBusManager.register(EEventType.装备强化, this);
        //EventBusManager.register(EEventType.装备升级, this);
    }

    /**
     * 取球队装备
     *
     * @param teamId
     * @return
     */
    public TeamEqui getTeamEqui(long teamId) {
        TeamEqui teamEqui = teamEquiMap.get(teamId);
        if (teamEqui == null) {
            List<EquiPO> list = teamEquiAO.getTeamEquiPOList(teamId);
            teamEqui = TeamEqui.instanceBeanSet(teamId, list);
            teamEquiMap.put(teamId, teamEqui);
            GameSource.checkGcData(teamId);
        }
        return teamEqui;
    }

    /**
     * 用户下线处理
     */
    @Override
    public void offline(long teamId) {
        teamEquiMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teamEquiMap.remove(teamId);
    }

    /**
     * 球队所有装备列表，不包括球衣
     */
    @ClientMethod(code = ServiceCode.EquiManager_showEquiList)
    public void showEquiList() {
        sendMessage(getTeamEquiData(getTeamEqui(getTeamId())));
    }

    /*
     * 装备列表
     * @param te
     * @return
     */
    public TeamEquiPB.TeamEquiData getTeamEquiData(TeamEqui te) {
        List<TeamEquiPB.PlayerEquiData> equiList = Lists.newArrayList();
        List<TeamEquiPB.PlayerEquiData> clothesList = Lists.newArrayList();
        te.getPlayerEquiList().stream().forEach(pe -> {
            equiList.add(getPlayerEquiData(pe));
        });
        te.getPlayerClothesList().stream().forEach(pe -> {
            clothesList.add(getPlayerEquiData(pe));
        });
        return TeamEquiPB.TeamEquiData.newBuilder()
                .setTeamId(te.getTeamId())
                .addAllPlayerEquiList(equiList)
                .addAllClothesEquiList(clothesList)
                .build();
    }

    private TeamEquiPB.PlayerEquiData getPlayerEquiData(PlayerEqui playerEqui) {
        TeamEquiPB.PlayerEquiData eData = TeamEquiPB.PlayerEquiData.newBuilder()
                .setPlayerId(playerEqui.getPlayerId())
                .addAllList(getEquiListData(playerEqui.getPlayerEqui()))
                .build();
        return eData;
    }

    /*
     * 装备协议消息
     */
    public static List<TeamEquiPB.EquiData> getEquiListData(Collection<Equi> collection) {
        List<TeamEquiPB.EquiData> list = Lists.newArrayList();
        for (Equi equi : collection) {
            list.add(getEquiData(equi));
        }
        return list;
    }

    /*
     * 装备协议消息
     */
    public static TeamEquiPB.EquiData getEquiData(Equi equi) {
        return TeamEquiPB.EquiData.newBuilder()
                .setId(equi.getId())
                .setEid(equi.getEquId())
                .setPlayerId(equi.getPlayerId())
                .setLv(0)
                .setStrlv(equi.getStrLv())
                .setExp(0)
                .setStrBless(equi.getStrBless())
                .setEndTime((int) (equi.getEndTime().getMillis() / 1000))
                .setEquiTeam(equi.getEquiTeam())
                .addAllRandAttr(getEquiAttrData(equi.getRandAttrMap()))
                .build();
    }

    /**
     * 装备随机属性加成值
     *
     * @param attrs
     * @return
     */
    public static List<TeamEquiPB.EquiAttrData> getEquiAttrData(Map<EActionType, Float> attrs) {
        List<TeamEquiPB.EquiAttrData> dataList = Lists.newArrayList();
        for (EActionType attr : attrs.keySet()) {
            dataList.add(TeamEquiPB.EquiAttrData.newBuilder()
                    .setAttr(attr.getConfigName())
                    .setVal(attrs.get(attr))
                    .build());
        }
        return dataList;
    }

    //-----------------------------养成接口-------------------------------------
    //	/**
    //	 * 球衣进阶
    //	 * @param playerId
    //	 */
    //	@Deprecated
    //	@ClientMethod(code = ServiceCode.EquiManager_upQuaClothes)
    //	public void upQuaClothes(int playerId, int itemId) {
    //		long teamId = getTeamId();
    //		// 没有球队归属的球员不能合成球衣
    //		PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
    //		if(pb == null || pb.getTeamId() == 0) {
    //			log.debug("该球员{}没有球队归属", playerId);
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
    //			return;
    //		}
    //		//
    //		TeamEqui teamEqui = getTeamEqui(teamId);
    //		PlayerEqui playerEqui = teamEqui.getPlayerEquiSetIfNullCreate(playerId, EEquiType.球衣.id);
    //		if(playerEqui == null) {
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Equi_1.code).build());
    //			return;
    //		}
    //		Equi equi = playerEqui.getPlayerEquiByType(EEquiType.球衣.id);
    //		if(equi == null || equi.getEquiTeam() == 0) {
    //			log.debug("装备数据不存在，操作异常");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Equi_1.code).build());
    //			return;
    //		}
    //		// 道具碎片类型是否合法
    //		EquiConsole.getClothesBean(equi.getEquiTeam());
    //		if(itemId != EquiConsole.getClothesUpQuaItem(equi.getEquiTeam())) {
    //			log.debug("球队{}，碎片类型不对{}", equi.getEquiTeam(), itemId);
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
    //			return;
    //		}
    //		if(!equiUpQuality.canUp(equi.getEquId())) {
    //			log.debug("该装备不能进阶");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Equi_2.code).build());
    //			return; //
    //		}
    //		// 当前等级需要数量
    //		EquiPropsBean upNeed = EquiConsole.getEquiQuaPropsBeanByType(equi.getQuality(), EEquiType.球衣);
    //		int needNum = upNeed.getPropsMap().get(1007).intValue();
    //		//
    //		TeamProp teamProp = propManager.getTeamProp(teamId);
    //		if(teamProp.getProp(itemId).getNum() < needNum) {
    //			log.debug("道具不足{}", itemId);
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
    //			return;
    //		}
    //		boolean isUp = teamProp.checkPropNum(itemId, needNum);
    //		if(!isUp) {
    //			// 不足以升级
    //			needNum = teamProp.getProp(itemId).getNum();
    //		}
    //		// 扣掉
    //		propManager.delProp(teamId, itemId, needNum, true, false);
    //		// 加经验
    //		equi.addQuaExp(needNum);
    //		if(isUp) {
    //			// 进阶
    //			EquiBean equiNew = EquiConsole.getEquiBean(equi.getEquId()+1);
    //			equi.setEquId(equiNew.getId());
    //			if(equiNew.getQuality()>=3 && equiNew.getQuality()<5){
    //				chatManager.pushGameTip(EGameTip.衣服合成34阶, 0, teamManager.getTeamName(teamId),"equ_"+equiNew.getId());
    //			}
    //			if(equiNew.getQuality()>=5){
    //				chatManager.pushGameTip(EGameTip.衣服合成5, 0, teamManager.getTeamName(teamId),"equ_"+equiNew.getId());
    //			}
    //		}
    //		equi.save();
    //		//
    //		EventBusManager.post(EEventType.装备进化, new EquiParam(teamId, equi.getEquId(), equi.getStrLv(), equi.getQuality(), isUp, EquiParam.EquiEVentType.强化));
    //		taskManager.updateTask(teamId, ETaskCondition.合成球衣, 1, equi.getQuality()+"");
    //		//
    //		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(isUp?1:0).build());
    //		sendMessage(teamId, getEquiData(equi), ServiceCode.EquiManager_Change_Topic);
    //	}

    @Subscribe
    public void strLvcallback(EquiParam param) {
        // 强化失败不处理
        if (!param.suc) { return; }
        long teamId = param.teamId;
        // 强化
        if (param.opeType == EquiParam.EquiEVentType.强化) {
            taskManager.updateTask(teamId, ETaskCondition.强化装备, 1, param.strLv + "");
            EquiUpStrBean upstrBean = EquiConsole.getEquiUpStrBean(param.strLv);
            if (upStrTipMap.containsKey(param.strLv)) {
                chatManager.pushGameTip(upStrTipMap.get(param.strLv), 0, teamManager.getTeamNameById(teamId), "equ_" + param.equiId, "" + upstrBean.getShowLv());
            } else if (upStarTipMap.containsKey(param.strLv)) {
                taskManager.updateTask(teamId, ETaskCondition.装备升星, 1, upstrBean.getShowStar() + "");
                chatManager.pushGameTip(upStarTipMap.get(param.strLv), 0, teamManager.getTeamNameById(teamId), "equ_" + param.equiId, "" + upstrBean.getShowStar());
            }
        }
        // 进化
        else if (param.opeType == EquiParam.EquiEVentType.进化_染色) {
            taskManager.updateTask(teamId, ETaskCondition.进阶装备, 1, param.quality + "");
            if (upQuaTipMap.containsKey(param.quality)) {
                chatManager.pushGameTip(upQuaTipMap.get(param.quality), 0, teamManager.getTeamNameById(teamId), "equ_" + param.equiId, "equlv_" + param.quality);
            }
        }
    }

    /**
     * 装备进化
     *
     * @param playerId
     * @param type     装备部位
     * @param prop1
     * @param prop2
     * @param prop3
     * @return
     */
    @ClientMethod(code = ServiceCode.EquiManager_upQuality)
    public void upQuality(int playerId, int type) {
        long teamId = getTeamId();
        Player player = playerManager.getTeamPlayer(teamId).getPlayer(playerId);
        TeamEqui teamEqui = getTeamEqui(teamId);
        PlayerEqui playerEqui = teamEqui.getPlayerEquiSetIfNullCreate(playerId, type);
        Equi equi = playerEqui == null ? null : playerEqui.getPlayerEquiByType(type);
        ErrorCode error = checkTeamPlayerEqui(teamId, player, playerEqui, equi, type);
        if (error != ErrorCode.Success) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(error.code).build());
            return;
        }
        // 升阶消耗的道具
        EquiUpQuaBean bean = EquiConsole.getEquiUpQuaBean(equi.getQuality());
        PropSimple needProp = bean.getNeedPropMap().get(EEquiType.getByType(type));
        if (!propManager.getTeamProp(teamId).checkPropNum(needProp)) {
            log.debug("道具不足{}", needProp.toString());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }
        if (equi.getQuality() + 1 > EquiConsole.MAX_QUALITY || needProp == null) {
            log.debug("该装备不能进阶，已满阶级");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Equi_2.code).build());
            return;
        }
        // 扣道具
        propManager.delProp(teamId, needProp, true, true);
        // 执行进阶
        boolean suc = true; // 没有概率了
        EquiBean equiNew = EquiConsole.getEquiBean(equi.getEquId() + 1);
        equi.setEquId(equiNew.getId());
        // 随机刷新属性
        EquiRefreshBean refreshBean = EquiConsole.getEquiRefreshBean(equiNew.getQuality());
        equi.setRandAttrMap(equiNew.randomAttr(refreshBean.getRandomStatCount(), refreshBean.getRandomStat()));
        equi.save();
        EventBusManager.post(EEventType.装备进化, new EquiParam(teamId, equi.getEquId(), equi.getStrLv(), equi.getQuality(), suc, EquiParam.EquiEVentType.进化_染色));
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(suc ? 1 : 0).build());
        sendMessage(teamId, getEquiData(equi), ServiceCode.EquiManager_Change_Topic);
    }

    /**
     * 同级进化
     * 刷新随机属性
     */
    @ClientMethod(code = ServiceCode.EquiManager_refreshAttr)
    public void refreshAttr(int playerId, int type) {
        long teamId = getTeamId();
        Player player = playerManager.getTeamPlayer(teamId).getPlayer(playerId);
        TeamEqui teamEqui = getTeamEqui(teamId);
        PlayerEqui playerEqui = teamEqui.getPlayerEquiSetIfNullCreate(playerId, type);
        Equi equi = playerEqui == null ? null : playerEqui.getPlayerEquiByType(type);
        ErrorCode error = checkTeamPlayerEqui(teamId, player, playerEqui, equi, type);
        if (error != ErrorCode.Success) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(error.code).build());
            return;
        }
        // 刷新消耗的道具
        EquiRefreshBean bean = EquiConsole.getEquiRefreshBean(equi.getQuality());
        PropSimple needProp = bean.getNeedPropMap().get(EEquiType.getByType(type));
        if (needProp == null || !propManager.getTeamProp(teamId).checkPropNum(needProp)) {
            log.debug("道具不足{}", needProp.toString());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }
        // 扣道具
        propManager.delProp(teamId, needProp, true, true);
        // 执行进阶
        boolean suc = true; // 没有概率了
        // 随机刷新属性
        EquiBean equiBean = EquiConsole.getEquiBean(equi.getEquId());
        equi.setRandAttrMap(equiBean.randomAttr(bean.getRandomStatCount(), bean.getRandomStat()));
        equi.save();
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(suc ? 1 : 0).build());
        sendMessage(teamId, getEquiData(equi), ServiceCode.EquiManager_Change_Topic);
    }

    /**
     * 通用检查装备的合法性
     *
     * @param teamId
     * @param player
     * @param equi
     * @return
     */
    private ErrorCode checkTeamPlayerEqui(long teamId, Player player, PlayerEqui playerEqui, Equi equi, int type) {
        if (!EEquiType.contains(type) || type == EEquiType.球衣.id) {
            log.debug("参数错误，不存在装备类型");
            return ErrorCode.ParamError;
        }
        if (player == null) {
            log.debug("球员不存在，异常操作");
            return ErrorCode.Player_Null;
        }
        if (playerEqui == null) {
            log.debug("装备已满");
            return ErrorCode.Equi_1;
        }
        if (equi == null) {
            log.debug("装备数据不存在，操作异常");
            return ErrorCode.Equi_1;
        }
        return ErrorCode.Success;
    }

    /**
     * 强化，进阶
     *
     * @param playerId
     * @param type
     * @param propIds  道具ID列表，号分割
     * @return
     */
    @ClientMethod(code = ServiceCode.EquiManager_upStrLv)
    public void upStrLv(int playerId, int type, String propIds) {
        long teamId = getTeamId();
        List<PropSimple> propsList = CheckAPI.converPropParamToMap(CheckAPI.converStringIds(propIds));
        TeamProp teamProp = propManager.getTeamProp(teamId);
        TeamMoney taemMoney = moneyManager.getTeamMoney(teamId);
        if (!CheckAPI.checkTeamPropNum(propsList, teamProp, taemMoney)) {
            log.debug("道具不足");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }
        //
        Player player = playerManager.getTeamPlayer(teamId).getPlayer(playerId);
        TeamEqui teamEqui = getTeamEqui(teamId);
        PlayerEqui playerEqui = teamEqui.getPlayerEquiSetIfNullCreate(playerId, type);
        Equi equi = playerEqui == null ? null : playerEqui.getPlayerEquiByType(type);
        ErrorCode error = checkTeamPlayerEqui(teamId, player, playerEqui, equi, type);
        if (error != ErrorCode.Success) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(error.code).build());
            return;
        }
        //
        if (equi.getStrLv() + 1 > EquiConsole.MAX_STRONGLV) {
            log.debug("装备不能强化");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Equi_2.code).build());
            return;
        }
        // 验证经费并且扣经费
        int gold = EquiConsole.getEquiUpStrBean(equi.getStrLv()).getMoney();
        if (gold > 0 && !moneyManager.updateTeamMoney(teamId, 0, 0 - Math.abs(gold), 0, 0, true, ModuleLog.getModuleLog(EModuleCode.装备, "强化"))) {
            log.debug("经费不够");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_0.code).build());
            return;
        }
        // 扣道具
        propManager.delProp(teamId, propsList, true, true);
        // 强化
        EquiUpStrBean upStrBean = EquiConsole.getEquiUpStrBean(equi.getStrLv());
        EquiPropsBean equiProps = upStrBean.getPropsBean();
        // 扣道具，强化概率处理
        boolean suc = false;
        float rate = equiProps.getRateByProps(propsList) + equi.getStrBless();
        //log.warn("强化装备[{}, 等级={}, 成功概率={}]", equi.getEquId(), equi.getStrLv(), rate);
        if (equi.getStrBless() == 100 || RandomUtil.randHit(10000, (int) (rate * 10000))) {
            equi.setStrLv(equi.getStrLv() + 1);
            equi.clearStrBless();
            equi.save();
            suc = true;
        } else {
            // 失败
            if (upStrBean.getType() == 2 && upStrBean.getAddProbability() > 0) {
                equi.addStrBless(upStrBean.getAddProbability());
                equi.save();
            }
            suc = false;
        }
        EventBusManager.post(EEventType.装备强化, new EquiParam(teamId, equi.getEquId(), equi.getStrLv(), equi.getQuality(), suc, EquiParam.EquiEVentType.强化));
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(suc ? 1 : 0).build());
        sendMessage(teamId, getEquiData(equi), ServiceCode.EquiManager_Change_Topic);
    }

    //	/**
    //	 * 已废弃
    //	 * 装备升级，拥有经验不满一级不能进行
    //	 * @param id
    //	 * @return
    //	 */
    //	@Deprecated
    //	@ClientMethod(code = ServiceCode.EquiManager_upLv)
    //	public void upLv(int playerId, int type) {
    //		long teamId = getTeamId();
    //		TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
    //		Player player = teamPlayer.getPlayer(playerId);
    //		if(player == null || !EEquiType.contains(type)) {
    //			log.debug("球员不存在，装备不存在");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
    //			return;
    //		}
    //		// 装备验证
    //		TeamEqui teamEqui = getTeamEqui(teamId);
    //		Equi equi = teamEqui.getPlayerEquiIfNullCreate(playerId, type);
    //		if(equi == null) {
    //			log.debug("该球员没有装备部位");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
    //			return; // 该球员没有装备部位。
    //		}
    //		// 经验量是道具
    //		TeamProp teamProp = propManager.getTeamProp(teamId);
    //		EquiUpLvBean upLv = EquiConsole.getEquiUpLvBean(equi.getLv());
    //		if(upLv ==null || upLv.getLv()+1 > EquiConsole.MAX_LV) {
    //			log.debug("装备已经是最高等级");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
    //			return;
    //		}
    //		// 剩余经验
    //		if(!teamProp.checkPropNum(EquiConsole.UPLV_PROP, upLv.getNeed())) {
    //			log.debug("剩余经验不足");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
    //			return;
    //		}
    //		// Buff一定的概率返回经验
    //		int needNum = upLv.getNeed();
    //		BuffSet buff = buffManager.getBuffSet(teamId, EBuffType.装备升级X概率经验返还X比例的装备经验);
    //		if(RandomUtil.randInt(100) < buff.getValueSum() * 100) {
    //			int subNum = (int) (needNum * buff.getValue(0, 1));
    //			needNum -= subNum;
    //			log.debug("返还装备经验数量：{}", subNum);
    //		}
    //		propManager.delProp(teamId, EquiConsole.UPLV_PROP, needNum, true, false);
    //		// 升级
    //		teamEqui.uplv(playerId, equi.getId(), equi.getType());
    //		EventBusManager.post(EEventType.装备升级, new EquiParam(teamId, equi.getEquId(), equi.getLv(), equi.getQuality(), true, EquiParam.EquiEVentType.强化));
    //		taskManager.updateTask(teamId, ETaskCondition.升级装备, 1, ""+equi.getLv());
    //		sendMessage(teamId, getEquiData(equi), ServiceCode.EquiManager_Change_Topic);
    //		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    //	}

    //	/**
    //	 * 装备转移，交换装备并穿上， 不包括球衣
    //	 * @param playerId 原装备球员套装
    //	 * @param playerId2  阵容目标球员
    //	 */
    //	@Deprecated
    //	@ClientMethod(code = ServiceCode.EquiManager_equiTransfer)
    //	public void equiTransfer(int playerId, int playerId2) {
    //		if(playerId == playerId2) {
    //			log.debug("操作异常，自己不能转给自己!");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
    //			return;
    //		}
    //		long teamId = getTeamId();
    //		TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
    //		TeamEqui teamEqui = getTeamEqui(teamId);
    //		if(!teamEqui.hasPlayerEqui(playerId)) {
    //			log.debug("装备不存在");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
    //			return;
    //		}
    //		if(teamEqui.hasPlayerEqui(playerId2)) {
    //			log.debug("目标球员已存在装备套装，不能再转移");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
    //			return;
    //		}
    //		Player p2 = teamPlayer.getPlayer(playerId2);
    //		if(p2 == null) {
    //			log.debug("目标球员不存在阵容!");
    //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
    //			return;
    //		}
    //		List<PlayerEqui> changeList = teamEqui.equiTransfer(playerId, playerId2);
    //		List<PlayerEquiData> dataList = Lists.newArrayList();
    //		for(PlayerEqui e : changeList) {
    //			dataList.add(getPlayerEquiData(e));
    //		}
    //		sendMessage(teamId, TeamEquiPB.PlyaerSuitChangeData.newBuilder().addAllSuitList(dataList).build(),
    //				ServiceCode.EquiManager_Suit_Change_Topic);
    //		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    //	}

    /**
     * 装备转换，不做校验
     */
    public void transEqui(long teamId, int playerId1, int playerId2) {
        TeamEqui teamEqui = getTeamEqui(teamId);
        //
        List<PlayerEqui> changeList = teamEqui.equiTransfer(playerId1, playerId2);
        List<PlayerEquiData> dataList = Lists.newArrayList();
        for (PlayerEqui e : changeList) {
            dataList.add(getPlayerEquiData(e));
        }
        sendMessage(teamId, TeamEquiPB.PlyaerSuitChangeData.newBuilder().addAllSuitList(dataList).build(),
                ServiceCode.EquiManager_Suit_Change_Topic);
    }

    /**
     * 装换球衣，不转换球衣所归属球队
     *
     * @param playerId1
     * @param playerId2
     */
    public void transClothes(long teamId, int playerId1, int playerId2) {
        TeamEqui teamEqui = getTeamEqui(teamId);
        //
        List<PlayerEqui> changeList = teamEqui.equiTransferClothes(playerId1, playerId2);
        List<PlayerEquiData> dataList = Lists.newArrayList();
        for (PlayerEqui e : changeList) {
            dataList.add(getPlayerEquiData(e));
        }
        sendMessage(teamId, TeamEquiPB.PlyaerSuitChangeData.newBuilder().addAllSuitList(dataList).build(),
                ServiceCode.EquiManager_Suit_Change_Topic);
    }

}

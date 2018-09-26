package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.card.PlayerCardGroupBean;
import com.ftkj.console.PlayerCardConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.db.ao.logic.IPlayerCardAO;
import com.ftkj.db.domain.CardPO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerStorage;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.playercard.Card;
import com.ftkj.manager.playercard.TeamPlayerCard;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerCardPB;
import com.ftkj.proto.PlayerCardPB.CollectData;
import com.ftkj.proto.PlayerCardPB.CollectData.Builder;
import com.ftkj.proto.PlayerCardPB.SimpleCardData;
import com.ftkj.proto.PropPB.PropSimpleData;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Jay
 * @Description:球星卡
 * @time:2017年4月11日 下午5:34:06
 */
public class PlayerCardManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(PlayerCardManager.class);
    @IOC
    private IPlayerCardAO playerCardAO;
    @IOC
    private TaskManager taskManager;
    @IOC
    private PropManager propManager;
    @IOC
    private PlayerManager playerManager;
    private Map<Long, TeamPlayerCard> teamPlayerCardMap;
    private ModuleLog moduleLog = ModuleLog.getModuleLog(EModuleCode.球星卡, "突破");

    @Override
    public void instanceAfter() {
        teamPlayerCardMap = Maps.newConcurrentMap();
    }

    public TeamPlayerCard getTeamPlayerCard(long teamId) {
        TeamPlayerCard tpc = teamPlayerCardMap.get(teamId);
        if (tpc == null) {
            List<CardPO> list = playerCardAO.getCardPOList(teamId);
            tpc = new TeamPlayerCard(teamId, list);
            teamPlayerCardMap.put(teamId, tpc);
            GameSource.checkGcData(teamId);
        }
        return tpc;
    }

    @Override
    public void offline(long teamId) {
        teamPlayerCardMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teamPlayerCardMap.remove(teamId);
    }

    /**
     * 制卡</BR>
     * 这是是一个接口，应该由球探和待签模块依赖，这里只负责生成球星卡</BR>
     * 1. 全套卡组，新秀卡组，全明星卡组的经验都是独立存在的。
     * 2. 制卡过程中，全套卡组内的卡都是100%激活且优先激活。
     * 3. 制卡过程中，新秀卡组和全明星卡组内的卡都是随机激活。
     * 4. 制卡过程中，新秀卡组和全明星卡组内的经验获得条件（新秀卡组合全明星卡组内的卡是已激活状态，若再次出现制成对应卡组内卡的操作时，则会转化为对应卡组的经验。反之则会并入到全套卡组经验。）
     */
    public Builder markCard(long teamId, int[] playerIds) {
        Builder collectData = CollectData.newBuilder();
        List<PropSimple> propList = Lists.newArrayList();
        List<Card> cardList = Lists.newArrayList();
        List<SimpleCardData> cardListData = Lists.newArrayList();
        List<PropSimpleData> propListData = Lists.newArrayList();
        TeamPlayerCard tpc = getTeamPlayerCard(teamId);
        for (int playerId : playerIds) {
            // 先看看是否存在全套卡
            boolean hasZeroCard = tpc.getCard(0, playerId) != null;
            PlayerCardGroupBean bean = PlayerCardConsole.getMakeCardRandGroup(playerId, hasZeroCard);
            // 如果已经激活，则掉经验
            if (tpc.isCollected(bean.getType(), playerId)) {
                PropSimple ps = bean.getRepeatedDrop();
                ps = new PropSimple(ps.getPropId(), ps.getNum() * PlayerConsole.getPlayerBean(playerId).getPrice());
                //
                Card card = tpc.getCard(bean.getType(), playerId);
                propList.add(ps);
                propListData.add(PropManager.getPropData(ps));
                cardListData.add(getSimpleCardData(card, 1));
            } else {
                Card card = tpc.createCard(bean.getType(), playerId);
                tpc.addToStorage(card);
                cardList.add(card);
                cardListData.add(getSimpleCardData(card, 0));
            }
            // 如果是全套卡
            if (bean.getType() == 0) {
                taskManager.updateTask(teamId, ETaskCondition.球星卡全套卡制作, 1, "");
            }
            taskManager.updateTask(teamId, ETaskCondition.make_card, 1, "");
        }
        //
        if (propList.size() > 0) {
            propList = PropSimple.getPropListComposite(propList);
            propManager.addPropList(teamId, propList, true, ModuleLog.getModuleLog(EModuleCode.球星卡, "制卡"));
        }
        collectData.addAllCardList(cardListData);
        collectData.addAllPropList(propListData);
        return collectData;
    }

    /**
     * 球星卡数据接口
     *
     * @return
     */
    @ClientMethod(code = ServiceCode.PlayerCardManager_View)
    public void showPlayerCardView() {
        // 显示所有卡库列表
        sendMessage(getPlayerCardMainData(getTeamId()));
    }

    public PlayerCardPB.PlayerCardMainData getPlayerCardMainData(long teamId) {
        // 显示所有卡库列表
        TeamPlayerCard tpc = getTeamPlayerCard(teamId);
        return getTeamPlayerCardData(tpc);
    }

    /**
     * 协议数据
     *
     * @param teamPlayerCard
     * @return
     */
    public PlayerCardPB.PlayerCardMainData getTeamPlayerCardData(TeamPlayerCard tpc) {
        List<PlayerCardPB.PlayerCardData> dataList = Lists.newArrayList();
        for (Card card : tpc.getAllPlayerCard()) {
            dataList.add(getPlayerCardData(card));
        }
        return PlayerCardPB.PlayerCardMainData.newBuilder().addAllCollectCard(dataList).build();
    }

    /**
     * 协议数据
     *
     * @param card
     * @return
     */
    public PlayerCardPB.PlayerCardData getPlayerCardData(Card card) {
        return PlayerCardPB.PlayerCardData.newBuilder()
            .setType(card.getType())
            .setPlayerId(card.getPlayerId())
            .setStarLv(card.getStarLv())
            .setExp(card.getExp())
            .setQuality(card.getQua())
            .setCostNum(card.getCostNum())
            .build();
    }

    /**
     * 简单卡，收集返回
     *
     * @param card
     * @param status
     * @return
     */
    public PlayerCardPB.SimpleCardData getSimpleCardData(Card card, int status) {
        return PlayerCardPB.SimpleCardData.newBuilder()
            .setType(card.getType())
            .setPlayerId(card.getPlayerId())
            .setStatus(status)
            .build();
    }

    /**
     * 合成并使用，直接扣掉碎片类型
     */
    // TODO 暂时屏蔽掉的功能
    //    @ClientMethod(code = ServiceCode.PlayerCardManager_composite)
    public void composite(int tab) {
        long teamId = getTeamId();
        Builder collectData = CollectData.newBuilder();
        PropSimple needProp = PlayerCardConsole.getCompositeByTab(tab);
        // 是否可以合成
        if (needProp == null) {
            sendMessage(collectData.setCode(ErrorCode.Error.code).build());
            return;
        }
        // 碎片数量是否足够
        if (!propManager.getTeamProp(teamId).checkPropNum(needProp)) {
            sendMessage(collectData.setCode(ErrorCode.Error.code).build());
            return;
        }
        // 扣掉合成道具
        propManager.delProp(teamId, needProp, true, true);
        // 得到合成的卡
        PlayerCardGroupBean bean = PlayerCardConsole.getCompositeCardByTab(tab);
        int playerId = bean.getPlayerList().get(RandomUtil.randInt(bean.getPlayerList().size()));
        //
        List<PropSimple> propList = Lists.newArrayList();
        List<Card> cardList = Lists.newArrayList();
        List<SimpleCardData> cardListData = Lists.newArrayList();
        List<PropSimpleData> propListData = Lists.newArrayList();
        TeamPlayerCard tpc = getTeamPlayerCard(teamId);
        Card card = tpc.createCard(bean.getType(), playerId);
        if (tpc.isCollected(bean.getType(), playerId)) {
            // 如果改变数量，重新new
            PropSimple ps = bean.getRepeatedDrop();
            // 全套卡数量是身价个数
            if (bean.getType() == 0) {
                ps = new PropSimple(ps.getPropId(), ps.getNum() * PlayerConsole.getPlayerBean(playerId).getPrice());
            }
            propList.add(ps);
            propListData.add(PropManager.getPropData(ps));
            cardListData.add(getSimpleCardData(card, 1));
        } else {
            tpc.addToStorage(card);
            cardList.add(card);
            cardListData.add(getSimpleCardData(card, 0));
        }
        //
        if (propList.size() > 0) {
            propList = PropSimple.getPropListComposite(propList);
            propManager.addPropList(teamId, propList, true, ModuleLog.getModuleLog(EModuleCode.球星卡, "合成"));
        }
        collectData.addAllCardList(cardListData);
        collectData.addAllPropList(propListData);
        sendMessage(collectData.setCode(ErrorCode.Success.code).build());
    }

    /**
     * 升星
     * 通过消耗球星经验来提升收集到的球星卡品质
     */
    @ClientMethod(code = ServiceCode.PlayerCardManager_upStarLv)
    public void upStarLv(int type, int playerId, int addExp) {
        long teamId = getTeamId();
        TeamPlayerCard tpc = getTeamPlayerCard(teamId);
        // 判断是否能升级的卡组
        if (addExp < 1) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        if (!PlayerCardConsole.isUpStarLv(type)) {
            log.debug("改类型卡组不能升级星级{},{}", type, playerId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Card_6.code).build());
            return;
        }
        if (!tpc.isCollected(type, playerId)) {
            log.debug("未收集过该球员球星卡{}", playerId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Card_6.code).build());
            return;
        }
        Card card = tpc.getCard(type, playerId);
        if (card == null) {
            log.debug("未收集过该球员球星卡{}", playerId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Card_6.code).build());
            return;
        }
        if (card.getStarLv() + 1 > 5) {
            log.debug("已是最高星级球星卡");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Card_1.code).build());
            return;
        }
        //        int needExp = PlayerCardConsole.getNextStarLvNeed(type, card.getQua(), card.getStarLv() + 1) - card.getExp();
        //        if (needExp <= 0) {
        //            log.debug("已是最高星级球星卡");
        //            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Card_1.code).build());
        //            return;
        //        }
        //        if(addExp > needExp) {
        //        	addExp = needExp;
        //        }
        // 算出应该扣的经验，和可以升的等级
        int currExp = card.getExp();
        int totalExp = currExp + addExp;
        int starLv = PlayerCardConsole.getOneKeyStarLvNeed(type, card.getQua(), totalExp);
        //
        int needPropId = PlayerCardConsole.getPlayerCardGroup(type).getRepeatedDrop().getPropId();
        TeamProp teamProp = propManager.getTeamProp(teamId);
        if (!teamProp.checkPropNum(needPropId, addExp)) {
            log.debug("升星经验不足");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_5.code).build());
            return;
        }
        // 升级
        boolean suc = teamProp.checkPropNum(needPropId, addExp);
        PropSimple needProp = new PropSimple(needPropId, addExp);
        // 扣掉
        propManager.delProp(teamId, needProp, true, true);
        // 满足升级经验才加等级
        if (suc) {
            card.setStarLv(starLv);
        }
        // 保存经验
        card.addExp(needProp.getNum());
        card.save();
        //
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(card.getExp()).setMsg(suc + "").build());
        taskManager.updateTask(teamId, ETaskCondition.球星卡升级, 1, "");
    }
    
    /**
     * 球星卡突破要吞噬一定数量的底薪球员,只有吞噬底薪球员达到一定
     * 的数量才能进行突破.
     * @param type			球星卡类型
     * @param playerId		需要突破的球星卡Id
     * @param delPids		被吞噬的底薪球员Id
     */
    @ClientMethod(code = ServiceCode.PlayerCardManager_breakupCostPlayers)
    public void breakupCostPlayers(int type, int playerId, String delPids) {
        ErrorCode ret = breakupCostPlayers0(type, playerId, new HashSet<>(Arrays.asList(StringUtil.toIntegerArray(delPids))));
        int num = 0;
        if (ret == ErrorCode.Success) {
        	long teamId = getTeamId();
        	TeamPlayerCard tpc = getTeamPlayerCard(teamId);
        	num = tpc.getCard(type, playerId).getCostNum();
		}
        
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ret.code).setMsg(type+","+playerId).setBigNum(num).build());
    }
    
    private ErrorCode breakupCostPlayers0(int type, int playerId, Set<Integer> delPids) {
        long teamId = getTeamId();
        TeamPlayerCard tpc = getTeamPlayerCard(teamId);
        if (!tpc.isCollected(type, playerId)) {
            return ErrorCode.Card_6;
        }
        Card card = tpc.getCard(type, playerId);
        if (card == null) {
            return ErrorCode.Card_6;
        }
        int needCount = PlayerCardConsole.getNextStarLvNeedLowPrice(type, card.getQua(), card.getStarLv());
        if (needCount == 0) {
            return ErrorCode.Common_6;
        }
        
        // 底薪数量
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        int minPrice = playerManager.getPlayerMinPrice(playerId);
        Map<Integer, Player> minPlayers = tp.getStoragePlayerList(playerId, minPrice);
        if (minPlayers == null || ((card.getCostNum() + delPids.size()) > needCount)) {
            return ErrorCode.Common_6;
        }
        if (!minPlayers.keySet().containsAll(delPids)) {
            return ErrorCode.Player_Null;
        }
        if (log.isDebugEnabled()) {
            log.debug("pcard breakup. tid {} type {} prid {} delPids {} neednum {} minprice {} minprs {}", teamId,
                type, playerId, delPids, needCount, minPrice, minPlayers.keySet());
        }
          
        for (Integer pid : delPids) {
            Player p = minPlayers.get(pid);
            if (p.getStorage() == EPlayerStorage.训练馆.getType()) {
                return ErrorCode.Train_Player_Training;
            }        
        }
        
        // 扣掉底薪，突破
        for (Integer pid : delPids) {
           playerManager.removePlayerFromStorage(teamId, pid, moduleLog);
        }
        
        //记录已经被吞噬底薪球员的数量
        card.setCostNum(card.getCostNum() + delPids.size());
        card.save();
        
        return ErrorCode.Success;
    }

    /**
     * 底薪球员消耗已经满足,直接可以突破.
     * 球星卡突破之后才会重新进入新一轮的升级。
     * 突破操作需增加特效。
     * 就是提升品质，消耗底薪个数跟品质有关
     * @param delPids 要消耗的低薪球员id列表
     */
    @ClientMethod(code = ServiceCode.PlayerCardManager_breakup)
    public void breakup(int type, int playerId, String delPids) {
        ErrorCode ret = breakup0(type, playerId);
        sendMessage(ret);
    }
    
    private ErrorCode breakup0(int type, int playerId) {
        long teamId = getTeamId();
        TeamPlayerCard tpc = getTeamPlayerCard(teamId);
        if (!tpc.isCollected(type, playerId)) {
            return ErrorCode.Card_6;
        }
        Card card = tpc.getCard(type, playerId);
        if (card == null) {
            return ErrorCode.Card_6;
        }
        int needCount = PlayerCardConsole.getNextStarLvNeedLowPrice(type, card.getQua(), card.getStarLv());
        if (needCount == 0 || needCount != card.getCostNum()) {
            return ErrorCode.Common_6;
        }
        
        card.setStarLv(0);
        card.setQuality(card.getQuality() + 1);
        card.setCostNum(0);//被吞噬的球员数量重置为0
        card.save();
        return ErrorCode.Success;
    }

//    private ErrorCode breakup0(int type, int playerId, Set<Integer> delPids) {
//        long teamId = getTeamId();
//        TeamPlayerCard tpc = getTeamPlayerCard(teamId);
//        if (!tpc.isCollected(type, playerId)) {
//            return ErrorCode.Card_6;
//        }
//        Card card = tpc.getCard(type, playerId);
//        if (card == null) {
//            return ErrorCode.Card_6;
//        }
//        int needCount = PlayerCardConsole.getNextStarLvNeedLowPrice(type, card.getQua(), card.getStarLv());
//        if (needCount == 0) {
//            return ErrorCode.Common_6;
//        }
//        
//        // 底薪数量
//        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
//        int minPrice = playerManager.getPlayerMinPrice(playerId);
//        Map<Integer, Player> minPlayers = tp.getStoragePlayerList(playerId, minPrice);
//        if (minPlayers == null || minPlayers.size() < needCount || delPids.size() < needCount) {
//            return ErrorCode.Common_6;
//        }
//        if (!minPlayers.keySet().containsAll(delPids)) {
//            return ErrorCode.Player_Null;
//        }
//        if (log.isDebugEnabled()) {
//            log.debug("pcard breakup. tid {} type {} prid {} delPids {} neednum {} minprice {} minprs {}", teamId,
//                type, playerId, delPids, needCount, minPrice, minPlayers.keySet());
//        }
//          
//        for (Integer pid : delPids) {
//            Player p = minPlayers.get(pid);
//            if (p.getStorage() == EPlayerStorage.训练馆.getType()) {
//                return ErrorCode.Train_Player_Training;
//            }        
//        }
//        
//        // 扣掉底薪，突破
//        for (Integer pid : delPids) {
//           playerManager.removePlayerFromStorage(teamId, pid, moduleLog);
//        }
//        
//        card.setStarLv(0);
//        card.setQuality(card.getQuality() + 1);
//        card.save();
//        return ErrorCode.Success;
//    }

    public void clearCardData(long teamId, int type, int playerId) {
        TeamPlayerCard tpc = getTeamPlayerCard(teamId);
        if (tpc == null) {
            return;
        }
        Card card = tpc.getCard(type, playerId);
        if (card == null) {
            return;
        }
        card.setStarLv(0);
        card.setQuality(0);
        card.addExp(0 - card.getExp());
        card.save();
    }

}

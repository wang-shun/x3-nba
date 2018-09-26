package com.ftkj.manager.playercard;

import com.ftkj.console.PlayerCardConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.db.domain.CardPO;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.player.PlayerBean;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamPlayerCard {

    /**
     * 球队id
     */
    private long teamId;
    /**
     * 收集列表
     * 卡组: 收集列表;
     */
    private Map<Integer, List<Card>> collectMap;

    public TeamPlayerCard(long teamId, List<CardPO> cardList) {
        this.teamId = teamId;
        this.collectMap = cardList.stream().map(po -> new Card(po)).collect(Collectors.groupingBy(v -> v.getType(), Collectors.toList()));
    }

    /**
     * 制卡
     *
     * @param playerId 球员ID
     * @param type     所属卡组
     * @return
     */
    public Card createCard(int type, int playerId) {
        CardPO card = new CardPO();
        card.setTeamId(teamId);
        card.setPlayerId(playerId);
        card.setType(type);
        card.setCreateTime(DateTime.now());
        return new Card(card);
    }

    /**
     * 添加到卡库
     * 会自动调用save()
     */
    public synchronized void addToStorage(Card card) {
        int type = card.getType();
        if (!this.collectMap.containsKey(type)) {
            this.collectMap.put(type, Lists.newArrayList());
        }
        this.collectMap.get(type).add(card);
        card.save();
    }

    /**
     * 卡组取卡
     *
     * @param type
     * @param playerId
     * @return
     */
    public Card getCard(int type, int playerId) {
        if (this.collectMap.containsKey(type)) {
            return this.collectMap.get(type).stream().filter(c -> c.getPlayerId() == playerId).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * 是否收集过该球员
     *
     * @param type     卡组
     * @param playerId 球员ID
     * @return true收集过
     */
    public boolean isCollected(int type, int playerId) {
        if (this.collectMap.containsKey(type)) {
            return this.collectMap.get(type).stream().anyMatch(s -> s.getPlayerId() == playerId);
        }
        return false;
    }

    public long getTeamId() {
        return teamId;
    }

    public List<Card> getTeamCollectCard(int type) {
        return this.collectMap.get(type);
    }

    public List<Card> getAllPlayerCard() {
        List<Card> list = Lists.newArrayList();
        for (List<Card> li : collectMap.values()) {
            list.addAll(li);
        }
        return list;
    }

    public Map<Integer, List<Card>> getCollectMap() {
        return collectMap;
    }

    /**
     * 球员单卡加成
     * 单卡加成 = 对应球员当前进攻值*α + 当前防守值*β；
     *
     * @param playerId
     * @return
     */
    public int getCardCap(int playerId, int type) {
        Card card = getCard(type, playerId);
        if (card == null) {
            return 0;
        }
        // 全套卡没球队的不算攻防.
        PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
        if (pb == null || (type == 0 && pb.getTeamId() == 0)) {
            return 0;
        }
        float cap = PlayerCardConsole.getPlayerGradeCap(type, card.getQua(), card.getStarLv()).getBaseCap(pb.getGrade());
        float val = (pb.getAbility(EActionType.ocap) + pb.getAbility(EActionType.dcap)) * cap;
        int roundVal = Math.round(val);
        //System.err.println(pb.getShortName()+",单卡:" + val +"  round:" + roundVal);
        return roundVal;
    }

    /**
     * 卡组加成
     * 卡组加成 = 该卡组内的所有球星卡当前基数之和；
     *
     * @param type
     * @return
     */
    public int getGroupCap(int type) {
        List<Card> cardList = collectMap.get(type);
        // TODO 全明星卡组比较特殊，去重组合再计算攻防.
        return cardList.stream().mapToInt(card -> getCardCap(card.getPlayerId(), card.getType())).sum();
    }

    public float[] getPlayerCardCap(int type, int groupTotal, int playerId) {
        float jg = 0;
        float fs = 0;
        Card card = getCard(type, playerId);
        if (card == null) {
            return new float[]{jg, fs};
        }
        PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
        float capRate = PlayerCardConsole.getPlayerGradeCap(type, card.getQua(), card.getStarLv()).getCardCap(pb.getGrade());
        float playerBase = pb.getAbility(EActionType.ocap) + pb.getAbility(EActionType.dcap);
        float total = playerBase * groupTotal * capRate;
        jg += total * 0.5f;
        fs += total * 0.5f;
        return new float[]{jg, fs};
    }

    //	/**
    //	 * 根据卡组类型搜索卡组
    //	 * @param type
    //	 * @return
    //	 */
    //	private List<Card> getMyCardGroup(int type) {
    //		PlayerCardGroupBean bean = PlayerCardConsole.getPlayerCardGroup(type);
    //		if(bean.getRate() != 0) {
    //			return collectMap.get(type);
    //		}
    //		// 梦幻组合是在所有卡里面去重
    //		return Lists.newArrayList();
    //	}

}

package com.ftkj.manager.draft;

import com.ftkj.console.DraftConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.enums.EDraftStage;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropPlayerBean;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年5月4日
 * 跨服选秀
 */
public class DraftRoom {
	
	private int roomId;
	private DateTime startTime;
	private int roomLevel;
	private List<DraftTeam> teamList;
	private EDraftStage stage;
	/**
	 * 999是自动选开始标识
	 */
	private int curOrder;
	private int []  cardIndex;    
	private List<DraftPlayer> players;
	private DateTime nextTime;
	private Set<String> nodes;
	private DateTime endTime; // 选秀结束时间
	/**生成新秀球员的配置数据*/
	private List<PropSimple> ps;
	
	private static final int second =21;
//	public DraftRoom(int roomId, int roomLevel, List<DraftPlayer> players) {
//		super();
//		this.roomId = roomId;
//		this.roomLevel = roomLevel;
//		this.players = players;
//		this.teamList = Lists.newArrayList();
//		this.stage = EDraftStage.等待玩家;
//		this.curOrder = 0;
//		this.nodes = Sets.newHashSet();
//		DraftRoomBean bean = DraftConsole.getDraftRoomBean(roomLevel);
//		if(bean.getTime() != null && !bean.getTime().equals("")) {
//			// 默认1个小时结束
//			this.endTime = DateTime.now().plusHours(1);
//		}
//	}
	
	/**
	 * 生成选秀房构造函数，原先生成选秀房直接生成了球员数据，包括球员的工资，这样会
	 * 导致如果球员的工资在每天的16点更新了，这里还是旧的工资数据。现在改为不先生成新秀球员，
	 * 等到选秀房间报名的玩家已经满了的时候在生成新秀球员的数据。
	 * @param ps			简单的道具对象，用于生成新秀球员
	 * @param roomId		房间的Id
	 * @param roomLevel		房间的等级(1-4级)
	 */
	public DraftRoom(List<PropSimple> ps, int roomId, int roomLevel) {
		super();
		this.roomId = roomId;
		this.roomLevel = roomLevel;
		this.ps = ps;
		this.teamList = Lists.newArrayList();
		this.stage = EDraftStage.等待玩家;
		this.curOrder = 0;
		this.nodes = Sets.newHashSet();
		DraftRoomBean bean = DraftConsole.getDraftRoomBean(roomLevel);
		if(bean.getTime() != null && !bean.getTime().equals("")) {
			// 默认1个小时结束
			this.endTime = DateTime.now().plusHours(1);
		}
	}

	public void putTeam(DraftTeam team){
		this.teamList.add(team);
		this.nodes.add(team.getNodeName());
	}
	
	public DraftTeam getCurTeam(){
		return this.teamList.stream().filter(team->team.getOrder()==this.curOrder).findFirst().orElse(null);
	}
	
	/**
	 * 没有签的自动千
	 * @return
	 */
	public List<DraftTeam> getAutoSignTeam() {
		return this.teamList.stream().filter(team->team.isAuto() && !team.isSign()).collect(Collectors.toList());
	}
	
	public DraftTeam getDraftTeam(long teamId){
		return this.teamList.stream().filter(team->team.getTeamId()==teamId).findFirst().orElse(null);
	}
	
	/**
	 * 原旧选秀开始抽签。
	 */
	public void start(){
		this.startTime = DateTime.now();
		this.nextTime = this.startTime.plusSeconds(second);//30秒抽签
		this.stage = EDraftStage.抽签阶段;
		this.curOrder = 0;
		List<DraftTeam> tmpList = Lists.newArrayList(teamList);
		Collections.shuffle(tmpList);
		this.cardIndex = new int[teamList.size()];
		for(int i = 0 ; i < tmpList.size();){//命中玩家顺序
			cardIndex[i] = -1;
			tmpList.get(i).setOrder(++i);
		}
		
	}
	
	/**
	 * 选秀开始抽签，并且修改房间状态为抽签阶段，生成参加选秀的新秀球员。
	 * @param _initDrop		物品掉落
	 */
	public void start(DropBean _initDrop){
		this.startTime = DateTime.now();
		this.nextTime = this.startTime.plusSeconds(second);//30秒抽签
		this.stage = EDraftStage.抽签阶段;
		this.curOrder = 0;
		List<DraftTeam> tmpList = Lists.newArrayList(teamList);
		Collections.shuffle(tmpList);
		this.cardIndex = new int[teamList.size()];
		for(int i = 0 ; i < tmpList.size();){//命中玩家顺序
			cardIndex[i] = -1;
			tmpList.get(i).setOrder(++i);
		}
		
		//生成选秀的新秀球员对象
		List<DraftPlayer> playersList = null;
		playersList = ps.stream().map(p -> (PropPlayerBean) (PropConsole.getProp(p.getPropId())))
				.distinct().map(p -> PlayerConsole.getPlayerBean(p.getHeroId()))
				.map(p -> new DraftPlayer(p.getPlayerRid(), p.getPrice(),PlayerTalent.createPlayerTalent(0, p.getPlayerRid(), 0, _initDrop, false)))
				.collect(Collectors.toList());
		// 返回按球员工资降序排序的集合
		this.players = playersList.stream().sorted((a, b) -> b.getPrice() - a.getPrice()).collect(Collectors.toList());
//		players.forEach(player -> {System.out.println("生成的球员Id:"+player.getPlayerId()+",球员的工资:"+player.getPrice());});
	}
	
	public void signPlayer(int playerId,DraftTeam team){
		DraftPlayer player  = getPlayer(playerId);
		player.setSignTeamName(team.getTeamName());
		team.setSign(true);
	}
	
	/**
	 * 下一个抽
	 */
	public void startNext(int currOrder) {
		//全部人选完了.修改状态为结束
		if(this.teamList.stream().allMatch(t-> t.isSign())){
			updateStage(EDraftStage.结束);
		}
		if(currOrder >= this.teamList.size()) {
			// 如果有自动选的，走自动选流程, 999特殊标识
			this.curOrder = 999; 
			return;
		}
		this.curOrder = currOrder + 1;
		this.nextTime = DateTime.now().plusSeconds(second);
	}
	
	public DraftPlayer getPlayer(int playerId){
		return this.players.stream().filter(player->player.getPlayerId() == playerId).findFirst().orElse(null);
	}
	
	public void updateStage(EDraftStage stage){
		this.stage = stage;
		this.nextTime = DateTime.now().plusSeconds(second);//30秒抽签\
		this.curOrder = 0;
		if(stage == EDraftStage.结束) {
			this.teamList.clear();
			this.endTime = DateTime.now();
		}
	}
	
	public Set<String> getNodes() {
		return nodes;
	}

	public int getNextSecond(){
		int second = DateTimeUtil.secondBetween(this.nextTime);
		return -second;
	}
	
	public DateTime getNextTime() {
		return nextTime;
	}

	public void setNextTime(DateTime nextTime) {
		this.nextTime = nextTime;
	}

	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public DateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}
	public int getRoomLevel() {
		return roomLevel;
	}
	public void setRoomLevel(int roomLevel) {
		this.roomLevel = roomLevel;
	}
	public List<DraftTeam> getTeamList() {
		return teamList;
	}
	public void setTeamList(List<DraftTeam> teamList) {
		this.teamList = teamList;
	}
	public EDraftStage getStage() {
		return stage;
	}
	public void setStage(EDraftStage stage) {
		this.stage = stage;
	}
	public int getCurOrder() {
		return curOrder;
	}
	public void setCurOrder(int curOrder) {
		this.curOrder = curOrder;
	}
	public List<DraftPlayer> getPlayers() {
		return players;
	}
	public void setPlayers(List<DraftPlayer> players) {
		this.players = players;
	}

	public int[] getCardIndex() {
		return cardIndex;
	}

//	public void setCardIndex(int[] cardIndex) {
//		this.cardIndex = cardIndex;
//	}

	public DateTime getEndTime() {
		return endTime;
	}

	public long getEndTimeMillis() {
		if(this.endTime == null) {
			return 0L;
		}
		return this.endTime.getMillis();
	}
	
	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}
	
	/**
	 * 获取生成新秀球员的配置数据
	 * @return
	 */
	public List<PropSimple> getPs() {
		return ps;
	}

	@Override
	public String toString() {
		return "DraftRoom [roomId=" + roomId + ", roomLevel=" + roomLevel + ", endTime=" + endTime + "]";
	}
	
}

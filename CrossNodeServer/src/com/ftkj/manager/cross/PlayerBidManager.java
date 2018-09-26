package com.ftkj.manager.cross;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.db.ao.common.INBADataAO;
import com.ftkj.db.domain.NBAPKScoreBoardDetail;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.bid.PlayerBidEndSource;
import com.ftkj.manager.bid.PlayerBidGuessDetail;
import com.ftkj.manager.bid.PlayerBidGuessMain;
import com.ftkj.manager.bid.PlayerBidGuessSimple;
import com.ftkj.manager.bid.TeamGuess;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.proto.GameLoadPB;
import com.ftkj.proto.PlayerBidPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.lambda.LBInt;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2018年3月22日
 * 球员竞价-跨服管理
 */
public class PlayerBidManager extends BaseManager {

	private Map<Integer,PlayerBidGuessDetail> playeDetailMap;
	private PlayerBidPB.PlayerBidBeforeMainData bidBeforeData;
	
	@IOC
	private INBADataAO nbaDataAO;
	
	private int maxPeople;
	private int _S2Count = 1;
	private int _SCount =2;
	
	
	private int _peopleMod;
	
	
	
	/**
	 * 竞价前界面
	 */
	@RPCMethod(code=CrossCode.PlayerBidManager_showBidBeforeMain,pool=EServerNode.Cross,type=ERPCType.MASTER)
	public void showBidBeforeMain(){
		RPCMessageManager.responseMessage(bidBeforeData);
	}
	
	
	/**
	 * 更新新的竞价数据
	 */
	public void updatePlayerBidBeforeData(){
		this.bidBeforeData = createRoom(new LBInt());
		//通知逻辑服务器，更新竞价信息
		RPCMessageManager.sendMessage(CrossCode.LocalPlayerBidManagger_reloadBeforeMain, null);
	}
	
	
	/**
	 * 竞价球员
	 * @param id
	 * @param team
	 */
	@RPCMethod(code=CrossCode.PlayerBidManager_bidPlayer,pool=EServerNode.Cross,type=ERPCType.MASTER)
	public void bidPlayer(TeamGuess team){
		int id = team.getId();
		PlayerBidGuessDetail detail = getPlayerGuessDetail(id);
		ErrorCode result = ErrorCode.Error;
		if(detail == null){
			log.error("异常的Id，取出无用数据:{}->{}",id,team);
		}else{
			result = detail.addTeam(team);
		}
		RPCMessageManager.responseMessage(result);
	}
	
	/**
	 * 这里能否重新？会重复发邮件？
	 */
	public void endBid(){ 
		//活动结束，结算结果
		Map<String,PlayerBidEndSource> nodeEndSource = Maps.newConcurrentMap();
		playeDetailMap.values().forEach(detail->{
			List<TeamGuess> teamList = detail.getWinTeamList(_peopleMod);
			Map<String,Set<Long>> teams = teamList.stream()
					.collect(Collectors.groupingBy(TeamGuess::getNode,
							Collectors.mapping(TeamGuess::getTeamId, Collectors.toSet())));
			teams.forEach((key,val)->_initPlayerBidEndSource(nodeEndSource,key,val,detail.getId()));
		});
		nodeEndSource.forEach((node,nodeSource)->RPCMessageManager.sendMessage(CrossCode.LocalPlayerBidManagger_endBid, node, nodeSource));
	}
	
	
	private void _initPlayerBidEndSource(Map<String,PlayerBidEndSource> nodeEndSource,String key,Set<Long> val,int id){
		PlayerBidEndSource source = nodeEndSource.computeIfAbsent(key, (kk)->new PlayerBidEndSource());
		source.putAllTeam(id, val);
	}
	
	public void autoUpdateBidSource(){
		Map<Integer,PlayerBidGuessSimple> guessMaps = playeDetailMap.values().stream()
				.collect(Collectors.toMap(PlayerBidGuessDetail::getId, 
						detail->new PlayerBidGuessSimple(detail.getId(),detail.getPlayerId(),detail.getMaxPrice(),detail.getPeople(),detail.getGroup(),detail.getLogTeamList())));
		PlayerBidGuessMain data = new PlayerBidGuessMain(guessMaps, maxPeople);
		RPCMessageManager.sendMessage(CrossCode.localplayerbidmanagger_updateguessmain, null, data);
	}
	@RPCMethod(code=CrossCode.PlayerBidManager_initPlayerBidBeforeData,pool=EServerNode.Cross,type=ERPCType.MASTER)
	public void initPlayerBidBeforeData(){
		Map<Integer,PlayerBidGuessSimple> guessMaps = playeDetailMap.values().stream()
				.collect(Collectors.toMap(PlayerBidGuessDetail::getId, 
						detail->new PlayerBidGuessSimple(detail.getId(),detail.getPlayerId(),detail.getMaxPrice(),detail.getPeople(),detail.getGroup(),detail.getLogTeamList())));
		PlayerBidGuessMain data = new PlayerBidGuessMain(guessMaps, maxPeople);
		RPCMessageManager.responseMessage(data);
		
		
	}
	
	
	
	
	private PlayerBidGuessDetail getPlayerGuessDetail(int id){
		return this.playeDetailMap.get(id);
	}
	
	private boolean initRoom(List<PlayerBidGuessDetail> resultList,List<Integer> levelList,int group,int index){
		List<Integer> groupList = Stream.generate(()->group).limit(5).collect(Collectors.toList());
//    	Collections.shuffle(groupList);
    	List<EPlayerPosition> positionList = Lists.newArrayList(EPlayerPosition.values());
    	positionList = positionList.stream().filter(position->position!=EPlayerPosition.NULL).collect(Collectors.toList());
    	Collections.shuffle(positionList);
    	//
    	int x = 0;
    	for(int i = 0 ; i < groupList.size() && x<=1000;x++){
    		int level = levelList.get(index);
    		EPlayerGrade [] grades = getGrade(level);
    		PlayerBean player = PlayerConsole.getRanPlayerByPosition(positionList.get(i), grades[0],grades[1]);
        	if(player == null || resultList.stream().filter(p->p.getPlayerId()==player.getPlayerRid()).findFirst().isPresent()) continue;
        	resultList.add(_instanceGuessDetail(index,player,groupList.get(i),positionList.get(i).getId()));
        	i++;
        	index++;
    	}
    	return x>=1000;
	}
	
    private PlayerBidPB.PlayerBidBeforeMainData createRoom(LBInt max) {
    	List<PlayerBidGuessDetail> resultList = Lists.newArrayList();
    	List<PlayerBidPB.PlayerBidPlayerDetailData> detailDataList = Lists.newArrayList();
    	//初始随机池中的数据
    	int index = 0;
//    	List<Integer> groupList = Stream.generate(()->1).limit(5).collect(Collectors.toList());
//    	Collections.shuffle(groupList);
//    	List<EPlayerPosition> positionList = Lists.newArrayList(EPlayerPosition.values());
//    	positionList = positionList.stream().filter(position->position!=EPlayerPosition.NULL).collect(Collectors.toList());
    	List<Integer> levelList = Stream.generate(()->1).limit(_S2Count).collect(Collectors.toList());
    	levelList.addAll(Stream.generate(()->2).limit(_SCount).collect(Collectors.toList()));
    	levelList.addAll(Stream.generate(()->3).limit(10-_S2Count-_SCount).collect(Collectors.toList()));
    	Collections.shuffle(levelList);
    	//找不到球员，递归
    	if(initRoom(resultList, levelList, 1, index) && max.increaseAndGet()<200){
    		return createRoom(max);
    	}
    	
    	if(initRoom(resultList, levelList, 2, 5) && max.increaseAndGet()<200){
    		return createRoom(max);
    	}
    	
//    	groupList.addAll(Stream.generate(()->2).limit(5).collect(Collectors.toList()));
//    	positionList.addAll(Lists.newArrayList(EPlayerPosition.values()));
//    	Collections.shuffle(positionList);

    	
    	
//    	for(int i = 0 ,x =0; i < groupList.size() && x<=1000;x++){
//    		int level = levelList.get(index);
//    		EPlayerGrade [] grades = getGrade(level);
//    		PlayerBean player = PlayerConsole.getRanPlayerByPosition(positionList.get(index), grades[0],grades[1]);
//        	if(player == null || resultList.stream().filter(p->p.getPlayerId()==player.getPlayerId()).findFirst().isPresent()) continue;
//        	resultList.add(_instanceGuessDetail(index,player,groupList.get(index),positionList.get(index).getId()));
//        	i++;
//        	index++;
//    	}
//    	
//    	
//    	//
//        for(int i = 0,x=0 ; i < _S2Count && x<=1000;x++){
//        	PlayerBean player = PlayerConsole.getRanPlayerByPosition(positionList.get(index), EPlayerGrade.S2,EPlayerGrade.S2);
//        	if(player == null || resultList.stream().filter(p->p.getPlayerId()==player.getPlayerId()).findFirst().isPresent()) continue;
//        	resultList.add(_instanceGuessDetail(index,player,groupList.get(index),positionList.get(index).getId()));
//        	i++;
//        	index++;
//        }
//        for(int i = 0,x=0 ; i < _SCount && x<=1000;x++){
//        	PlayerBean player = PlayerConsole.getRanPlayerByPosition(positionList.get(index), EPlayerGrade.S,EPlayerGrade.S1);
//        	if(player == null || resultList.stream().filter(p->p.getPlayerId()==player.getPlayerId()).findFirst().isPresent()) continue;
//        	resultList.add(_instanceGuessDetail(index,player,groupList.get(index),positionList.get(index).getId()));
//        	i++;
//        	index++;
//        }
//        int endSize = 10-index;
//        for(int i = 0,x=0 ; i < endSize && x<=1000;x++){
//        	PlayerBean player = PlayerConsole.getRanPlayerByPosition(positionList.get(index), EPlayerGrade.B,EPlayerGrade.A2);
//        	if(player == null || resultList.stream().filter(p->p.getPlayerId()==player.getPlayerId()).findFirst().isPresent()) continue;
//        	resultList.add(_instanceGuessDetail(index,player,groupList.get(index),positionList.get(index).getId()));
//        	i++;
//        	index++;
//        }
        //
        this.playeDetailMap = resultList.stream().collect(Collectors.toMap(PlayerBidGuessDetail::getId, val->val));
        resultList.forEach(p->detailDataList.add(getPlayerBidPlayerDetailData(p)));
        return PlayerBidPB.PlayerBidBeforeMainData.newBuilder().addAllBidPlayerList(detailDataList).build();
    }
    private static final EPlayerGrade[] level1 = new EPlayerGrade[]{EPlayerGrade.S2,EPlayerGrade.S2};
    private static final EPlayerGrade[] level2 = new EPlayerGrade[]{EPlayerGrade.S1,EPlayerGrade.S};
    private static final EPlayerGrade[] level3 = new EPlayerGrade[]{EPlayerGrade.B,EPlayerGrade.A2};
    
    private EPlayerGrade[] getGrade(int level){
    	if(level == 1){
    		return level1;
    	}else if(level == 2){
    		return level2;
    	}else {
    		return level3;
    	}
    }
    
    private PlayerBidPB.PlayerBidPlayerDetailData getPlayerBidPlayerDetailData(PlayerBidGuessDetail detail){
    	
    	return PlayerBidPB.PlayerBidPlayerDetailData.newBuilder().setPlayerGroup(detail.getGroup()).setPlayerPosition(detail.getPosition())
    				.setAbilitys(getNBAPlayerAbilityData(detail.getDetail())).setId(detail.getId()).build();
    	
    }
    
    private GameLoadPB.NBAPKScoreBoardDetailData getNBAPlayerAbilityData(NBAPKScoreBoardDetail nbaPKScoreBoardDetail){
        return GameLoadPB.NBAPKScoreBoardDetailData.newBuilder()
								        .setAst(nbaPKScoreBoardDetail.getAst())
								        .setBlk(nbaPKScoreBoardDetail.getBlk())
								        .setDreb(nbaPKScoreBoardDetail.getDreb())
								        .setEffectPoint(nbaPKScoreBoardDetail.getEffectPoint())
								        .setFga(nbaPKScoreBoardDetail.getFga())
								        .setFgm(nbaPKScoreBoardDetail.getFgm())
								        .setFta(nbaPKScoreBoardDetail.getFta())
								        .setFtm(nbaPKScoreBoardDetail.getFtm())
								        .setGameId(nbaPKScoreBoardDetail.getGameId())
								        .setId(nbaPKScoreBoardDetail.getId())
								        .setIsStarter(nbaPKScoreBoardDetail.getIsStarter())
								        .setMin(nbaPKScoreBoardDetail.getMin())
								        .setOreb(nbaPKScoreBoardDetail.getOreb())
								        .setPf(nbaPKScoreBoardDetail.getPf())
								        .setPlayerId(nbaPKScoreBoardDetail.getPlayerId())
								        .setPts(nbaPKScoreBoardDetail.getPts())
								        .setReb(nbaPKScoreBoardDetail.getReb())
								        .setStl(nbaPKScoreBoardDetail.getStl())
								        .setTeamId(nbaPKScoreBoardDetail.getTeamId())
								        .setThreePa(nbaPKScoreBoardDetail.getThreePa())
								        .setThreePM(nbaPKScoreBoardDetail.getThreePm())
								        .setTo(nbaPKScoreBoardDetail.getTo())
								        .build();
    }
    
    private PlayerBidGuessDetail _instanceGuessDetail(int id,PlayerBean p,int group,int position){
    	List<NBAPKScoreBoardDetail>  list = nbaDataAO.getNBAPKScoreBoardDetailByPlayer(p.getPlayerRid());
    	NBAPKScoreBoardDetail detail = new NBAPKScoreBoardDetail();
    	if(list != null && list.size()>0){
    		detail = list.get(RandomUtil.randInt(list.size()));
    	}
    	return new PlayerBidGuessDetail(id,p.getPlayerRid(),group,position,detail);
    }
	
	@Override
	public void initConfig() {
		_peopleMod = ConfigConsole.getIntVal(EConfigKey.Training_num);
	}

	@Override
	public void instanceAfter() {
		playeDetailMap = Maps.newConcurrentMap();
		updatePlayerBidBeforeData();
	}

}

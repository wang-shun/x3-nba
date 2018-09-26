package com.ftkj.manager.active.starload;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.AtvStarLoadPB;
import com.ftkj.proto.AtvStarLoadPB.AtvTeamRankData;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.GameSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class AtvStarLoadBaseManager extends ActiveBaseManager implements IStarLoadManager {
    private static final Logger log = LoggerFactory.getLogger(AtvStarLoadBaseManager.class);
	/**
	 * 排名列表
	 */
	protected List<AtvTeamRank> rank;
	
	@Override
	public void instanceAfter() {
		super.instanceAfter();
		loadRank();

	}
	
	private void loadRank() {
		if(getStatus() == EActiveStatus.已结束) {
			return;
		}
		// 从DB加载排名
		this.rank = Lists.newArrayList();
		List<ActiveBasePO> rankList = activeAO.queryActiveDataOrderByRank(GameSource.shardId, this.getId(), 3);
		for(ActiveBasePO po : rankList) {
			AtvPlayerStarRoad obj = new AtvPlayerStarRoad(po);
			checkUpdateRank(obj, obj.getRankValue(), obj.getRankValue(), true);
		}
	}
	
	/**
	 * 达到上榜条件，更新排名
	 * @param atvObj
	 */
	protected void updateRank(AtvPlayerStarRoad atvObj) {
		synchronized(this) {
			// 小于最后一名，直接退出
			if(this.rank.size() > 3 && atvObj.getRankValue() <= this.rank.get(this.rank.size()-1).getValue()) {
				return;
			}
			// 重复上榜，更新数据
			if(this.rank.stream().anyMatch(s-> s.getTeamId() == atvObj.getTeamId())) {
				AtvTeamRank tmp = this.rank.stream().filter(s-> s.getTeamId() == atvObj.getTeamId()).findFirst().get();
				// 只更新数值，不更新时间
				if(atvObj.getRankValue() > tmp.getRank()) {
					tmp.setUpdateTime(atvObj.getUpdateTime());
				}
				tmp.setValue(atvObj.getRankValue());
			}else {
				// 添加并更新排名
				this.rank.add(new AtvTeamRank(atvObj.getTeamId(), atvObj.getRankValue(), atvObj.getFinishTime()));
			}
			this.rank = this.rank.stream().sorted(new Comparator<AtvTeamRank>() {
				@Override
				public int compare(AtvTeamRank o1, AtvTeamRank o2) {
					return o1.getValue() == o2.getValue() ? (int)(o1.getUpdateTime().getMillis() - o2.getUpdateTime().getMillis()) : o2.getValue() - o1.getValue();
				}
			}).limit(3).collect(Collectors.toList());
			// 更新排名
			int rank = 0;
			for(AtvTeamRank r : this.rank) {
				rank += 1;
				r.setRank(rank);
			}
		}
	}
	
	/**
	 * 取排名，0是未上榜
	 * @param teamId
	 * @return
	 */
	protected int getMyRank(long teamId) {
		for(AtvTeamRank r : this.rank) {
			if(r.getTeamId() == teamId) return r.getRank();
		}
		return 0;
	}
	
	protected List<AtvTeamRankData> getRankData() {
		List<AtvTeamRankData> dataList = Lists.newArrayList();
		for(AtvTeamRank r : this.rank) {
			Team team = teamManager.getTeam(r.getTeamId());
			int rank = r.getRank();
			dataList.add(AtvTeamRankData.newBuilder()
					.setTeamId(team.getTeamId())
					.setTeamName(team.getName())
					.setLv(getRankValueData(team.getTeamId()))
					.setRank(rank)
					.build());
		}
		return dataList;
	}
	
	/**
	 * 前端显示的排名数据
	 * @return
	 */
	public int getRankValueData(long teamId) {
		AtvPlayerStarRoad atvObj = getTeamData(teamId);
		return atvObj.getRankValue();
	}
	
	/**
	 * 排名奖励未领取的将自动发件
	 */
	private void sendawardEmail() {
		for(AtvTeamRank rank : this.rank) {
			AtvPlayerStarRoad atvObj = getTeamData(rank.getTeamId());
			int idx = 13+rank.getRank();
			if(atvObj.getStatus().getValue(idx) == 2) {
				continue;
			}
			atvObj.getStatus().setValue(idx, 2);
			atvObj.save();
			// 发邮件
			String awardConfig = PropSimple.getPropStringByListNotConfig(getAwardConfigList().get(idx).getPropSimpleList());
			emailManager.sendEmailFinal(rank.getTeamId(), 1, 0, "巨星之路["+getBean().getName()+"]-排名奖励", "第"+rank.getRank()+"名", awardConfig);
		}
	}
	
	private void updateRankStatus() {
		for(AtvTeamRank rank : this.rank) {
			AtvPlayerStarRoad atvObj = getTeamData(rank.getTeamId());
			int idx = 13+rank.getRank();
			atvObj.getStatus().setValue(idx, 1);
			atvObj.save();
		}
	}
	
	@Override
	public void everyDayStart(DateTime time) {
		super.everyDayStart(time);
		if(GameSource.getServerStartDay() == 8) {
			updateRankStatus();
		}
		else if(GameSource.getServerStartDay() == 9) {
			sendawardEmail();
		}
	}
	
	/**
	 * 主界面
	 */
	@Override
	public void showView() { 
		long teamId = getTeamId();
		AtvPlayerStarRoad atvObj = getTeamData(teamId);
		int rank = getMyRank(teamId);
		int rankStatus = rank > 0 ?atvObj.getStatus().getValue(13+rank) : 0;
		sendMessage(AtvStarLoadPB.StarLoadData.newBuilder()
				.setValue(atvObj.getCount())
				.addAllStatus(atvObj.getStatus().getList())
				.setRank(rank)
				.setRankStatus(rankStatus)
				.addAllRankList(getRankData())
				.setServerStartDay(GameSource.getServerStartDay())
				.setAtvId(this.getId())
				.build());
		
	}
	
	/**
	 * 领取奖励
	 */
	@Override
	public void getAward(int id) {
		if(getStatus() == EActiveStatus.已结束) {
			log.debug("活动[{}]已结束!", this.getId());
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_2.code).build());
			return;
		}
		Team team = teamManager.getTeam(getTeamId());
		// 只能是当天领取当天的奖励
		SystemActiveCfgBean awardCfg = getAwardConfigList().get(id);
		if(awardCfg == null) {
			log.debug("兑换奖励类型[{}]没有找到!", id);
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_3.code).build());
			return;
		}
		int createDay = team.getCreateDay();
		if(id > createDay) {
			log.debug("[{}]不能提前兑换奖励[{}]!", createDay, id);
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_4.code).build());
			return;
		}
		AtvPlayerStarRoad atvObj = getTeamData(team.getTeamId());
		if(atvObj.getStatus().getValue(id-1) != 1) {
			log.debug("不满足兑换条件{}!", id);
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_5.code).build());
			return;
		}
		// 发奖励
		atvObj.getStatus().setValue(id-1, 2);
		atvObj.save();
		propManager.addPropList(team.getTeamId(), awardCfg.getPropSimpleList(), true, getActiveModuleLog());
		sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		//
		redPointPush(team.getTeamId());
	}
	
	/**
	 * 排名奖励
	 */
	@Override
	public void getRankAward() {
		if(getStatus() == EActiveStatus.已结束) {
			log.debug("活动[{}]已结束!", getId());
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_2.code).build());
			return;
		}
		if(GameSource.getServerStartDay() != 8) {
			log.debug("还未到领奖时间!{}", GameSource.getServerStartDay());
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_4.code).build());
			return;
		}
		long teamId = getTeamId();
		AtvTeamRank rank = this.rank.stream().filter(t-> t.getTeamId() == teamId).findFirst().orElse(null);
		if(rank == null) {
			log.debug("不在排名上!");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_6.code).build());
			return;
		}
		AtvPlayerStarRoad atvObj = getTeamData(teamId);
		int idx = 13 + rank.getRank();
		if(atvObj.getStatus().getValue(idx) != 1 || atvObj.getStatus().getValue(idx) == 2) {
			log.debug("不在排名上!");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_7.code).build());
			return;
		} 
		//
		atvObj.getStatus().setValue(idx, 2);
		atvObj.save();     
		// 注意奖励配置从1开始
		propManager.addPropList(teamId, getAwardConfigList().get(idx + 1).getPropSimpleList(), true, getActiveModuleLog());
		sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		//
		redPointPush(teamId);
	}
	
	/**
	 * 排名条件
	 * @return
	 */
	protected int getRankCondition() {
		return getConfigInt("num", 0);
	}
	
	/**
	 * 更新排名
	 * @param rankKey
	 * @param count
	 * @param atvObj
	 * @param oldValue 
	 */
	protected void checkUpdateRank(AtvPlayerStarRoad atvObj, int newValue, int oldValue, boolean isLoad) {
		// 上榜排名，开服第7天内结算，先达者完成
		int serverDay = GameSource.getServerStartDay();
		int rankCondition = getRankCondition(); 
		if(getStatus() != EActiveStatus.进行中 || newValue < rankCondition) {
			return;
		}
		if(isLoad || serverDay < 8) {
			if(!isLoad && newValue > oldValue) {
				atvObj.setRankValue(newValue);
				atvObj.setFinishTime(DateTime.now());
				atvObj.save();
			}
			updateRank(atvObj);
		}
	}
	
	protected boolean checkFinish(SystemActiveCfgBean awardCfg, int createDay, AtvPlayerStarRoad atvObj) {
		if(awardCfg == null) {
			log.debug("没有找到对应奖励");
			return false;
		}
		if(awardCfg.getId() > 14) {
			return false;
		}
		// 第8天开始，不可以完成前面的.
//		//所有都可以提前完成
//		//第八天以后只能之前或者当天达成。
		if(createDay > 15) {
			return false;
		}
		if(awardCfg.getId() > 7 && createDay > awardCfg.getId()) {
			return false;
		}
		// 不是这个状态，不可完成
		if(atvObj.getStatus().getValue(awardCfg.getId()-1) != 0) {
			log.debug("已完成过");
			return false;
		}
		return true;
	}
	
	/**
	 * 还有个高等级覆盖低等级数量的处理，只处理Map格式，尽量通用
	 * @param map
	 * @return
	 */
	protected Map<Integer, Long> getMapCountAddLow(Map<Integer, Long> map) {
		Map<Integer, Long> newMap = Maps.newHashMap();
		log.debug("累加之前：{}", map);
		// 高等级覆盖低等级
		int min = map.keySet().stream().mapToInt(i-> i).min().getAsInt();
		int max = map.keySet().stream().mapToInt(i-> i).max().getAsInt();
		for(int lv = min; lv <= max; lv++) {
			if(!map.containsKey(lv)) continue;
			int count = 0 ;
			for(int n=lv+1; n<=max; n++) {
				if(!map.containsKey(n)) continue;
				count += map.get(n);
			}
			newMap.put(lv, map.get(lv) + count);
		}
		log.debug("累加之后：{}", newMap);
		return newMap;
	}
	
	
	/**
	 * @param minKey
	 * @param map
	 * @return
	 */
	protected long getMapCountEqId(int minKey, Map<Integer, Long> map) {
		long count = 0;
		for(Integer key : map.keySet()) {
			if(key < minKey) continue;
			count += map.get(key);
		}
		return count;
	}
	
	/**
	 * 奖励变更提示
	 */
	@Override
	public int redPointNum(long teamId) {
		// 发送事件
		AtvPlayerStarRoad atvObj = getTeamData(teamId);
		int num = atvObj.getStatus().getList().stream().limit(teamManager.getTeam(teamId).getCreateDay()).filter(s-> s==1).collect(Collectors.toList()).size();
		if (GameSource.getServerStartDay() == 8) {
			num += atvObj.getStatus().getList().stream().skip(14).limit(3).filter(s-> s==1).collect(Collectors.toList()).size();
		}
		return num;
	}
}


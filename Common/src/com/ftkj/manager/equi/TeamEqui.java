package com.ftkj.manager.equi;

import com.ftkj.console.EquiConsole;
import com.ftkj.console.GameConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.db.domain.EquiPO;
import com.ftkj.enums.equi.EEquiType;
import com.ftkj.manager.equi.bean.Equi;
import com.ftkj.manager.equi.bean.PlayerEqui;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 球队装备
 * @author lin.lin
 *
 */
public class TeamEqui {
    private static final Logger log = LoggerFactory.getLogger(TeamEqui.class);
	private long teamId;
	
	/**
	 * 保证每个球员只能有一套装备， 这里面不包括球衣
	 * playerId : PlayerEqui
	 */
	private Map<Integer, PlayerEqui> playerEquiMap;
	
	/**
	 * 球队衣服
	 * playerId : playerEqui
	 */
	private Map<Integer, PlayerEqui> clothesMap;
	
	/**
	 * 增长序列，唯一ID
	 */
	private AtomicInteger eid;
	
	
	public TeamEqui(long teamId, List<Equi> list) {
		this.teamId = teamId;
		this.playerEquiMap = Maps.newConcurrentMap();
		this.clothesMap = Maps.newConcurrentMap();
		this.eid = new AtomicInteger(list.stream().mapToInt(e->e.getId()).max().orElse(0));
		// 加载装备
		equiListToPlayerEquiList(list.stream().filter(e-> e.getType()!=EEquiType.球衣.id).collect(Collectors.toList()))
			.stream().forEach(pe-> playerEquiMap.put(pe.getPlayerId(), pe));
		equiListToPlayerEquiList(list.stream().filter(e-> e.getType()==EEquiType.球衣.id).collect(Collectors.toList()))
		.stream().forEach(pe-> clothesMap.put(pe.getPlayerId(), pe));
		log.debug("playerEquiMap=", playerEquiMap.size());
		log.debug("clothesMap=", clothesMap.size());
	}
	
	/**
	 * 将装备列表转换成套装
	 * @param equiList
	 */
	private Collection<PlayerEqui> equiListToPlayerEquiList(List<Equi> equiList) {
		Map<Integer, PlayerEqui> map = Maps.newHashMap();
		for(Equi equi:equiList) {
			PlayerEqui pe = map.get(equi.getPlayerId());
			if(pe == null) {
				pe = new PlayerEqui(equi.getPlayerId());
				map.put(equi.getPlayerId(), pe);
			}
			pe.put(equi.getType(), equi);
		}
		return map.values();
	}
	
	/**
	 * 直接转换PO到Bean并封装set
	 * @param list
	 * @return
	 */
	public static TeamEqui instanceBeanSet(long teamId, List<EquiPO> list) {
		List<Equi> equiList = Lists.newArrayList();
		if(list != null) {
			list.stream().forEach( po-> {
				equiList.add(new Equi(po));
			});
		}
		return new TeamEqui(teamId, equiList);
	}
	
	/**
	 * 创建一件装备
	 * @param seqId
	 * @param equId
	 * @param playerId
	 * @return 
	 */
	private Equi instanceEqui(int equId, int playerId) {
		int type = EquiConsole.getEquiTypeByEid(equId);
		EquiPO po = new EquiPO();
		po.setId(getSeqId());
		po.setType(type);
		po.setTeamId(teamId);
		po.setEquId(equId);
		po.setPlayerId(playerId);
		po.setEquiTeam(type !=3 ? 0 : PlayerConsole.getPlayerBean(playerId).getTeamId());
		po.setEndTime(GameConsole.Max_Date);
		// 
		Equi equi = new Equi(po);
		return equi;
	}
	
	/**
	 * 创建一套装备,并保存到DB
	 * 球衣只返回球衣，其他返回四个部位
	 * @param playerId
	 * @return
	 */
	public PlayerEqui instancePlayerEqui(int playerId, int type) {
		if(type == EEquiType.球衣.id && clothesMap.size()+1 > EquiConsole.MAX_EQUI_NUM){
			return null;
		}else if(playerEquiMap.size() + 1 > EquiConsole.MAX_EQUI_NUM) {
			return null;
		}
		List<Equi> list = Lists.newArrayList();
		if(type == EEquiType.球衣.id) {
			list.add(instanceEqui(30011, playerId));
			list.stream().forEach(equi-> {
				equi.save();
			});
			PlayerEqui pe = new PlayerEqui(playerId, list); // 等等
			clothesMap.put(pe.getPlayerId(), pe);
			return pe;
		}else {
			EquiConsole.EQUI_INIT_LIST.stream().forEach(equId-> list.add(instanceEqui(equId, playerId)));
			list.stream().forEach(equi-> {
				equi.save();
			});
			PlayerEqui pe = new PlayerEqui(playerId, list); // 等等
			playerEquiMap.put(pe.getPlayerId(), pe);
			return pe;
		}
	}
	
	/**
	 * 球队装备的新序列ID
	 * @return
	 */
	private int getSeqId(){
		return eid.incrementAndGet();
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * 取装备列表
	 * @return
	 */
	public Collection<PlayerEqui> getPlayerEquiList() {
		return playerEquiMap.values();
	}
	/**
	 * 球衣列表
	 * @return
	 */
	public Collection<PlayerEqui> getPlayerClothesList() {
		return clothesMap.values();
	}
	
	/**
	 * 区分取出来的是球衣，还是普通装备
	 */
	public PlayerEqui getPlayerEquiByType(int playerId, int type) {
		if(type == 3) {
			return getPlayerClothes(playerId);
		}
		return getPlayerEqui(playerId);
	}

	/**
	 * 取球员装备
	 * @param playerId 球员装备ＩＤ
	 * @return
	 */
	public PlayerEqui getPlayerEqui(int playerId) {
		if(playerEquiMap.containsKey(playerId)) {
			return playerEquiMap.get(playerId);
		}
		return new PlayerEqui(playerId);
	}
	
	/**
	 * 取球员球衣
	 * @param playerId
	 * @return
	 */
	public PlayerEqui getPlayerClothes(int playerId) {
		if(clothesMap.containsKey(playerId)) {
			return clothesMap.get(playerId);
		}
		return new PlayerEqui(playerId);
	}
	
	/**
	 * 取所有装备，包括球衣
	 * @param playerId
	 * @return
	 */
	public Collection<Equi> getPlayerAllEqui(int playerId) {
		List<Equi> list = Lists.newArrayList();
		list.addAll(getPlayerClothes(playerId).getPlayerEqui());
		list.addAll(getPlayerEqui(playerId).getPlayerEqui());
		return list;
	}
	
	/**
	 * 球队拥有的所有装备
	 * @return
	 */
	public Collection<Equi> getTeamAllEqui() {
		List<Equi> list = Lists.newArrayList();
		clothesMap.values().stream().forEach(s-> list.addAll(s.getPlayerEqui()));
		playerEquiMap.values().stream().forEach(s-> list.addAll(s.getPlayerEqui()));
		return list;
	}
	
	/**
	 * 取球员身上的装备，如果不存在，则创建一套
	 * @param player
	 * @param type 不是球衣，传0进来即可
	 * @return 如果装备已满最大套数，可能为空
	 */
	public PlayerEqui getPlayerEquiSetIfNullCreate(int playerId, int type) {
		PlayerEqui playerEqui = getPlayerEquiByType(playerId, type);
		if(playerEqui == null || playerEqui.getPlayerEqui().size() == 0) {
			playerEqui = instancePlayerEqui(playerId, type);
		}
		return playerEqui;
	}
	
	/**
	 * 取球员某个部位的装备，养成取装备接口
	 * @param playerId
	 * @param type
	 * @return
	 */
	public Equi getPlayerEquiIfNullCreate(int playerId, int type) {
		PlayerEqui playerEqui = getPlayerEquiSetIfNullCreate(playerId, type);
		if(playerEqui != null) {
			return playerEqui.getPlayerEquiByType(type);
		}
		return null;
	}

	/**
	 * 装备转移，相互转移
	 * @param pe1
	 * @param player2
	 * @return 
	 */
	public List<PlayerEqui> equiTransfer(int playerId, int playerId2) {
		List<PlayerEqui> list = Lists.newArrayList();
		// 选择装备必定转移
		PlayerEqui player1Equi = playerEquiMap.remove(playerId);
		if(player1Equi != null) {
			player1Equi.setPlayerId(playerId2);
			player1Equi.save();
			list.add(player1Equi);
		}
		// 如果是相互转换，目标装备存在
		PlayerEqui player2Equi = playerEquiMap.remove(playerId2);
		if(player2Equi != null) {
			player2Equi.setPlayerId(playerId);
			player2Equi.save();
			list.add(player2Equi);
		}
		// 载入内存
		if(player1Equi != null) {
			playerEquiMap.put(player1Equi.getPlayerId(), player1Equi);
		}
		if(player2Equi != null) {
			playerEquiMap.put(player2Equi.getPlayerId(), player2Equi);
		}
		return list;
	}
	
	/**
	 * 转换球衣
	 * @param playerId
	 * @param playerId2
	 * @return
	 */
	public List<PlayerEqui> equiTransferClothes(int playerId, int playerId2) {
		List<PlayerEqui> list = Lists.newArrayList();
		// 选择装备必定转移
		PlayerEqui player1Equi = clothesMap.remove(playerId);
		if(player1Equi != null) {
			player1Equi.setPlayerId(playerId2);
			player1Equi.resetEquiTeam();
			player1Equi.save();
			list.add(player1Equi);
		}
		// 如果是相互转换，目标装备存在
		PlayerEqui player2Equi = clothesMap.remove(playerId2);
		if(player2Equi != null) {
			player2Equi.setPlayerId(playerId);
			player2Equi.resetEquiTeam();
			player2Equi.save();
			list.add(player2Equi);
		}
		// 载入内存
		if(player1Equi != null) {
			clothesMap.put(player1Equi.getPlayerId(), player1Equi);
		}
		if(player2Equi != null) {
			clothesMap.put(player2Equi.getPlayerId(), player2Equi);
		}
		return list;
	}
	

	public long getTeamId() {
		return teamId;
	}
	
	public boolean hasPlayerEqui(int playerId) {
		return playerEquiMap.containsKey(playerId);
	}

//	/**
//	 * 升级
//	 * @param player 球员
//	 * @param pid 装备 所传着的ID
//	 * @param type
//	 */
//	public void uplv(int playerId, int id, int type) {
//		Equi equi = getPlayerEquiByType(playerId, type).getPlayerEquiByType(type);
//		equi.setLv(equi.getLv() + 1);
//		equi.setExp(0);
//		equi.save();
//	}
}

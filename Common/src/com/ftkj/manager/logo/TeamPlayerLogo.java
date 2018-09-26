package com.ftkj.manager.logo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ftkj.console.LogoConsole;
import com.ftkj.db.domain.LogoPO;
import com.ftkj.db.domain.PlayerLogoPO;
import com.ftkj.manager.logo.bean.Logo;
import com.ftkj.manager.logo.bean.PlayerLogo;
import com.ftkj.manager.logo.cfg.LogoLvBean;
import com.ftkj.manager.player.Player;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @Description:球员头像
 * @author Jay
 * @time:2017年3月16日 上午10:11:37
 */
public class TeamPlayerLogo {
	
	private long teamId;

	/**
	 * 玩家头像养成(荣誉)
	 * 球员id：头像
	 */
	private Map<Integer, PlayerLogo> playerLogoMap;
	
	/**
	 * 拥有的头像列表
	 * id:Logo
	 */
	private Map<Integer, Logo> logoMap;
	
	/**
	 * 自增ID
	 */
	private AtomicInteger seqId;
	
	
	public TeamPlayerLogo(long teamId, List<PlayerLogoPO> playerLogoList, List<LogoPO> logoOPList) {
		this.teamId = teamId;
		//
		this.logoMap = logoOPList.stream().collect(Collectors.toMap(LogoPO::getId, logo-> new Logo(logo)));
		this.playerLogoMap = playerLogoList.stream().collect(Collectors.toMap(PlayerLogoPO::getPlayerId, logo-> new PlayerLogo(logo)));
		//
		int maxId = logoOPList.stream().mapToInt(e->e.getId()).max().orElse(0);
		seqId = new AtomicInteger(maxId);
	}
	
	/**
	 * 头像的ID
	 * @return
	 */
	public int getSeqId() {
		return seqId.incrementAndGet();
	}
	
	//---------------------获得头像---------------------
	/**
	 * 获得球员头像
	 * @param playerId
	 * @param quality
	 */
	public Logo addLogo(int playerId, int quality) {
		// 只会往logoList添加
		LogoPO logoPO = new LogoPO(getSeqId(), teamId, playerId, quality);
		Logo logo = new Logo(logoPO);
		logo.save();
		this.logoMap.put(logo.getLogoId(), logo);
		return logo;
	}
	
	/**
	 * 获取球员头像， 不存在则添加
	 * @param playerId
	 */
	public PlayerLogo getPlayerLogo(int playerId) {
		if(playerLogoMap.containsKey(playerId)) {
			return playerLogoMap.get(playerId);
		}
		// 没有等级就是0才对。
		PlayerLogoPO playerLogoPO = new PlayerLogoPO(teamId, playerId, 0, 0, 0);
		PlayerLogo pl = new PlayerLogo(playerLogoPO);
		this.playerLogoMap.put(playerId, pl);
		return pl;
	}
	
	/**
	 * 取球员头像品质
	 * @param playerId
	 * @return
	 */
	public int getPlayerLogoQuality(int playerId) {
		Logo lg = getLogo(getPlayerLogo(playerId).getLogoId());
		if(lg == null) {
			return 0;
		}
		return lg.getQuality();
	}
	
	//---------------------头像荣誉等级处理---------------------
	
	/**
	 * 更换头像
	 * @param playerId
	 * @param logoId
	 */
	public void changeLogo(Player player, int logoId) {
		PlayerLogo playerLo = getPlayerLogo(player.getPlayerRid());
		playerLo.setLogoId(logoId);
		playerLo.save();
	}
	
	/**
	 * 进阶点亮
	 * @param playerId
	 */
	public boolean forward(Player player) {
		PlayerLogo playerLogo = getPlayerLogo(player.getPlayerRid());
		//
		int lv = playerLogo.getLv();
		LogoLvBean plb = LogoConsole.getLogoLv(lv);
		int newStep = playerLogo.getStep() + 1;
		//是否大星脉
		boolean starUp = newStep % (plb.getStat()+1) == 0;
		boolean suc = false;
		// 升级概率
		if(starUp) {
			suc = RandomUtil.randHit(100, Float.valueOf(plb.getStarRate() * 100).intValue());
		}else {
			suc = RandomUtil.randHit(100, Float.valueOf(plb.getStepRate() * 100).intValue());
		}
		if(!suc) {
			return suc;
		}
		// 累计总步数
		playerLogo.forwardStep();
		// 成功操作
		if(starUp) {
			if(playerLogo.getLv() + 1 > LogoConsole.MAX_LV || playerLogo.getStarLv()+1 < plb.getStat()) {
				playerLogo.setStarLv(playerLogo.getStarLv() + 1);
			}else {
				// 升级
				playerLogo.upLv();
			}
		}
		playerLogo.save();
		return suc;
	}

	/**
	 * 检查是否拥有指定球员的头像
	 * @param logoId
	 * @param logoId 
	 * @return
	 */
	public boolean checkPlayerLogoId(int playerId, int logoId) {
		return logoMap.containsKey(logoId) && logoMap.get(logoId).getPlayerId() == playerId;
	}
	
	/**
	 * 检查是否拥有该头像
	 * @param logoId
	 * @return
	 */
	public boolean checkLogoId(int logoId) {
		return logoMap.containsKey(logoId);
	}
	
	/**
	 * 检查该头像是否在使用；
	 * @param logoId
	 * @return ture 在使用
	 */
	public boolean checkUseLogo(int logoId) {
		Logo logo = logoMap.get(logoId);
		int playerId = logo.getPlayerId();
		PlayerLogo pl = playerLogoMap.get(playerId);
		if(pl != null && pl.getLogoId() == logoId) {
			return true;
		}
		return false;
	}
	
	/**
	 * 等级转移所需要的卡数
	 * @param p1
	 * @param p2
	 * @return
	 */
	public int getTranCardNum(PlayerLogo p1, PlayerLogo p2) {
		int num1 = 0;
		int num2 = 0;
		if(p1 != null) {
			num1 = LogoConsole.getLogoLv(p1.getLv()).getCardNum();
		}
		if(p2 != null) {
			num2 = LogoConsole.getLogoLv(p2.getLv()).getCardNum();
		}
		return Math.max(num1, num2);
	}
	
	/**
	 * 头像荣誉等级转移
	 */
	public void tranLogoHonor(Player player1, Player player2) {
		//
		PlayerLogo p1 = getPlayerLogo(player1.getPlayerRid());
		PlayerLogo p2 = getPlayerLogo(player2.getPlayerRid());
		//
		int lv = p1.getLv();
		int starLv = p1.getStarLv();
		int step = p1.getStep();
		p1.setLv(p2.getLv());
		p1.setStarLv(p2.getStarLv());
		p1.setStep(p2.getStep());
		//
		p2.setLv(lv);
		p2.setStarLv(starLv);
		p2.setStep(step);
		
		p1.save();
		p2.save();
	}
	
	//---------------------------------四合一-------------------------------
	/**
	 * 头像进阶，四合一
	 */
	public boolean upQuality(int playerId, int quality) {
		// 移除4个同品质的头像，得到一个更加高级的头像；
		List<Logo> logoList = getPlayerQuaLogoIds(playerId, quality);
		if(logoList.size() < 4) {
			return false;
		}
		List<Integer> logoIds = logoList.stream().limit(4).mapToInt(logo-> logo.getLogoId()).boxed().collect(Collectors.toList());
		// 移除
		removeLogo(logoIds);
		// 合成新头像
		addLogo(playerId, quality+1);
		return true;
	}
	
	/**
	 * 移除logo
	 * @param logIds  
	 * @return 被移除的头像列表
	 */
	private List<Logo> removeLogo(Collection<Integer> collection) {
		List<Logo> removeList = Lists.newArrayList();
		collection.stream().forEach(id-> {
			Logo logo = logoMap.remove(id);
			logo.del();
			removeList.add(logo);
		});
		return removeList;
	}
	
	/**
	 * 判断该球员头像指定品质是否拥有足够数量， 不包括在用的
	 * @param playerId
	 * @param quality
	 * @return
	 */
	public boolean hasLogoNum(int playerId, int quality) {
		int size = getPlayerQuaLogoIds(playerId, quality).size();
		return size >= 4;
	}
	
	public List<Logo> getPlayerQuaLogoIds(int playerId, int quality) {
		return this.logoMap.values().stream()
		.filter(logo-> logo.getPlayerId() == playerId
				&& logo.getQuality() == quality
				&& !checkUseLogo(logo.getLogoId())
		).collect(Collectors.toList());
	}
	
	
	//---------------------------------分解---------------------------------
	/**
	 * 头像分解，可同时分解多个， 返回碎片数量，在外面发道具，这里不应用
	 */
	public int resolve(Collection<Integer> collection) {
		// 注意使用中的头像不可分解
		List<Logo> removeList = removeLogo(collection);
		// 计算碎片数量
		int num = removeList.stream().mapToInt(logo-> debrisNumByQuality(logo.getQuality()) ).sum();
		return num;
	}
	
	/**
	 * 根据品质计算分解成碎片数量
	 * @param quality 品质
	 * @return
	 */
	private int debrisNumByQuality(int quality) {
		return LogoConsole.getLogoQua(quality).getDebris();
	}

	/**
	 * 头像是否可以分解
	 * @param logoId
	 * @return
	 */
	public boolean isCanResolveLogo(int logoId) {
		if(!logoMap.containsKey(logoId)) {
			return false;
		}
		Logo logo = logoMap.get(logoId);
		// 红色以上不能分解
		if(logo.getQuality() > 5) {
			return false;
		}
		return true;
	}
	
	public List<Logo> getPlayerLogoList(int playerId) {
		return logoMap.values().stream().filter(logo-> logo.getPlayerId()==playerId).collect(Collectors.toList());
	}
	
	/**
	 * 取所有的头像列表
	 * 荣誉：头像列表
	 * @return
	 */
	public Map<PlayerLogo, List<Logo>> getPlayerTheLogoList() {
		Map<PlayerLogo, List<Logo>> map = Maps.newHashMap();
		int[] playerIds = logoMap.values().stream().mapToInt(logo-> logo.getPlayerId()).distinct().toArray();
		for(int playerId : playerIds) {
			List<Logo> logoList = getPlayerLogoList(playerId);
			map.put(getPlayerLogo(playerId), logoList);
		}
		return map;
	}
	
	/**
	 * 取玩家配带的头像
	 * @return
	 */
	public Logo getLogo(int logoId) {
		if(logoMap.containsKey(logoId)) {
			return logoMap.get(logoId);
		}
		return null;
	}

}

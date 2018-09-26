package com.ftkj.console;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.dao.common.ActiveDAO;
import com.ftkj.db.domain.active.base.SystemActivePO;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.server.GameSource;
import com.google.common.collect.Lists;

/**
 * 活动配置
 * @author Jay
 * @time:2017年9月6日 下午3:57:05
 */
public class SystemActiveConsole {

	private static final Logger log = LogManager.getLogger(SystemActiveConsole.class);
	private static Map<Integer, SystemActiveBean> systemActiveMap;
	
	public static void init(List<SystemActivePO> activePOList, List<SystemActiveBean> activeList, List<SystemActiveCfgBean> cfgList) {
		// 活动时间必须正确格式
		Map<Integer, SystemActiveBean> tempMap = activeList.stream().collect(Collectors.toMap(SystemActiveBean::getAtvId, (s)-> s));
		Map<Integer, SystemActivePO> poMap = activePOList.stream().collect(Collectors.toMap(SystemActivePO::getAtvId, (s)-> s));
		Map<Integer, List<SystemActiveCfgBean>> atvCfg = cfgList.stream().collect(Collectors.groupingBy(v->v.getAtvId(),Collectors.toList()));
		List<SystemActiveCfgBean> cfg = null;
		for(SystemActiveBean s : tempMap.values()) {
			// 数据库配的活动才是开启的
			if(poMap.containsKey(s.getAtvId())) {
				s.setActive(poMap.get(s.getAtvId()));
			}
			cfg = atvCfg.get(s.getAtvId());
			if(cfg == null) {
				continue;
			}
			s.setConfigList(cfg.stream().collect(Collectors.toMap(SystemActiveCfgBean::getId, (b)-> b)));
		}
		//
		systemActiveMap = tempMap;
	}
	
	/**
	 * 初始化Excel活动配置数据到DB,
	 * @param activeList
	 * @param cfgList
	 * @param isConfig 是否保存活动配置到DB
	 */
	public static void initActiveToDB(List<SystemActivePO> activePOList, List<SystemActiveBean> activeList) {
		// 活动时间必须正确格式
			Map<Integer, SystemActiveBean> tempMap = activeList.stream().collect(Collectors.toMap(SystemActiveBean::getAtvId, (s)-> s));
			Map<Integer, SystemActivePO> poMap = activePOList.stream().collect(Collectors.toMap(SystemActivePO::getAtvId, (s)-> s));
			for(SystemActiveBean s : tempMap.values()) {
				SystemActivePO activePO = null;
				if(poMap.containsKey(s.getAtvId())) {
					activePO = poMap.get(s.getAtvId());
					activePO.setName(s.getName());
					activePO.setEndTime(s.getEndDateTime());
				}else {
					// 保存到DB
					activePO = new SystemActivePO();
					activePO.setShardId(GameSource.shardId);
					activePO.setAtvId(s.getAtvId());
					activePO.setName(s.getName());
					activePO.setStatus(0);
					activePO.setStartTime(s.getStartDateTime());
					activePO.setEndTime(s.getEndDateTime());
				}
				activePO.save();
				s.setActive(activePO);
			}
			// 清理Excel配置已删除的活动遗留在DB的数据库
			for(SystemActivePO po : activePOList) {
				if(!tempMap.containsKey(po.getAtvId())) {
					po.del();
					ActiveDAO.runClearActiveData(po.getAtvId());
				}
			}
			ActiveDAO.runDel();
	}
	
	public static SystemActiveBean getSystemActive(int atvId) {
		return systemActiveMap.get(atvId);
	}
	
	public static List<SystemActiveBean> getAllSystemActive() {
		return systemActiveMap.values().stream().collect(Collectors.toList());
	}
	
	/**
	 * 有效的活动，status=1 并且结束时间在当前时间以后
	 * @return
	 */
	public static List<SystemActiveBean> getAllSystemActiveEffect() {
		Collection<SystemActiveBean> allList = systemActiveMap.values();
		// debug写法
		log.debug("活跃活动总数量：{}", allList.size());
		List<SystemActiveBean> activeList = Lists.newArrayList();
		DateTime now = DateTime.now();
		for(SystemActiveBean active : allList) {
			if(active.getEndDateTime().isAfter(now)) {
				activeList.add(active);
			}
		}
		return activeList;
		//return systemActiveMap.values().stream().filter(s->s!=null).filter(s-> s.getEndDateTime().isAfterNow()).collect(Collectors.toList());
	}
	
	/**
	 * 取活动某个时间点的状态
	 * @param atvId
	 * @param now
	 * @return
	 */
	public static EActiveStatus getActiveStatus(int atvId, DateTime now) {
		SystemActiveBean bean = getSystemActive(atvId);
		if(bean == null || bean.getActive() == null) {
			return EActiveStatus.未开始;
		}
		if(bean.getNoTimeLimit() == 1) {
			return EActiveStatus.进行中;
		}
		// 下面状态还没测试，先不放开
//		if(bean.getActive().getStatus() == EActiveStatus.维护.getStatusCode()) {
//			return EActiveStatus.维护;
//		}
//		if(bean.getActive().getStatus() == EActiveStatus.停止.getStatusCode()) {
//			return EActiveStatus.停止;
//		}
		if(bean.getStartDateTime().isAfter(now)) {
			return EActiveStatus.未开始;
		}
		if(bean.getEndDateTime().isBefore(now)) {
			return EActiveStatus.已结束;
		}
		// 运营的无效状态
		if(bean.getActive().getStatus() == 1) {
			return EActiveStatus.维护;
		}
		return EActiveStatus.进行中;
	}
	
	/**
	 * 老的状态判断
	 */
//	public static EActiveStatus getActiveStatus(int atvId, DateTime now) {
//		SystemActiveBean bean = getSystemActive(atvId);
//		if(bean == null) return EActiveStatus.未开始;
//		if(bean.getStartDateTime().isAfter(now)) return EActiveStatus.未开始;
//		if(bean.getEndDateTime().isBefore(now)) return EActiveStatus.已结束;
//		return EActiveStatus.进行中;
//	}
	
}

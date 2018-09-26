package com.ftkj.console;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ftkj.cfg.LeagueAppointBean;
import com.ftkj.cfg.LeagueScoreRewardBean;
import com.ftkj.cfg.LeagueUpgradeBean;
import com.ftkj.manager.league.LeagueHonorBean;
import com.ftkj.manager.league.LeagueTaskBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author tim.huang
 * 2017年6月1日
 *
 */
public class LeagueConsole {

	private static Map<Integer,LeagueHonorBeanTmp> leagueHonorMap;
	
	private static Map<Integer,LeagueTaskBeanTmp> leagueTaskTypeMap;
	
	private static Map<Integer,LeagueTaskBean> leagueTaskMap;
	
	private static Map<Integer,LeagueAppointBean> leagueAppointMap;
	/**联盟日常任务类型*/
	private static Set<Integer> leagueTaskTypeSet;
	/**key=捐献勋章id,value=任务id*/
	private static Map<Integer, Integer> leagueTaskPropIdMap;
	/**联盟升级配置数据,key=等级,value=升级配置数据*/
	private static Map<Integer, LeagueUpgradeBean> leagueUpgradeMap;
	/**联盟每日贡献累计加你配置数据,key=每个贡献值,value=每个贡献值对应的奖励配置数据*/
	private static Map<Integer, LeagueScoreRewardBean> leagueScoreRewardMap;
	/**联盟能够升到的最大等级*/
	private static int leagueMaxLevel = 0;
	
	public static void init(){
		Map<Integer,LeagueHonorBeanTmp> leagueHonorMapTmp = Maps.newHashMap();
		
		Map<Integer,LeagueTaskBeanTmp> leagueTaskTypeMapTmp = Maps.newHashMap();
		
		Map<Integer,LeagueTaskBean> leagueTaskMapTmp = Maps.newHashMap();
		Map<Integer,LeagueAppointBean> leagueAppointMapTmp = Maps.newHashMap();
		leagueTaskTypeSet = Sets.newLinkedHashSet();
		leagueTaskPropIdMap = Maps.newHashMap();
		leagueUpgradeMap = Maps.newHashMap();
		leagueScoreRewardMap = Maps.newHashMap();
		
		CM.leagueDailyTaskList.forEach(task->{
			LeagueTaskBean bean = new LeagueTaskBean(
					task.getTid(), 
					task.getType(), 
					task.getRate(), 
					PropSimple.getPropBeanByStringNotConfig(task.getTaskProps()), 
					PropSimple.getPropBeanByStringNotConfig(task.getGifts()));
			leagueTaskMapTmp.put(bean.getTid(), bean);
			leagueTaskTypeMapTmp.computeIfAbsent(bean.getType(), key->new LeagueTaskBeanTmp(key)).addTask(bean);
			leagueTaskTypeSet.add(bean.getType());
			if (bean.getTaskProps() != null && bean.getTaskProps().size() > 0) {
				leagueTaskPropIdMap.put(bean.getTaskProps().get(0).getPropId(), bean.getTid());
			}
		});
		
		
		CM.leagueHonorList.forEach(honor->{
			LeagueHonorBean honorBean = new LeagueHonorBean(honor.getHonorId(), honor.getName(),honor.getLevel(), 
					honor.getHonorConsume(), StringUtil.toIntArray(honor.getValues(), StringUtil.DEFAULT_ST)
					,honor.getWeekHonor(), honor.getLeagueLv());
			
			leagueHonorMapTmp.computeIfAbsent(honor.getHonorId(), key->new LeagueHonorBeanTmp(key)).putBean(honorBean);
		});
		
		CM.leagueAppointBeanList.forEach(p->leagueAppointMapTmp.put(p.getPropId(), p));
		for(LeagueUpgradeBean obj : CM.leagueUpgradeBeanList){
			leagueUpgradeMap.put(obj.getLv(), obj);
			if (obj.getLv() > leagueMaxLevel) {
				leagueMaxLevel = obj.getLv();
			}
		}
		
		CM.leagueScoreRewardBeanList.forEach(obj -> leagueScoreRewardMap.put(obj.getScore(), obj));
		
		leagueHonorMap = leagueHonorMapTmp;
		leagueTaskTypeMap = leagueTaskTypeMapTmp;
		leagueTaskMap = leagueTaskMapTmp;
		leagueAppointMap = leagueAppointMapTmp;
	}
	
	/**获取联盟能够升到的最大等级*/
	public static int getLeagueMaxLevel() {
		return leagueMaxLevel;
	}

	/**
	 * 根据捐赠物品的Id获得,日常任务的Id.
	 * @param propId
	 * @return
	 */
	public static int getLeagueTaskId(int propId){
		return leagueTaskPropIdMap.get(propId) == null ? 0 : leagueTaskPropIdMap.get(propId);
	}
	
	public static LeagueAppointBean getLeagueAppointBean(int propId){
		return leagueAppointMap.get(propId);
	}
	
	
	
	public static List<LeagueHonorBean> getAllBaseHonor(){
		return leagueHonorMap.values().stream().map(tmp->tmp.getBean(1)).collect(Collectors.toList());
	}
	
	
	public static LeagueHonorBean getLeagueHonorBean(int hid,int level){
		LeagueHonorBeanTmp tmp = leagueHonorMap.get(hid);
		return tmp == null ? null : tmp.getBean(level);
	}
	
	public static LeagueTaskBean getLeagueTaskBean(int tid){
		return leagueTaskMap.get(tid);
	}
	
	/**
	 * 获取有序的联盟日持任务类型.
	 * @return
	 */
	public static Set<Integer> getLeagueTaskTypeSet() {
		return leagueTaskTypeSet;
	}

	/**
	 * 根据任务类型随机一个任务.
	 * @param type
	 * @return
	 */
	public static LeagueTaskBean getRandomLeagueTaskBean(int type){
		LeagueTaskBeanTmp tmp  =leagueTaskTypeMap.get(type);
		return tmp.getRandomTask();
	}
	
	public static List<LeagueTaskBean> getLeagueTaskBeanByType(int type){
		LeagueTaskBeanTmp tmp  =leagueTaskTypeMap.get(type);
		return tmp.getTaskList();
	}
	
	/**
	 * 根据等级获取联盟对应的升级配置数据.
	 * @param level
	 * @return
	 */
	public static LeagueUpgradeBean getLeagueUpgradeBeanByLevel(int level){
		return leagueUpgradeMap.get(level);
	}
	
	/**
	 * 根据贡献值获取对应的贡献奖励配置数据.
	 * @param score
	 * @return
	 */
	public static LeagueScoreRewardBean getLeagueScoreRewardBeanByScore(int score){
		return leagueScoreRewardMap.get(score);
	}
	
	public static Map<Integer, LeagueScoreRewardBean> getLeagueScoreRewardMap() {
		return leagueScoreRewardMap;
	}

	static class LeagueTaskBeanTmp{
		private int type;
		/**相同类型日常任务的概率总和*/
		private int rateTotal;
		private List<LeagueTaskBean> taskList;
		
		public LeagueTaskBeanTmp(int type) {
			super();
			this.type = type;
			this.taskList = Lists.newArrayList();
		}
		
		/**
		 * 随机日常任务
		 * @return
		 */
		private LeagueTaskBean getRandomTask(){
			LeagueTaskBean bean = null;
			int start = 0, end = 0;
			int randomInt = RandomUtil.randInt(this.rateTotal);
			for (LeagueTaskBean obj : this.taskList) {
				end += obj.getRate();
				if (start <= randomInt && randomInt < end) {
					bean = obj;
					break;
				}
			}
			
			return bean;
		}
		
		public void addTask(LeagueTaskBean task){ 
			this.rateTotal += task.getRate();
			this.taskList.add(task);
		}
		
		public int getType() {
			return type;
		}
		public List<LeagueTaskBean> getTaskList() {
			return taskList;
		}

		public int getRateTotal() {
			return rateTotal;
		}
		
	}
	
	
	static class LeagueHonorBeanTmp{
		private int hid;
		private Map<Integer,LeagueHonorBean> levelMap;
		
		public LeagueHonorBeanTmp(int hid) {
			super();
			this.hid = hid;
			this.levelMap = Maps.newHashMap();
		}
		
		LeagueHonorBean getBean(int level){
			return this.levelMap.get(level);
		}
		
		 void putBean(LeagueHonorBean bean){
			 this.levelMap.put(bean.getLevel(), bean);
		 }
		
		 int getHid() {
			return hid;
		}
		 Map<Integer, LeagueHonorBean> getLevelMap() {
			return levelMap;
		}
		
	}
	
	
}

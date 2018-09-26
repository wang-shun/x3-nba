package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.LeagueAppointBean;
import com.ftkj.cfg.LeagueScoreRewardBean;
import com.ftkj.cfg.LeagueUpgradeBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.LeagueConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.db.ao.logic.ILeagueAO;
import com.ftkj.db.domain.LeagueHonorPO;
import com.ftkj.db.domain.LeagueHonorPoolPO;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EChat;
import com.ftkj.enums.ELeagueTeamLevel;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.param.LoginParam;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueHonor;
import com.ftkj.manager.league.LeagueHonorBean;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.LeaguePB;
import com.ftkj.proto.LeaguePB.LeagueHonorMain.Builder;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.util.StringUtil;
import com.ftkj.util.tuple.Tuple2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tim.huang
 * 2017年5月25日
 */
public class LeagueHonorManager extends BaseManager implements IRedPointLogic{

    @IOC
    private LeagueManager leagueManager;

    @IOC
    private PropManager propManager;

    @IOC
    private RankManager rankManager;

    @IOC
    private TaskManager taskManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private LeagueTaskManager leagueTaskManager;
    @IOC
    private RedPointManager redPointManager;
    
    @IOC
    private ILeagueAO leagueAO;
    @IOC
    private JedisUtil redis;

    private Map<Integer, LeagueHonor> leaguePool;

    private int _donateHour;
    
    
    @Override
	public RedPointParam redPointLogic(long teamId) {
    	RedPointParam rpp = null;
    	if (isCanGetReward(teamId)) {
    		rpp = new RedPointParam(teamId, ERedPoint.LeagueDailyGetReward.getId(), 1);
		}
        
        return rpp;
	}
    
    /**
     * 是否有联盟每日贡献奖励领取,true是,否则false.
     * @param teamId
     * @return
     */
    public boolean isCanGetReward(long teamId){
    	int leagueId = leagueManager.getLeagueId(teamId);
        if (leagueId <= 0) {
        	return false;
        }
        
        String key = RedisKey.getLeagueDayTotalScoreKey(leagueId);
    	//联盟每日累计的贡献值
    	Integer dailyTotalScore = redis.getObj(key);
    	if (dailyTotalScore == null || dailyTotalScore == 0) {
			return false;
		}
    	
    	String rewardKey = RedisKey.getLeagueDayScoreRewardKey(leagueId, teamId);
    	// 当前玩家已领取过的贡献奖励礼包的对应贡献值
    	List<Integer> list = redis.getList(rewardKey);
    	
    	// 达到贡献奖励礼包领取的贡献值
    	Set<Integer> keySet = LeagueConsole.getLeagueScoreRewardMap().keySet();
    	List<Integer> tmpList = new ArrayList<Integer>(keySet);
    	Collections.sort(tmpList);
    	list.forEach(obj -> tmpList.remove(obj));//删除已经领取过的
    	
    	//有礼包未领取
    	if (tmpList.size() > 0 && dailyTotalScore >= tmpList.get(0)) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
	 * 处理玩家跨零点在线,登录处理(玩家登录和定时器0点调用在线玩家).
	 * 跨天,联盟每日累计贡献重置,提示小红点数量为0. 
	 */
    @Subscribe
    public void login(LoginParam param) {
    	DateTime now = DateTime.now();
		// 不等于0点0分0秒返回
		if(!now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).equals(now)) {
			return;
		}
		
		//联盟每日累计贡献重置,发送小红点提示为数量为0,没有奖励领取
		this.redPointManager.sendRedPointTip(
			new RedPointParam(param.teamId, ERedPoint.LeagueDailyGetReward.getId(), 0));
    }

    /**
     * 显示联盟成就界面
     */
    @ClientMethod(code = ServiceCode.LeagueHonorManager_showHonorMain)
    public void showHonorMain() {
        long teamId = getTeamId();
        int leagueId = leagueManager.getLeagueId(teamId);
        LeagueHonor lh = getLeagueHonor(leagueId);
        List<LeaguePB.LeagueHonorPropData> datas = Lists.newArrayList();
        lh.getPoolProps().forEach(prop -> datas.add(getLeagueHonorPropData(prop.getPid(), prop.getNum())));
        Builder builder = LeaguePB.LeagueHonorMain.newBuilder().addAllProps(datas)
        	.setLeagueDailyTotalBallTicket(getLeagueDailyTotalBallTicket(teamId));
        String rewardKey = RedisKey.getLeagueDayScoreRewardKey(leagueId, teamId);
    	// 当前玩家已领取过的贡献奖励礼包的对应贡献值
    	List<Integer> list = redis.getList(rewardKey);
    	if (list != null && list.size() > 0) {
			list.forEach(obj -> builder.addLeagueScroeRewardList(obj));
		}
    	
        sendMessage(builder.build());
    }

    /**
     * 玩家联盟捐赠成就勋章.
     * @param pid
     * @param num
     */
    @ClientMethod(code = ServiceCode.LeagueHonorManager_appoint)
    public void appoint(int pid, int num) {
        long teamId = getTeamId();
        if (num <= 0 || num > 99) { return; }
        int leagueId = leagueManager.getLeagueId(teamId);
        LeagueHonor lh = getLeagueHonor(leagueId);
        if (lh == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_8.code).build());
            return;
		}
        
        if (propManager.delProp(teamId, pid, num, true, false) == null) {//道具不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }

        appoint0(pid, num);
        // 联盟日常任务同样需要统计捐赠
        leagueTaskManager.addAppointDailyTask(teamId, pid, num);
        // 联盟日常任务完成就发送小红点
        redPointManager.sendRedPointTip(leagueTaskManager.redPointLogic(teamId));
        taskManager.updateTask(teamId, ETaskCondition.联盟捐献勋章, num, EModuleCode.联盟.getName());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }
    
    /**
     * 联盟成就捐赠.
     * @param pid
     * @param num
     */
    public void appoint0(int pid, int num){
    	long teamId = getTeamId();
    	int leagueId = leagueManager.getLeagueId(teamId);
    	LeagueTeam team = leagueManager.getLeagueTeam(teamId);
    	LeagueHonor lh = getLeagueHonor(leagueId);
    	
    	synchronized (lh) {
    		League league = leagueManager.getLeague(leagueId);
            lh.appendHonorProp(pid, num);
            LeagueAppointBean lab = LeagueConsole.getLeagueAppointBean(pid);
            if (lab != null) {
                team.updateFeats(lab.getFeats() * num);// 更新联盟成员的功勋值
                leagueManager.updateLeagueTeamScore(leagueId, team, lab.getScore() * num);//更新联盟成员在联盟中的贡献值
                league.updateLeagueHonor(lab.getHonor() * num);// 更新联盟的荣誉值
                league.updateLeagueScore(lab.getScore() * num);//联盟贡献
                rankManager.updateLeagueRank(league, lh.getAllLevel());
            }
            
            String name = teamManager.getTeamNameById(teamId);
            PropBean pb = PropConsole.getProp(pid);
            leagueManager.addLeagueLog(leagueId, name,
                StringUtil.formatString(leagueManager._donateMedalLeagueLog, name, pb.getName(), num));
            
            //联盟成就捐赠获得经验和每日累计的贡献值
           appointLeagueDonte(league, lab, num);
		}
    	
    }
    
    /**
     * 领取联盟成就每日捐赠达到固定贡献值奖励
     * @param num	需求贡献值(比如配置是:达到500就有一个礼包奖励,则num值发500过来
     * 表示领取的是500对应的礼包奖励),用于领取每日贡献礼包.
     */
    @ClientMethod(code = ServiceCode.LeagueHonorManager_get_reward)
    public void appointGetReward(int num) {
    	long teamId = getTeamId();
        if (num <= 0) { 
        	sendMessage(ErrorCode.ParamError);
        	return; 
        }
        
        int leagueId = leagueManager.getLeagueId(teamId);
        if (leagueId <= 0) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_8.code).build());
			return;
		}
        
        LeagueTeam leagueTeam = leagueManager.getLeagueTeam(teamId);
    	Integer value = (leagueTeam == null) ? null : leagueTeam.getScore();
    	// 联盟成员累计捐赠的贡献值没有达到指定数量则不能领取礼包奖励
    	if (value == null || value < ConfigConsole.getGlobal().leagueActiveLimit) {
    		sendMessage(ErrorCode.League_15);
			return;
		}
        
    	String key = RedisKey.getLeagueDayTotalScoreKey(leagueId);
    	//联盟每日累计的贡献值
    	value = redis.getObj(key);
    	if (value == null || num > value) {
    		sendMessage(ErrorCode.League_16);
    		return;
		}
    	
    	String rewardKey = RedisKey.getLeagueDayScoreRewardKey(leagueId, teamId);
    	List<Integer> list = redis.getList(rewardKey);
    	if (list != null && list.contains(num)) {
    		sendMessage(ErrorCode.League_17);
			return;
		}
    	
    	LeagueScoreRewardBean bean = LeagueConsole.getLeagueScoreRewardBeanByScore(num);
    	if (bean == null) {
    		sendMessage(ErrorCode.ParamError);
			return;
		}
    	
    	List<PropSimple> props = PropSimple.getPropBeanByStringNotConfig(bean.getReward());
        propManager.addPropList(teamId, props, true, ModuleLog.getModuleLog(EModuleCode.联盟, ""));
        if (list == null) {
        	redis.expire(rewardKey, RedisKey.DAY);
        }
        
    	redis.addListValueL(rewardKey, num);
    	sendErrorCode(ErrorCode.Success);
    }
    
    /**
     * 联盟成就捐赠获得经验,每日累计的贡献.有礼包奖励则发送小红点.
     * 此方法要在同步块中调用.
     * @param league
     * @param lab
     * @param num
     */
    public void appointLeagueDonte(final League league, LeagueAppointBean lab, int num){
    	//累加联盟每日贡献
        appointLeagueAddDailyScroe(league.getLeagueId(), lab, num);
		//联盟获得经验
        appointLeagueAddExp(league, lab, num);
        // 累加球券
        appointLeaguePlayerAddBallTicket(league.getLeagueId(), lab, num);
        //推送 联盟等级,经验,每日贡献的变化
        sendLeagueDonateMsg(league);
        
        long teamId = getTeamId();
        //小红点提示
        if (isCanGetReward(teamId)) {
        	redPointManager.sendRedPointTip(new RedPointParam(teamId, ERedPoint.LeagueDailyGetReward.getId(), 1));
		}
    }
    
    /**
     * 给联盟在线的成员推送联盟等级,经验,每日贡献的变化消息.
     * @param leagueId
     */
    private void sendLeagueDonateMsg(final League league){
    	Set<Long> set = leagueManager.getLeagueTeamList(league.getLeagueId());
    	if (set != null && set.size() > 0) {
    		String key = RedisKey.getLeagueDayTotalScoreKey(league.getLeagueId());
        	final Integer dailyScore = redis.getObj(key) == null ? 0 : redis.getObj(key);
    		final LeaguePB.LeagueHonorDonateResp.Builder builder = LeaguePB.LeagueHonorDonateResp.newBuilder();
			set.forEach(teamId -> {
				if (GameSource.isOline(teamId)) {
					builder.setLeagueDailyTotalScore(dailyScore);
					builder.setLeagueExp(league.getLeagueExp());
					builder.setLeagueLevel(league.getLeagueLevel());
					sendMessage(teamId, builder.build(), ServiceCode.LeagueHonorManager_push_msg);
				}
			});
		}
    }
    
    /**
     * 累加联盟成员自己捐赠的球券数量.
     * @param leagueId
     * @param teamId
     * @param lab
     * @param num
     */
    private void appointLeaguePlayerAddBallTicket(int leagueId, LeagueAppointBean lab, int num){
    	if (lab.getPropId() == 4004) {
    		long teamId = getTeamId();
			String key = RedisKey.getLeagueDayDonateScoreNumKey(leagueId, teamId);
			Integer value = redis.getObj(key);
			num += (value == null ? 0 : value);
			redis.set(key, num);
		}
    }
    
    /**
     * 累计联盟每日贡献 ,还有联盟成员的每日捐的贡献.
     * 此方法需要放在同步块中调用.
     * @param leagueId
     * @param lab
     * @param num
     * @return
     */
    private Integer appointLeagueAddDailyScroe(int leagueId, LeagueAppointBean lab, int num){
    	int total = 0;
    	Integer count = lab.getScore() * num;
    	String key = RedisKey.getLeagueDayTotalScoreKey(leagueId);
    	Integer value = redis.getObj(key);
    	count +=(value == null ? 0 : value);
    	redis.set(key, count);
    	total = count;
    	
    	key = RedisKey.getLeaguePlayerDayDonateScore(leagueId, getTeamId());
    	value = redis.getObj(key);
    	count = lab.getScore() * num;
    	count +=(value == null ? 0 : value);
    	redis.set(key, count);
    	
    	return total;
    }

    /**
     * 根据联盟Id获取联盟当日的累计贡献值.
     * @param leagueId
     * @return
     */
    public int getLeagueDailyTotalScore(int leagueId){
    	String key = RedisKey.getLeagueDayTotalScoreKey(leagueId);
    	Integer value = redis.getObj(key);
    	return value == null ? 0 : value;
    }
    
    /**
     * 获得玩家每日球券捐赠的数量.
     * @param teamId
     * @return
     */
    public int getLeagueDailyTotalBallTicket(long teamId){
    	LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
    	if (lt == null) {
			return 0;
		}
    	
    	String key = RedisKey.getLeagueDayDonateScoreNumKey(lt.getLeagueId(), teamId);
		Integer value = redis.getObj(key);
		return (value == null ? 0 : value);
    }
    
    /**
     * 联盟成就捐赠获得经验.
     * 此方法需要放在同步块中调用.
     * @param pid
     * @param num
     */
    private void appointLeagueAddExp(League league, LeagueAppointBean lab, int num) {
    	if (league.getLeagueLevel() >= LeagueConsole.getLeagueMaxLevel()) {
			return;
		}
    	
    	long teamId = getTeamId();
    	int fixedCount = ConfigConsole.getGlobal().leagueDonateMax;
    	// 如果捐赠的是球券
    	if (lab.getPropId() == 4004) {
    		String key = RedisKey.getLeagueDayDonateScoreNumKey(league.getLeagueId(), teamId);
			Integer value = redis.getObj(key);
    		int beforePidNum = value == null ? 0 : value;
    		//捐赠球券数据量超过一个固定值就不会获得经验
        	if (beforePidNum >= fixedCount) {
    			return;
    		}
        	
    		num = (beforePidNum + num) > fixedCount ? (fixedCount - beforePidNum) : num;
		}
    	
    	
    	int addExp = lab.getExp() * num + league.getLeagueExp();
    	int addLevel = 0;
    	for (int i = league.getLeagueLevel(); i < LeagueConsole.getLeagueMaxLevel(); i++) {
			LeagueUpgradeBean bean = LeagueConsole.getLeagueUpgradeBeanByLevel(i);
			if (addExp >= bean.getNeed()) {
				addExp -= bean.getNeed();
				addLevel++;
			}else {
				break;
			}
		}
    	
    	league.updateLevel(league.getLeagueLevel() + addLevel);
    	league.updateLeagueExp(addExp);
    	league.updateLeagueTotalExp(lab.getExp() * num);
    	
	}

    /**
     * 升级成就
     * @param hid
     */
    @ClientMethod(code = ServiceCode.LeagueHonorManager_levelUp)
    public void levelUp(int hid) {
        long teamId = getTeamId();
        LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || !(lt.getLevel() == ELeagueTeamLevel.盟主 || lt.getLevel() == ELeagueTeamLevel.副盟主)) { //只有盟主有任命权限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        LeagueHonor lh = getLeagueHonor(leagueId);
        League league = leagueManager.getLeague(leagueId);

        //判断联盟等级是否足够
        LeagueHonorPO po = lh.getLeagueHonorPO(hid);
		int hLevel  = po.getLevel();//当前的成就等级
		LeagueHonorBean lhbObj = LeagueConsole.getLeagueHonorBean(hid, hLevel);
		if (lhbObj == null) {
			sendErrorCode(ErrorCode.ParamError);
			return;
		}
		
		//需要联盟达到指定等级
		if (lhbObj.getLeaguelv() > league.getLeagueLevel()) {
			sendErrorCode(ErrorCode.honor_team_level);
			return;
		}
		
        boolean ok = lh.levelUp(hid, league);
        ErrorCode code = ErrorCode.Fail;
        if (ok) {
            code = ErrorCode.Success;
            //取最小等级更新联盟等级
            int curLevel = lh.getMinLevel();
            LeagueHonorBean lhb = LeagueConsole.getLeagueHonorBean(hid, curLevel);
            leagueManager.addLeagueLog(leagueId, league.getTeamName(),
                StringUtil.formatString(leagueManager._honorLevelUpLeagueLog, lhb.getHonorName(), curLevel));
            if (curLevel != league.getLeagueLevel()) {//升级
                leagueManager.addLeagueLog(leagueId, league.getTeamName(),
                    StringUtil.formatString(leagueManager._levelUpLeagueLog, curLevel));
            }
            
            //更新排行榜数据
            rankManager.updateLeagueRank(league, lh.getAllLevel());
            LeaguePB.LeagueHonorData data = getLeagueHonorData(hid, lh.getLevel(hid), lh.isActivate(hid));

            sendMessage(ServiceConsole.getChatKey(EChat.联盟聊天),
                data, ServiceCode.Push_League_Honor_Update);

        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(code.code).build());
    }

    /**
     * 激活成就
     *
     * @param hid
     */
    @ClientMethod(code = ServiceCode.LeagueHonorManager_activate)
    public void activate(int hid) {
        long teamId = getTeamId();
        LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || lt.getLevel() != ELeagueTeamLevel.盟主) { //只有盟主有任命权限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        LeagueHonor lh = getLeagueHonor(leagueId);
        if (lh.isActivate(hid)) {//成就已激活
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }
        League l = leagueManager.getLeague(leagueId);

        boolean ok = activate(l, lh, hid);
        if (ok) {
            //			List<LeaguePB.LeagueHonorPropData> datas = Lists.newArrayList();
            //			lh.getPoolProps().forEach(prop->datas.add(getLeagueHonorPropData(prop.getPid(),prop.getNum())));
            LeaguePB.LeagueHonorData data = getLeagueHonorData(hid, lh.getLevel(hid), true);
            sendMessage(ServiceConsole.getChatKey(EChat.联盟聊天),
                data, ServiceCode.Push_League_Honor_Update);
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ok ? ErrorCode.Success.code : ErrorCode.Fail.code).build());
    }

    private boolean activate(League l, LeagueHonor lh, int hid) {
        LeagueHonorPO po = lh.getLeagueHonorPO(hid);
        if (po == null) { return false; }
        int curLevel = po.getLevel();
        LeagueHonorBean lhb = LeagueConsole.getLeagueHonorBean(hid, curLevel);
        
        if (l.getHonor() < lhb.getActiveHonor()) { return false; }
        l.updateLeagueHonor(-lhb.getActiveHonor());
        //
        DateTime now = DateTime.now();
        int week = now.getDayOfWeek();
        if (week >= 6) {// 下周
            now = now.plusDays(7);
        }
        now = now.withDayOfWeek(6).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
        po.setEndTime(now);
        po.save();
        return true;
    }
    
    /**
     * 定时自动激活联盟成就,三个一起激活,如果荣誉值不够则一个都不激活.
     */
    private void allActivate(){
    	DateTime now = DateTime.now();
        int week = now.getDayOfWeek();
        if (week >= 6) {// 下周
            now = now.plusDays(7);
        }
        
        now = now.withDayOfWeek(6).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
        
    	for (LeagueHonor honor : leaguePool.values()) {
    		int totalConsumeHonor = 0;
    		League l = leagueManager.getLeague(honor.getLeagueId());
    		for (LeagueHonorPO leagueHonorPO : honor.getHonorMap().values()) {
    			int curLevel = leagueHonorPO.getLevel();
    			int hid = leagueHonorPO.getHonorId();
    			LeagueHonorBean lhb = LeagueConsole.getLeagueHonorBean(hid, curLevel);
    			totalConsumeHonor += lhb.getActiveHonor();
    		}
    		
    		//满足同时激活三个成就
    		if(l.getHonor() >= totalConsumeHonor){
    			l.updateLeagueHonor(-totalConsumeHonor);
    			for (LeagueHonorPO po : honor.getHonorMap().values()) {
    				po.setEndTime(now);
    		        po.save();
    			}
    		}
		}
    	
    }

    public TeamAbility getLeagueAbility(int leagueId) {
        LeagueHonor lh = getLeagueHonor(leagueId);
        TeamAbility ability = new TeamAbility(AbilityType.League);
        if (lh.isActivate(1)) {//成就已激活
            LeagueHonorPO po = lh.getLeagueHonorPO(1);
            LeagueHonorBean bean = LeagueConsole.getLeagueHonorBean(po.getHonorId(), po.getLevel());
            ability.addAttr(EActionType.ocap, bean.getValues()[0]);
            ability.addAttr(EActionType.dcap, bean.getValues()[1]);
        }
        return ability;
    }

    public int getLeagueTeamPriceCap(LeagueTeam lt) {
        if (lt == null) {
            return 0;
        }
        LeagueHonor lh = getLeagueHonor(lt.getLeagueId());
        League league = leagueManager.getLeague(lt.getLeagueId());
        if (league != null && lh != null && lh.isActivate(3) && lt.getScore() >= league.getHonorLimit()) {//成就已激活
            LeagueHonorPO po = lh.getLeagueHonorPO(3);
            LeagueHonorBean bean = LeagueConsole.getLeagueHonorBean(po.getHonorId(), po.getLevel());
            return bean.getValues()[2];
        }
        return 0;
    }

    public int getLeagueTeamPriceCapByTeamId(long teamId) {
        LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        return getLeagueTeamPriceCap(lt);
    }

    /**
     * 定时器调用,每周6的0点调用
     */
    public void updateActivate() {
//        leaguePool.values().forEach(honor -> {
//            League l = leagueManager.getLeague(honor.getLeagueId());
//            honor.getHonorMap().values().forEach(hh -> activate(l, honor, hh.getHonorId()));
//        });
    	 allActivate();
    }

    public LeagueHonor createLeague(int leagueId) {
        LeagueHonor res = LeagueHonor.createLeagueHonor(leagueId);
        res.createActivateAll();
        leaguePool.put(leagueId, res);
        return res;
    }

    private LeaguePB.LeagueHonorPropData getLeagueHonorPropData(int pid, int num) {
        return LeaguePB.LeagueHonorPropData.newBuilder().setNum(num).setPropId(pid).build();

    }

    public List<LeaguePB.LeagueHonorData> getLeagueHonorMain(int leagueId) {
        List<LeaguePB.LeagueHonorData> dataList = Lists.newArrayList();
        LeagueHonor honor = getLeagueHonor(leagueId);
        if (honor != null) {
        	honor.getHonorMap().values().stream().forEach(h -> dataList.add(getLeagueHonorData(h.getHonorId(), h.getLevel(), honor.isActivate(h.getHonorId()))));
		}
        return dataList;
    }

    public LeagueHonor getLeagueHonor(int leagueId) {
        return leaguePool.get(leagueId);
    }

    public LeagueHonor getCalAbilityLeagueHonor(long teamId) {
        LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) { return null; }
        LeagueHonor lh = getLeagueHonor(lt.getLeagueId());
        League league = leagueManager.getLeague(lt.getLeagueId());
        if (league != null && lh != null && lh.isActivate(1) && lt.getScore() >= league.getHonorLimit()) {//成就已激活
            return lh;
        }
        return null;
    }

    public LeaguePB.LeagueHonorData getLeagueHonorData(int hid, int level, boolean isActive) {
        return LeaguePB.LeagueHonorData.newBuilder().setHonorId(hid).setLevel(level).setActivate(isActive).build();
    }

    @Override
    public void initConfig() {
        _donateHour = 24;
    }

    @Override
    public int getOrder() {
        return ManagerOrder.LeagueHonor.getOrder();
    }

    @Override
    public void instanceAfter() {
        leaguePool = Maps.newConcurrentMap();

        List<LeagueHonorPO> honorList = leagueAO.getAllLeagueHonor();
        honorList.forEach(h -> {
            leaguePool.computeIfAbsent(h.getLeagueId(), key -> new LeagueHonor(key)).initAppendHonor(h);
        });

        List<LeagueHonorPoolPO> honorPoolList = leagueAO.getAllLeagueHonorPool();

        honorPoolList.forEach(h -> {
            LeagueHonor lh = leaguePool.get(h.getLeagueId());
            if (lh != null) {
                lh.getPool().initAppendProp(h);
            }
        });

    }

}

package com.ftkj.manager.active.base;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.console.SystemActiveConsole;
import com.ftkj.db.ao.common.IActiveAO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EPropType;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.active.SystemActiveManager;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.longtime.AtvRechargeStatisticsManager;
import com.ftkj.manager.active.longtime.AtvTeamCloseManager;
import com.ftkj.manager.logic.ChatManager;
import com.ftkj.manager.logic.IRedPointLogic;
import com.ftkj.manager.logic.MainMatchManager;
import com.ftkj.manager.logic.PlayerManager;
import com.ftkj.manager.logic.PropManager;
import com.ftkj.manager.logic.TaskManager;
import com.ftkj.manager.logic.TeamEmailManager;
import com.ftkj.manager.logic.TeamManager;
import com.ftkj.manager.logic.TeamMoneyManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropRandomHit;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.prop.bean.PropExtPlayerBean;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;
import com.ftkj.proto.AtvCommonPB.AtvCommonData;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * 活动基类
 *
 * @author Jay
 * @time:2017年8月30日 下午7:54:27
 */
public abstract class ActiveBaseManager extends BaseManager implements OfflineOperation, IRedPointLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveBaseManager.class);
    @IOC
    private SystemActiveManager systemActiveManager;
    @IOC
    protected TeamManager teamManager;
    @IOC
    protected TeamMoneyManager moneyManager;
    @IOC
    protected TaskManager taskManager;
    @IOC
    protected PropManager propManager;
    @IOC
    protected IActiveAO activeAO;
    @IOC
    protected AtvTeamCloseManager atvTeamCloseManager;
    @IOC
    protected TeamEmailManager emailManager;
    @IOC
    protected AtvRechargeStatisticsManager rechargeManager;
    @IOC
    protected ChatManager chatManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private MainMatchManager mainMatchManager;

    /**
     * 所有的实现类注册
     */
    private static Map<Integer, ActiveBaseManager> activeMap;
    /**
     * 活动ID
     */
    private int id = -1;
    /**
     * 红点模块ID
     */
    private int redPointID = 0;
    /**
     * 活动数据类型
     */
    @SuppressWarnings("rawtypes")
    private Class clazz;

    /**
     * 全局活动数据类型
     */
    @SuppressWarnings("rawtypes")
    private Class shareClass;

    /**
     * 存 redis 的类型，默认和class相同，如果不同，注解标注即可
     */
    @SuppressWarnings("rawtypes")
    private Class redisClass;

    /**
     * 玩家的活动数据
     */
    private Map<Long, ActiveBase> teamDataMap;
    /**
     * 活动公共数据
     */
    private ActiveBase shareData;

    static {
        activeMap = Maps.newConcurrentMap();
    }

    /**
     * 所有的活动实现调用注册
     *
     * @param atvId
     * @param manager
     */
    public static void registerActiveManager(int atvId, ActiveBaseManager manager) {
        if (atvId < 0) { return; }
        if (activeMap.containsKey(atvId)) {
            //log.error("重复的活动manager注册["+atvId+", calss="+manager.getClass()+"]");
        }
        //log.debug("注册活动[{}]实现manager", atvId);
        activeMap.put(atvId, manager);
    }

    public static ActiveBaseManager getManager(int atvId) {
        if (activeMap.containsKey(atvId)) {
            return activeMap.get(atvId);
        }
        return null;
    }

    /**
     * excel?
     * <p>
     * 活动开始：
     * <p>
     * 所有活动状态的变化，都会给前台推包，所有在线用户；
     * 用户上线要请求当前开启的活动列表
     * <p>
     * 活动独立，基本不被其他类调用，通过事件的方式来解耦。
     */

    @Override
    public void instanceAfter() {
        LOGGER.debug("初始化活动类型的Manager[{}]！！！", this.getClass().getSimpleName());

        this.teamDataMap = Maps.newConcurrentMap();
        ActiveAnno anno = this.getClass().getAnnotation(ActiveAnno.class);
        if(anno == null) {
        	return;
        }
        this.id = anno.atv().atvId;
        // 计算红点ID
        this.redPointID = (anno.redType() == ERedType.活动 ? ERedPoint.活动.getId() : ERedPoint.福利.getId()) + this.id;
        this.clazz = anno.clazz();
        this.shareClass = anno.shareClass();
        this.redisClass = anno.redisClass() == Object.class ? this.clazz : anno.redisClass();
        if (this.clazz == null) {
            LOGGER.debug("{}活动数据类未实现！！！", this.clazz.getSimpleName());
        }
        //
        registerActiveManager(id, this);
        // 事件注册
        EventRegister reg = this.getClass().getAnnotation(EventRegister.class);
        if (reg != null) {
            for (EEventType e : reg.value()) {
                EventBusManager.register(e, this);
            }
        }
        // 定时器注册
        EventBusManager.register(EEventType.活动定时器, this);
    }

    public SystemActiveBean getBean() {
        return SystemActiveConsole.getSystemActive(id);
    }

    /**
     * 初始化
     */
    public void init() {
        LOGGER.debug("活动[{}]初始化", this.id);
    }

    /**
     * 活动开始调用
     */
    public void start() {
        LOGGER.debug("活动[{}]开始", this.id);
    }

    /**
     * 活动结束调用
     */
    public void end() {
        LOGGER.debug("活动[{}]结束", this.id);
    }

    /**
     * 活动开始也会调用
     * 每天开始调用，也就是当天到第二天时间的交替
     */
    public void everyDayStart(DateTime time) {
        LOGGER.debug("活动[{}]每天初始化{}", this.id, time);
    }

    /**
     * 活动每天结束调用</BR>
     * 注意方法里面的当前之间取参数time使用，活动状态判断也是
     * @param time
     */
    public void everyDayEnd(DateTime time) {
        LOGGER.debug("活动[{}]每天结束{}", this.id, time);
    }

    /**
     * 每秒执行，子类可以重写实现其他功能
     *
     * @param time
     */
    public void everySecond(DateTime time) {

    }

    /**
     * 取活动基本配置
     */
    public Map<String, String> getConfig() {
        SystemActiveBean bean = getBean();
        if (bean == null) { return Maps.newHashMap(); }
        Map<String, String> map = bean.getConfigMap();
        if (map == null) { return Maps.newHashMap(); }
        return map;
    }

    /**
     * @param key      键名
     * @param defValue 如果不存在，默认值
     * @return
     */
    public int getConfigInt(String key, int defValue) {
        Map<String, String> map = getConfig();
        if (!map.containsKey(key)) { return defValue; }
        return Integer.valueOf(map.get(key));
    }

    public float getConfigFloat(String key, float defValue) {
        Map<String, String> map = getConfig();
        if (!map.containsKey(key)) { return defValue; }
        return Float.valueOf(map.get(key));
    }

    /**
     * 基本配置
     *
     * @param key
     * @return
     */
    public String getConfigStr(String key) {
        Map<String, String> map = getConfig();
        if (!map.containsKey(key)) { return ""; }
        return map.get(key);
    }

    /**
     * 取活动奖励配置列表
     *
     * @return
     */
    public Map<Integer, SystemActiveCfgBean> getAwardConfigList() {
        SystemActiveBean bean = getBean();
        if (bean == null) { return Maps.newHashMap(); }
        return bean.getAwardConfigList();
    }
    
    /**
     * 根据活动Id,获取该活动能够获得的奖励球员个数.
     * 有些活动获得球员是直接放仓库.
     * @param atcId
     * @return		返回的Map中key=1,存储的是球员奖励数据.key=2,存储的是道具或物品奖励数据.
     */
    public Map<Integer, List<PropSimple>> getAwardPlayersNum(int actId){
    	SystemActiveCfgBean cfgBean = getAwardConfigList().get(actId);
    	if (cfgBean == null) {
			return ImmutableMap.of();
		}
    	
    	List<PropSimple> tmpList = cfgBean.getPropSimpleList();
    	if (tmpList == null || tmpList.size() == 0) {
			return ImmutableMap.of();
		}
    	
    	//key=1,球员. key=2,物品或者道具
    	Map<Integer, List<PropSimple>> map = Maps.newHashMap();
    	int type = 1;
    	for (PropSimple obj : tmpList) {
    		PropBean prop = PropConsole.getProp(obj.getPropId());
			if (prop.getType() == EPropType.Player || prop.getType() == EPropType.Wrap_Player) {
				type = 1;
			}else {
				type = 2;
			}
			
			if (map.get(type) == null) {
				map.put(type, Lists.newArrayList());
			}
			
			map.get(type).add(obj);
		}
    	
    	return map;
    }

    /**
     * 取现在时刻活动状态
     * 如果不收时间控制的活动，都是进行中状态
     * @return
     */
    public EActiveStatus getStatus() {
        return SystemActiveConsole.getActiveStatus(id, DateTime.now());
    }

    public EActiveStatus getStatus(DateTime now) {
        return SystemActiveConsole.getActiveStatus(id, now);
    }

    public int getId() {
        return id;
    }

    /**
     * 当天活动第几天，自然天，只算日期，不算小时
     *
     * @return 0是活动开始的第一天
     */
    public int getDay() {
        return DateTimeUtil.getDaysBetweenNum(getBean().getStartDateTime(), DateTime.now(), 0);
    }

    /**
     * 小时差的天数
     *
     * @return 0是活动开始的第一天
     */
    public int getDayTime() {
        return DateTimeUtil.getDaysBetweenNum(getBean().getStartDateTime(), DateTime.now());
    }

    /**
     * 活动结束第几天
     *
     * @return
     */
    public int getEndDay() {
        return DateTimeUtil.getDaysBetweenNum(getBean().getEndDateTime(), DateTime.now());
    }

    /**
     * 取活动公共数据，只是本区
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends ActiveBase> T getShareData() {
        if (shareData == null) {
            ActiveBasePO po = queryActiveShareData();
            if (po == null) {
                po = new ActiveBasePO();
                po.setAtvId(this.id);
                po.setTeamId(0L);
                po.setShardId(GameSource.shardId);
                shareData = createActiveShareData(po);
            } else {
                shareData = createActiveShareData(po);
            }
        }
        return (T) shareData;
    }

    /**
     * 取玩家的活动数据
     *
     * @param teamId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends ActiveBase> T getTeamData(long teamId) {
        ActiveBase atvObj = teamDataMap.get(teamId);
        if (atvObj == null) {
            ActiveBasePO po = queryActiveData(teamId);
            if (po == null) {
                atvObj = createActiveData(teamId);
            } else {
                atvObj = createActiveBase(po);
            }
            teamDataMap.put(teamId, atvObj);
            // 活动数据不检查回收，正常下线才回收
        }
        return (T) atvObj;
    }

    /**
     * 每天活动数据
     *
     * @param teamId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends ActiveBase> T getTeamDataRedisDay(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.AtvDataObject + this.id + "_");
        ActiveBase atvObj = redis.getObj(key);
        if (atvObj == null || atvObj.getPo() == null) {
            atvObj = createActiveBaseRedisDay(teamId);
            atvObj.setRedisData(true);
            redis.set(key, atvObj, RedisKey.DAY);
        }
        return (T) atvObj;
    }

    /**
     * 保存redis数据
     *
     * @param teamId
     * @param atvObj
     */
    public <T extends ActiveBase> void saveDataReidsDay(long teamId, T atvObj) {
        String key = RedisKey.getDayKey(teamId, RedisKey.AtvDataObject + this.id + "_");
        redis.set(key, atvObj, RedisKey.DAY);
    }

    /**
     * 子类可自行实现, DB查询
     *
     * @param teamId
     * @return
     */
    public ActiveBasePO queryActiveData(long teamId) {
        return activeAO.getActiveBase(GameSource.shardId, this.id, teamId);
    }

    /**
     * 15天前的记录
     *
     * @param teamId
     * @return
     */
    public List<ActiveBasePO> queryActiveDataListBeforeDay(long teamId, int beforeDays) {
        return activeAO.getActiveBaseListBeforeDay(GameSource.shardId, this.id, teamId, beforeDays);
    }

    /**
     * 公共数据
     *
     * @return
     */
    public ActiveBasePO queryActiveShareData() {
        return activeAO.getActiveShareData(GameSource.shardId, this.id);
    }

    /**
     * 子类可自行实现, 创建
     *
     * @param teamId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends ActiveBase> T createActiveData(long teamId) {
    	T t = (T) newTclass(this.clazz, new ActiveBasePO(this.getId(), GameSource.shardId, teamId, teamManager.getTeamName(teamId)));
    	createInit(t);
        return t ;
    }
    
    /**
     * 创建ActiveData 初始化调用 </BR>
     * 如果处理了数据初始化的操作，自行调用save()保存 </BR>
     * 注意：不能在这里再调用 getTeamData ，否则会死循环，里面的调用也是 </BR>
     * @param <T>
     */
    public <T extends ActiveBase> void createInit(T t) {
    	// 方便子类实现创建,初始化
    }

    /**
     * 子类可自行实现, 创建
     *
     * @param teamId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends ActiveBase> T createActiveBaseRedis(long teamId) {
        return (T) newTclass(this.clazz, new ActiveBasePO(this.getId(), GameSource.shardId, teamId, teamManager.getTeamName(teamId)));
    }

    /**
     * 子类可自行实现, 创建
     *
     * @param teamId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends ActiveBase> T createActiveBaseRedisDay(long teamId) {
        return (T) newTclass(this.redisClass, new ActiveBasePO(this.getId(), GameSource.shardId, teamId, teamManager.getTeamName(teamId)));
    }

    @SuppressWarnings("unchecked")
    public <T extends ActiveBase> T createActiveBase(ActiveBasePO po) {
        return (T) newTclass(this.clazz, po);
    }

    @SuppressWarnings("unchecked")
    public <T extends ActiveBase> T createActiveShareData(ActiveBasePO po) {
        return (T) newTclass(this.shareClass, po);
    }

    @Override
    public void offline(long teamId) {
        // 检查是否隐藏用户活动入口
        if (checkHideWindow(teamId)) {
            atvTeamCloseManager.addTeamCloseAtv(teamId, this.getId());
        }
        teamDataMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teamDataMap.remove(teamId);
    }

    /**
     * 检查满足条件则隐藏用户入口
     * 自行自行实现，默认返回false
     *
     * @param teamId
     */
    public boolean checkHideWindow(long teamId) {
        return false;
    }

    private <T extends ActiveBase> T newTclass(Class<T> clazz, ActiveBasePO po) {
        T a = null;
        try {
            a = clazz.getConstructor(ActiveBasePO.class).newInstance(po);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return a;
    }

    /**
     * 定时调用，每秒
     * 子类不要覆盖，可以重写everySecond方法
     */
    @Subscribe
    public final void onSecondCall(Date date) {
        DateTime now = new DateTime(date);
        LOGGER.debug("活动[{}]定时器执行：{}", id, now.toString());
        // 检查活动
        EActiveStatus old = SystemActiveConsole.getActiveStatus(this.id, now.minusSeconds(1)); // 上一秒状态
        EActiveStatus news = SystemActiveConsole.getActiveStatus(this.id, now); // 当前秒状态
        onStatusChange(old, news);
        // 今天结束执行
        if (now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).equals(now)) {
            everyDayEnd(now);
        }
        // 0点0秒执行
        else if (now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).equals(now)) {
            everyDayStart(now);
        }
        // 每秒执行
        everySecond(now);
    }

    /**
     * 状态变化
     * 未开始
     * 开始
     * 结束->
     */
    public void onStatusChange(EActiveStatus curr, EActiveStatus next) {
        if (curr == EActiveStatus.已结束) {
            return;
        }
        //
        if (curr == EActiveStatus.未开始 && next == EActiveStatus.进行中) {
            // 活动开始
            systemActiveManager.pushActiveChange(this.id);
            start();
        } else if (curr == EActiveStatus.进行中 && next == EActiveStatus.已结束) {
            // 活动结束
            systemActiveManager.pushActiveChange(this.id);
            end();
        } else if (curr == EActiveStatus.进行中 && next == EActiveStatus.进行中) {
            // 进行中
        }
    }

    /**
     * 清理活动数据
     * 清理活动数据内存（让直接修改DB数据生效）
     * 子类可以重写额外要清理的数据
     */
    public void clearActiveBase(long teamId) {
        activeAO.clearActiveData(teamId, this.id);
        teamDataMap.remove(teamId);
        String key = RedisKey.getDayKey(teamId, RedisKey.AtvDataObject + this.id + "_*");
        redis.delRedisCache(key);
    }

    /**
     * 清理活动数据
     * 清理活动数据内存（让直接修改DB数据生效）
     * 子类可以重写额外要清理的数据
     */
    public void clearActiveBase() {
        activeAO.clearActiveData(this.id);
        teamDataMap.clear();
        redis.delRedisCache(RedisKey.AtvDataObject + this.id + "_*");
    }

    /**
     * 如果true，按指定错误码回包
     *
     * @param condition
     * @param errorCode
     * @return true是不通过
     */
    public boolean checkTodo(boolean condition, int errorCode) {
        if (condition) { sendMessage(AtvAwardData.newBuilder().setCode(errorCode).build()); }
        return condition;
    }

    /**
     * 主界面  </BR>
     * 如有特殊，子类自行重写  </BR>
     * -----------------------------协议----------------------------------------------  </BR>
     * required int32 atvId = 1; //活动ID                                   atvId  </BR>
     * repeated int32 finishStatus = 2; // 完成状态，包含奖励ID即完成       finishStatus  </BR>
     * repeated int32 awardStatus = 3; // 领奖状态，包含即领取				awardStatus  </BR>
     * optional int32 value = 4; //										  	iData1  </BR>
     * optional int32 other = 5; //										  	iData2  </BR>
     * optional string extend = 6; //										sData3  </BR>
     */
    public void showView() {
        long teamId = getTeamId();
        ActiveBase atvObj = getTeamData(teamId);
        sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
                .setAtvId(this.getId())
                .addAllFinishStatus(atvObj.getFinishStatus().getList())
                .addAllAwardStatus(atvObj.getAwardStatus().getList())
                .setValue(atvObj.getiData1())
                .setOther(atvObj.getiData2())
                .setExtend(atvObj.getPropNum().getValueStr())
                .build());
    }

    /**
     * 通用领奖接口 <BR/>
     * 调用要求： <BR/>
     * 1,finishStatus必须是完成状态，包含才可以领奖 <BR/>
     * 2,awardStatus是领奖状态，包含是已经领奖 <BR/>
     * 3,默认不做活动状态限制，只要是完成的奖励，都可以领取 <BR/>
     * @param Config里面配置的id来领取奖励，如果有特殊条件和判断，自己的活动实现类完成 <BR/>
     */
    public void getAward(int id) {
        long teamId = getTeamId();
        //
        ActiveBase atvObj = getTeamData(teamId);
        // 校验
        ErrorCode code = checkGetAward(teamId, atvObj, id);
        if (checkTodo(code != ErrorCode.Success, code.code)) {
            return;
        }
        // 保存数据
        atvObj.getAwardStatus().addValue(id);
        atvObj.save();
        // 发奖
        List<PropSimple> awardList = sendAward(teamId, atvObj, id);
        sendMessage(AtvAwardData.newBuilder()
                .setAtvId(this.getId())
                .setAwardId(id)
                .setCode(ErrorCode.Success.code)
                .addAllAwardList(PropManager.getPropSimpleListData(awardList))
                .addAllFinishStatus(atvObj.getFinishStatus().getList())
                .addAllAwardStatus(atvObj.getAwardStatus().getList())
                .build());
        // 领奖后处理
        getAwardFinish(teamId, atvObj, id);
        // 红点逻辑处理
        redPointPush(teamId);
    }

    /**
     * getAward发奖
     *
     * @param teamId
     * @param atvObj 这个对象不能是redis，redis数据进来不会被保存
     * @param id
     */
    public List<PropSimple> sendAward(long teamId, ActiveBase activeBase, int id) {
        // 领奖励
        SystemActiveCfgBean cfgBean = getAwardConfigList().get(id);
        List<PropSimple> awardList = cfgBean.getPropSimpleList();
        if (cfgBean.getAwardType().equals("PropActiveData")) {
            for (PropSimple ps : awardList) {
                activeBase.getPropNum().setValueAdd(ps.getPropId() - 1, ps.getNum());
            }
            activeBase.save();
        } else {
            propManager.addPropList(teamId, awardList, true, getActiveModuleLog());
        }
        return awardList;
    }
    
    /**
     * 特殊奖励发放方法,如果奖励的是球员直接入仓库.
     * @param teamId
     * @param actId
     */
    public void sendAward(long teamId, int actId) {
    	final SystemActiveCfgBean cfg = getAwardConfigList().get(actId);
    	Map<Integer, List<PropSimple>> map = getAwardPlayersNum(actId);
    	// 奖励球员
		List<PropSimple> list = map.get(1);
		if (list != null && list.size() > 0) {
			list.forEach(obj -> {
				PropExtPlayerBean prb = PropConsole.getPlayerProp(obj.getPropId());
				if (prb == null) {
					return;
				}
				
				int price = obj.getNum();
		        int playerId = prb.getHeroId();
		        //需求修改直接添加球员到仓库
		        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
		        PlayerTalent pt = PlayerTalent.createPlayerTalent(teamId, prb.getHeroId(), 
		        	tp.getNewTalentId(), PlayerManager._initDrop, true);
		        PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
		        int fprice = price;
		        if (price == 9999) {  // 取当前市价
		            fprice = pb.getPrice();
		        } else if (price == 8888) { // 取本服低薪
		            fprice = playerManager.getPlayerMinPrice(playerId);
		        }
		        if (fprice <= 0) {
		            fprice = 2051;
		        }
		        Player player = tp.createPlayer(pb, fprice, EPlayerPosition.NULL.name(), pt, prb.isBind());
		        playerManager.addPlayerToStore(teamId, tp, player, pb, ModuleLog.getModuleLog(EModuleCode.主线赛程, "通关送球员"));
		    	// 额外赠送挑战次数
		    	int addNum = new Integer(cfg.getConditionMap().getOrDefault("addFightNum", "0"));
		    	ErrorCode code = mainMatchManager.addMatchNum(teamId, addNum);
		    	log.debug("主线赛程通关奖励，增加挑战次数code={}, addNum={}", code, addNum);
			});
		}
		
		//奖励其他物品或道具
		propManager.addPropList(teamId, map.get(2), true, getActiveModuleLog());
		
    }

    /**
     * 发抽奖奖励
     *
     * @param teamId
     * @param id
     * @return
     */
    public PropRandomHit sendLotteryAward(long teamId, int id, Set<Integer> continueSet) {
        SystemActiveCfgBean cfgBean = getAwardConfigList().get(id);
        PropRandomHit hit = cfgBean.getPropRandomSet().random(continueSet);
        List<PropSimple> awardList = hit.getItem().getPropSimpleList();
        propManager.addPropList(teamId, awardList, true, getActiveModuleLog());
        return hit;
    }

    /**
     * 领取每天奖励
     * 子类自己实现
     */
    public void getDayAward() {
        sendMessage(AtvAwardData.newBuilder().setAtvId(this.getId()).setAwardId(-1).setCode(ErrorCode.Success.code).build());
    }

    /**
     * 直接购买完成
     * 子类自己实现
     *
     * @param tp
     */
    public void buyFinish(int tp) {
        sendMessage(AtvAwardData.newBuilder().setAtvId(this.getId()).setAwardId(tp).setCode(ErrorCode.Success.code).build());
    }

    /**
     * 抽奖
     * tp 转盘类型
     * 前置条件，消耗，发奖，限量
     */
    public void lottery(int tp) {
        long teamId = getTeamId();
        // 前置条件
        ErrorCode code = checkLotteryCondition(tp);
        if (checkTodo(code != ErrorCode.Success, code.code)) {
            return;
        }
        // 扣
        ActiveBase activeBase = getTeamData(teamId);
        code = payLottery(activeBase, tp);
        if (checkTodo(code != ErrorCode.Success, code.code)) {
            return;
        }
        //
        PropRandomHit hit = sendLotteryAward(teamId, tp, null);
        //
        lotteryFinish(teamId, tp, hit);
        //
        sendMessage(AtvAwardData.newBuilder()
                .setAtvId(this.getId())
                .setAwardId(tp)
                .setCode(ErrorCode.Success.code)
                .setValue(hit.getIndex())
                .addAllAwardList(PropManager.getPropSimpleListData(hit.getItem().getPropSimpleList()))
                .build());
    }

    /**
     * 抽奖完成处理
     *
     * @param teamId
     * @param tp
     * @param hit
     */
    public void lotteryFinish(long teamId, int tp, PropRandomHit hit) {
    }

    private ErrorCode checkLotteryCondition(int tp) {
        if (getStatus() != EActiveStatus.进行中) {
            return ErrorCode.Active_2;
        }
        SystemActiveCfgBean cfgBean = getAwardConfigList().get(tp);
        if (cfgBean == null) {
            return ErrorCode.Error;
        }
        if (!cfgBean.getAwardType().equals("PropRandom") || cfgBean.getPropRandomSet() == null) {
            return ErrorCode.Error;
        }
        return checkLotteryCustom();
    }

    /**
     * 自定义抽奖验证
     *
     * @return
     */
    public ErrorCode checkLotteryCustom() {
        return ErrorCode.Success;
    }

    /**
     * 抽奖消耗，一般是球券
     *
     * @param <T>
     * @return
     */
    public <T extends ActiveBase> ErrorCode payLottery(T atvObj, int tp) {
        int needMoney = getAwardConfigList().get(tp).getBuyFinish();
        if (needMoney < 1) {
            return ErrorCode.Error;
        }
        boolean paySuc = moneyManager.updateTeamMoney(atvObj.getTeamId(), 0 - needMoney, 0, 0, 0, true, getActiveModuleLog());
        if (!paySuc) {
            return ErrorCode.Error;
        }
        return ErrorCode.Success;
    }

    /**
     * 检查是否能直接购买完成
     *
     * @param atvObj
     * @param teamMoney
     * @param tp
     * @return
     */
    public ErrorCode checkBuyFinish(ActiveBase atvObj, TeamMoney teamMoney, int tp) {
        if (getStatus() != EActiveStatus.进行中) {
            return ErrorCode.Active_2;
        }
        SystemActiveBean activeBean = getBean();
        if (!activeBean.getConfigList().containsKey(tp)) {
            return ErrorCode.Active_3;
        }
        int buyFk = activeBean.getAwardConfigList().get(tp).getBuyFinish();
        if (buyFk <= 0) {
            return ErrorCode.Money_1;
        }
        if (atvObj.getFinishStatus().containsValue(tp)) {
            LOGGER.debug("重复完成");
            return ErrorCode.Error;
        }
        // 只能扣球券
        if (!moneyManager.updateTeamMoney(teamMoney, 0 - Math.abs(buyFk), 0, 0, 0, true, getActiveModuleLog())) {
            return ErrorCode.Money_1;
        }
        return ErrorCode.Success;
    }

    /**
     * 领取奖励完成，需要做其他处理的，请实现这里
     *
     * @param atvObj
     * @param id
     */
    public void getAwardFinish(long teamId, ActiveBase atvObj, int id) {
        // 自己实现 ，如logicCall()调用
    }

    /**
     * 领取奖励通用检查条件
     *
     * @param teamId
     * @param atvObj
     * @param id
     * @return
     */
    public ErrorCode checkGetAwardCommon(long teamId, ActiveBase atvObj, int id) {
        // 奖励是否存在
    	SystemActiveBean bean = getBean();
    	if(bean == null) {
    		LOGGER.debug("兑换奖励类型[{}]没有找到!", id);
            return ErrorCode.Active_3;
    	}
        Map<Integer, SystemActiveCfgBean> awardCfg = bean.getAwardConfigList();
        if (awardCfg == null || !awardCfg.containsKey(id)) {
            LOGGER.debug("兑换奖励类型[{}]没有找到!", id);
            return ErrorCode.Active_3;
        }
        // 是否完成
        if (atvObj.getFinishStatus().valueCount(id) - atvObj.getAwardStatus().valueCount(id) < 1) {
            LOGGER.debug("未满足领取条件");
            return ErrorCode.Active_5;
        }
        return ErrorCode.Success;
    }

    /**
     * 检查是否可以领奖,自定义条件
     * true是可以领，false是不可以领
     * 默认返回0可以领,错误码不能领
     *
     * @param atvObj
     * @param id
     * @return
     */
    public ErrorCode checkGetAwardCustom(long teamId, ActiveBase atvObj, int id) {
        return ErrorCode.Success;
    }

    /**
     * 领奖校验，先调用通用，再调用自定义
     *
     * @param teamId
     * @param atvObj
     * @param id
     * @return
     */
    public ErrorCode checkGetAward(long teamId, ActiveBase atvObj, int id) {
        ErrorCode code = checkGetAwardCommon(teamId, atvObj, id);
        if (code != ErrorCode.Success) {
            return code;
        }
        // 自定义校验
        code = checkGetAwardCustom(teamId, atvObj, id);
        if (code != ErrorCode.Success) {
            return code;
        }
        return ErrorCode.Success;
    }

    /**
     * 红点变化逻辑
     * 通用领奖状态
     */
    @Override
    public RedPointParam redPointLogic(long teamId) {
    	int num = 0;
    	// 已结束的活动，屏蔽红点
    	if(getStatus() == EActiveStatus.进行中) {
    		num = redPointNum(teamId); 
    	}
        return new RedPointParam(teamId, this.redPointID, num);
    }

    /**
     * 红点逻辑计算
     *
     * @param teamId
     * @return
     */
    public int redPointNum(long teamId) {
        ActiveBase atvObj = getTeamData(teamId);
        int num = atvObj.getFinishStatus().count() - atvObj.getAwardStatus().count();
        return num;
    }

    /**
     * 单个模块推送红点变化
     *
     * @param teamId
     */
    public void redPointPush(long teamId) {
        EventBusManager.post(EEventType.奖励提示, redPointLogic(teamId));
    }

    public ModuleLog getActiveModuleLog() {
        return ModuleLog.getModuleLog(EModuleCode.活动, SystemActiveConsole.getSystemActive(id).getName());
    }

    /**
     * 运行后台控制触发事件 </BR>
     */
	public void shootEvent() {
		LOGGER.error("触发活动发奖事件，atvId={}", this.getId());
	}

    public void gmReset(long teamId, AtvCommonData cd) {
    }
}

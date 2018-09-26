package com.ftkj.manager.logic.match;

import com.ftkj.cfg.KnockoutMatchBean;
import com.ftkj.console.KnockoutMatchConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.db.domain.match.MatchPKPO;
import com.ftkj.db.domain.match.MatchPO;
import com.ftkj.db.domain.match.MatchSignPO;
import com.ftkj.enums.EMatchStatus;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.logic.KnockoutMatchPKManager;
import com.ftkj.manager.logic.LocalBattleManager;
import com.ftkj.manager.logic.TeamManager;
import com.ftkj.manager.match.MatchPK;
import com.ftkj.manager.match.MatchSign;
import com.ftkj.server.GameSource;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.ThreadPoolUtil;
import com.ftkj.xxs.core.util.DebugTool;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class KnockoutMatch {
    private static final Logger log = LoggerFactory.getLogger(KnockoutMatch.class);
    //
    private MatchPO matchPO;
    /*
     * 比赛开启线程
     */
    private MatchPKThread thread;
    //
    private KnockoutMatchPKManager manager;
    private TeamManager teamManager;
    //
    private LocalBattleManager localBattleManager;

    /**
     * 报名列表
     */
    private ConcurrentMap<Long, MatchSign> teamSignMap;
    /**
     * 赛程
     * 轮数：比赛记录
     */
    private ConcurrentMap<Integer, List<MatchPK>> roundPKMap;
    /**
     * PK的ID号，比赛
     */
    private ConcurrentMap<Long, MatchPK> matchPKMap;
    // 统计排名
    private AtomicInteger rank;
    /**
     * 是否保存DB，用来多虑超快赛类型Type=2
     */
    private boolean isSaveDB = true;
    /**
     * 赛前通知是否已发送
     */
    private boolean isStartTipTopic = false;

    /**
     * 本比赛类型对应枚举
     */
    public EBattleType battleType;

    /**
     * 开始比赛失败次数统计
     * 超快赛卡比赛释放机制
     * 开始比赛失败+1
     */
    private int startPKCount;
    /**
     * 开始比赛失败的话记录时间
     * startPKCount >= 1 时，才不为空
     */
    private DateTime startPKFailTime;

    /**
     * 开始比赛时间：第一次开始比赛
     */
    private DateTime startPKTime;

    /**
     * 创建一次比赛
     *
     * @param seqId
     * @param matchId
     * @return
     */
    public static KnockoutMatch createMatch(int seqId, int matchId) {
        log.debug("创建多人赛[{}[{}],{}]", KnockoutMatchConsole.getName(matchId), matchId, seqId);
        // 创建比赛
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
        int maxRound = KnockoutMatchConsole.getMatchMaxRound(matchId);
        //  计算比赛开始时间;(赛前通知用)
        DateTime matchTime = getStartMatchTime(bean);
        MatchPO po = new MatchPO(seqId, matchId, EMatchStatus.开始报名.status, maxRound, matchTime, GameSource.serverName);
        KnockoutMatch match = new KnockoutMatch(po, null, null);
        if (bean.getType() == 2 || bean.getType() == 3) {
            match.setSaveDB(false);
        }
        match.save();
        return match;
    }

    /**
     * 私有化构造
     */
    @SuppressWarnings("unused")
    private KnockoutMatch() {

    }

    /**
     * 加载比赛的构造调用方式
     *
     * @param matchPO
     * @param signList
     * @param pkList
     */
    public KnockoutMatch(MatchPO matchPO, List<MatchSignPO> signList, List<MatchPKPO> pkList) {
        //
        this.matchPO = matchPO;
        this.thread = new MatchPKThread(this);
        this.manager = InstanceFactory.get().getInstance(KnockoutMatchPKManager.class);
        this.teamManager = InstanceFactory.get().getInstance(TeamManager.class);
        this.localBattleManager = InstanceFactory.get().getInstance(LocalBattleManager.class);
        //
        this.teamSignMap = Maps.newConcurrentMap();
        this.roundPKMap = Maps.newConcurrentMap();
        this.matchPKMap = Maps.newConcurrentMap();

        this.battleType = EBattleType.getBattleType(matchPO.getMatchId());
        // 已报名加载
        if (signList != null && signList.size() > 0) {
            for (MatchSignPO signPO : signList) {
                teamSignMap.put(signPO.getTeamId(), new MatchSign(signPO));
            }
        }
        // 已经进入比赛的加载
        int alePkListSize = 0;
        if (pkList != null && pkList.size() > 0) {
            for (MatchPKPO pkInfo : pkList) {
                MatchPK matchPK = new MatchPK(pkInfo);
                this.matchPKMap.put(pkInfo.getBattleId(), matchPK);
                int round = pkInfo.getRound();
                if (!this.roundPKMap.containsKey(round)) {
                    this.roundPKMap.put(round, Lists.newArrayList());
                }
                this.roundPKMap.get(round).add(matchPK);
                if (pkInfo.getStatus() == 1) {
                    alePkListSize++;
                }
            }
            log.debug("多人赛{},已打完的比赛数：{}", matchPO.getMatchId(), alePkListSize);
        }
        // 默认最大排名数
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(this.getMatchId());
        this.rank = new AtomicInteger(maxRank(bean.getMaxTeam(), alePkListSize));
        log.debug("多人赛[{},比赛时间：{}]当前未进行排名数：{}", bean.getName(), DateTimeUtil.getString(this.getMatchTime()), this.rank.get());
        // 如果已经错过比赛时间，则正常延顺到下一周期
        if (bean.getType() == 1 && this.getStatus() == EMatchStatus.开始报名.status && this.getMatchTime().isBeforeNow()) {
            this.setMatchTime(getStartMatchTime(bean));
            this.save();
        }
        // 非调试模式才自动开启比赛
        if (this.getStatus() == EMatchStatus.比赛中.status) {
            // 继续比赛线程
            startPKRoundThred(60);
        }
    }

    /**
     * 计算最大排名
     *
     * @param maxTeam    最大报名人数
     * @param pkListSize 已经打完的比赛数
     * @return
     */
    private int maxRank(int maxTeam, int pkListSize) {
        if (pkListSize == 0) { return maxTeam; }
        return maxTeam - pkListSize;
    }

    /**
     * 取最近场次比赛开始时间
     *
     * @param bean
     * @return
     */
    public static DateTime getStartMatchTime(KnockoutMatchBean bean) {
        DateTime matchTime = DateTime.now();
        if (bean.getType() == 1) {
            int dow = matchTime.getDayOfWeek();
            matchTime = matchTime.withHourOfDay(bean.getStarHour()).withMinuteOfHour(bean.getStartMin());
            if (!bean.getStartDay().contains(dow) || matchTime.isBeforeNow()) {
                // 计算最近场次的比赛时间
                int pDay = 0;
                for (int d : bean.getStartDay()) {
                    if (d > dow) {
                        pDay += d - dow;
                        break;
                    }
                }
                if (pDay == 0) {
                    pDay = 7 - dow + bean.getStartDay().get(0);
                }
                matchTime = matchTime.plusDays(pDay);
            }
        }
        return matchTime.withSecondOfMinute(0);
    }

    /**
     * 保存本届比赛数据
     * 超快赛不保存，根据isSaveDB字段判断
     */
    public void save() {
        //log.debug("多人赛保存：{}, {}", this.getMatchId(), this.isSaveDB);
        if (this.isSaveDB) {
            this.matchPO.save();
        }
    }

    /** 比赛结束处理 */
    synchronized void matchEnd(BattleSource bs) {
        // 不是比赛中的的结束，不结算
        if (getStatus() != EMatchStatus.比赛中.status) {
            return;
        }
        // 比赛结果
        EndReport report = bs.getEndReport();
        long battleId = bs.getInfo().getBattleId();
        //		log.debug("多人赛比赛[{}]结果!", battleId);
        //更新比赛胜利球队数据
        MatchPK matchPK = this.matchPKMap.get(battleId);
        matchPK.setStatus(1);
        matchPK.setWinTeamId(report.getWinTeamId());
        matchPK.setHomeScore(report.getHomeScore());
        matchPK.setAwayScore(report.getAwayScore());
        matchPK.setEndTime(DateTime.now());
        matchPK.save();
        // 更新失败玩家排名
        long lostTeam = matchPK.getWinTeamId() == matchPK.getHomeId() ? matchPK.getAwayId() : matchPK.getHomeId();
        MatchSign matchSign = teamSignMap.get(lostTeam);
        matchSign.setOut(true);//TODO 
        updateTeamRank(matchSign);
        // 通知失败的玩家
        this.manager.topicMatchReport(matchSign);
        //如果还有新的一轮比赛，则继续
        log.info("knockout match end. kid {} seqid {} round {}/{} bid {} htid {} atid {} score {}:{} loserank {}",
            getMatchId(), getSeqId(), getRound(), getMaxRound(), bs.getBattleId(), bs.getHomeTid(),
            bs.getAwayTid(), bs.getHomeScore(), bs.getAwayScore(), matchSign.getRank());
        //        log.debug("多人赛[{}]第[{}]轮比赛[{}VS{}，比分{}:{}]结束,{}获得胜利,失败排名{}", this.thread.getName(),
        //            this.getRound(), matchPK.getHomeId(), matchPK.getAwayId(), report.getHomeScore(), report.getAwayScore(), matchPK.getWinTeamId(), matchSign.getRank());
        List<MatchPK> pkList = this.roundPKMap.get(this.getRound());
        if (log.isDebugEnabled()) {
            log.debug(pkList.toString());
        }
        boolean isOver = pkList.stream().allMatch(pk -> pk.getStatus() == 1);
        if (!isOver || this.getStatus() == EMatchStatus.结束.status) {
            return;
        }
        // 本轮结束，直接进入下一轮
        log.debug("多人赛[{}]第[{}]轮比赛结束", this.thread.getName(), this.getRound());
        if (this.getRound() < this.getMaxRound()) {
            log.debug("多人赛[{}]线程状态为：{}", this.thread.getName(), this.thread.getStatus());
            // 下一轮
            startPKRoundThred(KnockoutMatchConsole.Round_Delay);
        } else {
            //给第一名发奖励
            MatchSign matchSignWin = teamSignMap.get(matchPK.getWinTeamId());
            updateTeamRank(matchSignWin);
            log.info("knockout match end. kid {} seqid {} round {}/{} bid {} htid {} atid {} score {}:{} 1strank {}",
                getMatchId(), getSeqId(), getRound(), getMaxRound(), bs.getBattleId(), bs.getHomeTid(),
                bs.getAwayTid(), bs.getHomeScore(), bs.getAwayScore(), matchSignWin.getRank());
            this.manager.topicMatchReport(matchSignWin);
            this.manager.pushMatchPKList(this);
            // 结束比赛线程，比赛结束
            log.debug("多人赛[{}]比赛结束", this.thread.getName());
            this.setStatus(EMatchStatus.结束.status);
            this.save();
            // 比赛结束,更新历史最佳,创建新比赛
            this.manager.matchEnd(this, getJoinPKTeamList());
            // 跑马灯
            this.manager.matchEndGameTip(matchSignWin.getTeamId(), this.getMatchId());
        }
    }

    /** 更新排名 */
    private synchronized void updateTeamRank(MatchSign matchSign) {
        int newRank = this.rank.getAndDecrement();
        //        log.info("knockout rank. kid {} team {} rank {}", getMatchId(), matchSign.getTeamId(), newRank);
        if (newRank < 1) {
            log.error("knockout rank < 1. ms {} trace \n{}", matchSign, DebugTool.printlnStackTrace());
        }
        matchSign.setRank(newRank);
        if (isSaveDB) {
            matchSign.save();
        }
    }

    /**
     * 是否已报名
     *
     * @param teamId
     * @return
     */
    public boolean isSign(long teamId) {
        return teamSignMap.containsKey(teamId);
    }

    /**
     * 比赛中
     * @param teamId
     * @return
     */
    public boolean isInMatching(long teamId) {
      return teamSignMap.containsKey(teamId) && teamSignMap.get(teamId).isOut() == false;
    }
    
    public Set<Long> getSignTeam() {
        return this.teamSignMap.keySet();
    }

    /**
     * 所有报名
     *
     * @return
     */
    public List<Long> getSignTeamList() {
        return this.teamSignMap.keySet().stream().collect(Collectors.toList());
    }

    /**
     * 所有报名NPC列表
     *
     * @return
     */
    public List<Long> getSignNpcList() {
        return this.teamSignMap.keySet().stream().filter(t -> NPCConsole.isNPC(t)).collect(Collectors.toList());
    }

    /**
     * 已报名非NPC列表
     *
     * @return
     */
    public List<Long> getSignTeamListNotNpc() {
        return this.teamSignMap.keySet().stream().filter(t -> !NPCConsole.isNPC(t)).collect(Collectors.toList());
    }

    /**
     * 报名
     *
     * @param teamId
     * @return
     */
    public synchronized void sign(long teamId, int teamCap) {
        if (teamSignMap.containsKey(teamId)) {
            return;
        }
        // 防止开始比赛后报名
        MatchSign sign = new MatchSign(new MatchSignPO(this.getSeqId(), this.getMatchId(), teamId, teamCap));
        // 设置成最后的排名
        sign.setRank(this.rank.get());
        // 预设是正式报名
        sign.setStatus(1);
        // 是否保存，多人赛不存
        if (this.isSaveDB) {
            sign.save();
        }
        teamSignMap.put(teamId, sign);
    }

    /**
     * 取报名人数
     *
     * @return
     */
    public int getSignNum() {
        if (this.teamSignMap == null) {
            return 0;
        }
        return this.teamSignMap.size();
    }

    /**
     * 报名的正式参与比赛玩家
     *
     * @return
     */
    public List<MatchSign> getJoinPKTeamList() {
        return teamSignMap.values().stream().filter(sign -> sign.getStatus() == 1).collect(Collectors.toList());
    }

    /**
     * 报名但没进入比赛的玩家
     *
     * @return
     */
    @SuppressWarnings("unused")
    private List<MatchSign> getOutPKTeamList() {
        return teamSignMap.values().stream().filter(sign -> sign.getStatus() == 0).collect(Collectors.toList());
    }

    //-------------------------------------------------------------------------

    /**
     * 开始比赛
     */
    public synchronized void startPK() {
        log.info("报名结束，开始比赛【{}】多人赛", this.thread.getName());
        /* 1,检查是否满足PK人数下限，如果不满足，比赛不开始，延顺报名
         * 2,如果人数不满人数上限，不满NPC球队
         * 3,如果报名人数超过比赛人数上限，自动挤掉战力排行版的球队，更新报名表正式成员状态（不包括被挤掉的，战力排行）
         * 4,更新比赛的状态为：比赛中，创建新的一轮比赛赛程，开始比赛
         * 5,比赛完成，该轮的所有比赛完成，更新比赛名次，初始化下一轮，如果是最后一轮，则停止比赛，更新比赛状态：已结束
         * 6,根据比赛排名发送奖励，更新历史战绩，最佳等等；
         * 7,开启新的一届比赛报名
         */
        // 准备比赛人数
        if (!readyPK()) {
            this.addStartPKCount(1);
            log.warn("赛前准备不通过，没有开始【{}】类型比赛", this.thread.getName());
            return;
        }
        // 开始比赛线程
        this.setStatus(EMatchStatus.比赛中.status);
        if (this.getStartPKTime() == null) {
            // 超快赛做超时处理
            this.setStartPKTime(DateTime.now());
        }
        this.save();
        startPKRoundThred(KnockoutMatchConsole.Start_Delay);
    }

    /**
     * 延时执行线程，秒数
     *
     * @param delay
     */
    public synchronized void startPKRoundThred(int delay) {
        if (!GameSource.serverName.equals(this.matchPO.getLogicName())) {
            log.warn("多人赛[{}]所属服务器节点={}，非本节点创建比赛不开启线程!", this.getName(), this.matchPO.getLogicName());
            return;
        }
        // 推送比赛列表
        this.manager.pushMatchPKList(this);
        // 先匹配，延迟开始比赛；
        if (this.thread.isDelay() || this.thread.isAlive()) {
            log.warn("多人赛[{}]线程正在执行中。。。status={}", this.getName(), this.getStatus());
            return;
        }
        startPKRoundThred(0, delay);
    }

    /**
     * 设置线程初始状态，延时执行
     *
     * @param status
     * @param delay
     */
    public void startPKRoundThred(int status, int delay) {
        this.thread.setDelay(true);
        this.thread.setStatus(status);
        log.info("激活多人赛[{}]线程,状态：{},延时:{}", this.thread.getName(), this.thread.getStatus(), delay);
        ThreadPoolUtil.getScheduledPool(KnockoutMatchConsole.Thread_Name).schedule(this.thread, delay, TimeUnit.SECONDS);
    }
    //-------------------------------------------------------------------

    /** 赛前准备 */
    private boolean readyPK() {
        // 更新非NPC战力
        teamSignMap.values().stream().filter(s -> !NPCConsole.isNPC(s.getTeamId())).forEach(s -> {
            int cap = teamManager.getTeamAllAbility(s.getTeamId()).getTotalCap();
            s.setTeamCap(cap);
            if (this.isSaveDB) {
                s.save();
            }
        });
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(this.getMatchId());
        int signNum = getSignNum();
        //大于下限但是没有报满，自动补NPC
        if (signNum >= bean.getMinTeam() && signNum < bean.getMaxTeam() && bean.getAutoNpc() == 1) {
            // 是否自动补满NPC，开始比赛前，大于下限自动补
            int npcNum = bean.getMaxTeam() - signNum;
            log.debug("多人赛[{}], 补发npc报名人数{}", this.getName(), npcNum);
            List<Long> npcTeamList = NPCConsole.getSeqNpcId(npcNum, this.getSignNpcList(), KnockoutMatchConsole.NPC_Min_ID, KnockoutMatchConsole.NPC_Max_ID);
            for (long npcId : npcTeamList) {
                sign(npcId, 100);//-- NPC的战力值，补NPC时，100这个值没意义
            }
            return true;
        }
        // 开始比赛
        else if (signNum == bean.getMaxTeam()) {
            return true;
        }
        // 超出最大比赛人数，挤出战力较低的球队
        else if (signNum > bean.getMaxTeam()) {
            // 被挤出规则： 1，报名人数超出； 2，战力小于最小战力
            // 排序战力，移除的status设置为0
            List<MatchSign> removeSign = teamSignMap.values().stream()
                .sorted((o1, o2) -> o2.getTeamCap() - o1.getTeamCap())
                .skip(bean.getMaxTeam())
                .collect(Collectors.toList());
            // 比较，从最后一名开始
            for (MatchSign delSign : removeSign) {
                delSign.setStatus(0);
                if (this.isSaveDB) {
                    delSign.save();
                }
                // 被挤出去的玩家，直接获得最后一名
                this.manager.topicMatchReport(delSign);
            }
            return true;
        }
        // 不满足最低报名人数，延顺比赛周期
        else {
            // 其他情况延顺
            log.warn("多人赛[{}], 不满足最低报名下限，不开始比赛!", this.thread.getName());
            this.save();
            return false;
        }
    }

    //	private void setMatchTimeToNext(KnockoutMatchBean bean) {
    //		// 比赛周期延顺到系统时间的往后一届
    //		DateTime now = DateTime.now();
    //		if(bean.getType() == 1) {
    //			// N*7天
    //			int day = Days.daysBetween(this.getMatchTime(), now).getDays() / 7 + 1;
    //			this.setMatchTime(this.getMatchTime().plusDays(7 * day));
    //			this.save();
    //		}
    //	}

    //-------------------------------------------------------------------

    /**
     * 是否能进行匹配比赛
     *
     * @return true可以
     */
    public synchronized int matchRound() {
        String matchName = this.thread.getName();
        //
        if (this.getRound() > this.getMaxRound()) {
            log.debug("多人赛[{}]匹配异常，已是最大轮", matchName);
            return 1;
        }
        // 进入下一轮比赛
        int currRound = this.getRound();
        // 上轮没打完 或者 本轮已创建
        if (getTheRoundMatchPKNoStart().size() > 0) {
            log.debug("多人赛[{}]匹配异常，第{}轮没打完", matchName, currRound);
            return 0;
        }
        // 第0轮或者本轮打完，直接进入下一轮
        int nextRound = currRound;
        if (currRound == 0 || isPeakFinish(currRound)) {
            nextRound = currRound + 1;
        }
        /*
         * homeId : awayId
         * 主队VS副队
         */
        long[][] matchInfo = null;
        // 第一轮从报名列表取
        if (nextRound == 1) {
            List<MatchSign> list = getJoinPKTeamList();
            Collections.shuffle(list);
            matchInfo = converSignToTeamMatchInfo(list);
        } else {
            List<MatchPK> list = this.roundPKMap.get(this.getRound());
            Collections.shuffle(list);
            matchInfo = converMatchToTeamMatchInfo(list);
        }
        //
        if (matchInfo == null) {
            log.debug("[{}]-比赛##########异常##########,第几轮:{}，比赛队伍:{}", matchName, nextRound, matchInfo == null ? 0 : matchInfo.length);
            return 1;
        }
        //LoggerManager.debug("多人赛[{}]开始匹配第{}轮对手,排名数{}, 匹配球队:{}", matchName, nextRound, this.rank.get(), matchInfo);
        // 如果出现上一轮匹配出错的，删掉重新匹配
        startPKTask(matchInfo, nextRound);
        return 0;
    }

    /**
     * 对应轮数所有比赛是否已经匹配完成
     *
     * @param round 必 >=1
     * @return true 已匹配完， false 还没完
     */
    private boolean isPeakFinish(int round) {
        if (round == 0) { return true; }
        int pkNum = 0;
        if (this.roundPKMap.containsKey(round)) {
            pkNum = this.roundPKMap.get(this.getRound()).size();
        }
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(this.getMatchId());
        int shouldNum = bean.getMaxTeam() / (int) Math.pow(2, round);
        return pkNum == shouldNum;
    }

    /**
     * 取排序配置
     *
     * @return
     */
    public int getSort() {
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(this.getMatchId());
        if (bean != null) { return bean.getSort(); }
        return 0;
    }

    private void startPKTask(long[][] matchInfo, int round) {
        int pkSize = matchInfo.length;
        List<MatchPK> pkList = Lists.newArrayList();
        for (int i = 0; i < pkSize; i++) {
            long battleId = localBattleManager.getNewBattleId();
            //log.debug("创建多人赛[{}], id=[{}]", this.getMatchId(), battleId);
            MatchPK pk = new MatchPK(battleId, this.getSeqId(), this.getMatchId(), round, matchInfo[i][0], matchInfo[i][1]);
            pkList.add(pk);
            // 方便查找
            this.matchPKMap.put(pk.getBattleId(), pk);
            if (this.isSaveDB) {
                pk.save();
            }
        }
        // 保存比赛进行到哪一轮，创建比赛记录就要保存；
        this.setRound(round);
        if (this.isSaveDB) {
            this.save();
        }
        // 存放每轮的比赛列表
        this.roundPKMap.put(round, pkList);
        log.info("多人赛[{}]匹配对手完毕[最大回合数{},当前：{}, 比赛场次：{}]，准备进入比赛", this.thread.getName(), this.getMaxRound(), round, pkList.size());
        this.manager.pushMatchPKList(this);
    }

    private long[][] converMatchToTeamMatchInfo(List<MatchPK> list) {
        if (list == null || list.size() == 0) { return null; }
        int size = list.size() / 2;
        long[][] matchInfo = new long[size][];
        for (int i = 0; i < size; i++) {
            matchInfo[i] = new long[]{list.get(i * 2).getWinTeamId(), list.get(i * 2 + 1).getWinTeamId()};
        }
        return matchInfo;
    }

    private long[][] converSignToTeamMatchInfo(List<MatchSign> list) {
        int size = list.size() / 2;
        long[][] matchInfo = new long[size][2];
        for (int i = 0; i < size; i++) {
            matchInfo[i] = new long[]{list.get(i * 2).getTeamId(), list.get(i * 2 + 1).getTeamId()};
        }
        return matchInfo;
    }

    /**
     * 取本轮未打完比赛的匹配
     *
     * @return
     */
    public List<MatchPK> getTheRoundMatchPKNoStart() {
        if (!this.roundPKMap.containsKey(this.getRound())) {
            return Lists.newArrayList();
        }
        return this.roundPKMap.get(this.getRound()).stream()
            .filter(pk -> pk.getStatus() == 0).collect(Collectors.toList());
    }

    /**
     * 取本轮的所有比赛
     *
     * @return
     */
    public List<MatchPK> getRoundPKList() {
        return this.roundPKMap.get(this.getRound());
    }

    /**
     * 比赛回合推送
     *
     * @param bs
     * @param report
     */
    public void roundReport(BattleSource bs, RoundReport report) {
        this.manager.roundReport(bs, report, this);
    }

    /**
     * 是否必然会开始比赛
     *
     * @return
     */
    public boolean isMustStart() {
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(this.getMatchId());
        if (getSignNum() < bean.getMinTeam()) {
            return false;
        }
        if (getSignNum() < bean.getMaxTeam() && bean.getAutoNpc() == 0) {
            return false;
        }
        return true;
    }

    //-----------------------------------------------------

    public int getMatchId() {
        return this.matchPO.getMatchId();
    }

    public void setMatchId(int matchId) {
        this.matchPO.setMatchId(matchId);
    }

    public void setStatus(int status) {
        this.matchPO.setStatus(status);
    }

    public Map<Long, MatchSign> getTeamSignMap() {
        return teamSignMap;
    }

    public Map<Integer, List<MatchPK>> getMatchPKMap() {
        return roundPKMap;
    }

    public int getRound() {
        return this.matchPO.getRound();
    }

    public void setRound(int round) {
        this.matchPO.setRound(round);
    }

    public int getMaxRound() {
        return this.matchPO.getMaxRound();
    }

    public DateTime getMatchTime() {
        return this.matchPO.getMatchTime();
    }

    public void setMatchTime(DateTime matchTime) {
        this.matchPO.setMatchTime(matchTime);
    }

    public int getSeqId() {
        return this.matchPO.getSeqId();
    }

    public int getStatus() {
        return this.matchPO.getStatus();
    }

    public boolean isSaveDB() {
        return isSaveDB;
    }

    public void setSaveDB(boolean isSaveDB) {
        this.isSaveDB = isSaveDB;
    }

    public boolean isStartTipTopic() {
        return isStartTipTopic;
    }

    public void setStartTipTopic(boolean isStartTipTopic) {
        this.isStartTipTopic = isStartTipTopic;
    }

    public void setLocalBattleManager(LocalBattleManager localBattleManager) {
        this.localBattleManager = localBattleManager;
    }

    /**
     * 今天是否显示的比赛
     *
     * @return
     */
    public boolean isTodayShow() {
        DateTime mt = getMatchTime();
        int dayOfWeek = mt.getDayOfWeek();
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(this.getMatchId());
        boolean show = bean.getType() == 2 || bean.getVtype() == 2 || (dayOfWeek == DateTime.now().getDayOfWeek() && mt.isAfterNow());
        return show;
    }

    @Override
    public String toString() {
        return "KnockoutMatch [matchPO=" + matchPO + "]";
    }

    /**
     * 打印线程信息
     */
    public void printThread() {

    }

    public int getStartPKCount() {
        return startPKCount;
    }

    public void addStartPKCount(int startPKCount) {
        this.startPKCount = startPKCount;
        this.startPKFailTime = DateTime.now();
        this.setStatus(EMatchStatus.异常.status);
    }

    public String getName() {
        return this.thread.getName();
    }

    public DateTime getStartPKFailTime() {
        return startPKFailTime;
    }

    public Thread.State getThreadStatus() {
        return this.thread.getState();
    }

    public boolean isDelayThread() {
        return this.thread.isDelay();
    }

    public DateTime getStartPKTime() {
        return startPKTime;
    }

    public void setStartPKTime(DateTime startPKTime) {
        this.startPKTime = startPKTime;
    }

}

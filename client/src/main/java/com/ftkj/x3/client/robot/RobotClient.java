package com.ftkj.x3.client.robot;

import com.ftkj.x3.client.ClientConfig;
import com.ftkj.x3.client.X3RunnableTask;
import com.ftkj.x3.client.X3SpringConfig;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.net.X3ClientChannelGroup;
import com.ftkj.x3.client.net.X3ClientChannelHolder;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.TestContext;
import com.ftkj.x3.client.task.TestContext.TestParams;
import com.ftkj.x3.client.task.helper.LoginHelper;
import com.ftkj.x3.client.task.logic.player.PlayerClient;
import com.ftkj.x3.client.task.logic.player.PlayerClient.PlayerTestContext;
import com.ftkj.x3.client.task.logic.sys.MailClient;
import com.ftkj.x3.client.task.logic.sys.ScoutClient;
import com.ftkj.x3.client.task.logic.sys.ScoutClient.ScoutTestContext;
import com.ftkj.x3.client.util.AccountOverrideException;
import com.ftkj.xxs.core.util.DateTimeUtil;
import com.ftkj.xxs.core.util.StringUtil;
import com.ftkj.xxs.core.util.XxsThreadFactory;
import com.ftkj.xxs.core.util.concurrent.HandlingExceptionScheduledExecutor;
import com.ftkj.xxs.util.FileUtils;
import com.ftkj.xxs.util.Server;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 机器人自动玩游戏.
 * * <pre>
 * -ea
 * -server
 * -Dapp.name=mnba.client.robot
 * -XX:+HeapDumpOnOutOfMemoryError
 * -Xms2048m
 * -Xmx2048m
 * -XX:+UseG1GC
 * -Dlog4j.configurationFile=sub/robot/log4j2-robot.xml
 * -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
 * </pre>
 *
 * @author luch
 */
@Component
public class RobotClient extends X3RunnableTask {
    private static final Logger log = LoggerFactory.getLogger(RobotClient.class);
    @Autowired
    private ClientConfig clientConfig;
    @Autowired
    private Config globalConfig;
    @Autowired
    private X3ClientChannelHolder clientChannelHolder;
    @Autowired
    private LoginHelper loginHelper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private ScoutClient scoutClient;
    @Autowired
    private PlayerClient playerClient;

    private RobotConfig config;
    private ScheduledExecutorService executorService;

    /**
     * 所有参与的机器人配置. map[accountId, RobotTeam]
     * 其中一部分会登录到游戏进行操作
     */
    private Map<Long, RobotTeamConfig> teamConfig = Collections.emptyMap();
    /** 登录后进行操作的球队 */
    private ConcurrentMap<Long, RobotTeam> runningTeams = new ConcurrentHashMap<>();

    private AtomicBoolean stop = new AtomicBoolean();

    public static void main(String[] args) {
        new RobotClient().run(X3SpringConfig.PROFILE_ROBOT, args, false);
    }

    @Override
    protected Ret run0(String[] args) {
        log.info("=============> 开始模拟 <=================");
        initConfig(args);
        log.info("=============> 所有球队开始使用功能 <=================");
        runAllRobotModule();
        return succ();
    }

    /** 初始化配置 */
    private void initConfig(String[] args) {
        Config robotConfig = globalConfig.getConfig("x3.client.robot");
        log.info("robot config {}", robotConfig.root().render(
                ConfigRenderOptions.defaults().setComments(false)
                        .setOriginComments(false)));
        config = new RobotConfig(robotConfig);

        //初始化机器人配置
        Map<Long, RobotTeamConfig> teamConfig = new ConcurrentHashMap<>();
        if (StringUtil.notEmpty(config.teamConfig)) {
            teamConfig.putAll(readRobotTeamConfig(config.teamConfig));

            if (teamConfig.get(config.testAccount) == null) {
                throw configError("配置了 team-config %s, 但是测试账号 test-account %s 的配置不在里面", config.teamConfig, config.testAccount);
            }
            int teamSize = teamConfig.size();
            if (teamSize < config.onlineNum) {
                throw configError("球队配置文件team-config %s 中的球队数量 < 在线数量 online-num %s", config.teamConfig, config.onlineNum);
            }
            log.info("load team config from {}, team size {}", config.teamConfig, teamSize);
        } else {
            //没有指定机器人行为配置, 则创建 min( (accountEnd-accountStart), onlineNum * 10) 个机器人配置
            long accountNum = config.accountEnd - config.accountStart;
            if (accountNum < config.onlineNum) {
                throw configError("帐号数量 account.start - account.end %s < 在线数量 online-num %s", accountNum, config.onlineNum);
            }
            int robotNum = (int) Math.min(config.onlineNum * 10, config.accountEnd - config.accountStart);
            for (long aid = config.accountStart; aid <= config.accountStart + robotNum; aid++) {
                teamConfig.put(aid, RobotTeamConfig.newDefault(clientConfig.getServerShardId(), aid));
            }
            log.info("create default team config, team size {}", robotNum);
        }
        this.teamConfig = teamConfig;
    }

    private RuntimeException configError(String format, Object... args) {
        return new IllegalArgumentException(String.format(format, args));
    }

    @Override
    public void beforeStop() {
        log.info("robot client stop...");
        stop.set(true);
        logStats();
        super.beforeStop();
        if (executorService != null && !executorService.isShutdown()) {
            List<Runnable> task = executorService.shutdownNow();
            log.info("shut down executorService. remain task size [{}]", task.size());
        }
        log.info("robot client stop");
    }

    private void logStats() {
    }

    private void runAllRobotModule() {
        executorService = new HandlingExceptionScheduledExecutor(
                Math.min(Runtime.getRuntime().availableProcessors() * 3, 20),
                new XxsThreadFactory("robot"));

        if (config.testMode) {
            log.info("测试模式. 只登录一个球队 {}", config.testAccount);
            RobotTeamConfig rt = teamConfig.get(config.testAccount);
            if (rt == null) {
                log.error("测试模式, 找不到测试帐号[{}]的配置", config.testAccount);
                throw new NullPointerException("测试模式, 找不到测试帐号的配置");
            }
            runModule(rt);
            return;
        }

        int firstLoginCount = 0;
        long delayMin = 0;
        long delayMax = config.firstDelayMaxMillis;
        for (RobotTeamConfig rt : teamConfig.values()) {
            if (firstLoginCount < config.batchLoginNum) {
                scheduleRunModule(rt, config.firstDelayMinMillis, config.firstDelayMaxMillis, true);
            } else {
                scheduleRunModule(rt, delayMin, delayMax, false);
            }
            firstLoginCount++;
            if (firstLoginCount % config.batchLoginNum == 0) {
                delayMin = delayMin + config.firstDelayMaxMillis;
                delayMax = delayMax + config.firstDelayMaxMillis;
                log.debug("next batch login delay {} {}", delayMin, delayMax);
            }
        }

        executorService.scheduleWithFixedDelay(this::logStats, 1, 1, TimeUnit.MINUTES);
    }

    /** 延迟一定时间运行一次"使用功能" */
    private void scheduleRunModule(RobotTeamConfig rt) {
        scheduleRunModule(rt, config.nextDelayMinMillis, config.nextDelayMaxMillis, true);
    }

    /** 延迟一定时间运行一次"使用功能" */
    private void scheduleRunModule(RobotTeamConfig rt, long peroidMin, long peroidMax, boolean logg) {
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        long delay = tlr.nextLong(peroidMin, peroidMax);
        try {
            if (!stop.get()) {
                ScheduledFuture<?> sf = executorService.schedule(() -> runModule(rt), delay, TimeUnit.MILLISECONDS);
                if (logg) {
                    log.info("aid {} 将在 {} 后登录并使用功能", rt.getAccountId(), DateTimeUtil.duration(sf));
                }
            }
        } catch (Exception e) {
            log.error("提交任务失败. tid {} msg {}", rt.getAccountId(), e.getMessage());
        }
    }

    private void runModule(RobotTeamConfig rt) {
        X3ClientChannelGroup ccg = clientChannelHolder.channelGroup();
        if (ccg.getLoginedUserClientSize() >= config.onlineNum) {
            log.info("登录的球队已达到上限[{}]/[{}]个. 玩家 {} 不使用功能, 延迟到下一次",
                    ccg.getLoginedUserClientSize(), config.onlineNum, rt.getAccountId());
            scheduleRunModule(rt);
            return;
        }
        if (stop.get()) {
            log.info("tid {} system stop, ret", rt.getAccountId());
        }

        try {
            runModule0(ccg, rt);
            scheduleRunModule(rt);
        } catch (AccountOverrideException e) {
            log.warn("aid {} 被禁止登录, 移除. [{}]", rt.getAccountId(), e.getMessage());
            teamConfig.remove(rt.getAccountId());
        } catch (RejectedExecutionException e) {
            log.error("tid {} msg {}", rt.getAccountId(), e.getMessage());
        } catch (Exception e) {
            if (e.getCause() != null || e.getCause() instanceof InterruptedException) {
                log.error("tid {} Interrupted msg {}", rt.getAccountId(), e.getMessage());
            } else {
                log.error(e.getMessage(), e);
                scheduleRunModule(rt);
            }
        }
    }

    private void runModule0(X3ClientChannelGroup ccg, RobotTeamConfig rt) {
        log.info("===>>> 玩家 {} 使用功能 ===>>>", rt.getAccountId());
        long start = System.currentTimeMillis();
        try {
            UserClient uc = loginRobotTeam(rt);
            if (uc == null) {
                log.warn("aid {} 没有登录, 不使用功能", rt.getAccountId());
                return;
            }
            ClientUser cu = uc.user();
            runModule0(uc, cu, rt);
        } finally {
            UserClient uc = ccg.getUserClientByAccountId(rt.getAccountIdStr());
            if (uc != null && ccg.getLoginedUserClientSize() >= config.onlineNum) {
                ClientUser cu = uc.user();
                if (cu != null) {
                    log.info("tid {} logout lev {} vipLev {} money {}",
                            cu.getTid(), cu.getTeam().getLevel(), cu.getVip().getLevel(),
                            cu.getMoney().getMoney());
                }
                loginHelper.logout(uc);
                log.info("logout aid {}, client online size[{}]",
                        rt.getAccountId(), ccg.getLoginedUserClientSize());
            }
        }

        long time = System.currentTimeMillis() - start;
        log.info("<<<=== 玩家 {} 使用功能完毕,耗时[{}] <<<===",
                rt.getAccountId(), Duration.ofMillis(time));
    }

    /** 登录 */
    private UserClient loginRobotTeam(RobotTeamConfig rt) {
        UserClient uc = clientChannelHolder.channelGroup().getUserClientByAccountId(rt.getAccountIdStr());
        if (uc != null) {
            log.info("玩家 {} 已经登录 ret", rt.getAccountId());
            return uc;
        }
        Server server = clientConfig.getServer(rt.getShardId());
        if (server == null) {
            log.error("分区[{}]服务器连接配置不存在", rt.getShardId());
            return null;
        }
        return loginHelper.login(server, rt.getAccountId(), "" + rt.getAccountId(), false);
    }

    /** 使用功能 */
    private void runModule0(UserClient uc, ClientUser cu, RobotTeamConfig rt) {
        log.info("tid {} lev {} vipLev {} money {}",
                cu.getTid(), cu.getTeam().getLevel(), cu.getVip().getLevel(), cu.getMoney().getMoney());
        //领取未读邮件
        if (cu.hasEmail()) {
            mailClient.receiveAll(uc, cu);
        }
        if (cu.getMoney().getMoney() <= 0) {
            log.info("tid [{}] money <= 0. ret", cu.getTid());
            return;
        }
        ThreadLocalRandom tlr = ThreadLocalRandom.current();

        log.info("tid {} 随机升级球员", cu.tid());
        TestParams baseTC = TestContext.robotParam(tlr);
        Ret ret = playerClient.robotPlayerTest(uc, cu, new PlayerTestContext(baseTC, 1, 15));
        log.info("tid {} 球探签约球员. 上一个功能结果 {}", cu.tid(), ret);
        ret = scoutClient.robotTest(uc, cu, new ScoutTestContext(baseTC));

        log.info("tid {} 所有功能测试完毕, client online size {}. 上一个功能结果 {}", cu.tid(),
                clientChannelHolder.channelGroup().getLoginedUserClientSize(), ret);
        //使用道具

        //领取体力
        //        if (rt.canReceiveEnergy()) {
        //            receiveEnergy(uc, cu);
        //        }
        //屏蔽所有聊天消息推送
        //        if (rt.disableChatPush()) {
        //            teamBaseClient.disableAllChatPush(uc, cu);
        //        }
        //兑换经费
        //        if (exchangeMoneyFpNum > 0) {
        //            exchangeMoney(uc, cu);
        //        }
    }

    private Map<Long, RobotTeamConfig> readRobotTeamConfig(String file) {
        List<String> list = FileUtils.readAllLines(file);
        log.debug("file [{}] lines [{}]", file, list.size());
        Map<String, Integer> nameAndIdx = parseServerHead(list.get(0));//处理服务器端表头
        Map<Long, RobotTeamConfig> teams = new HashMap<>(list.size());
        //跳过第二行,客户端配置
        //跳过第三行注释

        //跳过表头
        final int startLineNum = 3;
        for (int i = startLineNum; i < list.size(); i++) {
            String str = list.get(i);
            if (str == null || str.isEmpty() || str.startsWith("#")) {
                continue;
            }
            try {
                RobotTeamConfig rt = parseTeam(nameAndIdx, str);
                teams.put(rt.getAccountId(), rt);
            } catch (Exception e) {
                throw new RuntimeException(str, e);
            }
        }

        String logLine = list.subList(startLineNum, list.size()).stream()
                .filter(str -> !str.startsWith("#"))
                .findFirst()
                .orElse("");
        log.info("read file [{}] [{}] lines, parsed [{}] robot team. first team {}",
                file, list.size(), teams.size(), parseTeam(nameAndIdx, logLine));
        return teams;
    }

    private Map<String, Integer> parseServerHead(String line) {
        String[] arr = line.split(StringUtil.COMMA);
        Map<String, Integer> nameAndIdx = new LinkedHashMap<>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            nameAndIdx.put(arr[i].toLowerCase(), i);
        }
        return nameAndIdx;
    }

    private RobotTeamConfig parseTeam(Map<String, Integer> nAi, String line) {
        String[] arr = line.split(StringUtil.COMMA);
        int shardId = getInt(nAi, arr, "shardId");
        long accountId = getLong(nAi, arr, "accountId");

        return new RobotTeamConfig(shardId,
                accountId,
                getInt(nAi, arr, "exchangeEnergyNum"),
                getInt(nAi, arr, "shop"),
                getInt(nAi, arr, "chatPush"),
                getInt(nAi, arr, "receiveEnergy")
        );
    }

    private String getStr(Map<String, Integer> nameAndIdx, String[] arr, String headName) {
        int idx = nameAndIdx.get(headName.toLowerCase());
        if (idx >= arr.length) {
            return "";
        }
        return arr[idx];
    }

    private int getInt(Map<String, Integer> nameAndIdx, String[] arr, String headName) {
        return getInt(nameAndIdx, arr, headName, 0);
    }

    private int getInt(Map<String, Integer> nameAndIdx, String[] arr, String headName, int defaultValue) {
        int idx = nameAndIdx.get(headName.toLowerCase());
        if (idx >= arr.length) {
            return defaultValue;
        }
        String str = arr[idx];
        if (str == null || str.isEmpty()) {
            return defaultValue;
        }
        return Integer.parseInt(str);
    }

    private long getLong(Map<String, Integer> nameAndIdx, String[] arr, String headName) {
        return getLong(nameAndIdx, arr, headName, 0);
    }

    private long getLong(Map<String, Integer> nameAndIdx, String[] arr, String headName, long defaultValue) {
        int idx = nameAndIdx.get(headName.toLowerCase());
        if (idx >= arr.length) {
            return defaultValue;
        }
        String str = arr[idx];
        if (str == null || str.isEmpty()) {
            return defaultValue;
        }
        return Long.parseLong(str);
    }

    public static final class RobotConfig {
        /** 模式: prod : 登录所有配置文件中的球队 , test : 只登录一个 test-account 球队 */
        private String mode;
        private boolean testMode;
        /** 机器人起始帐号 */
        private long accountStart;
        /** 机器人结束帐号 */
        private long accountEnd;
        /** 球队配置文件. 当球队配置存在时, 忽略帐号(account)配置 */
        private String teamConfig;
        /** 同时在线数量 */
        private int onlineNum;
        /** 当 online-num 比较大时, 每批次登录的球队数量 */
        private int batchLoginNum;
        /** 每天兑换经费次数上限 */
        private int exchangeMoneyFpNum = 20;
        /** 测试模式下的测试帐号 */
        private long testAccount;
        /** 登录后, 第一次使用功能时延迟下限(Duration). 默认 10 s */
        private long firstDelayMinMillis;
        /** 登录后, 第一次使用功能时延迟上限 */
        private long firstDelayMaxMillis;
        /** 每个球队2次使用所有功能的间隔下限 */
        private long nextDelayMinMillis;
        /** 每个球队2次使用所有功能的间隔上限 */
        private long nextDelayMaxMillis;

        public RobotConfig(Config appConfig) {
            init(appConfig);
        }

        public void init(Config config) {
            mode = config.getString("mode");
            accountStart = config.getLong("account.start");
            accountEnd = config.getLong("account.end");
            teamConfig = config.getString("team-config");
            onlineNum = config.getInt("online-num");
            batchLoginNum = config.getInt("batch-login-num");
            testAccount = config.getLong("test-account");
            firstDelayMinMillis = config.getDuration("behavior.first-delay-min")
                    .toMillis();
            firstDelayMaxMillis = config.getDuration("behavior.first-delay-max")
                    .toMillis();
            nextDelayMinMillis = config.getDuration("behavior.next-delay-min")
                    .toMillis();
            nextDelayMaxMillis = config.getDuration("behavior.next-delay-max")
                    .toMillis();

            testMode = mode.equalsIgnoreCase("test");

            if (accountStart > accountEnd) {
                throw new IllegalStateException("account.start > account.end");
            }

            if (batchLoginNum > onlineNum) {
                throw new IllegalStateException("batch-login-num > online-num");
            }
            if (firstDelayMinMillis > firstDelayMaxMillis) {
                throw new IllegalStateException("first-delay-min > first-delay-max");
            }
            if (nextDelayMinMillis > nextDelayMaxMillis) {
                throw new IllegalStateException("next-delay-min > next-delay-max");
            }
        }

    }

}

package com.ftkj.console;

import com.ftkj.cfg.PlayerGradeBean;
import com.ftkj.cfg.PlayerPowerBean;
import com.ftkj.cfg.PlayerStarBean;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.db.domain.bean.PlayerMoneyBeanPO;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.ENBAPlayerTeam;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EVersion;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerPriceBean;
import com.ftkj.manager.player.PlayerSimple;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.api.PlayerAbilityAPI;
import com.ftkj.manager.player.api.PositionAPI;
import com.ftkj.manager.player.api.zh.PositionAPIZH;
import com.ftkj.server.GameSource;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tim.huang
 * 2017年3月3日
 * 球员控制
 */
public class PlayerConsole {
    public static final int Team_Player_Num = 10;
    private static final int Lineup_Player_Num = 5;
    private static final Logger log = LogManager.getLogger(PlayerConsole.class);

    private static PositionAPI positionAPI;

    private static Map<Integer, PlayerBean> playerBeanMap;
    /**
     * tId:球员列表
     */
    private static Map<Integer, List<PlayerBean>> playerTeamMap;

    private static Map<Integer, List<PlayerMoneyBeanPO>> playerMoneyHistoryMap;

    private static Map<Integer, PlayerPowerBean> playerPowerRatMap;

    private static Map<Integer, PlayerGradeBean> playerGradeMap;

    private static Map<Integer, PlayerStarBean> playerStarGradeMap;

    private static List<PlayerPriceBean> playerPriceBeanList;

    /**
     * 球队球员上限
     */
    public static final int MAX_PLAYER = 11;
    /**
     * 仓库上限
     */
    public static int MAX_STORAGE_SIZE = 10;

    /**
     * 球队创建X球员选择
     */
    private static List<Integer> createTeamXList;

    private static Map<EPlayerPosition, CreateTeamPlayerOhter> createTeamPlayerMap;

    public static void init(List<PlayerBeanVO> list, List<PlayerMoneyBeanPO> playerMoneyBeanPOList, List<PlayerBeanVO> avgList) {
        log.info("init player console start...");
        final long curr = System.currentTimeMillis();
        if (GameSource.charset == EVersion.zh) {
            positionAPI = new PositionAPIZH();
        }
        Map<Integer, PlayerBean> tmpMap = Maps.newConcurrentMap();
        Map<Integer, List<PlayerBean>> tmptMap = Maps.newConcurrentMap();
        Map<Integer, PlayerPowerBean> playerPowerRatMapTmp = Maps.newConcurrentMap();
        CM.playerPowerBeanList.forEach(pp -> playerPowerRatMapTmp.put(pp.getPlayerId(), pp));
        playerPowerRatMap = playerPowerRatMapTmp;
        Map<Integer, PlayerBeanVO> avgMap = avgList.stream().collect(Collectors.toMap(PlayerBeanVO::getPlayerId, val -> val));
        //TODO:初始化球员配置
        list.stream()
            .filter(po -> po.getTeamId() >= 0)
            .map(po -> new PlayerBean(po, avgMap.get(po.getPlayerId()), getPlayerPower(po.getPlayerId()).getRate()))
            .peek(bean -> tmptMap.computeIfAbsent(bean.getTeam().getTid(), (k) -> Lists.newArrayList()).add(bean))
            .forEach(po -> tmpMap.put(po.getPlayerRid(), po));
        playerBeanMap = tmpMap;
        playerTeamMap = tmptMap;
        initCreateTeamPlayer();

        playerGradeMap = CM.playerGradeList.stream().collect(Collectors.toMap(PlayerGradeBean::getGrade, val -> val));
        playerStarGradeMap = CM.playerStarList.stream().collect(Collectors.toMap(PlayerStarBean::getGrade, val -> val));
        playerMoneyHistoryMap = playerMoneyBeanPOList.stream().collect(Collectors.groupingBy(PlayerMoneyBeanPO::getPlayerId));
        log.debug("球员历史身价{}", playerMoneyHistoryMap.size());

        setPlayerPriceBeanList(CM.playerPriceBeanList);

        // 仓库上限
        MAX_STORAGE_SIZE = ConfigConsole.getIntVal(EConfigKey.MAX_STORAGE_SIZE);
        log.info("init player console done. time {}ms", System.currentTimeMillis() - curr);

    }

    public static void initCreateTeamPlayer() {
        List<Integer> createTeamXList = Lists.newArrayList();
        createTeamXList.add(9000300);
        createTeamXList.add(9000301);
        createTeamXList.add(9000302);
        createTeamXList.add(9000303);
        createTeamXList.add(9000304);
        createTeamXList.add(9000125);
        createTeamXList.add(9000201);
        createTeamXList.add(9000234);
        createTeamXList.add(9000147);
        createTeamXList.add(9000148);
        createTeamXList.add(9000181);
        createTeamXList.add(9000281);
        createTeamXList.add(9000117);
        createTeamXList.add(9000122);
        createTeamXList.add(9000154);
        createTeamXList.add(9000258);
        createTeamXList.add(9000106);
        createTeamXList.add(9000138);
        createTeamXList.add(9000165);
        createTeamXList.add(9000266);
        PlayerConsole.createTeamXList = createTeamXList;
        initCreatePlayerList();
    }

    public static List<PlayerMoneyBeanPO> getPlayerMoneyList(int playerId) {
        return playerMoneyHistoryMap.get(playerId);
    }

    public static Map<Integer, PlayerGradeBean> getPlayerGradeMap() {
        return playerGradeMap;
    }

    public static PlayerGradeBean getPlayerGradeBean(int grade) {
        return playerGradeMap.get(grade);
    }

    public static PlayerStarBean getPlayerStarGradeBean(int grade) {
        return playerStarGradeMap.get(grade);
    }

    public static String getPlayerGrade(int playerId) {
        return getPlayerBean(playerId).getGrade().getGrade();
    }

    public static List<PlayerPriceBean> getPlayerPriceBeanList() {
        return playerPriceBeanList;
    }

    public static void setPlayerPriceBeanList(List<PlayerPriceBean> playerPriceBeanList) {
        PlayerConsole.playerPriceBeanList = playerPriceBeanList;
    }

    /**
     * 初始化创建球队球员数据
     */
    private static void initCreatePlayerList() {
        Map<EPlayerPosition, CreateTeamPlayerOhter> createTeamPlayerTempMap = Maps.newHashMap();
        int tryCount = 500;
        final List<EPlayerPosition> positionList = Lists.newArrayList();
        //将两个替补放到最后
        positionList.add(EPlayerPosition.C);
        positionList.add(EPlayerPosition.PF);
        positionList.add(EPlayerPosition.PG);
        positionList.add(EPlayerPosition.SG);
        positionList.add(EPlayerPosition.NULL);
        final CreateTeamPlayerOhter ct = new CreateTeamPlayerOhter();
        Stream.generate(() -> getCreatePlayer(EPlayerPosition.SF, positionList))
            .limit(tryCount)
            .filter(list -> list != null)
            .filter(list -> list.size() >= Lineup_Player_Num)
            .forEach(list -> ct.put(list));
        createTeamPlayerTempMap.put(EPlayerPosition.SF, ct);
        log.info("创建{}球队符合条件的球员有{}种", EPlayerPosition.SF.name(), ct.getSize());
        positionList.clear();
        //将两个替补放到最后
        positionList.add(EPlayerPosition.SF);
        positionList.add(EPlayerPosition.PF);
        positionList.add(EPlayerPosition.PG);
        positionList.add(EPlayerPosition.SG);
        positionList.add(EPlayerPosition.NULL);
        final CreateTeamPlayerOhter ct2 = new CreateTeamPlayerOhter();
        Stream.generate(() -> getCreatePlayer(EPlayerPosition.C, positionList))
            .limit(tryCount)
            .filter(list -> list != null)
            .filter(list -> list.size() >= Lineup_Player_Num)
            .forEach(list -> ct2.put(list));
        createTeamPlayerTempMap.put(EPlayerPosition.C, ct2);
        log.info("创建{}球队符合条件的球员有{}种", EPlayerPosition.C.name(), ct2.getSize());

        positionList.clear();
        //将两个替补放到最后
        positionList.add(EPlayerPosition.C);
        positionList.add(EPlayerPosition.SF);
        positionList.add(EPlayerPosition.PG);
        positionList.add(EPlayerPosition.SG);
        positionList.add(EPlayerPosition.NULL);
        final CreateTeamPlayerOhter ct3 = new CreateTeamPlayerOhter();
        Stream.generate(() -> getCreatePlayer(EPlayerPosition.PF, positionList))
            .limit(tryCount)
            .filter(list -> list != null)
            .filter(list -> list.size() >= Lineup_Player_Num)
            .forEach(list -> ct3.put(list));
        createTeamPlayerTempMap.put(EPlayerPosition.PF, ct3);
        log.info("创建{}球队符合条件的球员有{}种", EPlayerPosition.PF.name(), ct3.getSize());

        positionList.clear();
        //将两个替补放到最后
        positionList.add(EPlayerPosition.C);
        positionList.add(EPlayerPosition.PF);
        positionList.add(EPlayerPosition.SF);
        positionList.add(EPlayerPosition.SG);
        positionList.add(EPlayerPosition.NULL);
        final CreateTeamPlayerOhter ct4 = new CreateTeamPlayerOhter();
        Stream.generate(() -> getCreatePlayer(EPlayerPosition.PG, positionList))
            .limit(tryCount)
            .filter(list -> list != null)
            .filter(list -> list.size() >= Lineup_Player_Num)
            .forEach(list -> ct4.put(list));
        createTeamPlayerTempMap.put(EPlayerPosition.PG, ct4);
        log.info("创建{}球队符合条件的球员有{}种", EPlayerPosition.PG.name(), ct4.getSize());

        positionList.clear();
        //将两个替补放到最后
        positionList.add(EPlayerPosition.C);
        positionList.add(EPlayerPosition.PF);
        positionList.add(EPlayerPosition.PG);
        positionList.add(EPlayerPosition.SF);
        positionList.add(EPlayerPosition.NULL);
        final CreateTeamPlayerOhter ct5 = new CreateTeamPlayerOhter();
        Stream.generate(() -> getCreatePlayer(EPlayerPosition.SG, positionList))
            .limit(tryCount)
            .filter(list -> list != null)
            .filter(list -> list.size() >= Lineup_Player_Num)
            .forEach(list -> ct5.put(list));
        createTeamPlayerTempMap.put(EPlayerPosition.SG, ct5);
        log.info("创建{}球队符合条件的球员有{}种", EPlayerPosition.SG.name(), ct5.getSize());
        //
        createTeamPlayerMap = createTeamPlayerTempMap;
    }

    /**
     * @param npcId
     * @param players
     * @return
     */
    public static List<Player> getNPCPlayerList(long npcId, String players) {
        String[] ps = StringUtil.toStringArray(players, StringUtil.DEFAULT_ST);
        List<Integer> pids = Arrays.stream(ps).map(p -> Ints.tryParse(p)).collect(Collectors.toList());
        List<Player> resultList = Lists.newArrayList();
        if (pids.size() < Lineup_Player_Num) {//取球队，
            //            log.info("npcid {} players {} pid {}", npcId, players, pids.size());
            pids = playerTeamMap.get(pids.get(0)).stream()
                .filter(p -> p.getPlayerRid() < 9000000)
                .map(p -> p.getPlayerRid()).collect(Collectors.toList());
        }
        //取配置NPC， 前5位置固定。剩余全部是替补
        resultList = pids.stream()
            .map(p -> playerBeanMap.get(p))
            .map(p -> Player.createPlayer(npcId, 0, p.getPlayerRid(), p.getPrice(), p.getPosition()[0].name()
                , EPlayerPosition.NULL.name(), false, PlayerTalent.Empty))
            .collect(Collectors.toList());
        //
        autoLineupPlayer(resultList);
        if (resultList.size() > Team_Player_Num) {
            List<Player> lineupList = resultList.stream().filter(p -> p.getLineupPosition() != EPlayerPosition.NULL).collect(Collectors.toList());
            resultList = resultList.stream().filter(p -> p.getLineupPosition() == EPlayerPosition.NULL)
                .sorted(PlayerConsole.so)
                .limit(Team_Player_Num - Lineup_Player_Num)
                .collect(Collectors.toList());
            resultList.addAll(lineupList);
        }
        return resultList;
    }

    public static PlayerPowerBean getPlayerPower(int playerId) {
        return playerPowerRatMap.computeIfAbsent(playerId, key -> new PlayerPowerBean(key));
    }

    /**
     * 战力比较器，没有计算装备
     */
    public static Comparator<Player> so = (a, b) -> {
        int aCap = PlayerAbilityAPI.getPlayerAbility(a).getTotalCap();
        int bCap = PlayerAbilityAPI.getPlayerAbility(b).getTotalCap();
        return Integer.compare(bCap, aCap);
    };

    /**
     * 取列表中指定位置最强球员
     *
     * @param players
     * @param position
     * @return
     */
    public static Player autoLineupPlayer(List<Player> players, EPlayerPosition position) {
        Player player = players.stream()
            .filter(p -> p.getPlayerPosition() == position)
            .sorted(so).findFirst().orElse(null);
        return player;
    }

    /**
     * 取列表中指定位置最强球员， 如果没有，取其他位置
     *
     * @param players
     * @param position
     * @return
     */
    public static Player autoLineupPlayerFirst(List<Player> players, EPlayerPosition position) {
        Player player = autoLineupPlayer(players, position);
        if (player == null) {
            player = players.stream().sorted(so).findFirst().orElse(null);
        }
        return player;
    }

    /**
     * 自动调整最优阵容
     *
     * @param players
     */
    private static void autoLineupPlayer(List<Player> players) {
        //
        List<EPlayerPosition> emptyPosition = Lists.newArrayList();

        Player player = autoLineupPlayer(players, EPlayerPosition.PG);
        if (player == null) { emptyPosition.add(EPlayerPosition.PG); } else {
            player.updateLinuePosition(EPlayerPosition.PG, false);
        }

        player = autoLineupPlayer(players, EPlayerPosition.C);
        if (player == null) { emptyPosition.add(EPlayerPosition.C); } else {
            player.updateLinuePosition(EPlayerPosition.C, false);
        }

        player = autoLineupPlayer(players, EPlayerPosition.PF);
        if (player == null) { emptyPosition.add(EPlayerPosition.PF); } else {
            player.updateLinuePosition(EPlayerPosition.PF, false);
        }

        player = autoLineupPlayer(players, EPlayerPosition.SF);
        if (player == null) { emptyPosition.add(EPlayerPosition.SF); } else {
            player.updateLinuePosition(EPlayerPosition.SF, false);
        }

        player = autoLineupPlayer(players, EPlayerPosition.SG);
        if (player == null) { emptyPosition.add(EPlayerPosition.SG); } else {
            player.updateLinuePosition(EPlayerPosition.SG, false);
        }

        if (emptyPosition.size() > 0) {
            List<Player> NULLList = players.stream()
                .filter(p -> p.getLineupPosition() == EPlayerPosition.NULL)
                .sorted(so)
                .collect(Collectors.toList());
            for (int i = 0; i < emptyPosition.size(); i++) {
                NULLList.get(i).updateLinuePosition(emptyPosition.get(i), false);
            }
        }
    }

    private static List<PlayerSimple> getCreatePlayer(EPlayerPosition XPosition, List<EPlayerPosition> positionList) {

        List<EPlayerGrade> gradeList = Lists.newArrayList();
        gradeList.add(EPlayerGrade.B);
        gradeList.add(EPlayerGrade.C);
        gradeList.add(EPlayerGrade.C);
        gradeList.add(EPlayerGrade.C);
        gradeList.add(EPlayerGrade.D);
        EPlayerPosition MaxPosition = null;
        EPlayerPosition tempPosition = null;
        EPlayerGrade maxGrade = EPlayerGrade.D1;
        PlayerBean pb = null;
        EPlayerGrade tmpGrade = null;

        List<PlayerSimple> resultList = Lists.newArrayList();
        for (int i = 0, x = 0; i < 2000 && x < positionList.size(); i++) {
            tempPosition = positionList.get(x);
            if (x == 4) {//取X球员位置替补
                tempPosition = XPosition;
            } else if (x == Lineup_Player_Num) {//取除了X以外，身价最高的位置替补
                tempPosition = MaxPosition;
            }
            if (x == 6) {
                break;
            }
            pb = getRanPlayerByPosition(tempPosition, EPlayerGrade.B2);
            if (pb == null) { continue; }
            tmpGrade = EPlayerGrade.getGrade(pb.getGrade());
            if (gradeList.remove(tmpGrade)) {
                resultList.add(PlayerSimple.newPlayerSimple(pb.getPlayerRid(),
                    positionList.get(x), pb.getPosition()[0], 1, pb.getPrice()));
                x++;
                //取除了X以外能力最高的位置，做为最后两个球员的判断
                if (tmpGrade.ordinal() > maxGrade.ordinal()) {
                    maxGrade = tmpGrade;
                    MaxPosition = tempPosition;
                }
            }
        }
        int totalPrice = resultList.stream().mapToInt(player -> player.getPrice()).sum();
        if (totalPrice < 2000 || totalPrice > 2500) { return null; }
        return resultList;
    }

    public static PlayerBean getRanPlayerByPosition(EPlayerPosition position, EPlayerGrade maxGrade) {
        List<PlayerBean> pbList = playerBeanMap.values().stream()
            .filter(player -> player.getPosition()[0] == position)
            .filter(player -> player.getTeam() != ENBAPlayerTeam.退役)
            .filter(player -> player.getTeam() != ENBAPlayerTeam.无队)
            .filter(player -> player.getGrade() != null)
            .filter(player -> player.getGrade().ordinal() <= maxGrade.ordinal())
            .collect(Collectors.toList());
        if (pbList.size() <= 0) { return null; }
        return pbList.get(RandomUtil.randInt(pbList.size()));
    }

    public static PlayerBean getRanPlayerByPosition(EPlayerPosition position, EPlayerGrade minGrade, EPlayerGrade maxGrade) {
        List<PlayerBean> pbList = playerBeanMap.values().stream()
            .filter(player -> player.getPosition()[0] == position)
            .filter(player -> player.getTeam() != ENBAPlayerTeam.退役)
            .filter(player -> player.getTeam() != ENBAPlayerTeam.无队)
            .filter(player -> player.getGrade() != null)
            .filter(player -> player.getGrade().ordinal() <= maxGrade.ordinal())
            .filter(player -> player.getGrade().ordinal() >= minGrade.ordinal())
            .collect(Collectors.toList());
        if (pbList.size() <= 0) { return null; }
        return pbList.get(RandomUtil.randInt(pbList.size()));
    }

    /**
     * 根据X球员位置取随机的匹配球员
     *
     * @param Xposition
     * @return
     */
    public static int getCreateTeamPlayerSize(EPlayerPosition Xposition) {
        return createTeamPlayerMap.get(Xposition).getSize();
    }

    /**
     * 根据X球员位置和匹配球员下标，取随机的
     *
     * @param Xposition
     * @param index
     * @return
     */
    public static List<PlayerSimple> getCreateTeamPlayers(EPlayerPosition Xposition, int index) {
        return createTeamPlayerMap.get(Xposition).getPlayerList(index);
    }

    /**
     * 判断是否创建球队的X球员
     *
     * @param playerId
     * @return
     */
    public static boolean existCreateXPlayer(int playerId) {
        return createTeamXList.contains(playerId);
    }

    public static List<Integer> getCreateTeamXList() {
        return createTeamXList;
    }

    public static PlayerBean getPlayerBean(int playerId) {
        return playerBeanMap.get(playerId);
    }

    public static boolean existPlayer(int playerId) {
        return playerBeanMap.containsKey(playerId);
    }

    /**
     * 随机取N个球员
     *
     * @param num
     * @return
     */
    public static List<PlayerBean> getRanPlayer(int num) {
        List<PlayerBean> p = Lists.newArrayList(playerBeanMap.values());
        return Stream.generate(() -> p.get(RandomUtil.randInt(num + 50)))
            .distinct().filter(s -> s.getGrade() != null).limit(num).collect(Collectors.toList());
    }
    
    public static PlayerBean getRanPlayer(EPlayerGrade minGrade, EPlayerGrade maxGrade, EPlayerPosition position, String draft) {
    	return getRanPlayer(minGrade, maxGrade, position, draft, 1).get(0);
    }

    public static List<Integer> getRanPlayerIds(EPlayerGrade minGrade, EPlayerGrade maxGrade, EPlayerPosition position, String draft, int size, List<Integer> filterIds) {
    	return getRanPlayer(minGrade, maxGrade, position, draft, size, filterIds).stream().mapToInt(p-> p.getPlayerRid()).boxed().collect(Collectors.toList());
    }
    /**
     * 通过条件随机出一个球员
     *
     * @param minGrade 最小品质
     * @param maxGrade 最高品质
     * @param position 指定位置
     * @return
     */
    public static List<PlayerBean> getRanPlayer(EPlayerGrade minGrade, EPlayerGrade maxGrade, EPlayerPosition position, String draft, int size) {
    	return getRanPlayer(minGrade, maxGrade, position, draft, size, null);
    }
    /**
     * 通过条件随机出一个球员
     *
     * @param minGrade 最小品质
     * @param maxGrade 最高品质
     * @param position 指定位置
     * @return
     */
    public static List<PlayerBean> getRanPlayer(EPlayerGrade minGrade, EPlayerGrade maxGrade, EPlayerPosition position, String draft, int size, List<Integer> filterIds) {
        List<PlayerBean> pbList = playerBeanMap.values().stream()
                .filter(player -> player.getGrade() != null)
                .filter(player -> player.getTeam() != ENBAPlayerTeam.退役)
                .filter(player -> player.getTeam() != ENBAPlayerTeam.无队)
                .filter(player -> player.getGrade().ordinal() >= minGrade.ordinal())
                .filter(player -> player.getGrade().ordinal() <= maxGrade.ordinal())
                .filter(player -> position == EPlayerPosition.NULL || player.getPosition()[0] == position)
                .filter(player-> filterIds==null ? true : !filterIds.contains(player.getPlayerRid()))
                .filter(player-> {
                	if(draft == null || draft.equals("")) {
                		return true;
                	}
                	int[] draftArray = player.getDraft();
                	if(draftArray == null || draftArray.length < 3) {
                		return false;
                	}
                	String[] draftCfg = draft.split("_");
                	for(int i=0; i<draftCfg.length; i++) {
                		if(draftArray[i] != Integer.valueOf(draftCfg[i])) {
                			return false;
                		}
                	}
                	return true;
                })
                .collect(Collectors.toList());
        List<Integer> all = RandomUtil.getRandomBySeed(DateTime.now().getMillis() + RandomUtil.randInt(Integer.MAX_VALUE), pbList.size(), size, false);
//        List<Integer> all = RandomUtil.getRandomBySeed(DateTime.now().getMillis(), pbList.size(), size, false);
//        System.err.println("随机球员：" + maxGrade + "   size=" + pbList.size() + " hit=" + all + "  playerId=" + pbList.get(all.get(0)).getPlayerRid());
        return all.stream().map(idx-> pbList.get(idx)).collect(Collectors.toList());
//        return pbList.get(RandomUtil.randInt(pbList.size()));
    }

    /**
     * 随机位置以外等级球员
     * @param minGrade
     * @param maxGrade
     * @param position
     * @return
     */
    public static PlayerBean getRanPlayerOr(EPlayerGrade minGrade, EPlayerGrade maxGrade, EPlayerPosition position) {
        List<PlayerBean> pbList = playerBeanMap.values().stream()
            .filter(player -> player.getGrade() != null)
            .filter(player -> player.getTeam() != ENBAPlayerTeam.退役)
            .filter(player -> player.getTeam() != ENBAPlayerTeam.无队)
            .filter(player -> player.getGrade().ordinal() >= minGrade.ordinal())
            .filter(player -> player.getGrade().ordinal() <= maxGrade.ordinal())
            .filter(player -> player.getPosition()[0] != position)
            .collect(Collectors.toList());
        return pbList.get(RandomUtil.randInt(pbList.size()));
    }

    public static PositionAPI getPositionAPI() {
        return positionAPI;
    }

    /**
     * @author tim.huang
     * 2017年3月17日
     * 创建球队，除X外的其余球员
     */
    static class CreateTeamPlayerOhter {
        private Map<Integer, List<PlayerSimple>> playerMap;
        private int index = 0;

        public void put(List<PlayerSimple> list) {
            this.playerMap.put(index++, list);
        }

        private CreateTeamPlayerOhter() {
            super();
            this.playerMap = Maps.newHashMap();
        }

        public List<PlayerSimple> getPlayerList(int index) {
            return playerMap.get(index);
        }

        public int getSize() {
            return index;
        }

    }

    /**
     * 取所有
     *
     * @return
     */
    public static Collection<PlayerBean> getPlayerBeanList() {
        return playerBeanMap.values();
    }

    public static Map<Integer, PlayerBean> getPlayerBeanMap() {
        return playerBeanMap;
    }

    /**
     * 取全队球满足数
     *
     * @param teamId
     * @param playerIds
     * @return 满足所有球员的球队ID数组
     */
    public static int[] getTeamPlayerCount(List<Integer> playerIds) {
        // 先按球队分组
        // 如果分组后，球员数等于， 所有分组的球员数，则留下，最终返回球队数
        Map<Integer, List<PlayerBean>> playerBeans = playerIds.stream().
            map(playerId -> getPlayerBean(playerId)).
            collect(Collectors.groupingBy(PlayerBean::getTeamId, Collectors.toList()));
        return playerBeans.keySet().stream().filter(k -> playerBeans.get(k).size() == playerTeamMap.get(k).size()).mapToInt(k -> k).toArray();
    }

}

package com.ftkj.manager.player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EPlayerStorage;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang 2017年2月16日 玩家拥有球员数据
 */
public class TeamPlayer {
    /** 球队ID */
    private long teamId;
    /** 队伍球员列表 */
    private List<Player> players;
    /** 仓库 */
    private List<Player> storagePlayers;
    /** 自由市场冻结球员 */
    private List<Player> marketPlayers;
    /** 自球员天赋 */
    private Map<Integer, PlayerTalent> playerTalentMap;
    /** 玩家球员自增长id，每个玩家自己维护该字段 */
    private AtomicInteger pid;
    /** 天赋id */
    private AtomicInteger talentId;

    public TeamPlayer(long teamId) {
        this.teamId = teamId;
        this.players = Lists.newArrayList();
        this.storagePlayers = Lists.newArrayList();
        this.marketPlayers = Lists.newArrayList();
        this.pid = new AtomicInteger();
        this.talentId = new AtomicInteger();
        this.playerTalentMap = Maps.newHashMap();
    }

    public TeamPlayer(long teamId, List<Player> playerList, int talentId, Map<Integer, PlayerTalent> playerTalentMap) {
        this.teamId = teamId;
        this.players = playerList.stream().filter(p -> p.getStorage() == EPlayerStorage.阵容.getType()).collect(Collectors.toList());
        this.storagePlayers = playerList.stream()
            .filter(p -> p.getStorage() == EPlayerStorage.仓库.getType() || p.getStorage() == EPlayerStorage.训练馆.getType())
            .collect(Collectors.toList());
        this.marketPlayers = playerList.stream().filter(p -> p.getStorage() == EPlayerStorage.交易.getType()).collect(Collectors.toList());
        int maxId = playerList.stream().mapToInt(player -> player.getId()).max().orElse(0);
        this.pid = new AtomicInteger(maxId);
        this.talentId = new AtomicInteger(talentId);
        this.playerTalentMap = playerTalentMap;
    }

    public void putPlayerTalent(PlayerTalent talent) {
        playerTalentMap.put(talent.getId(), talent);
    }

    public void removePlayerTalent(int id) {
        this.playerTalentMap.remove(id);
    }

    public PlayerTalent getPlayerTalent(int id) {
        return this.playerTalentMap.get(id);
    }

    /**
     * 取工资帽
     *
     * @return
     */
    public int getPlayerPrice() {
        return players.stream().mapToInt(player -> player.getPrice()).sum();
    }

    /**
     * 判断阵容是否存在该球员
     *
     * @param playerId
     * @return
     */
    public boolean existPlayer(int playerId) {
        return players.stream().filter(player -> player.getPlayerRid() == playerId).findFirst().isPresent();
    }

    /**
     * 判断是否是N球员
     * @param playerId
     * @return
     */
    public boolean isNPlayer(int playerRid) {
        return PlayerConsole.getPlayerBean(playerRid).getGrade() == EPlayerGrade.N;
    }
    
    /**
     * 判断阵容是否存在该球员
     *
     * @param pid 唯一ID
     * @return
     */
    public boolean existPlayerPid(int pid) {
        return players.stream().filter(player -> player.getId() == pid).findFirst().isPresent();
    }

    /**
     * 判断仓库是否存在该球员
     *
     * @param playerId
     * @return
     */
    public boolean existStoragePlayer(int pid) {
        return storagePlayers.stream().filter(player -> player.getId() == pid).findFirst().isPresent();
    }

    /**
     * 判断自由市场是否存在该球员
     *
     * @param playerId
     * @return
     */
    public boolean existMarketPlayer(int pid) {
        return marketPlayers.stream().filter(player -> player.getId() == pid).findFirst().isPresent();
    }

    /**
     * 根据基础ID取指定球员
     *
     * @param playerId
     * @return
     */
    public Player getPlayer(int playerId) {
        return players.stream().filter(player -> player.getPlayerRid() == playerId).findFirst().orElse(null);
    }

    /**
     * 取X球员
     *
     * @param playerId
     * @return
     */
    public Player getXPlayer() {
        Player xp = players.stream()
            .filter(player -> PlayerConsole.getPlayerBean(player.getPlayerRid()).getGrade() == EPlayerGrade.X)
            .findFirst().orElse(null);

        if (xp != null) {
            return xp;
        }

        return storagePlayers.stream()
            .filter(splayer -> PlayerConsole.getPlayerBean(splayer.getPlayerRid()).getGrade() == EPlayerGrade.X)
            .findFirst().orElse(null);
    }
    
    /**
     * 取N球员
     * @param playerId
     * @return
     */
    public List<Player> getNPlayers() {
        List<Player> playerNList = players.stream()
                .filter(player -> PlayerConsole.getPlayerBean(player.getPlayerRid()).getGrade() == EPlayerGrade.N)
                .collect(Collectors.toList());
        
        return playerNList;
    }
    
    
    /**
     * 取N球员（包含仓库）
     * @param playerId
     * @return
     */
    public List<Player> getNPlayersAndStroage() {
        List<Player> playerNList = players.stream()
                .filter(player -> PlayerConsole.getPlayerBean(player.getPlayerRid()).getGrade() == EPlayerGrade.N)
                .collect(Collectors.toList());
        
       
        playerNList.addAll(storagePlayers.stream()
                .filter(player -> PlayerConsole.getPlayerBean(player.getPlayerRid()).getGrade() == EPlayerGrade.N)
                .collect(Collectors.toList()));
        return playerNList;
    }
    
    /**
     * 取N球员基础ID列表
     * @param playerId
     * @return
     */
    public List<Integer> getNPrids() {
        return this.getNPlayers().stream().map(Player::getPlayerRid).collect(Collectors.toList());
    }
    
    /**
     * 取N球员基础ID列表
     * @param playerId
     * @return
     */
    public Set<Integer> getNPridsAndStorage() {
        return getNPlayersAndStroage().stream().map(Player::getPlayerRid).collect(Collectors.toSet());
    }
  
    /**
     * 根据唯一ID取指定球员
     *
     * @param pid
     * @return
     */
    public Player getPlayerPid(int pid) {
        Player p = players.stream().filter(player -> player.getId() == pid).findFirst().orElse(null);
        if (p != null) {
            return p;
        }

        return storagePlayers.stream().filter(splayer -> splayer.getId() == pid).findFirst().orElse(null);
    }

    /**
     * 取所有的球员，包括阵容，仓库，交易市场
     *
     * @param playerId
     * @return
     */
    public List<Player> getAllPlayerByPlayerId(int playerId) {
        List<Player> list = Lists.newArrayList();
        list.addAll(this.players);
        list.addAll(this.storagePlayers);
        list.addAll(this.marketPlayers);
        return list.stream().filter(p -> p.getPlayerRid() == playerId).collect(Collectors.toList());
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public synchronized void addPlayerToStorage(Player player) {
        player.setStorage(EPlayerStorage.仓库.getType());
        this.storagePlayers.add(player);
    }

    /**
     * 从阵容列表中移除，未做保存
     *
     * @param playerId
     * @return
     */
    public synchronized Player removeFromTeam(int pid) {
        Player player = getPlayerPid(pid);
        this.players = players.stream().filter(p -> p.getId() != pid).collect(Collectors.toList());
        // PlayerConsole.autoLineupPlayerFirst(this.players,
        // player.getLineupPosition());
        return player;
    }

    /**
     * 从仓库列表中移除，未做保存
     *
     * @param pid 唯一ID
     * @return
     */
    public synchronized Player removeFromStorage(int pid) {
        Player player = getStoragePlayer(pid);      
        this.storagePlayers = storagePlayers.stream().filter(p -> p.getId() != pid).collect(Collectors.toList());
        return player;
    }

    /**
     * 从市场列表中移除，未做保存
     *
     * @param pid 唯一ID
     */
    private synchronized Player removeFromMarket(int pid) {
        Player player = getMarketPlayer(pid);
        this.marketPlayers = marketPlayers.stream().filter(p -> p.getId() != pid).collect(Collectors.toList());
        return player;
    }

    public int getNewPid() {
        return pid.incrementAndGet();
    }

    public int getNewTalentId() {
        return this.talentId.incrementAndGet();
    }

    /**
     * 获得首发阵容球员列表
     */
    public List<Player> getStartingPlayers() {
        return players.stream().filter(player -> EPlayerPosition.NULL != player.getLineupPosition())
            .collect(Collectors.toList());
    }

    /**
     * 获得替补阵容球员列表
     */
    public List<Player> getBenchPlayers() {
        return players.stream().filter(player -> EPlayerPosition.NULL == player.getLineupPosition())
            .collect(Collectors.toList());
    }

    /**
     * 获得首发+替补球员列表
     */
    public List<Player> getPlayers() {
        return players;
    }

    public void tranPlayerPosition(int pid1, int pid2) {
        Player p1 = getPlayerPid(pid1);
        Player p2 = getPlayerPid(pid2);
        EPlayerPosition p1pos = p1.getLineupPosition();
        EPlayerPosition p2pos = p2.getLineupPosition();
        p1.updateLinuePosition(p2pos, true);
        p2.updateLinuePosition(p1pos, true);
    }

    /**
     * 取仓库球员列表，包含交易市场球员
     */
    public List<Player> getStoragesAndMarket() {
        List<Player> list = Lists.newArrayList();
        list.addAll(this.storagePlayers);
        list.addAll(this.marketPlayers);
        return list;
    }

    /**
     * 取仓库球员列表
     */
    public List<Player> getStorages() {
        List<Player> list = Lists.newArrayList();
        list.addAll(this.storagePlayers);
        return list;
    }

    /**
     * 取仓库指定球员
     *
     * @param pid 唯一ID
     * @return
     */
    public Player getStoragePlayer(int pid) {
        if (this.storagePlayers == null) { return null; }
        return storagePlayers.stream().filter(player -> player.getId() == pid).findFirst().orElse(null);
    }  
    
    /**
     * 取仓库球员
     * @param playerId
     * @return
     */
    public List<Player> getStoragePlayerList(int playerId) {
        if(this.storagePlayers == null) return null;
        return storagePlayers.stream().filter(player -> player.getPlayerRid() == playerId).collect(Collectors.toList());
    }
    
    /**
     * 取仓库球员, 指定价格
     *
     * @param playerId
     * @param price
     * @return map[pid, player]
     */
    public Map<Integer, Player> getStoragePlayerList(int playerId, int price) {
        if (this.storagePlayers == null) {
            return null;
        }
        return storagePlayers.stream()
            .filter(player -> player.getPlayerRid() == playerId && player.getPrice() == price)
            .collect(Collectors.toMap(Player::getId, t -> t));
    }

    /**
     * 取市场指定球员
     *
     * @param pid 唯一ID
     * @return
     */
    public Player getMarketPlayer(int pid) {
        return marketPlayers.stream().filter(player -> player.getId() == pid).findFirst().orElse(null);
    }

    /**
     * 从阵容放入仓库
     *
     * @param pid
     */
    public synchronized void teamToStorage(int pid) {
        Player p = removeFromTeam(pid);
        p.setStorage(EPlayerStorage.仓库.getType());
        p.updateLinuePosition(EPlayerPosition.NULL, true);
        p.save();
        this.storagePlayers.add(p);
    }

    /**
     * 从仓库放入阵容
     *
     * @param pid
     */
    public synchronized void storageToTeam(int pid, EPlayerPosition position) {
        Player p = removeFromStorage(pid);
        p.setStorage(EPlayerStorage.阵容.getType());
        p.updateLinuePosition(position, true);
        p.save();
        addPlayer(p);
        EventBusManager.post(EEventType.球队现有工资, p.getTeamId());
    }

    /**
     * 从仓库放入市场
     *
     * @param pid
     */
    public synchronized void storageToMarket(int pid) {
        Player p = removeFromStorage(pid);
        p.setStorage(EPlayerStorage.交易.getType());
        p.save();
        this.marketPlayers.add(p);
    }

    /**
     * 从市场放入仓库
     *
     * @param pid
     * @return
     */
    public synchronized Player marketToStorage(int pid) {
        Player p = removeFromMarket(pid);
        if (p == null) {
            return null;
        }
        p.setStorage(EPlayerStorage.仓库.getType());
        p.save();
        this.storagePlayers.add(p);
        return p;
    }

    /**
     * 删除市场球员
     *
     * @param pid
     * @return
     */
    public synchronized Player delFromMarket(int pid) {
        Player p = removeFromMarket(pid);
        p.setStorage(EPlayerStorage.删除.getType());
        p.del();
        p.save();
        return p;
    }

    /**
     * 创建球员接口
     */
    public Player createPlayer(PlayerBean bean, int price, String lineupPosition, PlayerTalent pt) {
        // 做位置处理，如果有老球员，以老球员的位置为准，没有则以PlayerBean的默认位置为准
        EPlayerPosition position = getplayerPosition(bean);
        return Player.createPlayer(teamId, getNewPid(), bean.getPlayerRid(), price, position.name(),
            lineupPosition, false, pt);
    }

    /**
     * 创建球员接口
     *
     * @param bind 是否绑定
     */
    public Player createPlayer(PlayerBean bean, int price, String lineupPosition, PlayerTalent pt, boolean bind) {
        // 做位置处理，如果有老球员，以老球员的位置为准，没有则以PlayerBean的默认位置为准
        EPlayerPosition position = getplayerPosition(bean);
        return Player.createPlayer(teamId, getNewPid(), bean.getPlayerRid(), price, position.name(),
            lineupPosition, bind, pt);
    }

    /**
     * 取玩家球员的默认位置 如果有该球员，以已获得球员位置为准 如果没有，以默认位置为准
     *
     * @param playerId
     * @return
     */
    public EPlayerPosition getplayerPosition(int playerId) {
        return getplayerPosition(PlayerConsole.getPlayerBean(playerId));
    }

    public EPlayerPosition getplayerPosition(PlayerBean pb) {
        List<Player> playerList = Lists.newArrayList();
        playerList.addAll(this.players);
        playerList.addAll(this.storagePlayers);
        playerList.addAll(this.marketPlayers);
        Player oldPlayer = playerList.stream().filter(p -> p.getPlayerRid() == pb.getPlayerRid()).findFirst()
            .orElse(null);
        EPlayerPosition position = oldPlayer != null ? oldPlayer.getPlayerPosition() : pb.getPosition()[0];
        return position;
    }

    /**
     * 取玩家的所有Ids
     *
     * @return
     */
    public List<Integer> getAllPlayerIds() {
        List<Player> playerList = Lists.newArrayList();
        playerList.addAll(this.players);
        playerList.addAll(this.storagePlayers);
        playerList.addAll(this.marketPlayers);
        return playerList.stream().mapToInt(p -> p.getPlayerRid()).distinct().boxed().collect(Collectors.toList());
    }

}

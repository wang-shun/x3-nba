package com.ftkj.x3.client.model;

import com.ftkj.manager.match.RankedMatch;
import com.ftkj.manager.match.SysAllStar;
import com.ftkj.xxs.client.net.XxsClientUser;

import java.util.Map;

/**
 * @author luch
 */
public class ClientUser implements XxsClientUser {
    /** 分区id */
    private int partitionId = 0;
    private String partitionIdStr;
    private long accountId;
    private String accountIdStr;
    private long userId;
    private String userIdStr;

    private ClientTeam team;
    private int createDay;

    private ClientMoney money;
    private ClientTeamEquip equips;
    private ClientTeamFriends friends;
    private ClientTeamPlayer players;
    private ClientTeamStatus status;
    /** 训练球员列表 playerId:train */
    private Map<Integer, ClientTrainPlayer> trainPlayers;
    private Map<Integer, ClientEmail> emails;
    private Map<Integer, ClientProp> props;
    private ClientVip vip;
    private Map<Integer, ClientBuff> buffs;
    private ClientTeamMainMatch teamMainMatch;
    private RankedMatch rankedMatch;
    private ClientTeamAllStar allStar;
    private SysAllStar sysAllStar;
    private ClientArena arena;
    @Override
    public String getAccountId() {
        return accountIdStr;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
        this.accountIdStr = String.valueOf(accountId);
    }

    @Override
    public String getUserId() {
        return userIdStr;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        this.userIdStr = String.valueOf(userId);
    }

    public long getTid() {
        return team.getTeamId();
    }

    public long tid() {
        return team.getTeamId();
    }

    public ClientTeam getTeam() {
        return team;
    }

    public void setTeam(ClientTeam team) {
        this.team = team;
    }

    @Override
    public String getName() {
        return team.getName();
    }

    public int getCreateDay() {
        return createDay;
    }

    public void setCreateDay(int createDay) {
        this.createDay = createDay;
    }

    public ClientMoney getMoney() {
        return money;
    }

    public void setMoney(ClientMoney money) {
        this.money = money;
    }

    public ClientTeamEquip getEquips() {
        return equips;
    }

    public void setEquips(ClientTeamEquip equips) {
        this.equips = equips;
    }

    public ClientTeamFriends getFriends() {
        return friends;
    }

    public void setFriends(ClientTeamFriends friends) {
        this.friends = friends;
    }

    public ClientTeamPlayer getPlayers() {
        return players;
    }

    public void setPlayers(ClientTeamPlayer players) {
        this.players = players;
    }

    public ClientTeamStatus getStatus() {
        return status;
    }

    public void setStatus(ClientTeamStatus status) {
        this.status = status;
    }

    public Map<Integer, ClientTrainPlayer> getTrainPlayers() {
        return trainPlayers;
    }

    public void setTrainPlayers(Map<Integer, ClientTrainPlayer> trainPlayers) {
        this.trainPlayers = trainPlayers;
    }

    public Map<Integer, ClientEmail> getEmails() {
        return emails;
    }

    public void setEmails(Map<Integer, ClientEmail> emails) {
        this.emails = emails;
    }

    public Map<Integer, ClientProp> getProps() {
        return props;
    }

    public void setProps(Map<Integer, ClientProp> props) {
        this.props = props;
    }

    public ClientVip getVip() {
        return vip;
    }

    public void setVip(ClientVip vip) {
        this.vip = vip;
    }

    public Map<Integer, ClientBuff> getBuffs() {
        return buffs;
    }

    public void setBuffs(Map<Integer, ClientBuff> buffs) {
        this.buffs = buffs;
    }

    public ClientTeamMainMatch getTeamMainMatch() {
        return teamMainMatch;
    }

    public void setTeamMainMatch(ClientTeamMainMatch teamMainMatch) {
        this.teamMainMatch = teamMainMatch;
    }

    public RankedMatch getRankedMatch() {
        return rankedMatch;
    }

    public void setRankedMatch(RankedMatch rankedMatch) {
        this.rankedMatch = rankedMatch;
    }

    public boolean hasEmail() {
        for (ClientEmail mail : emails.values()) {
            if (mail.isRead()) {
                continue;
            }
            if (mail.hasProp()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasProp(int propId, int count) {
        ClientProp p = props.get(propId);
        return p != null && p.getNum() >= count;
    }

    public ClientTeamAllStar getAllStar() {
        return allStar;
    }

    public void setAllStar(ClientTeamAllStar allStar) {
        this.allStar = allStar;
    }

    public SysAllStar getSysAllStar() {
        return sysAllStar;
    }

    public void setSysAllStar(SysAllStar sysAllStar) {
        this.sysAllStar = sysAllStar;
    }

    public ClientArena getArena() {
        return arena;
    }

    public void setArena(ClientArena arena) {
        this.arena = arena;
    }
}

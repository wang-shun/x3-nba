package com.ftkj.x3.client.model;

import java.util.Map;

/**
 * @author luch
 */
public class ClientTeamFriends {
    /**
     * 好友
     */
    private Map<Long, ClientFriend> friends;
    /**
     * redis
     * 黑名单
     */
    private Map<Long, ClientFriend> blackList;

    public final static int Friend = 1;
    public final static int Black = 2;

    public Map<Long, ClientFriend> getFriends() {
        return friends;
    }

    public void setFriends(Map<Long, ClientFriend> friends) {
        this.friends = friends;
    }

    public Map<Long, ClientFriend> getBlackList() {
        return blackList;
    }

    public void setBlackList(Map<Long, ClientFriend> blackList) {
        this.blackList = blackList;
    }
}

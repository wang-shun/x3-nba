package com.ftkj.manager;

import com.google.common.collect.Maps;
import org.apache.mina.core.session.IoSession;

import java.util.Date;
import java.util.Map;

public class User {
    private long teamId;//
    private IoSession session;
    private Date loginDate;
    private long lasttime;//最后发喇叭时间
    private long loginTime = 0;//登录时间
    private boolean leagueChat = false;
    private int rate = 100;//防沉谜
    private long lastChatWord;//最后时间发言时间

    //
    private Map<String, String> valMap;

    public static final String Level_Key = "level";

    public User() {
    }

    public User(long teamId, IoSession session) {
        super();
        this.teamId = teamId;
        this.session = session;
        this.valMap = Maps.newHashMap();
    }

    public String getVal(String key) {
        return this.valMap.getOrDefault(key, "");
    }

    public void setVal(String key, String val) {
        this.valMap.put(key, val);
    }

    //心跳
    public void check() {
        lasttime = System.currentTimeMillis();
    }

    public void login() {
        loginTime = System.currentTimeMillis();
        loginDate = new Date();
    }

    public long getLogin() {
        return loginTime;
    }

    //在线时长
    public int getLoginTime() {
        if (loginTime == 0) { return 0; }
        return Math.round((System.currentTimeMillis() - loginTime) / (1000 * 60.0f));
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
        this.rate = 100;
    }

    public long getTeamId() {
        return teamId;
    }

    public boolean isLeagueChat() {
        return leagueChat;
    }

    public void setLeagueChat(boolean leagueChat) {
        this.leagueChat = leagueChat;
    }

    public long getLasttime() {
        return lasttime;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    public long getLastChatWord() {
        return lastChatWord;
    }

    public void setLastChatWord(long lastChatWord) {
        this.lastChatWord = lastChatWord;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public static final class T {}

    @Override
    public String toString() {
        return "User [teamId=" + teamId + "]";
    }

}

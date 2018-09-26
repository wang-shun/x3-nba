package com.ftkj.manager.team;

import com.ftkj.cfg.TeamExpBean;
import com.ftkj.console.GameConsole;
import com.ftkj.console.GradeConsole;
import com.ftkj.db.domain.TeamPO;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ETeamTitle;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.LoginParam;
import com.ftkj.manager.ablity.BaseAbility;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.Map;

/**
 * @author tim.huang
 * 2017年3月2日
 * 球队信息
 */
public class Team {

    private TeamPO teamInfo;

    private boolean npc;

    /**
     * 全队加成属性配置，不包含球员等单独加成数据
     * 一般玩家战力数据使用该字段获得。
     */
    private Map<AbilityType, BaseAbility> ability;

    public static Team createTeam(long teamId, long userId, int shardId, String name, String logo, int secId) {
        TeamPO po = new TeamPO();
        po.setTeamId(teamId);
        po.setUserId(userId);
        po.setShardId(shardId);
        po.setLevel(1);
        po.setLineupCount(2);
        po.setName(name);
        po.setLogo(logo);
        po.setTitle("");
        po.setSecId(secId);
        TeamExpBean bean = GradeConsole.getTeamExpBean(1);
        po.setPrice(bean.getPrice());
        DateTime now = DateTime.now();
        po.setLastLoginTime(GameConsole.Min_Date);
        po.setLastOfflineTime(GameConsole.Min_Date);
        po.setCreateTime(now);
        po.setHelp("");
        Team team = new Team(po);
        team.npc = false;
        return team;
    }

    public static Team createNPC(long teamId, String name, String logo, float attack, float defend, int level) {
        TeamPO po = new TeamPO();
        po.setTeamId(teamId);
        po.setLevel(level);
        po.setName(name);
        po.setLogo(logo);
        po.setTitle("");
        TeamExpBean bean = GradeConsole.getTeamExpBean(1);
        po.setPrice(bean.getPrice());
        DateTime now = DateTime.now();
        po.setLastLoginTime(now);
        po.setLastOfflineTime(now);
        po.setCreateTime(now);
        po.setHelp("");
        Team team = new Team(po);
        TeamAbility ab = new TeamAbility(AbilityType.Npc_Buff);
        ab.addAttr(EActionType.ocap, attack);
        ab.addAttr(EActionType.dcap, defend);
        team.getAbility().put(AbilityType.Npc_Buff, ab);
        team.npc = true;
        return team;
    }

    public void updateLineupCount(int count) {
        this.teamInfo.setLineupCount(count);
        this.teamInfo.save();
    }

    public void updatePriceCount(int count) {
        this.teamInfo.setPriceCount(count);
        this.teamInfo.save();
        EventBusManager.post(EEventType.球队工资帽, getTeamId());
    }

    public void updatePrice(int price) {
        this.teamInfo.setPrice(price);
        this.teamInfo.save();
    }

    public void updateHelp(String help) {
        this.teamInfo.setHelp(help);
        this.teamInfo.save();
    }

    public void battleWin() {
        this.teamInfo.setPkWin(this.teamInfo.getPkWin() + 1);
        this.teamInfo.save();
    }

    /** 改变球队名称 */
    public void changeName(String name) {
        this.teamInfo.setName(name);
        this.teamInfo.save();
    }

    public void login() {
        // 用户登录事件
        DateTime now = DateTime.now();
        boolean isTodayFirst = DateTimeUtil.getDaysBetweenNum(getLastLoginTime(), now, 0) != 0;
        long offlineSecond = 0;
        if (!GameConsole.Min_Date.equals(this.teamInfo.getLastOfflineTime())) {
            offlineSecond = (now.getMillis() - this.teamInfo.getLastOfflineTime().getMillis()) / 1000;
        }
        this.teamInfo.setLastLoginTime(now);
        this.teamInfo.save();
        EventBusManager.post(EEventType.登录, new LoginParam(this.getTeamId(),
        		this.getLastLoginTime(), offlineSecond, isTodayFirst));
    }

    /**
     * 下线
     */
    public void offline() {
        this.teamInfo.setLastOfflineTime(DateTime.now());
        this.teamInfo.save();
    }

    public void save() {
        this.teamInfo.save();
    }

    public Team(TeamPO team) {
        this.teamInfo = team;
        this.ability = Maps.newHashMap();
    }

    public boolean isNpc() {
        return npc;
    }

    public int getLineupCount() {
        return this.teamInfo.getLineupCount();
    }

    public String getHelp() {
        return this.teamInfo.getHelp();
    }

    public int getPriceCount() {
        return this.teamInfo.getPriceCount();
    }

    public int getPrice() {
        return this.teamInfo.getPrice();
    }

    public int getSecId() {
        return this.teamInfo.getSecId();
    }

    public ETeamTitle getTitle() {
        return ETeamTitle.getETeamTitle(this.teamInfo.getTitle());
    }

    public Map<AbilityType, BaseAbility> getAbility() {
        return ability;
    }

    public void updateAbility(BaseAbility ability) {
        this.ability.put(ability.getType(), ability);
    }

    public long getTeamId() {
        return teamInfo.getTeamId();
    }

    public int getLevel() {
        return teamInfo.getLevel();
    }

    public int getTaskStep() {
        return teamInfo.getTaskStep();
    }

    public String getName() {
        return teamInfo.getName();
    }

    public DateTime getLastLoginTime() {
        return teamInfo.getLastLoginTime();
    }

    public DateTime getCreateTime() {
        return teamInfo.getCreateTime();
    }

    public String getLogo() {
        return teamInfo.getLogo();
    }

    public void setLevel(int level) {
        this.teamInfo.setLevel(level);
    }

    public void setTaskStep(int step) {
        this.teamInfo.setTaskStep(step);
    }

    public void setChatStatus(int status) {
        this.teamInfo.setChatStatus(status);
    }

    public void setUserStatus(int status) {
        this.teamInfo.setUserStatus(status);
    }

    public void changeLogo(String logo) {
        this.teamInfo.setLogo(logo);
        this.save();
    }

    public void changeSec(int id) {
        this.teamInfo.setSecId(id);
        this.save();
    }

    /**
     * 取玩家创建号的天数
     * 第一天是1
     *
     * @return
     */
    public int getCreateDay() {
        int day = DateTimeUtil.getDaysBetweenNum(getCreateTime(), DateTime.now(), 0) + 1;
        return day < 1 ? 0 : day;
    }

    @Override
    public String toString() {
        return "Team [teamInfo=" + teamInfo + "]";
    }

    public TeamPO getTeamInfo() {
        return teamInfo;
    }
}

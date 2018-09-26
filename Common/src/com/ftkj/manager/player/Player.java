package com.ftkj.manager.player;

import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.console.PlayerConsole;
import com.ftkj.db.domain.PlayerPO;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.manager.ablity.PlayerAbility;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年3月2日
 * 单个球员数据
 */
public class Player {
    private PlayerPO player;
    private PlayerTalent playerTalent;
    /**
     * 球员相关能力加成属性，如果相关能力有变动需要更新该字段中对应的数据，
     * 保证球员属性的实时有效性
     */
    private Map<AbilityType, PlayerAbility> abilitys;

    public Player(PlayerPO player) {
        this.player = player;
        this.abilitys = Maps.newHashMap();
    }

    public Player(PlayerPO player, PlayerTalent playerTalent) {
        this(player);
        if (playerTalent != null && playerTalent.getId() != player.getTalentId()) {
            player.setTalentId(playerTalent.getId());
            player.save();
        }
        this.playerTalent = playerTalent;
    }

    public void updatePlayerPosition(String position) {
        this.player.setPosition(position);
    }

    public PlayerTalent getPlayerTalent() {
        return playerTalent;
    }

    public PlayerPO getPlayer() {
        return player;
    }

    public void updateAbility(PlayerAbility ability) {
        this.abilitys.put(ability.getType(), ability);
    }
    
    public int getPid() {
        return this.player.getId();
    }

    /**
     * 创建球员的唯一接口
     *
     * @param teamId         球队id
     * @param id             球队球员唯一id
     * @param playerId       配置id
     * @param price          身价
     * @param position       位置
     * @param lineupPosition 阵容位置
     * @param bind           是否绑定
     * @param pt             天赋
     * @return
     */
    public static Player createPlayer(long teamId,
                                      int id,
                                      int playerId,
                                      int price,
                                      String position,
                                      String lineupPosition,
                                      boolean bind,
                                      PlayerTalent pt) {
        PlayerPO po = new PlayerPO();
        po.setTeamId(teamId);
        po.setId(id);
        po.setPlayerRid(playerId);
        po.setPrice(price);
        po.setPosition(position);
        po.setLineupPosition(lineupPosition);
        po.setCreateTime(DateTime.now());
        po.setBind(bind);
        Player p = new Player(po, pt);
        return p;
    }

    public void updatePrice(int price) {
        this.player.setPrice(price);
        this.player.save();
    }

    public long getTeamId() {
        return this.player.getTeamId();
    }

    public void save() {
        this.player.save();
    }

    public void del() {
        this.player.del();
    }

    public void updateLinuePosition(EPlayerPosition position, boolean save) {
        this.player.setLineupPosition(position.name());
        if (save) {
            this.player.save();
        }
    }

    public PlayerBean getPlayerBean() {
        return PlayerConsole.getPlayerBean(this.player.getPlayerRid());
    }

    public Map<AbilityType, PlayerAbility> getAbilitys() {
        return abilitys;
    }

    public EPlayerPosition getPlayerPosition() {
        return EPlayerPosition.valueOf(this.player.getPosition());
    }

    public EPlayerPosition getLineupPosition() {
        return EPlayerPosition.valueOf(this.player.getLineupPosition());
    }

    public String getPosition() {
        return this.player.getPosition();
    }

    public int getPrice() {
        return player.getPrice();
    }

    public int getId() {
        return player.getId();
    }

    public int getPlayerRid() {
        return player.getPlayerRid();
    }

    public int getStorage() {
        return this.player.getStorage();
    }

    public void setStorage(int storage) {
        this.player.setStorage(storage);
    }

    public void setPlayerRid(int playerId) {
        this.player.setPlayerRid(playerId);
    }

    public void setPrice(int price) {
        this.player.setPrice(price);
    }

    public void setPosition(String position) {
        this.player.setPosition(position);
    }

    public void setLineupPosition(String lineupPosition) {
        this.player.setLineupPosition(lineupPosition);
    }

    public boolean isBind() {
        return player.isBind();
    }

    @Override
    public String toString() {
        return "Player [player=" + getPlayerBean().getName() + "," + this.getPlayerRid() + "," + this.getId() + "]";
    }

}

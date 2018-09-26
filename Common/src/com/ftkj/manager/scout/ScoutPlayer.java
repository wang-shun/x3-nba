package com.ftkj.manager.scout;

import com.ftkj.enums.EStatus;
import com.ftkj.manager.player.PlayerTalent;

import java.io.Serializable;

/**
 * @author tim.huang 2017年3月22日 球探球员
 */
public class ScoutPlayer implements Serializable {


  private static final long serialVersionUID = 1L;

  /** 基础球员ID */
  private int playerId;
  /** 工资帽 */
  private int price;
  private int index;
  private EStatus status;
  private PlayerTalent talent;
  private boolean vip;// 是否会员专属
  private boolean basePrice;// 是否底薪
  private boolean openSpecial;// 是否额外送的
    /** 是否绑定 */
    private boolean bind;
  public ScoutPlayer(int playerId, int price, int index, boolean vip, boolean basePrice, PlayerTalent talent) {
    super();
    this.playerId = playerId;
    this.price = price;
    this.index = index;
    this.vip = vip;
    this.basePrice = basePrice;
    this.talent = talent;
    this.status = EStatus.ScoutDefault;
  }

  public PlayerTalent getTalent() {
    return talent;
  }

  public void sign() {
    this.status = EStatus.ScoutSign;
  }

  public int getIndex() {
    return index;
  }

  public int getPlayerId() {
    return playerId;
  }

  public int getPrice() {
    return price;
  }

  public EStatus getStatus() {
    return status;
  }

  public boolean isVip() {
    return vip;
  }

  public boolean isBasePrice() {
    return basePrice;
  }

  public boolean isOpenSpecial() {
    return openSpecial;
  }

  public void setOpenSpecial(boolean openSpecial) {
    this.openSpecial = openSpecial;
  }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }
}

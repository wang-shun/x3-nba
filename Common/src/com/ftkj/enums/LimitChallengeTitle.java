package com.ftkj.enums;

public enum LimitChallengeTitle {
  //得分+篮板
  两双王_d_l,
  
  //得分+助攻
  两双王_d_z,
  
  //得分+篮板+助攻
  三双王,
  
  //得分+篮板+助攻+抢断+盖帽
  全能王,
  
  //抢断+盖帽
  得分王,
  
  //助攻+盖帽
  助攻王,
  
  //篮板+抢断+盖帽
  篮板王
  ;
  
  public static LimitChallengeTitle valueOf(int code) {
    for(LimitChallengeTitle o : LimitChallengeTitle.values()) {
      if(code == o.ordinal()) {
        return o;
      }
    }
    return null;
  }
}

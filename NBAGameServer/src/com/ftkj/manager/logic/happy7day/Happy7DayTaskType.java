package com.ftkj.manager.logic.happy7day;

public enum Happy7DayTaskType {
  type1(1), // 登录游戏
  type2(2), // 升级N次技能
  type3(3), // N个M阶技能到达X级
  type4(4), // 制作N张球星卡
  type5(5), // 球星卡总数到达N张
  type6(6), // 强化N次装备
  type7(7), // N件装备强化到M级
  type8(8), // 装备升星N次
  type9(9), // N件装备强化到M星
  type10(10), // 装备染色N次
  type11(11), // N件装备染色到M色
  type12(12), // 刷新N次球员天赋
  type13(13), // N个球员天赋到达M
  type14(14), // 进行N场天梯赛
  type15(15), // 天梯积分到达N-------改成战力
  type16(16),// 球队等级到达N级
  type17(17),// 充值N球券
  type18(18),// 进行N次招募
  type19(19),//进行N次选秀
  ;
  private int code;

  private Happy7DayTaskType(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}

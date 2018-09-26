package com.ftkj.manager.logic.match;

/**
 * 比赛类型
 * @author zehong.he
 *
 */
public enum MatchType {
  
  /**
   * 奖杯超快赛
   */
  j_b_c_k_s(100),
  
  /**
   * 球券赛
   */
  q_q_s(110),
  
  /**
   * 经费赛
   */
  j_f_s(120),
  
  /**
   * 技能点对抗赛
   */
  j_n_d_d_k_s(130),
  
  /**
   * 强化石对抗赛
   */
  q_h_s_d_k_s(140),
  
  /**
   * 周末球券赛
   */
  z_m_q_q_s(150),
  
  
  ;
  
  private int type;
  
  private MatchType(int type) {
    this.type = type;
  }
  
  public int getType() {
    return type;
  }
}

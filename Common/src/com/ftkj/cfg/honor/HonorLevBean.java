package com.ftkj.cfg.honor;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import com.ftkj.cfg.MMatchLevBean.Star;
import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ParseListColumnUtil;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple4;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple4;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableList;

/**
 * 球星荣耀关卡配置
 * @author zehong.he
 *
 */
public class HonorLevBean extends AbstractExcelBean{

  //关卡id
  private int id;
  //章节id
  private int divId;
  //对应的NPC
  private int npcId;
  //开启本关卡需要的上一个关卡id
  private int enablePreId;
  //开启本关卡需要的上一个关卡id
  private int enablePreStar;
  //是否可扫荡
  private int finish;
  //球队等级限制
  private int teamLev;
  //星级1
  private int starNum1;
  //星级1的奖励（道具）
  private String starItem1;
  //星级1的奖励（掉落）
  private String starDrop1;
  //星级2
  private int starNum2;
  //获得本星级需要的条件，多个逗号分隔
  private String starCond2;
  //星级2的奖励（道具）
  private String starItem2;
  //星级2的奖励（掉落）
  private String starDrop2;
  //星级3
  private int starNum3;
  //获得本星级需要的条件，多个逗号分隔
  private String starCond3;
  //星级3的奖励（道具）
  private String starItem3;
  //星级3的奖励（掉落）
  private String starDrop3;
  
  /** 星级配置 */
  private Map<Integer, Star> stars = Collections.emptyMap();
  
  public String reward(int star) {
    if(star == 1) {
      return starItem1;
    }else if(star == 2) {
      return starItem2;
    }else if(star == 3) {
      return starItem3;
    }
    return null;
  }
  
  public String drop(int star) {
    if(star == 1) {
      return starDrop1;
    }else if(star == 2) {
      return starDrop2;
    }else if(star == 3) {
      return starDrop3;
    }
    return null;
  }
  
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public int getDivId() {
    return divId;
  }
  public void setDivId(int divId) {
    this.divId = divId;
  }
  public int getNpcId() {
    return npcId;
  }
  public void setNpcId(int npcId) {
    this.npcId = npcId;
  }
  public int getEnablePreId() {
    return enablePreId;
  }
  public void setEnablePreId(int enablePreId) {
    this.enablePreId = enablePreId;
  }
  public int getEnablePreStar() {
    return enablePreStar;
  }
  public void setEnablePreStar(int enablePreStar) {
    this.enablePreStar = enablePreStar;
  }
  public int getFinish() {
    return finish;
  }
  public void setFinish(int finish) {
    this.finish = finish;
  }
  public int getTeamLev() {
    return teamLev;
  }
  public void setTeamLev(int teamLev) {
    this.teamLev = teamLev;
  }
  public int getStarNum1() {
    return starNum1;
  }
  public void setStarNum1(int starNum1) {
    this.starNum1 = starNum1;
  }
  public String getStarItem1() {
    return starItem1;
  }
  public void setStarItem1(String starItem1) {
    this.starItem1 = starItem1;
  }
  public String getStarDrop1() {
    return starDrop1;
  }
  public void setStarDrop1(String starDrop1) {
    this.starDrop1 = starDrop1;
  }
  public int getStarNum2() {
    return starNum2;
  }
  public void setStarNum2(int starNum2) {
    this.starNum2 = starNum2;
  }
  public String getStarCond2() {
    return starCond2;
  }
  public void setStarCond2(String starCond2) {
    this.starCond2 = starCond2;
  }
  public String getStarItem2() {
    return starItem2;
  }
  public void setStarItem2(String starItem2) {
    this.starItem2 = starItem2;
  }
  public String getStarDrop2() {
    return starDrop2;
  }
  public void setStarDrop2(String starDrop2) {
    this.starDrop2 = starDrop2;
  }
  public int getStarNum3() {
    return starNum3;
  }
  public void setStarNum3(int starNum3) {
    this.starNum3 = starNum3;
  }
  public String getStarCond3() {
    return starCond3;
  }
  public void setStarCond3(String starCond3) {
    this.starCond3 = starCond3;
  }
  public String getStarItem3() {
    return starItem3;
  }
  public void setStarItem3(String starItem3) {
    this.starItem3 = starItem3;
  }
  public String getStarDrop3() {
    return starDrop3;
  }
  public void setStarDrop3(String starDrop3) {
    this.starDrop3 = starDrop3;
  }
  @Override
  public void initExec(RowData row) {
    //starNum2  starMatchData2  starItem2  starDrop2
    //星级2  星级2需要的比赛数据id  星级2的奖励(道具)  星级2的奖励(掉落)
    //int  string  string  int
    IDListTuple4<Integer, Integer, String, String, String> starIlt =
            ParseListColumnUtil.parse(row, toInt,
                    "starNum", toInt, "starCond", toStr, "starItem", toStr, "starDrop", toStr);
    Map<Integer, Star> ss = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));
    for (IDTuple4<Integer, Integer, String, String, String> tp : starIlt.getTuples().values()) {
        if (tp.getE1() <= 0) {
            continue;
        }
        ss.put(tp.getE1(),
                new Star(tp.getE1(),
                        ImmutableList.copyOf(StringUtil.toIntegerArray(tp.getE2(), StringUtil.COMMA)),
                        PropSimple.parseItems(tp.getE3()),
                        ImmutableList.copyOf(StringUtil.toIntegerArray(tp.getE4(), StringUtil.COMMA))));
    }
    this.stars = ss;
  }
  
  public Map<Integer, Star> getStars() {
    return stars;
  }

  public Star getStar(int star) {
    return stars.get(star);
}
  
}

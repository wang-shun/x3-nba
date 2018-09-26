package com.ftkj.manager.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.proto.HonorMatchPB.HonorMatchBoxReward;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * 球星荣耀
 * @author zehong.he
 *
 */
public class HonorMatch extends AsynchronousBatchDB {

  /**
   * 
   */
  private static final long serialVersionUID = 5653544117348836456L;

  public static final String Division_1 = "_";
  public static final String Division_2 = ",";
  public static final String Division_3 = ";";
  
  /**
   * 玩家ID
   */
  private long teamId;
  
  /**
   * 章节 章节ID,1星_2星_3星,总星数,宝箱1状态_宝箱2状态_宝箱3状态;...
   * 星状态用二进制位移表示
   */
  private String divInfo;
  
  /**
   * 创建时间
   */
  private DateTime createTime;
  
  /**
   * 更新时间
   */
  private DateTime updateTime;
  
  /**
   * 最大通关章节
   */
  private int maxDiv;
  
  /**
   * 反序列化章节
   * @return
   */
  public Map<Integer,HDiv> getDivMap(){
    Map<Integer,HDiv> map = new HashMap<Integer, HonorMatch.HDiv>();
    if(divInfo == null || "".equals(divInfo)) {
      return map;
    }
    String[] divArr = divInfo.split(Division_3);
    for(String divItem : divArr) {
      HDiv hDiv = new HDiv();
      
      String[] item0 = divItem.split(Division_2);
      String divId = item0[0];
      String stars = item0[1];
      String totalStarNum = item0[2];
      String box = item0[3];
      hDiv.setDivId(Integer.parseInt(divId));
      
      String[] starArr = stars.split(Division_1);
      hDiv.setStar1(Integer.parseInt(starArr[0]));
      hDiv.setStar2(Integer.parseInt(starArr[1]));
      hDiv.setStar3(Integer.parseInt(starArr[2]));
      
      hDiv.setTotalStarNum(Integer.parseInt(totalStarNum));
      
      String[] boxrArr = box.split(Division_1);
      hDiv.setBox1State(Integer.parseInt(boxrArr[0]));
      hDiv.setBox2State(Integer.parseInt(boxrArr[1]));
      hDiv.setBox3State(Integer.parseInt(boxrArr[2]));
      map.put(hDiv.getDivId(), hDiv);
    }
    return map;
  }
  
  /**
   * 序列号章节
   * @param map
   */
  public void setDiv0(Map<Integer,HDiv> map) {
    if(map == null) {
      return;
    }
    StringBuffer sb = new StringBuffer();
    int index = 0;
    for(HDiv hDiv : map.values()) {
      index++;
      sb.append(hDiv.obj2String());
      if(index < map.size()) {
        sb.append(Division_3);
      }
    }
    setDivInfo(sb.toString());
    DateTime date = new DateTime();
    this.updateTime = date;
  }
  
  public static class HDiv{
    //宝箱不可领取
    public static final int box_state_no = 0;
    //宝箱可领取
    public static final int box_state_can_get = 1;
    //宝箱已领取
    public static final int box_state_has_got = 2;
    
    //章节ID
    private int divId;
    //星1通过关卡
    private int star1;
    //星2通过关卡
    private int star2;
    //星3通过关卡
    private int star3;
    //总星
    private int totalStarNum;
    //宝箱1状态
    private int box1State;
    //宝箱2状态
    private int box2State;
    //宝箱3状态
    private int box3State;
    
    public void setBoxStateIsGet(int boxId) {
      if(boxId == 1) {
        box1State = box_state_has_got;
      }else if(boxId == 2) {
        box2State = box_state_has_got;
      }else if(boxId == 3) {
        box3State = box_state_has_got;
      }
    }
    
    public int getBoxState(int boxId) {
      if(boxId == 1) {
        return box1State;
      }else if(boxId == 2) {
        return box2State;
      }else if(boxId == 3) {
        return box3State;
      }
      return 0;
    }
    
    public int getStart(int index) {
      if(star(3,index)) {
        return 3;
      }else if(star(2,index)) {
        return 2;
      }else if(star(1,index)) {
        return 1;
      }
      return 0;
    }
    
    public void setStar(int star,int index) {
      if(star == 3) {
        star1 = calcStar(star1,index);
        star2 = calcStar(star2,index);
        star3 = calcStar(star3,index);
      }else if(star == 2) {
        star1 = calcStar(star1,index);
        star2 = calcStar(star2,index);
      }else if(star == 1) {
        star1 = calcStar(star1,index);
      }
    }
    
    private int calcStar(int flag,int index) {
      flag |= 1 << (index - 1);
      return flag;
    }
    
    public boolean star(int star,int index) {
      int flag = 0;
      if(star == 1) {
        flag = star1;
      }else if(star == 2) {
        flag = star2;
      }else {
        flag = star3;
      }
      if (flag == 0) {
        return false;
      }
      return (flag & (1 << (index - 1))) == (1 << (index - 1));
    }
    
    public List<HonorMatchBoxReward.Builder> boxBuilders(){
      HonorMatchBoxReward.Builder builder1 = HonorMatchBoxReward.newBuilder();
      builder1.setId(1);
      builder1.setState(box1State);
      HonorMatchBoxReward.Builder builder2 = HonorMatchBoxReward.newBuilder();
      builder2.setId(2);
      builder2.setState(box2State);
      HonorMatchBoxReward.Builder builder3 = HonorMatchBoxReward.newBuilder();
      builder3.setId(3);
      builder3.setState(box3State);
      List<HonorMatchBoxReward.Builder> list = new ArrayList<>();
      list.add(builder1);
      list.add(builder2);
      list.add(builder3);
      return list;
    }
    
    public String obj2String() {
      StringBuffer sb = new StringBuffer();
      sb.append(divId)
      .append(Division_2)
      .append(star1).append(Division_1).append(star2).append(Division_1).append(star3)
      .append(Division_2)
      .append(totalStarNum)
      .append(Division_2)
      .append(box1State).append(Division_1).append(box2State).append(Division_1).append(box3State);
      return sb.toString();
    }
    
    public static HDiv valueOf(int divId) {
      HDiv div=  new HDiv();
      div.divId = divId;
      return div;
    }
    
    public static HDiv valueOf(int divId,int star1,int star2,int star3,int totalStarNum,int box1State,int box2State,int box3State) {
      HDiv div=  new HDiv();
      div.divId = divId;
      div.star1 = star1;
      div.star2 = star2;
      div.star3 = star3;
      div.totalStarNum = totalStarNum;
      div.box1State = box1State;
      div.box2State = box2State;
      div.box3State = box3State;
      return div;
    }
    
    public int getDivId() {
      return divId;
    }
    public void setDivId(int divId) {
      this.divId = divId;
    }
    public int getStar1() {
      return star1;
    }
    public void setStar1(int star1) {
      this.star1 = star1;
    }
    public int getStar2() {
      return star2;
    }
    public void setStar2(int star2) {
      this.star2 = star2;
    }
    public int getStar3() {
      return star3;
    }
    public void setStar3(int star3) {
      this.star3 = star3;
    }
    public int getTotalStarNum() {
      return totalStarNum;
    }
    public void setTotalStarNum(int totalStarNum) {
      this.totalStarNum = totalStarNum;
    }
    public int getBox1State() {
      return box1State;
    }
    public void setBox1State(int box1State) {
      this.box1State = box1State;
    }
    public int getBox2State() {
      return box2State;
    }
    public void setBox2State(int box2State) {
      this.box2State = box2State;
    }
    public int getBox3State() {
      return box3State;
    }
    public void setBox3State(int box3State) {
      this.box3State = box3State;
    }
  }
  
  
  public long getTeamId() {
    return teamId;
  }

  public void setTeamId(long teamId) {
    this.teamId = teamId;
  }
  

  public String getDivInfo() {
    return divInfo;
  }

  public void setDivInfo(String divInfo) {
    this.divInfo = divInfo;
  }

  public DateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(DateTime createTime) {
    this.createTime = createTime;
  }

  public DateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(DateTime updateTime) {
    this.updateTime = updateTime;
  }

  
  public int getMaxDiv() {
    return maxDiv;
  }

  public void setMaxDiv(int maxDiv) {
    this.maxDiv = maxDiv;
  }

  @Override
  public String getSource() {
    return StringUtil.formatSQL(teamId, createTime,
        updateTime,maxDiv,divInfo);
  }

  @Override
  public String getRowNames() {
    return "team_id,create_time,update_time,max_div,div_info";
  }

  @Override
  public String getTableName() {
    return "t_u_honor_match";
  }

  @Override
  public synchronized void save() {
      super.save();
  }
  
  @Override
  public void del() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public List<Object> getRowParameterList() {
    return Lists.newArrayList(teamId, createTime,
        updateTime,maxDiv,divInfo);
  }

}

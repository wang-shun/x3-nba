package com.ftkj.cfg.honor;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

/**
 * 球星荣耀章节配置
 * @author zehong.he
 *
 */
public class HonorDivBean extends ExcelBean{

  private int id;
  
  //星级礼包id
  private int starIdx1;
  
  //礼包星级要求
  private int starNum1;
  
  //礼包奖励（道具）
  private String starItem1;
  
  //礼包奖励（掉落）
  private String starDrop1;
  
  //星级礼包id
  private int starIdx2;
  
  //礼包星级要求
  private int starNum2;
  
  //礼包奖励（道具）
  private String starItem2;
  
  //礼包奖励（掉落）
  private String starDrop2;
  
  //星级礼包id
  private int starIdx3;
  
  //礼包星级要求
  private int starNum3;
  
  //礼包奖励（道具）
  private String starItem3;
  
  //礼包奖励（掉落）
  private String starDrop3;
  
  
  public String getItem(int boxId) {
    if(boxId == 1) {
      return starItem1;
    }else if(boxId == 2) {
      return starItem2;
    }else if(boxId == 3) {
      return starItem3;
    }
    return "";
  }
  
  public String getDrop(int boxId) {
    if(boxId == 1) {
      return starDrop1;
    }else if(boxId == 2) {
      return starDrop2;
    }else if(boxId == 3) {
      return starDrop3;
    }
    return "";
  }
  
  
  
  @Override
  public void initExec(RowData row) {
    // TODO Auto-generated method stub
    
  }


  public int getId() {
    return id;
  }


  public void setId(int id) {
    this.id = id;
  }


  public int getStarIdx1() {
    return starIdx1;
  }


  public void setStarIdx1(int starIdx1) {
    this.starIdx1 = starIdx1;
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


  public int getStarIdx2() {
    return starIdx2;
  }


  public void setStarIdx2(int starIdx2) {
    this.starIdx2 = starIdx2;
  }


  public int getStarNum2() {
    return starNum2;
  }


  public void setStarNum2(int starNum2) {
    this.starNum2 = starNum2;
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


  public int getStarIdx3() {
    return starIdx3;
  }


  public void setStarIdx3(int starIdx3) {
    this.starIdx3 = starIdx3;
  }


  public int getStarNum3() {
    return starNum3;
  }


  public void setStarNum3(int starNum3) {
    this.starNum3 = starNum3;
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
  
  

}

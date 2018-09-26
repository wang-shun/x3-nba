package com.ftkj.manager.player;

/**
 * @author tim.huang
 * 2017年3月22日
 *
 */
public class PlayerMinPrice {
    
    /** 球员ID*/
    private int playerId;
    /** 球员底薪*/
    private int minPrice;       
    
    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }
    
	/**
	 * 判断价格如果价格比当前底薪更低，则进行数据更新
	 * @param price
	 */
	public void update(int price){
		if(this.minPrice < price){
		    minPrice = price;  
		}
	}
}

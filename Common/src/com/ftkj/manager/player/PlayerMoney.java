package com.ftkj.manager.player;

import com.ftkj.db.domain.PlayerMoneyPO;
import org.joda.time.DateTime;

/**
 * @author tim.huang 2017年3月22日
 */
public class PlayerMoney {

    private PlayerMoneyPO playerMoney;

    public PlayerMoney(PlayerMoneyPO playerMoney) {
        this.playerMoney = playerMoney;
    }

    public int getPrice() {
        return this.playerMoney.getPrice();
    }

    /**
     * 判断价格如果价格比当前底薪更低，则进行数据更新
     */
    public void update(int price) {
        if (playerMoney.getPrice() > price) {
            playerMoney.setPrice(price);
            playerMoney.setUpdateTime(DateTime.now());
            playerMoney.save();
        }
    }
}

package com.ftkj.manager.prop.bean;

import com.ftkj.db.domain.bean.PropBeanVO;
import com.ftkj.enums.EPropType;

/**
 * @author tim.huang
 * 2016年3月14日
 * 英雄类型道具  ID=3
 */
public class PropPlayerBean extends PropBean {
    private static final long serialVersionUID = 1L;
    private int heroId;

    public PropPlayerBean(int playerId, String playerName) {
        super(new PropBeanVO(convertHeroId(playerId), convertHeroId(playerId), EPropType.Player.getType(), playerName));
        this.heroId = playerId;
    }

    public PropPlayerBean(PropPlayerBean pb) {
        super(pb.getProp());
        this.heroId = pb.heroId;
    }

    public static int convertHeroId(int playerId) {
        return 100000000 + playerId;
    }

    @Override
    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "hero": {
                this.heroId = Integer.parseInt(valStr);
                break;
            }
            default:
                super.setAttr(nameStr, valStr);
        }
    }

    public int getHeroId() {
        return heroId;
    }

}

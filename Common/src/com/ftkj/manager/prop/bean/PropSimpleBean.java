package com.ftkj.manager.prop.bean;

import com.ftkj.db.domain.bean.PropBeanVO;

/**
 * @author tim.huang
 * 2016年3月14日
 * 常规物品  ID =1
 */
public class PropSimpleBean extends PropBean {
    private static final long serialVersionUID = 1L;

    private int playerExp;

    public PropSimpleBean(PropBeanVO prop) {
        super(prop);
    }

    public int getPlayerExp() {
        return playerExp;
    }

    public void setPlayerExp(int playerExp) {
        this.playerExp = playerExp;
    }

    @Override
    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "playerExp": {
                this.setPlayerExp(Integer.parseInt(valStr));
                break;
            }
            default:
                super.setAttr(nameStr, valStr);
        }
    }

}

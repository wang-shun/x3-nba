package com.ftkj.manager.prop.bean;

import com.ftkj.db.domain.bean.PropBeanVO;

/**
 * @author tim.huang
 * 2016年3月14日
 * 玩家货币属性类型道具  ID=4
 */
public class PropMoneyBean extends PropBean {
    private static final long serialVersionUID = 1L;
    private int gold;
    private int exp;
    private int money;
    private int tacticsKey;
    private int tacticsPith;
    private int cardExp;
    private int build;
    private int honor;
    private int bdmoney;
    private int custom;
    private int feats;

    public PropMoneyBean(PropBeanVO prop) {
        super(prop);
    }

    @Override
    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "gold": {
                this.gold += Integer.parseInt(valStr);
                break;
            }
            case "tacticsKey": {
                this.tacticsKey += Integer.parseInt(valStr);
                break;
            }
            case "tacticsPith": {
                this.tacticsPith += Integer.parseInt(valStr);
                break;
            }
            case "cardExp": {
                this.cardExp += Integer.parseInt(valStr);
                break;
            }
            case "build": {
                this.build += Integer.parseInt(valStr);
                break;
            }
            case "honor": {
                this.honor += Integer.parseInt(valStr);
                break;
            }
            case "exp": {
                this.exp += Integer.parseInt(valStr);
                break;
            }
            case "money": {
                this.money += Integer.parseInt(valStr);
                break;
            }
            case "bdmoney": {
                this.bdmoney += Integer.parseInt(valStr);
                break;
            }
            case "costom": {
                this.custom += Integer.parseInt(valStr);
                break;
            }
            case "feats": {
                this.feats += Integer.parseInt(valStr);
                break;
            }
            default:
                super.setAttr(nameStr, valStr);
        }
    }

    public int getFeats() {
        return feats;
    }

    public int getCustom() {
        return custom;
    }

    public int getBdmoney() {
        return bdmoney;
    }

    public int getTacticsKey() {
        return tacticsKey;
    }

    public int getTacticsPith() {
        return tacticsPith;
    }

    public int getCardExp() {
        return cardExp;
    }

    public int getBuild() {
        return build;
    }

    public int getHonor() {
        return honor;
    }

    public int getGold() {
        return gold;
    }

    public int getExp() {
        return exp;
    }

    public int getMoney() {
        return money;
    }
}

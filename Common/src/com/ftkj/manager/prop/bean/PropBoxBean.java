package com.ftkj.manager.prop.bean;

import com.ftkj.console.DropConsole;
import com.ftkj.db.domain.bean.PropBeanVO;
import com.ftkj.manager.system.bean.DropBean;

/**
 * @author tim.huang
 * 2016年3月14日
 * 礼包类道具 ID=2
 */
public class PropBoxBean extends PropBean {
    private static final long serialVersionUID = 1L;
    /**
     * 礼包打开方式
     */
    private DropBean drop;

    public PropBoxBean(PropBeanVO prop) {
        super(prop);
    }

    @Override
    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "drop": {
                this.drop = DropConsole.getDrop(Integer.parseInt(valStr));
                break;
            }
            default:
                super.setAttr(nameStr, valStr);
        }
    }

    public DropBean getDrop() {
        return drop;
    }

}

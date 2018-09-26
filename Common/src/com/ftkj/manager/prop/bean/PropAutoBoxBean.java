package com.ftkj.manager.prop.bean;

import com.ftkj.console.DropConsole;
import com.ftkj.db.domain.bean.PropBeanVO;
import com.ftkj.manager.system.bean.DropBean;

/**
 * @author tim.huang
 * 2016年3月14日
 * 自动使用的特殊礼包
 */
public class PropAutoBoxBean extends PropBean {
    private static final long serialVersionUID = 1L;
    private DropBean drop;

    public PropAutoBoxBean(PropBeanVO prop) {
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

package com.ftkj.manager.prop.bean;

import com.ftkj.db.domain.bean.PropBeanVO;

/** 球员包装道具 */
public class PropWrapPlayerBean extends PropBean {
    private static final long serialVersionUID = 1L;
    /** 常规球员道具id */
    private int playerPropRid;
    /** 是否绑定 */
    private boolean bind;

    public PropWrapPlayerBean(PropBeanVO prop) {
        super(prop);
    }

    @Override
    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "pprid": {
                this.playerPropRid = Integer.parseInt(valStr);
                break;
            }
            case "bind": {
                this.bind = Integer.parseInt(valStr) == 1;
                break;
            }
            default:
                super.setAttr(nameStr, valStr);
        }
    }

    public int getPlayerPropRid() {
        return playerPropRid;
    }

    public boolean isBind() {
        return bind;
    }
}

package com.ftkj.manager.prop.bean;

/**
 * 扩展球员道具. 整合 {@link PropPlayerBean} 和 {@link PropWrapPlayerBean}
 */
public class PropExtPlayerBean extends PropPlayerBean {
    private static final long serialVersionUID = 1L;
    /** 是否绑定 */
    private final boolean bind;

    public PropExtPlayerBean(PropPlayerBean pb, boolean bind) {
        super(pb);
        this.bind = bind;
    }

    public boolean isBind() {
        return bind;
    }
}

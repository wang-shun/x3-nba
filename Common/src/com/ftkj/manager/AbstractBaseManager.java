package com.ftkj.manager;

/**
 *
 */
public abstract class AbstractBaseManager extends BaseManager {
    protected AbstractBaseManager() {
        super();
    }

    protected AbstractBaseManager(boolean init) {
        super(init);
    }

    @Override
    public void instanceAfter() {
    }
}

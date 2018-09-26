package com.ftkj.x3.client.task;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author luch
 */
public class ParamTestContext extends TestContext {
    private final TestParams base;

    public ParamTestContext(TestParams base) {
        this.base = base;
    }

    public TestParams getBase() {
        return base;
    }

    public ThreadLocalRandom getTlr() {
        return base.getTlr();
    }

    public int sleep() {
        return sleep(base);
    }
}

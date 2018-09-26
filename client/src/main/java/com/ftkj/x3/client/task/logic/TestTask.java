package com.ftkj.x3.client.task.logic;

import com.ftkj.x3.client.proto.Ret;
import org.springframework.stereotype.Component;

/**
 * 客户端逻辑测试.
 *
 * @author luch
 */
@Component
public class TestTask extends LogicTask {

    public static void main(String[] args) {
        new TestTask().run();
    }

    @Override
    protected Ret run0(String[] args) {
        return succ();
    }
}

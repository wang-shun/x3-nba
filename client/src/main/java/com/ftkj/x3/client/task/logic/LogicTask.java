package com.ftkj.x3.client.task.logic;

import com.ftkj.console.ConfigConsole;
import com.ftkj.enums.EConfigKey;
import com.ftkj.x3.client.X3RunnableTask;
import com.ftkj.x3.client.X3SpringConfig;

/**
 * @author luch
 */
public abstract class LogicTask extends X3RunnableTask {

    public final void run() {
        run(EMPTY_ARGS, true);
    }

    public final void run(String[] args) {
        run(args, true);
    }

    public final void run(String[] args, boolean autoClose) {
        run(X3SpringConfig.PROFILE_LOGIC_TASK, args, autoClose);
    }

    public static int getConfigInt(EConfigKey key) {
        return ConfigConsole.getIntVal(key);
    }
}

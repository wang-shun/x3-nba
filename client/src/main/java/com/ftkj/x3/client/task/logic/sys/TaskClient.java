package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.console.TaskConsole;
import com.ftkj.proto.TaskPB;
import com.ftkj.proto.TaskPB.TaskData;
import com.ftkj.proto.TaskPB.TaskMain;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 模板 client.
 *
 * @author luch
 */
@Component
public class TaskClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(TaskClient.class);

    public static void main(String[] args) {
        new TaskClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        ClientUser cu = uc.user();
        return moduleTest(uc, cu);
    }

    public Ret moduleTest(UserClient uc, ClientUser cu) {
        return succ();
    }

    public TaskMain showTaskMain(UserClient uc, ClientUser cu) {
        TaskMain resp = uc.req(uc, cu, ServiceCode.TaskManager_showTaskMain, TaskPB.TaskMain.getDefaultInstance());
        log.info("task tid {} trunk {} daily {} starNum {} starId {}", cu.tid(), resp.getTrunkListCount(),
            resp.getDayListCount(), resp.getStarNum(), resp.getStarAwardIdCount());
        if (log.isDebugEnabled()) {
            logAllTask(cu, resp);
        }
        return resp;
    }

    private void logAllTask(ClientUser cu, TaskMain resp) {
        StringBuilder sb = new StringBuilder();
        sb.append("trunk\n");
        for (TaskData td : resp.getTrunkListList()) {
            sb.append(shortDebug(td)).append(" type ").append(TaskConsole.getTaskBean(td.getTid()).getType().name()).append("\n");
        }
        sb.append("daily\n");
        for (TaskData td : resp.getDayListList()) {
            sb.append(shortDebug(td)).append(" type ").append(TaskConsole.getTaskBean(td.getTid()).getType().name()).append("\n");
        }
        sb.append("startId ").append(resp.getStarAwardIdList());
        log.debug("task tid {} tasks {}", cu.tid(), sb);
    }

}

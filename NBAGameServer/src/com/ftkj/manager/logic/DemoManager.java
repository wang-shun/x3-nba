package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.UnCheck;
import com.ftkj.db.ao.logic.DemoAO;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.CloseOperation;
import com.ftkj.server.ServiceCode;

/**
 * Manager案例
 *
 * @author tim.huang
 * 2015年12月11日
 */
public class DemoManager extends BaseManager implements CloseOperation {
    private int num = 0;
    @IOC
    private DemoAO demoAO;

    @Override
    public void close() throws Exception {
    }

    public void helloQuartz() {
        //		log.error("Quartz is run.[{}]",num++);
    }

    @ClientMethod(code = ServiceCode.DemoManager_dbTest)
    @UnCheck
    public void dbTest(String name) {
        long teamId = getTeamId();
        //		demoAO.testLongList();
        //		sendMessage(DemoPB.DemoDataMain.newBuilder().setName(name).setNum(0).build());
    }

    @Override
    public void instanceAfter() {

    }
}

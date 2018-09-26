package com.ftkj.x3.client;

import com.ftkj.x3.client.X3SpringConfig.AppConfig;
import com.ftkj.xxs.client.XxsClientConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author luch
 */
public class SpringTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.getEnvironment().setActiveProfiles(X3SpringConfig.PROFILE_LOGIC_TASK);
        ctx.refresh();
        //init
        XxsClientConfig cc = ctx.getBean(XxsClientConfig.class);
        System.out.println(cc.toString());
    }
}

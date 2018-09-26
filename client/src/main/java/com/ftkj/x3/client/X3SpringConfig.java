package com.ftkj.x3.client;

import com.codahale.metrics.MetricRegistry;
import com.ftkj.x3.client.net.X3ClientChannelHolder;
import com.ftkj.x3.client.util.X3ClientMessageMetric;
import com.ftkj.x3.net.X3MessageMetric;
import com.ftkj.xxs.client.SpringConfig;
import com.ftkj.xxs.core.util.XxsThreadFactory;
import com.ftkj.xxs.util.config.X3ConfigFactory;
import com.typesafe.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.Executors;

/**
 * spring config
 *
 * @author luch
 */
public final class X3SpringConfig {
    public static final String PROFILE_LOGIC_TASK = "logictask";
    public static final String PROFILE_ROBOT = "robot";
    public static final String PROFILE_BENCHMARK = "benchmark";
    public static final String PROFILE_generate = "generate";

    @Configuration
    @ComponentScan("com.ftkj.x3.client")
    @Import({ProfileLogicTask.class, ProfileRobot.class})
    public static class AppConfig implements SpringConfig {
        /**
         * Obtains the default application-specific configuration,
         * which defaults to parsing <code>client.conf</code>,
         * <code>client.json</code>, and
         * <code>client.properties</code> on the classpath, but
         * can also be rerouted using the <code>x3.config.file</code>,
         * <code>x3.config.resource</code>, and <code>x3.config.url</code>
         * system properties.
         */
        @Bean
        public Config config() {
            //            System.out.println("load config");
            return X3ConfigFactory.load("client.dev");
        }

        @Bean
        public ClientConfig clientConfig(Config config) {
            //            System.out.println("init client config");
            //                    + config.getConfig("x3").root().render(
            //                    ConfigRenderOptions.defaults().setComments(false)
            //                            .setOriginComments(false)
            //            ));
            return new ClientConfig(config.getConfig("x3"));
        }

        @Bean
        public X3ClientChannelHolder ClientChannelHolder() {
            return new X3ClientChannelHolder();
        }

        @Bean
        public MetricRegistry metricRegistry() {
            return new MetricRegistry();
        }

        @Bean
        public X3MessageMetric clientMessageMetric(MetricRegistry registry) {
            return new X3ClientMessageMetric(registry,
                    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new XxsThreadFactory("metric")));
        }

    }

    @Configuration
    @Profile(PROFILE_LOGIC_TASK)
    public static class ProfileLogicTask {

    }

    @Configuration
    @Profile(PROFILE_LOGIC_TASK)
    public static class ProfileRobot {

    }
}


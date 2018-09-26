package com.ftkj.x3.client;

import com.ftkj.x3.client.LoggerLineNumT.Obj1.Obj11;
import com.ftkj.xxs.core.logging.InternalLogger;
import com.ftkj.xxs.core.logging.InternalLoggerFactory;
import org.apache.logging.log4j.util.StackLocatorUtil;

/**
 * @author luch
 */
public class LoggerLineNumT {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(LoggerLineNumT.class);

    public static void main(String[] args) {
        //        test();
        StackTraceElement element = new Obj11().line();
        print(element);
        log.info("main LoggerLineNumT");
    }

    private static void test() {
        StackTraceElement element = StackLocatorUtil.calcLocation(LoggerLineNumT.class.getName());
        print(element);
    }

    private static void print(StackTraceElement element) {
        System.out.println(element);
        System.out.println(element.getLineNumber());
    }

    public static final class Obj1 {
        public static final class Obj11 {
            public StackTraceElement line() {
                String fqcn = Obj11.class.getName();
                return StackLocatorUtil.calcLocation(fqcn);
            }
        }
    }

    public static final class Obj2 {

    }

    public static final class Obj3 {}
}

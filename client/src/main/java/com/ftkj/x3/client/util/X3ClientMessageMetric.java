package com.ftkj.x3.client.util;

import com.codahale.metrics.MetricRegistry;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceCode.Code;
import com.ftkj.x3.net.X3MessageMetric;
import com.ftkj.xxs.core.util.XxsThreadFactory;
import com.ftkj.xxs.net.stats.AlignFormat;
import com.ftkj.xxs.net.stats.MessageSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author luch
 */
public class X3ClientMessageMetric extends X3MessageMetric {
    private static final Logger log = LoggerFactory.getLogger(X3ClientMessageMetric.class);

    public X3ClientMessageMetric(MetricRegistry registry, ExecutorService executorService) {
        super(registry, executorService);
    }

    public X3ClientMessageMetric(MetricRegistry registry, ExecutorService executorService, String globalPrefix, int unknownCode, int allCode) {
        super(registry, executorService, globalPrefix, unknownCode, allCode);
    }

    @Override
    protected Iterator<ClientCodeStat> initAllCodeStats() {
        Collection<Code> codes = Code.getAllCode();
        log.info("code num {}", codes.size());
        Iterator<Code> it = codes.iterator();
        return new Iterator<ClientCodeStat>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public ClientCodeStat next() {
                Code code = it.next();
                return new ClientCodeStat(code.getCode(),
                        ServiceCode.simpleName(code.getCodeName()));
            }
        };
    }

    public static void main(String[] args) throws Exception {
        X3MessageMetric mm = new X3ClientMessageMetric(new MetricRegistry(),
                Executors.newFixedThreadPool(4, new XxsThreadFactory("metric")));
        mm.init();

        log.info("add msg stat...");
        addTestMsgStat(mm);
        log.info("add msg stat done");
        mm.gracefulShutdownNow(10);
        log.info("shutdown");
        MessageSnapshot ms = mm.getSnapshot();
        Thread.sleep(500);

        Locale locale = Locale.getDefault();
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMaximumFractionDigits(2);
        AlignFormat lf = new AlignFormat(true, false, false, 10, nf, locale);
        String stat = lf.formatString(ms);

        System.out.println(stat);
        log.info("关闭");

    }

    private static void addTestMsgStat(X3MessageMetric ms) {

        ms.addSendMsgStat(ServiceCode.GameManager_loginPC, 67);
        ms.addReceivedMsgStat(ServiceCode.GameManager_loginPC, 23, 33);

        ms.addSendMsgStat(ServiceCode.GameManager_loadData, 2);
        ms.addReceivedMsgStat(ServiceCode.GameManager_loadData, 1424, 53);

        ms.addSendMsgStat(ServiceCode.GMManager_addMoney, 27);
        ms.addReceivedMsgStat(ServiceCode.Push_Money, 27);
        ms.addReceivedMsgStat(ServiceCode.GMManager_addMoney, 48, 52);

        ms.addSendMsgStat(ServiceCode.ScoutManager_showRollPlayer, 2);
        ms.addReceivedMsgStat(ServiceCode.ScoutManager_showRollPlayer, 64, 4);

        ms.addSendMsgStat(ServiceCode.GMManager_addPlayer, 128);

        ms.addSendMsgStat(89003, 28);
        ms.addReceivedMsgStat(20023, 24);
        ms.addReceivedMsgStat(89003, 48, 44);

        ms.addSendMsgStat(31207, 9);
        ms.addReceivedMsgStat(20023, 26);
        ms.addReceivedMsgStat(34115, 18);
        ms.addReceivedMsgStat(31207, 78, 44);

        ms.addSendMsgStat(89003, 28);
        ms.addReceivedMsgStat(20023, 24);
        ms.addReceivedMsgStat(89003, 48, 43);

        ms.addSendMsgStat(ServiceCode.Battle_Start_Push, 28);
        ms.addSendMsgStat(ServiceCode.Push_Custom_Money, 28);
        ms.addSendMsgStat(ServiceCode.Push_Friend, 28);
        ms.addSendMsgStat(ServiceCode.Push_Money, 28);
        ms.addSendMsgStat(ServiceCode.Push_Arena_Attack_Team, 28);
        ms.addSendMsgStat(ServiceCode.Push_Arena_Steal_Team, 28);
        ms.addSendMsgStat(ServiceCode.Push_League_Honor_Update, 28);
        ms.addSendMsgStat(ServiceCode.Battle_Round_Push, 256);

        ms.addSendMsgStat(ServiceCode.RMatch_Join_Pool, 4);
        ms.addReceivedMsgStat(ServiceCode.RMatch_Join_Pool, 12, 2);
        ms.addSendMsgStat(ServiceCode.RMatch_Info, 4);
        ms.addReceivedMsgStat(ServiceCode.RMatch_Info, 8, 2);
        ms.addSendMsgStat(ServiceCode.RMatch_Rank, 4);
        ms.addReceivedMsgStat(ServiceCode.RMatch_Rank, 24, 1414);

        //
        //        ms.addSendMsgStat(31207, 9);
        //        ms.addReceivedMsgStat(20023, 26);
        //        ms.addReceivedMsgStat(31207, 90, 48);
        //
        //        ms.addSendMsgStat(31201, 2);
        //        ms.addReceivedMsgStat(31201, 108, 12);
        //
        //        ms.addSendMsgStat(31501, 2);
        //        ms.addReceivedMsgStat(31501, 565, 3);
        //
        //        ms.addSendMsgStat(31503, 5);
        //        ms.addReceivedMsgStat(100001, 27);
        //        ms.addReceivedMsgStat(100001, 27);
        //        ms.addReceivedMsgStat(20006, 28);
        //        ms.addReceivedMsgStat(31503, 15, 54);
        //
        //        ms.addSendMsgStat(31503, 5);
        //        ms.addReceivedMsgStat(100001, 27);
        //        ms.addReceivedMsgStat(100001, 27);
        //        ms.addReceivedMsgStat(20006, 26);
        //        ms.addReceivedMsgStat(31503, 15, 46);
        //
        //        ms.addSendMsgStat(31504, 6);
        //        ms.addReceivedMsgStat(100001, 27);
        //        ms.addReceivedMsgStat(100300, 16);
        //        ms.addReceivedMsgStat(31504, 15, 43);
        //
        //        ms.addSendMsgStat(31504, 6);
        //        ms.addReceivedMsgStat(100001, 27);
        //        ms.addReceivedMsgStat(100300, 18);
        //        ms.addReceivedMsgStat(31504, 15, 42);

    }
}

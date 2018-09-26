package com.ftkj.console;

import com.ftkj.db.domain.bean.DropBeanVO;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tim.huang
 * 2016年3月14日
 * 掉落配置控制台
 */
public class DropConsole extends AbstractConsole {

    private static Map<Integer, DropBean> dropMap;

    public static void init(List<DropBeanVO> dropBeanList) {
        dropMap = new HashMap<>();
        for (DropBeanVO po : dropBeanList) {
            DropBean db = dropMap.get(po.getDropId());
            if (db == null) {
                db = DropBean.newDrop(po.getDropId(), po.getDropType());
                dropMap.put(po.getDropId(), db);
            }
            db.appendDropProp(po, PropConsole.getProp(po.getPropId()));
        }
    }

    public static DropBean getDrop(int dropId) {
        return dropMap.get(dropId);
    }

    public static List<PropSimple> getAndRoll(int dropId) {
        DropBean db = dropMap.get(dropId);
        return db != null ? db.roll() : Collections.emptyList();
    }

    public static boolean containsKey(int dropId) {
        return dropMap.containsKey(dropId);
    }

    public static void validate(int dropId, String msg, Object... msgArgs) {
        validate(Collections.singletonList(dropId), msg, msgArgs);
    }

    public static void validate(List<Integer> drops, String msg, Object... msgArgs) {
        String preMsg = String.format(msg, msgArgs);
        for (Integer dropId : drops) {
            if (dropId > 0 && getDrop(dropId) == null) {
                throw exception(String.format(preMsg + "掉落包 %s 没有配置", dropId));
            }
        }
    }
}

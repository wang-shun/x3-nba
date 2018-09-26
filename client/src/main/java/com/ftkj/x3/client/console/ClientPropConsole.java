package com.ftkj.x3.client.console;

import com.ftkj.console.PropConsole;
import com.ftkj.enums.EPropType;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.prop.bean.PropSimpleBean;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luch
 */
@Component
public class ClientPropConsole {
    private List<Integer> allIds;
    private int size;
    private Map<Integer, PropSimpleBean> moneyProps = Collections.emptyMap();

    public void init() {
        allIds = ImmutableList.copyOf(PropConsole.getPropMap().keySet());
        size = allIds.size();

        Map<Integer, PropSimpleBean> moneyProps = new HashMap<>();
        for (PropBean pb : PropConsole.getPropMap().values()) {
            EPropType type = EPropType.getEPropType(pb.getPropType());
            switch (type) {
                case Common:
                    PropSimpleBean psb = (PropSimpleBean) pb;
                    if (psb.getPlayerExp() > 0) {
                        moneyProps.put(pb.getPropId(), psb);
                    }
                    break;
            }
        }

        this.moneyProps = ImmutableMap.copyOf(moneyProps);
    }

    public int getSize() {
        return size;
    }

    public List<Integer> getAllIds() {
        return allIds;
    }

    public Map<Integer, PropSimpleBean> getMoneyProps() {
        return moneyProps;
    }

    public PropSimpleBean getMaxPlayerExpProp(int exp) {
        PropSimpleBean max = null;
        for (PropSimpleBean pm : moneyProps.values()) {
            if (pm.getPlayerExp() > exp) {
                return pm;
            }
            if (max == null || pm.getPlayerExp() > max.getPlayerExp()) {
                max = pm;
            }
        }
        return max;
    }
}

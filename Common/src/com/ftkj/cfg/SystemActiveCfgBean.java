package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropAwardConfig;
import com.ftkj.manager.prop.PropRandom;
import com.ftkj.manager.prop.PropRandomSet;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.PropSimpleSet;
import com.ftkj.util.excel.RowData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SystemActiveCfgBean extends ExcelBean {

    private int atvId;
    private int id;
    /**
     * 球券直接完成，>0可以直接购买奖励
     */
    private int buyFinish;
    /**
     * 可重复领奖次数
     */
    private int repeated;
    private String awardType = "";
    private Map<String, String> conditionMap;
    private List<PropAwardConfig> awardList;
    /**
     * 抽奖专用类型
     */
    private PropRandomSet propRandomSet;

    @Override
    public void initExec(RowData row) {
        if (awardType.equals("PropSimple")) {
            this.awardList = PropSimple.getPropBeanByStringNotConfig(row.get("award"))
                .stream().map(s -> (PropAwardConfig) s).collect(Collectors.toList());
        } else if (awardType.equals("PropRandom")) {
            this.awardList = PropRandomSet.getPropBeanByStringNotConfig(row.get("award"))
                .stream().map(s -> (PropAwardConfig) s).collect(Collectors.toList());
            this.propRandomSet = new PropRandomSet(this.getPropRandomList());
        } else if (awardType.equals("PropActiveData")) {
            this.awardList = PropSimple.getPropBeanByStringNotConfig(row.get("award"))
                .stream().map(s -> (PropAwardConfig) s).collect(Collectors.toList());
        } else if (awardType.equals("PropSimpleSet")) { // 每天一种奖励
            this.awardList = Lists.newArrayList();
            for (String aCfg : row.get("award").toString().split("_")) {
                this.awardList.add(new PropSimpleSet(PropSimple.getPropBeanByStringNotConfig(aCfg)));
            }
        } else {
            this.awardList = Lists.newArrayList();
        }
        //
        String config = row.get("condition");
        if (config != null && !config.trim().equals("")) {
            String[] s = config.trim().split(",");
            this.conditionMap = Maps.newHashMap();
            for (String c : s) {
                String[] k = c.split("=");
                this.conditionMap.put(k[0], k[1]);
            }
        }
    }

    /**
     * 根据类型，每种奖励类型提供一种返回
     *
     * @return
     */
    public List<PropSimple> getPropSimpleList() {
        return awardList.stream().map(s -> (PropSimple) s).collect(Collectors.toList());
    }

    /**
     * 抽奖奖励封装，一般不对外
     *
     * @return
     */
    private List<PropRandom> getPropRandomList() {
        return awardList.stream().map(s -> (PropRandom) s).collect(Collectors.toList());
    }

    public List<PropAwardConfig> getAwardList() {
        return awardList;
    }

    public int getAtvId() {
        return atvId;
    }

    public void setAtvId(int atvId) {
        this.atvId = atvId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, String> getConditionMap() {
        if (conditionMap == null) {
            return Maps.newHashMap();
        }
        return conditionMap;
    }

    public void setConditionMap(Map<String, String> conditionMap) {
        this.conditionMap = conditionMap;
    }

    public String getAwardType() {
        return awardType;
    }

    public void setAwardType(String awardType) {
        this.awardType = awardType;
    }

    /**
     * 强制取绝对值，避免填返了增加球券
     *
     * @return
     */
    public int getBuyFinish() {
        return Math.abs(buyFinish);
    }

    @Override
    public String toString() {
        return "SystemActiveCfgBean [atvId=" + atvId + ", id=" + id + ", conditionMap=" + conditionMap + "]";
    }

    public PropRandomSet getPropRandomSet() {
        return propRandomSet;
    }

    public int getRepeated() {
        return repeated;
    }

    public void setRepeated(int repeated) {
        this.repeated = repeated;
    }

}

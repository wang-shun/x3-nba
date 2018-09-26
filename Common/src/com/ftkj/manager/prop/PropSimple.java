package com.ftkj.manager.prop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年2月16日
 * 简单的道具对象，一般用来临时存放物品基础信息
 */
public class PropSimple extends PropAwardConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(PropSimple.class);
    /** 道具ID*/
    private int propId;
    /** 道具数量*/
    private int num;  
    /** 道具有效时间*/
    private int hour;
    /** 道具配置*/
    private String config;

    public PropSimple() {
    }

    public PropSimple(PropSimple ps) {
        this.propId = ps.getPropId();//get 获取子类信息
        this.num = ps.getNum();
        this.hour = ps.getHour();
        this.config = ps.getConfig();
    }

    public PropSimple(int propId, int num) {
        super();
        this.propId = propId;       
        this.num = num;
        this.config = "0";
    }      

    public PropSimple(int propId, int num, int hour) {
        super();
        this.propId = propId;
        this.num = num;
        this.hour = hour;
        this.config = "0";
    }

    public PropSimple(int propId, int num, int hour, String config) {
        super();
        this.propId = propId;
        this.num = num;
        this.hour = hour;
        if ("".equals(config)) { config = "0"; }
        this.config = config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }

    /**
     * 配置格式： [道具ID：数量]
     * 格式错误会返回空
     */
    public static PropSimple getPropSimpleByString(String awards) {
        if (awards == null || awards.trim().equals("") || !awards.contains(":")) { return null; }
        String[] cfg = awards.split(":");
        return new PropSimple(Integer.valueOf(cfg[0]), Integer.valueOf(cfg[1]));
    }

    /**
     * 无时限配置
     */
    public static List<PropSimple> getPropBeanByString(String awards) {
        List<PropSimple> propList = new ArrayList<>();
        if (awards.length() > 0 && !awards.equals("0")) {
            String[] t1 = StringUtil.toStringArray(awards, StringUtil.DEFAULT_ST);
            for (String temp : t1) {
                if (temp.equals("")) {
                    continue;
                }
                String[] t2 = StringUtil.toStringArray(temp, StringUtil.DEFAULT_FH);
                propList.add(new PropSimple(Integer.parseInt(t2[0]), Integer.parseInt(t2[1]), 0, t2[2]));
            }
        }
        return propList;
    }

    public static List<PropSimple> parseItems(String itemsStr) {
        return getPropBeanByStringNotConfig(itemsStr);
    }

    public static List<PropSimple> getPropBeanByStringNotConfig(String awards) {
        return getPropBeanByStringNotConfig(awards, StringUtil.DEFAULT_ST, StringUtil.DEFAULT_FH);
    }

    public static List<PropSimple> getPropBeanByStringNotConfig(String awards, String sp, String sl) {
        List<PropSimple> propList = new ArrayList<>();
        if (awards != null && awards.length() > 0 && !awards.equals("0")) {
            String[] t1 = StringUtil.toStringArray(awards, sp);
            for (String temp : t1) {
                if (temp.equals("")) {
                    continue;
                }
                String[] t2 = StringUtil.toStringArray(temp, sl);
                try {
                    propList.add(new PropSimple(Integer.parseInt(t2[0]), Integer.parseInt(t2[1]), 0, ""));
                } catch (Exception e) {
                    log.error("propsimple parse error. awards {} sp {} sl {} t2 {}", awards, sp, sl, Arrays.toString(t2));
                }
            }
        }
        return propList;
    }

    /**
     * 放大倍数
     */
    public static List<PropSimple> getPropListMult(List<PropSimple> propList, int beishu) {
        List<PropSimple> list = Lists.newArrayList();
        for (PropSimple s : propList) {
            list.add(new PropSimple(s.getPropId(), s.getNum() * beishu));
        }
        return list;
    }

    /**
     * 重复道具数量合成
     */
    @SafeVarargs
    public static List<PropSimple> getPropListComposite(List<PropSimple>... listarr) {
        Map<Integer, PropSimple> propMap = Maps.newHashMap();
        for (List<PropSimple> list : listarr) {
            if (list == null || list.isEmpty()) {
                continue;
            }
            for (PropSimple ps : list) {
                if (!propMap.containsKey(ps.getPropId())) {
                    propMap.put(ps.getPropId(), new PropSimple(ps));
                } else {
                    propMap.get(ps.getPropId()).addNum(ps.getNum());
                }
            }
        }
        return new ArrayList<>(propMap.values());
    }

    public static List<PropSimple> conertItem(Map<Integer, Integer> idAndNum) {
        List<PropSimple> props = new ArrayList<>();
        if (idAndNum == null || idAndNum.isEmpty()) {
            return props;
        }
        idAndNum.forEach((id, num) -> props.add(new PropSimple(id, num)));
        return props;
    }

    public static String getPropStringByList(List<PropSimple> list) {
        if (list == null || list.size() <= 0) {
            return "";
        }
        String result = list.stream()
                .map(ps -> ps.getPropId() + ":" + ps.getNum() + ":" + ps.getConfig())
                .collect(Collectors.joining(","));
        return result;
    }

    public static String getPropStringByListNotConfig(List<PropSimple> list) {
        if (list == null || list.size() <= 0) {
            return "";
        }
        String result = list.stream()
                .map(ps -> ps.getPropId() + ":" + ps.getNum())
                .collect(Collectors.joining(","));
        return result;
    }

    public static String getPropString(PropSimple ps) {
        String result = ps.getPropId() + ":" + ps.getNum() + ":" + ps.getConfig();
        return result;
    }

    public static String getPropStringNotConfig(PropSimple ps) {
        String result = ps.getPropId() + ":" + ps.getNum();
        return result;
    }

    /** 同类物品，数量叠加 */
    public static Map<Integer, PropSimple> mergeProps(List<PropSimple> props) {
        Map<Integer, PropSimple> mergedProps = Maps.newHashMap();
        mergeProps(props, mergedProps);
        return mergedProps;
    }

    /** 同类物品，数量叠加 */
    public static void mergeProps(List<PropSimple> props, Map<Integer, PropSimple> mergedProps) {
        if (props == null || props.isEmpty()) {
            return;
        }
        for (PropSimple award : props) {
            mergeProps(award, mergedProps);
        }
    }

    /** 同类物品，数量叠加 */
    public static void mergeProps(PropSimple props, Map<Integer, PropSimple> mergedProps) {
        mergedProps.computeIfAbsent(props.getPropId(), key -> new PropSimple(props.getPropId(), 0))
                .addNum(props.getNum());
    }

    public int getHour() {
        return hour;
    }

    public int getPropId() {
        return propId;
    }

    public int getNum() {
        return num;
    }

    public void addNum(int num) {
        this.num += num;
    }    

    public void setNum(int num) {
        this.num = num;
    }


    @Override
    public String toString() {
        return "{" +
                "\"rid\":" + propId +
                ", \"num\":" + num +
                '}';
    }

    @Override
    public List<PropSimple> getPropSimpleList() {
        return Lists.newArrayList(this);
    }
}

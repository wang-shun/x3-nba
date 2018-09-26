package com.ftkj.manager.prop;

import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 道具随机项
 * @author Jay
 * @time:2018年2月6日 上午11:28:57
 */
public class PropRandomSet implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(PropRandomSet.class);
    private static final long serialVersionUID = 1L;

    
    private List<PropRandom> propList;
    private Map<Integer, Integer> rateMap;
    
    /**
     * 格式：   道具ID：数量：概率  [,...]
     * @param config
     */
    public static List<PropRandom> getPropBeanByStringNotConfig(String awards) {
    	List<PropRandom> propList = Lists.newArrayList();
        if (awards.length() > 0 && !awards.equals("")) {
            String[] t1 = StringUtil.toStringArray(awards, StringUtil.DEFAULT_ST);
            for (String temp : t1) {
                if (temp.equals("")) {
                    continue;
                }
                String[] t2 = StringUtil.toStringArray(temp, StringUtil.DEFAULT_FH);
                propList.add(new PropRandom(Integer.parseInt(t2[0]), Integer.parseInt(t2[1]), Integer.parseInt(t2[2])));
            }
        }
        return propList;
	}
    
    public PropRandomSet(List<PropRandom> propList) {
    	this.propList = propList;
    	initRateMap();
    }
    
    /**
     * 特殊算法继承重写
     */
    private void initRateMap() {
    	rateMap = Maps.newHashMap();
    	for(int i=0; i<this.propList.size(); i++) {
    		rateMap.put(i, this.propList.get(i).getRate());
    	}
    }
    
    /**
     * 随机算法
     * 特殊随机算法请继承重写
     * @return
     */
    private int randIndex() {
    	return RandomUtil.randMap(rateMap);
    }
    
    /**
     * 补偿概率，也可以拿来做限量
     * @param adjustMap 限量，某下标：负概率
     * @return
     */
    private int randIndex(Map<Integer, Integer> adjustMap) {
    	return RandomUtil.randMap(rateMap, adjustMap);
    }
    
    /**
     * 限量随机
     * @param continueSet 过滤
     * @return 
     */
    private int randIndex(Set<Integer> continueSet) {
    	if(continueSet == null || continueSet.size() == 0) {
    		return RandomUtil.randMap(rateMap);
    	}
    	Map<Integer, Integer> nMap = Maps.newHashMap(rateMap);
    	for(int key : continueSet) {
    		nMap.remove(key);
    	}
    	log.warn("限量抽奖下标={}, 概率={}", continueSet, nMap);
    	return RandomUtil.randMap(nMap);
    }
    
    /**
     * 随机一次
     * @return
     */
    public PropRandomHit random(Map<Integer, Integer> adjustMap) {
    	int hitIndex = randIndex(adjustMap);
    	return propList.get(hitIndex).copyNew(hitIndex);
    }
    /**
     * 随机一次
     * @return
     */
    public PropRandomHit random() {
    	int hitIndex = randIndex();
    	return propList.get(hitIndex).copyNew(hitIndex);
    }
    
    public PropRandomHit random(Set<Integer> continueSet) {
    	int hitIndex = randIndex(continueSet);
    	return propList.get(hitIndex).copyNew(hitIndex);
    }
    
    /**
     * 随机指定次数
     * @param num
     * @return
     */
    public List<PropRandomHit> random(int num) {
    	List<PropRandomHit> list = Lists.newArrayList();
    	for(int i=0; i<num; i++) {
    		list.add(random());
    	}
    	return list;
    }
    
}

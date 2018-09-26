package com.ftkj.console;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.MMatchConditionBean;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.cfg.honor.HonorDivBean;
import com.ftkj.cfg.honor.HonorLevBean;
import com.google.common.collect.ImmutableMap;

/**
 * 球星荣耀
 * @author zehong.he
 *
 */
public class HonorMatchConsole extends AbstractConsole implements ValidateBean {
  
    private static final Logger log = LoggerFactory.getLogger(HonorMatchConsole.class);
    
    /** 赛区. map[赛区配置id, Bean] */
    private static Map<Integer, HonorDivBean> divs = Collections.emptyMap();
    /** 关卡. map[关卡配置id, Bean] */
    private static Map<Integer, HonorLevBean> levs = Collections.emptyMap();
    /** 胜利条件. map[条件配置id, Bean] */
    private static Map<Integer, MMatchConditionBean> winConditions = Collections.emptyMap();
    /** 赛区和关卡. map[赛区id, set[关卡id]] */
    private static Map<Integer, Set<HonorLevBean>> levsOfDiv = Collections.emptyMap();
    //key:divId,value:maxLv
    private static Map<Integer, Integer> maxlevsOfDiv = new HashMap<>();

    public static int minDivId = Integer.MAX_VALUE;
    
    public static void init() {
        Map<Integer, HonorDivBean> divs = new LinkedHashMap<>();
        Map<Integer, HonorLevBean> levs = new LinkedHashMap<>();
        Map<Integer, Set<HonorLevBean>> levsOfDiv = new LinkedHashMap<>();
        ImmutableMap.Builder<Integer, MMatchConditionBean> wcs = ImmutableMap.builder();
        maxlevsOfDiv = new HashMap<>();
        minDivId = Integer.MAX_VALUE;
        
        for (HonorDivBean divb : CM.hMatchDivs) {
            divs.put(divb.getId(), divb);
            if(minDivId > divb.getId()) {
              minDivId = divb.getId();
            }
        }
        for (HonorLevBean levb : CM.hMatchLevs) {
            levs.put(levb.getId(), levb);
            levsOfDiv.computeIfAbsent(levb.getDivId(), levId -> new LinkedHashSet<>()).add(levb);
            Integer maxLv = maxlevsOfDiv.get(levb.getDivId());
            if(maxLv == null) {
              maxlevsOfDiv.put(levb.getDivId(), levb.getId());
            }else {
              if(levb.getId() > maxLv) {
                maxlevsOfDiv.put(levb.getDivId(), levb.getId());
              }
            }
            
        }
        for (MMatchConditionBean wc : CM.hMatchWcs) {
            wcs.put(wc.getId(), wc);
        }

        HonorMatchConsole.divs = ImmutableMap.copyOf(divs);
        HonorMatchConsole.levs = ImmutableMap.copyOf(levs);
        HonorMatchConsole.levsOfDiv = ImmutableMap.copyOf(levsOfDiv);
        HonorMatchConsole.winConditions = wcs.build();
        log.debug("divs {} levs {} wcs {}", divs.size(), levs.size(), winConditions.size());
    }

    @Override
    public void validate() {
      
    }


    public static int getMaxLvIDByDivId(int divId) {
      return maxlevsOfDiv.get(divId) ==  null ? 0 : maxlevsOfDiv.get(divId);
    }
    
    public static HonorDivBean getDivBean(int divId) {
        return divs.get(divId);
    }

    public static Map<Integer, Set<HonorLevBean>> getLevsOfDiv() {
        return levsOfDiv;
    }

    public static Set<HonorLevBean> getLevsOfDiv(int divId) {
        return levsOfDiv.get(divId);
    }

    public static HonorLevBean getLevBean(int levId) {
        return levs.get(levId);
    }

    public static Map<Integer, MMatchConditionBean> getWinConditions() {
        return winConditions;
    }

    public static MMatchConditionBean getWinCondition(int winConditionId) {
        return winConditions.get(winConditionId);
    }
}

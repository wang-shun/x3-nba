package com.ftkj.manager.system;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ftkj.console.PropConsole;
import com.ftkj.enums.EMoneyType;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.prop.bean.PropBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @Description:验证工具类
 * @author Jay
 * @time:2017年3月23日 上午10:49:38
 */
public class CheckAPI {

	
	/**
	 * 字符串分割多个id的参数解析
	 * @param ids
	 * @return
	 */
	public static int[] converStringIds(String ids) {
		String[] idStr = (ids==null?"":ids).split("[,]");
		if(idStr.length < 1) {
			return new int[]{};
		}
		return Arrays.stream(idStr).mapToInt(id-> Integer.valueOf(id)).toArray();
	}
	
	/**
	 * 通用
	 * 参数去<0参数，必须是>=0，重复数量汇总
	 * 参数转换，考虑有重复的，所以汇总数量
	 * @return
	 */
	public static Map<Integer, Integer> converParamToMap(int... ids) {
		Map<Integer, Integer> map = Maps.newHashMap();
		Arrays.stream(ids).filter(id -> id > 0).forEach(p-> {
					if(map.containsKey(p)) {
						map.put(p, map.get(p)+1);
					}else {
						map.put(p, 1);
					}
				});
		return map;
	}
	
	/**
	 * 道具专用
	 * 道具参数去0，重复数量汇总
	 * 道具参数转换，考虑有重复的道具ID，所以汇总数量
	 * @param props
	 * @return
	 */
	public static List<PropSimple> converPropParamToMap(int... props) {
		Map<Integer, Integer> map = Maps.newHashMap();
		Arrays.stream(props).filter(prop -> prop > 0 && PropConsole.checkIsProp(prop)).forEach(p-> {
					if(map.containsKey(p)) {
						map.put(p, map.get(p)+1);
					}else {
						map.put(p, 1);
					}
				});
		List<PropSimple> list = Lists.newArrayList();
		for(int pid : map.keySet()) {
			list.add(new PropSimple(pid, map.get(pid)));
		}
		return list;
	}
	
	/**
	 * 检查用户的道具需求是否满足，包括货币
	 * 一般检查条件调用
	 * @param props  被验证道具列表，一定不能为空，空直接返回false
	 * @param teamProp
	 * @return
	 */
	public static boolean checkTeamPropNum(List<PropSimple> props, TeamProp teamProp, TeamMoney teamMoney) {
		if(teamProp == null || teamMoney == null || props == null || props.size() == 0) {
			return false;
		}
		for(PropSimple ps : props) {
			// 货币
			if(PropConsole.isMoney(ps.getPropId())) {
				PropBean bean = PropConsole.getProp(ps.getPropId());
				EMoneyType moneyType = EMoneyType.getMoneyByName(bean.getConfig().split(":")[0]);
				if(!teamMoney.hasMoney(moneyType, ps.getNum())) {
					return false;
				}
			}
			// 道具
			else if(!teamProp.checkPropNum(ps)) {
				return false;
			}
		}
		return true;
	}
}

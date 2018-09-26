package com.ftkj.console;

import java.util.List;
import java.util.Map;

import com.ftkj.db.domain.bean.DraftRoomVO;
import com.ftkj.manager.draft.DraftRoomBean;
import com.ftkj.manager.prop.PropSimple;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年5月8日
 * 选秀控制台
 */
public class DraftConsole {
	
	private static Map<Integer,DraftRoomBean> roomMap;
	
	
	public static DraftRoomBean getDraftRoomBean(int roomLevel){
		return roomMap.get(roomLevel);
	}
	
	public static List<DraftRoomBean> getDraftList(){
		return Lists.newArrayList(roomMap.values());
	}
	
	public static void init(List<DraftRoomVO> list){
		roomMap = Maps.newHashMap();
		list.forEach(room->roomMap.put(room.getRoomLevel(),
				new DraftRoomBean(room.getRoomLevel(),room.getMaxPlayerCount(), room.getLimitMinLevel(), 
						room.getLimitMaxLevel(), DropConsole.getDrop(room.getDropId()), 
						PropSimple.getPropBeanByStringNotConfig(room.getProps()), room.getMaxRoom(), room.getCdTime(), room.getTime()
						))
				);
	}
	
}

package com.ftkj.manager.gym;

import com.ftkj.enums.EArenaRollType;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ArenaRoll {
	private static final Logger log = LoggerFactory.getLogger(ArenaRoll.class);
	private int rid;
	private List<ArenaRollItem> list;
	private int totalP;
	
	public int getRid() {
		return rid;
	}


	public ArenaRoll(int rid,List<ArenaRollItem> list) {
		super();
		this.rid = rid;
		this.list = Lists.newArrayList();
		list.forEach(item->appendItem(item));
	}

	public void appendItem(ArenaRollItem item){
		this.list.add(item);
		this.totalP += item.getP();
	}
	
	public ArenaRollItem roll(){
		int ran = RandomUtil.randInt(this.totalP);
		int start=0,end=0;
		ArenaRollItem item = null;
		for(int i =0 ; i < this.list.size();i++){
			item = this.list.get(i);
			end += item.getP();
			if(ran>=start && ran<end){
				return item;
			}
			start = end;
		}
		log.error("Exception-转盘概率随机出现问题---->{}", this.rid);
		return this.list.get(0);
	}
	
	public ArenaRollItem gmRoll(EArenaRollType type){
		return this.list.stream().filter(item->item.getType()==type).findFirst().orElse(null);
	}
	

	
	
	
}

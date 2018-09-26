package com.ftkj.manager.prop;

import java.util.List;

public class PropSimpleSet extends PropAwardConfig {

	private List<PropSimple> propList;
	
	
	public PropSimpleSet(List<PropSimple> propList) {
		super();
		this.propList = propList;
	}

	@Override
	public List<PropSimple> getPropSimpleList() {
		return propList;
	}

}

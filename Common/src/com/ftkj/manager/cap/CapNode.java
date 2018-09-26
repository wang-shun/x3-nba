package com.ftkj.manager.cap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ftkj.enums.ECapModule;
import com.google.common.collect.Lists;

public class CapNode {

	// 树的每个节点对应的下标
	//private int index;
	private ECapModule type;
	private List<CapNode> childList = Lists.newArrayList();
	private boolean isRoot = false;
	
	public CapNode(ECapModule type, boolean isRoot) {
		this.isRoot = isRoot;
		this.type = type;
	}
	public CapNode(ECapModule type) {
		this.type = type;
	}
	
	public ECapModule getType() {
		return type;
	}
	
	public List<CapNode> getChildList() {
		return childList;
	}
	public boolean isRoot() {
		return isRoot;
	}
	/**
	 * 添加子集
	 * @param childList
	 */
	public void addChildList(ECapModule... childList) {
		if(childList != null) {
			this.childList.addAll(Arrays.stream(childList).map(n-> new CapNode(n)).collect(Collectors.toList()));
		}
	}
}

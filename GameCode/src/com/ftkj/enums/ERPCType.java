package com.ftkj.enums;

public enum ERPCType {
	ALL,//池组内所有节点
	MASTER,//池组内的主节点
	NONE,//默认分配模式,根据分配机制,取池集群中的某个节点 ##revices=null是负载推送
	ALLNODE;//所有池中的所有节点
}

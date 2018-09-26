package com.ftkj.ao;

import com.ftkj.invoker.ResourceCache;

public class BaseAO {
	
	public BaseAO(){
		ResourceCache.get().init(this);
	}
	
}

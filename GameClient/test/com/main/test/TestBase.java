package com.main.test;

import com.ftkj.console.CM;
import com.ftkj.manager.common.CacheManager;

public class TestBase {

	static {
		CM.init(false);
		new CacheManager().resetCache();
	} 
	
}

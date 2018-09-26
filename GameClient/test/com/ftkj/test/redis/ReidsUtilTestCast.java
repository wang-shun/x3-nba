package com.ftkj.test.redis;

import java.util.HashMap;
import java.util.Map;

import com.ftkj.db.domain.match.MatchBestPO;
import com.ftkj.manager.match.MatchBest;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.tool.redis.Jredis;

public class ReidsUtilTestCast {

	public static void main(String[] args) {
	   Jredis j = new Jredis();
	   j.setPort(6379);
	   j.setConnectionCount(2);
	   j.setDatabase(0);
	   j.setHost("192.168.10.181");
	   j.setPassword("zgame2017");
	   JedisUtil util = new JedisUtil(j);
	   
	   Map<Integer, MatchBest> map = new HashMap<>();
	   map.put(1, new MatchBest(new MatchBestPO(1, 1, 1)));
	   util.putMapAllValue("aaa111", null);
//	   System.err.println(util.getMapAllKeyValues("aaa111").toString());
	}
	
}

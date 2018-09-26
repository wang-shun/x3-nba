package com.ftkj;

import com.ftkj.manager.BaseManager;
import com.ftkj.tool.redis.JedisUtil;

public class Test extends BaseManager
 {
    
    protected static JedisUtil redis;
    
    
    public void main(String[] args) {       
//        redis.zadd(RedisKey.Train_Refrush_List, 1, 10);
//        redis.zadd(RedisKey.Train_Refrush_List, 2, 20);
//        redis.zadd(RedisKey.Train_Refrush_List, 3, 30);
//        redis.zadd(RedisKey.Train_Refrush_List, 4, 40);
//        redis.zadd(RedisKey.Train_Refrush_List, 5, 50);
//        redis.zadd(RedisKey.Train_Refrush_List, 6, 60);
//        redis.zadd(RedisKey.Train_Refrush_List, 7, 70);
//        
//        Set<String> capSet =  redis.zrevrangeByScore(RedisKey.Train_Refrush_List, 60, 10, 0, 6);
//        
//        while(capSet.iterator().hasNext()) {
//            String cap = capSet.iterator().next();
//            
//            System.out.println("cap :" + cap);
//            
//        }
      
       // redis.zrem(RedisKey.Train_Refrush_List, 1+ "");
    }
	
    @Override
    public void instanceAfter() {
        // TODO Auto-generated method stub
        
    }
}
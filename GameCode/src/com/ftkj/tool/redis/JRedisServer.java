package com.ftkj.tool.redis;


/**
 * redis服务
 * @author tim.huang
 * 2015年12月16日
 */
//@Deprecated
public class JRedisServer {
//	
//	JRedisService jredis;
//	static Logger log = LoggerFactory.getLogger(JRedisServer.class);
//	
//	public JRedisServer(Jredis j){
////		jredis = new JRedisService(j.getHost(),j.getPort(),j.getPassword(),j.getDatabase(),j.getConnectionCount());
//	}
//	
//	public JRedisService getRedis(){
//		return jredis;
//	}	
//	
//	public Map<String,String> info(){
//		try{
//			return jredis.info();
//		}catch(Throwable e){
//			return new HashMap<String,String>();
//		}
//	}	
//
//	//确认一个key是否存在
//	public boolean exits(String key){
//		try {
//			boolean ret =  jredis.exists(key);
//			return ret;
//		} catch (Throwable e) {
//			log.error("exits:"+key, e);
//			return false;
//		}
//	}
//
//	//删除一个key
//	public void del(String key){
//		try {
//			jredis.del(key);
//		} catch (Throwable e) {
//			log.error("del:"+key, e);
//		}
//	}	
//
//	//给数据库中名称为key的string赋予值value
////	public void set(String key,String str){
////		try {
////			jredis.set(key, str);
////		} catch (Throwable e) {
////			log.error("set:"+key, e);
////		}
////	}
////
////	public void set(String key,Integer str){
////		try {
////			
////			jredis.set(key, str);
////			
////		} catch (Throwable e) {
////			log.error("set:"+key, e);
////		}
////	}
//	
//	public <T extends Serializable> void set(String key, T obj, int expire){
//		try {
//			if(obj instanceof String){
//				jredis.set(key, ""+obj);
//			}else if(obj instanceof Integer){
//				jredis.set(key, ""+obj);
//			}else{
//				jredis.set(key, obj);
//			}
//			setTimeOut(key,expire);
//		} catch (Throwable e) {
//			log.error("set:"+key, e);
//			throw new IllegalStateException(e);
//		}
//	}
//	
//	public <T extends Serializable> void set(String key, T obj){
//		try {
//			jredis.set(key, obj);
//		} catch (Throwable e) {
//			log.error("set:"+key, e);
//			throw new IllegalStateException(e);
//		}
//	}
//
//
//	public void setTimeOut(String key,int time){		
//		try{
//			//
//			jredis.expire(key, time);
//			//
//		}catch (Throwable e) {
//			log.error("setTimeOut:"+key, e);
//		}
//	}
//
//	//返回数据库中名称为key的string的value
//	public String getStr(String key){
//		try {
//			
//			byte[] date = jredis.get(key);
//			String res = null;
//			if(date!=null){
//				res = new String(date);
//			}
//			
//			return res;
//		} catch (Throwable e) {
//			log.error("getStr:"+key, e);
//			return null;
//		}
//	}
//	//返回数据库中名称为key的string的value
//	public String getStr2(String key){
//		try {
//			
//			byte[] date = jredis.get(key);
//			String res = null;
//			if(date!=null){
//				res = new String(date);
//			}
//			
//			return res;
//		} catch (Throwable e) {
//			throw new IllegalStateException(e);
//		}
//	}
//
//	public <T extends Serializable>T getObj(String key){
//		try {
//			
//			byte[] date = jredis.get(key);
//			T res = null;
//			if(date!=null){
//				res = (T)DefaultCodec.decode(date);
//			}
//			return res;
//		} catch (Throwable e) {
//			log.error("getObj:"+key, e);
//			return null;
//		}
//	}
//	
//	public long getLong(String key){
//		try {
//			
//			byte[] date = jredis.get(key);
//			long res = 0;
//			if(date!=null){
//				res = DefaultCodec.toLong(date);
//			}
//			return res;
//		} catch (Throwable e) {
//			log.error("getObj:"+key, e);
//			return 0;
//		}
//	}
//
//	//对List操作的命令	
//	public <T extends Serializable>void pushxx(String key,List<T> obj,int expire){
//		try{
//			jredis.set(key, new ListObj<T>(obj));
//			setTimeOut(key,expire);
//		}catch (Throwable e) {
//			log.error("push:"+key, e);
//		}
//	}
//	
//	//对List操作的命令	
//	public <T extends Serializable>void pushxx(String key,List<T> obj){
//		try{
//			jredis.set(key, new ListObj<T>(obj));
//		}catch (Throwable e) {
//			log.error("push:"+key, e);
//		}
//	}
//
//	//取数据	
//	public <T extends Serializable>List<T> listxx(String key){
//		Object ob = getObj(key);
//		if(ob == null){
//			return null;
//		}
//		ListObj<T> res = (ListObj<T>)ob ;
//		return res.getList();			
//	}
//
//	//取数据	
//	public <T extends Serializable>List<T> list(String key){
//			return lrange(key,0,-1);
//	}
//
//	public <T extends Serializable>void push(String key,List<T> obj){
//		synchronized(key){
//			//先删除	
//			del(key);
//			//一个个放入
//			for(T t:obj){
//				rpush_notcheck(key,t);
//			}			
//		}
//	}
//
//	public <T extends Serializable>void rpush_notcheck(String key, T value){
//		try {
//			//
//			jredis.rpush(key, value);			
//			//
//		} catch (Throwable e) {
//			log.error("rpush:"+key, e);
//		}
//	}
//
//	//rpush(key, value)：在名称为key的list尾添加一个值为value的元素
//	public <T extends Serializable>void rpush(String key, T value){
//		try {			
//			if(exits(key)){
//				
//				jredis.rpush(key, value);
//								
//			}
//		} catch (Throwable e) {
//			log.error("rpush:"+key, e);
//		}
//	}	
//	//lpush(key, value)：在名称为key的list头添加一个值为value的 元素
//	public <T extends Serializable>void lpush(String key, T value){
//		try {			
//			if(exits(key)){
//				
//				jredis.lpush(key, value);
//								
//			}
//		} catch (Throwable e) {
//			log.error("lpush:"+key, e);
//		}
//	}
//	//llen(key)：返回名称为key的list的长度
//	public int llen(String key){
//		try {
//			
//			int res = (int) jredis.llen(key);
//			return res;
//		} catch (Throwable e) {
//			log.error("llen:"+key, e);
//			return -1;
//		}
//	}
//	//lrange(key, start, end)：返回名称为key的list中start至end之间的元素（下标从0开始，下同）
//	public <T extends Serializable>List<T> lrange(String key, int start, int end){
//		try {
//			if(exits(key)){
//				List<T> res = DefaultCodec.decode(jredis.lrange(key, start, end));
//				return res;
//			}else{
//				return new ArrayList<T>(1);
//			}
//		} catch (Throwable e) {
//			log.error("lrange:"+key, e);
//			return null;
//		}
//	}
//	
//	public long incr(String key){
//		try {
//			return jredis.incr(key);
//		} catch (RedisException e) {
//		}
//		return 0;
//	}
//	
//	public long incr(String key ,int timeout){
//		try {
//			long num = incr(key);
//			if(!jredis.exists(key))
//				setTimeOut(key, timeout);
//			return num;
//		} catch (RedisException e) {
//		}
//		return 0;
//	}
//	
//	public void zadd(String key,int score,Serializable val){
//		try {
//			jredis.zadd(key, score, val);
//		} catch (RedisException e) {
//		}
//	}
//	
//	public <T extends Serializable> List<T> zget(String key,int start,int end){
//		try {
//			return DefaultCodec.decode(jredis.zrevrange(key, start, end));
//		} catch (RedisException e) {
//		}
//		return null;
//	}
//	
//	
//	
	
}

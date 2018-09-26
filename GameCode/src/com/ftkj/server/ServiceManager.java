package com.ftkj.server;

import com.ftkj.manager.Service;
import com.ftkj.manager.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务订阅管理
 * @author tim.huang
 * 2015年12月8日
 */
public class ServiceManager {
	private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
	private static Map<String,Service> SERVICE_MAP = new ConcurrentHashMap<String, Service>();
	
	/**
	 * 离线移除服务订阅
	 * @param teamId
	 */
	public static void offline(long teamId){
		User user = GameSource.getUser(teamId);
		List<Service> services = new ArrayList<Service>(SERVICE_MAP.values());
		for(Service service : services){
			service.remove(user);
		}
	}
	
	public static Set<User> getUsers(String key){
		Service service = SERVICE_MAP.get(key);
		if(service == null)
			return new HashSet<User>(1);
		return service.getUsers();
	}
	
	public synchronized static void addService(String key,long teamId){
		if(GameSource.isNPC(teamId)) return;
		User user = GameSource.getUser(teamId);
		Service service = SERVICE_MAP.get(key);
		if(service == null){
			service = new Service();
			SERVICE_MAP.put(key, service);
		}
		service.putUser(user);
	}
	
	public static void removeService(String key,long teamId){
		User user = GameSource.getUser(teamId);
		Service service = SERVICE_MAP.get(key);
		if(service != null){
			service.remove(user);
		}
	}
	
	public static void clearTimeOutService(){
		Set<String> keys = SERVICE_MAP.keySet();
		Service service = null;
		for(String key : keys){
			service = SERVICE_MAP.get(key);
			if(service.size()<=0){
				SERVICE_MAP.remove(key);
				log.debug("service remove key[{}]",key);
			}
		}
	}
	
	public static void removeService(String key){
		SERVICE_MAP.remove(key);
	}
	
}

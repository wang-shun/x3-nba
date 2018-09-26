

import java.nio.charset.Charset;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.google.common.collect.Lists;


/**
 * @author tim.huang
 * 2017�?3�?15�?
 */
public class ZookeepServer {
	
	private ZooKeeper zookeeper;
//	private RPCServer server;
	
	public static final String Logic = "/node";
	public static final String Config = "/config";
	public static final String Route = "/route";
	public static final String Master = "/master";
	
	private static ZookeepServer zkServer;
	
	
	
	public ZookeepServer(ZookeepConfig config,Watcher watcher) throws Exception{
		zookeeper = new ZooKeeper(config.getIpPort(), 3000, watcher);
	}
	
	public static ZookeepServer getInstance(){
		return zkServer;
	}
	
	
	
	public String create(String node,String msg){
		try {
			String path = zookeeper.create(node, msg.getBytes(Charset.forName("UTF8")), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			return path;
		} catch (Exception e) {
			return "";
		}  
	}
	public String create(String node,byte[] msg){
		try {
			String path = zookeeper.create(node, msg, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return path;
		} catch (Exception e) {
			return "";
		} 
	}
	
	public String get(String node){
		String msg="";
		try {
			msg = new String(zookeeper.getData(node, true, null));
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	public byte[] getBytes(String node){
		byte[] msg= null;
		try {
			msg = zookeeper.getData(node, true, null);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	public Stat exists(String node,Watcher watcher){
		try {
			return zookeeper.exists(node, watcher);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	public Stat exists(String node){
		try {
			return zookeeper.exists(node, true);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getChild(String node,Watcher watcher) {
		List<String> nodeList = Lists.newArrayList();
		try {
			nodeList = zookeeper.getChildren(node, watcher);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
		return nodeList;
	}
	
	public List<String> getChild(String node){
		List<String> nodeList = Lists.newArrayList();
		try {
			nodeList = zookeeper.getChildren(node, true);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
		return nodeList;
	}
	
	public void del(String node){
		try {
			zookeeper.delete(node, -1);
		} catch (InterruptedException | KeeperException e) {
			e.printStackTrace();
		}
	}
	
	public void set(String node,String data){
		try {
			zookeeper.setData(node, data.getBytes(Charset.forName("UTF8")), -1);
		} catch (InterruptedException | KeeperException e) {
			e.printStackTrace();
		}
	}
	public void set(String node,byte[] data){
		try {
			zookeeper.setData(node, data, -1);
		} catch (InterruptedException | KeeperException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			zookeeper.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

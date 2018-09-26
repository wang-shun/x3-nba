package com.ftkj.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Before;

import com.ftkj.ClientHandlerTestCase;
import com.ftkj.ExcuteReturnData;
import com.ftkj.GoogleCodecFactoryTestCase;
import com.ftkj.proto.Mo;
import com.ftkj.proto.ProtoType;
import com.ftkj.proto.Response;

/**
 * @author Marc.Wang 2011-9-22 上午10:19:46
 * 功能：
 */
public abstract class BaseServiceTestCase extends TestCase 
implements ExcuteReturnData{
	//
	private IoSession session;
	private String KEY = "Ω"; 
	//
	@Before
	public void setUp(){
		NioSocketConnector connector = new NioSocketConnector(1);
		connector.getSessionConfig().setTcpNoDelay(true);
		
		
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new GoogleCodecFactoryTestCase(this)));
		connector.setHandler(new ClientHandlerTestCase());
		// 建立连接 192.168.11.176 localhost
		ConnectFuture cf = connector.connect(new InetSocketAddress(
				"192.168.11.178", 8038));
		// 等待连接创建完成
		cf.awaitUninterruptibly();
		session = cf.getSession();
	}
	
	//
	public void sendData(ProtoType type,String... params){
		Map<String,String> m = new HashMap<String, String>();
		if(params!=null&&params.length>1){
			  for(int i=0;i<params.length;i+=2){
				  m.put(params[i], params[i+1]);
			  }
		}
		String str = mapToString(m);
		Mo.MoData  data = Mo.MoData.newBuilder()
		  .setService(type.getService())
		  .setData(str)
		  .build();
		
		Response res  = new Response(type,data);
		  //res.setData(data);
		  res.setReqid(1111);
		  
		session.write(res);
	}
	
	
	//
	private String mapToString(Map<String,String> m){
		StringBuilder str = new StringBuilder("");
		for(String key : m.keySet()){
			str.append(key+"="+m.get(key).toString()+KEY);
		}
		if(str.length()>1){
			return str.substring(0, str.length()-1);
		}
		return "";
	}

	@Override
	public Object getObject(int type,InputStream is, boolean isZip) throws IOException{
		return transToObject(type,is,isZip);
	}	
	//将响应结果转换成数据
	abstract Object transToObject(int type,InputStream is, boolean isZip) throws IOException;
}

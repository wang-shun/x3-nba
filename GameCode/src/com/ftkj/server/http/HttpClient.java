package com.ftkj.server.http;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import java.net.URLDecoder;

public class HttpClient {
	
	public static String post(String url,String...postData) throws Exception{
		Request re = Request.Post(url);
		Form f = Form.form();
		if(postData!=null && postData.length%2==0){
			for(int i =0;i<postData.length; ){
				f.add(postData[i++],postData[i++]);
			}	
		}
		return re.body(new UrlEncodedFormEntity(f.build(), "utf-8")).connectTimeout(2000).socketTimeout(2000).execute().returnContent().asString();
	}
	
	public static String postAndDecode(String url,String...postData) throws Exception{
		String res = post(url, postData);
		return URLDecoder.decode(res, "utf-8");
	}
	
	
	public static String get(String url,String...getData) throws Exception{
//		Form f = Form.form();
		StringBuffer sb = new StringBuffer();
		if(getData!=null && getData.length%2==0){
			for(int i =0;i<getData.length; ){
				sb.append(getData[i++]);
				sb.append("=");
				sb.append(getData[i++]);
				sb.append("&");
			}	
		}
		if(sb.length()>0)
			url = url + sb.substring(0,sb.length()-1);
		Request re = Request.Get(url);
		return re.connectTimeout(10000).socketTimeout(10000).execute().returnContent().asString();
//		return re.body(new UrlEncodedFormEntity(f.build(), "utf-8")).connectTimeout(2000).socketTimeout(2000).execute().returnContent().asString();
	}
	
	public static void main(String[] args) {
		try {
			String msg = HttpClient.get("http://192.168.12.84:8084/xgame/recharge/create?price=10&teamId=50001&shardId=101");
			System.err.println(msg);
		} catch (Exception e) {
		}
	}
	
	
	
	
	
}

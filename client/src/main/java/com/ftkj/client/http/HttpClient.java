package com.ftkj.client.http;

import java.net.URLDecoder;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import com.google.gson.Gson;


public class HttpClient {

	public static void main(String[] args) throws Exception {
		FTXLogin f = new FTXLogin(101,10110100,"token_123456_特殊字符测试+ /?%#&=","userId_123456","{\"ex\":\"ex\"}");
		Gson g = new Gson();
		String js = g.toJson(f);
		System.err.println(js);
		String is = Request.Post("http://192.168.12.73:8080/sdk/verifyTokenPlf")
				 .bodyForm(Form.form().add("data",  js)
				 .build())
				 .execute().returnContent().asString();
		String rjs = URLDecoder.decode(is,"utf-8");
		System.err.println("["+rjs+"]");
		FTXLoginResponse res = g.fromJson(rjs, FTXLoginResponse.class);
		System.err.println("--"+res.getUserId());
	}

}

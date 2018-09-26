package com.ftkj.service;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.ftkj.proto.ProtoType;
public class ChatServiceTestCase extends BaseServiceTestCase {
	
	//
	@Test
	public void testLogin(){
		sendData(ProtoType.gm_topic, 
				"msg","[:eq:]删档测试期间，注册就送500球卷。222222");
	}
	
	@Override
	Object transToObject(int type,InputStream is, boolean isZip) throws IOException {
		// TODO Auto-generated method stub
		return "";
	}

}

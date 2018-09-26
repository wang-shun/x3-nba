package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.AtvAnswerMt;
/**
 * @author Marc.Wang 2012-5-25 上午11:06:45
 * 功能：答题活动ServiceTestCase
 */
public class AtvAnswerServiceTestCase extends BaseServiceTestCase {
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.atvAswShowView);
		Thread.sleep(3 * 1000);
	}
	//
	@Override
	public Object transToObject(int type, InputStream is, boolean isZip)
	throws IOException {
		Object obj = "";
		if(type==ProtoType.atvAswShowView.getService()){
			obj = isZip?AtvAnswerMt.AtvAnswerData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						AtvAnswerMt.AtvAnswerData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}

}

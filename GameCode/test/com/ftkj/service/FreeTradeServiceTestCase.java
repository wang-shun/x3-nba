package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.FreeTradeMt;

public class FreeTradeServiceTestCase extends BaseServiceTestCase {
	
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.freTradeList);
		Thread.sleep(3 * 1000);
	}
	
	@Override
	Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.freTradeList.getService()){
			obj = isZip?FreeTradeMt.FreeTradeDataList.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						FreeTradeMt.FreeTradeDataList.parseFrom(new DataInputStream(is));
		}
		return obj;
	}

}

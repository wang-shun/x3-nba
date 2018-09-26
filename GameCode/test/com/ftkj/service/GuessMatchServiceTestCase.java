package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.Default;
import com.ftkj.proto.mt.GuessMatchMt;

public class GuessMatchServiceTestCase extends BaseServiceTestCase {
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.gusMatchShowView);
		Thread.sleep(3 * 1000);
	}
	
	@Override
	public Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.gusMatchShowView.getService()){
			obj = isZip?GuessMatchMt.GuessMatchDataList.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						GuessMatchMt.GuessMatchDataList.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.gusMatchExctGus.getService()){
			obj = isZip?Default.DefaultData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						Default.DefaultData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}

}

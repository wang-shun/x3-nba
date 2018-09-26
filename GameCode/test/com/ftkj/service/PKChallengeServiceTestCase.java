package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.PKChallengeMt;

public class PKChallengeServiceTestCase extends BaseServiceTestCase{
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.pkcleshowView);
		Thread.sleep(3 * 1000);
	}
	
	@Override
	public Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.pkcleshowView.getService()){
			obj = isZip?PKChallengeMt.MyPKChallengeData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						PKChallengeMt.MyPKChallengeData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}

}

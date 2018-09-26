package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.ChristmasMt;
import com.ftkj.proto.mt.PKAuctionMt;

public class PKAuctionServiceTestCase extends BaseServiceTestCase {
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030406224","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.pkAutShowView);
		Thread.sleep(3 * 1000);
	}
	
	@Override
	Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.pkAutShowView.getService()){
			obj = isZip?PKAuctionMt.PKAuctionData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						PKAuctionMt.PKAuctionData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}

}

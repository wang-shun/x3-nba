package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.BidPlayerMt;
import com.ftkj.proto.mt.Default;

public class BidPlayerServiceTestCase extends BaseServiceTestCase {
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.bidplayershow);
		Thread.sleep(3 * 1000);
	}

	@Override
	Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.bidplayershow.getService()){
			obj = isZip?BidPlayerMt.BidPlayerDataList.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						BidPlayerMt.BidPlayerDataList.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.bidexecuteBid.getService()){
			obj = isZip?Default.DefaultData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						Default.DefaultData.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.bidshowMyBid.getService()){
			obj = isZip?BidPlayerMt.MyBidPlayerMoneyData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						BidPlayerMt.MyBidPlayerMoneyData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}
}

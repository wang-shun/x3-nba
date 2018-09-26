package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.PKAuctionMt;
import com.ftkj.proto.mt.PKMazeMt;

/**
 * @author Marc.Wang 2012-3-28 下午03:44:19
 * 功能：
 */
public class PKMazeServiceTestCase extends BaseServiceTestCase {
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030406224","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.pkMazeShowView);
		Thread.sleep(3 * 1000);
	}
	//
	@Override
	public Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.pkMazeShowView.getService()){
			obj = isZip?PKMazeMt.PKMazeData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						PKMazeMt.PKMazeData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}

}

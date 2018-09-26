package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import org.junit.Test;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.Default;
import com.ftkj.proto.mt.PKLaddersMt;

/**
 * @author Marc.Wang 2011-11-8 下午07:39:58
 * 功能：测试天梯赛接口
 */
public class PKLaddersServiceTestCase extends BaseServiceTestCase {
	
	@Test
	public void testShowView() throws InterruptedException{
		//102030405061
		
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		//
		Thread.sleep(3 * 1000);
		//
		//sendData(ProtoType.pkldquitJoin);
		//
		sendData(ProtoType.pkldrshowView,"teamId","101102030405226");
		Thread.sleep(3 * 1000);
		
	}
	
	
	//
	@Override
	public Object transToObject(int type,InputStream is, boolean isZip) throws IOException {
		Object data = "";
		if(type==ProtoType.pkldrshowView.getService()){
			data = isZip?PKLaddersMt.MyPKLaddersData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						PKLaddersMt.MyPKLaddersData.parseFrom(new DataInputStream(is));
			
		}else if(type==ProtoType.pkldrjoin.getService()){
			data = isZip?Default.DefaultData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						Default.DefaultData.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.pkldpkRoomList.getService()){
			data = isZip?PKLaddersMt.PKLaddersRoomList.parseFrom(
					new InflaterInputStream(new DataInputStream(is))):
						PKLaddersMt.PKLaddersRoomList.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.pkldshowResult.getService()){
			data = isZip?PKLaddersMt.PKLaddersResultData.parseFrom(
					new InflaterInputStream(new DataInputStream(is))):
						PKLaddersMt.PKLaddersResultData.parseFrom(new DataInputStream(is));
		}
		return data;
	}

}

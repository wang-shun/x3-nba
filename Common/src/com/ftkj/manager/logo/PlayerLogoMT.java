package com.ftkj.manager.logo;

import java.util.List;
import java.util.Map;

import com.ftkj.manager.logo.bean.Logo;
import com.ftkj.manager.logo.bean.PlayerLogo;
import com.ftkj.proto.PlayerLogoPB;
import com.google.common.collect.Lists;

public class PlayerLogoMT {

	/**
	 * 取所有头像协议消息
	 * @param teamLogo
	 * @param luckyValue
	 * @return
	 */
	public static PlayerLogoPB.PlayerLogoData getPlayerLogoData(TeamPlayerLogo teamLogo, int luckyValue) {
		com.ftkj.proto.PlayerLogoPB.PlayerLogoData.Builder dt = PlayerLogoPB.PlayerLogoData.newBuilder();
		List<PlayerLogoPB.LogoHonorData> dataList = Lists.newArrayList();
		Map<PlayerLogo, List<Logo>> playerLogoMap = teamLogo.getPlayerTheLogoList();
		for(PlayerLogo playerLogo : playerLogoMap.keySet()) {
			List<Logo> logoList = playerLogoMap.get(playerLogo);
			dataList.add(getLogoHonorData(playerLogo, logoList));
		}
		dt.addAllHonorList(dataList);
		dt.setLucky(luckyValue);
		return dt.build();
	}
	
	public static List<PlayerLogoPB.LogoData> getLogoListData(List<Logo> list) {
		List<PlayerLogoPB.LogoData> datas = Lists.newArrayList();
		for(Logo logo:list) {
			datas.add(getLogoData(logo));
		}
		return datas;
	}
	
	public static PlayerLogoPB.LogoData getLogoData(Logo logo) {
		return PlayerLogoPB.LogoData.newBuilder()
				.setId(logo.getLogoId())
				.setPlayerId(logo.getPlayerId())
				.setQuality(logo.getQuality()).build();
	}
	
	public static PlayerLogoPB.LogoHonorData getLogoHonorData(PlayerLogo plogo, List<Logo> list) {
		return PlayerLogoPB.LogoHonorData.newBuilder()
				.addAllLogoList(getLogoListData(list))
				.setPlayerId(plogo.getPlayerId())
				.setLogoId(plogo.getLogoId())
				.setLv(plogo.getLv())
				.setStep(plogo.getStep())
				.build();
	}
}

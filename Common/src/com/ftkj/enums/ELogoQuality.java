package com.ftkj.enums;

/**
 * @Description:荣誉头像品质枚举
 * @author Jay
 * @time:2017年3月15日 下午7:41:43
 */
public enum ELogoQuality {
	绿(1),
	蓝(2),
	紫(3),
	橙(4),
	红(5),
	金(6),
	;
	
	private int quality;

	private ELogoQuality(int quality) {
		this.quality = quality;
	}

	public int getQuality() {
		return quality;
	}
	
}

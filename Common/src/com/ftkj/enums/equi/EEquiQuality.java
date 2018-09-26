package com.ftkj.enums.equi;

public enum EEquiQuality {
	白色(0),
	绿色(1),
	蓝色(2),
	紫色(3),
	橙色(4),
	红色(5),
	金色(6)
	
	;
	
	public int quality;
	
	EEquiQuality(int quality) {
		this.quality = quality;
	}
}

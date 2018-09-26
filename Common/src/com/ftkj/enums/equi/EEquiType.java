package com.ftkj.enums.equi;

public enum EEquiType {
	头带(1),
	护腕(2),
	球衣(3),
	护膝(4),
	球鞋(5),
	;
	
	public int id;
	
	EEquiType(int id) {
		this.id = id;
	}
	
	public static boolean contains(int id){  
        for(EEquiType typeEnum : EEquiType.values()){  
            if(typeEnum.id == id){  
                return true;  
            }  
        }  
        return false;  
    } 
	
	public static EEquiType getByType(int type) {
		for(EEquiType t : EEquiType.values()) {
			if(t.id == type) {
				return t;
			}
		}
		return null;
	}
}

package com.ftkj.db.conn.dao;

public enum ResourceType {
	DB_data("DB_data"),	
	DB_game("DB_game"),	
	DB_nba("DB_nba"),	
	
	Jredis("Jredis"),	
	Zookeep("Zookeep"),	
	;
	
    private final String resName;   
    
    ResourceType(String resName) { 
        this.resName = resName;         
    } 

    public String getResName() { 
        return resName; 
    }    
   

}

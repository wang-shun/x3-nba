package com.ftkj.db.conn.ao;

public abstract class BaseAO {
	
	protected TranscationManager transcationManager;
	
	public BaseAO(){
		transcationManager = new TranscationManager();
	}

	
}

package com.ftkj.enums;

public enum ETrain {
	
    /** 训练类型*/
    /** 4小时*/
    TRAIN_TYPE_1(1), 
    /** 8小时*/
    TRAIN_TYPE_2(2),
   
    /**训练奖励-未领取*/
    TRAIN_RSTATE_0(0),
    /**训练奖励-可领取*/
    TRAIN_RSTATE_1(1),   
    
    /**训练位-个人位*/
    TRAIN_0(0),
    /**训练位-联盟位*/
    TRAIN_1(1),
    
    /**训练馆状态-空置*/
    TRAINING_0(0),
    /**训练馆状态-训练中*/
    TRAINING_1(1),
    /**训练馆状态-训练结束*/
    TRAINING_2(2),
    /**训练馆状态-关闭*/
    TRAINING_3(3),
    
    
    /**训练馆抢夺列表长度*/
    TRAIN_SHOW_LENGTH(6),
    
    ;
    
    private int id;
    
    private ETrain(int id) {
        this.id = id;        
    }
    
    public int getId() {
        return id;
    }
}

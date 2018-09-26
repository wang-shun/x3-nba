package com.ftkj.cfg;

public class EmailViewBean {

    private int id;
    private int type;
    private String title;
    private String content; // 用来计算参数个数；

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /** 天梯赛赛季排名奖励 */
    public static final int Ranked_Match_Season = 37001;
    /** 全明星排名奖励 */
    public static final int All_Star_Rank = 38001;
    /** 全明星击杀奖励 */
    public static final int All_Star_Kill = 38002;
    /** 竞技场. 排名奖励 */
    public static final int Arena_Rank = 39001;
    /** 极限挑战排名奖励*/
    public static final int limit_challenge_rank = 40012;
}

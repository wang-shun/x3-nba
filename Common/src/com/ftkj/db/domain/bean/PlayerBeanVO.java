package com.ftkj.db.domain.bean;

/**
 * @author tim.huang
 * 2017年3月3日
 * 球员配置
 */
public class PlayerBeanVO {	
    
	/** 球员配置ID*/
	private int playerId;
	/** 球队ID*/
	private int teamId;
	/** 球队名称*/
	private String name;
	/** 简称*/
	private String shortName;
	/** 位置*/
	private String position;
	/** 英文名*/
	private String enName;
	/** 等级*/
	private String grade;
	/** 工资帽*/
	private int price;
	/** 前一天工资*/
	private int beforePrice;
	/** 球员类型（0:普通 1:周最佳 2:月最佳)*/
	private int playerType;
	/** 伤病*/
	private int injured;

	/** 投篮 */
	private float fgm;
	/** 罚球 */
	private float ftm;
	/** 得分*/
	private float pts;
	/** 三分*/
	private float threePm;
	/** */
	private float threePa;
	/** 前蓝板*/
	private float oreb;
	/** 后篮板*/
	private float dreb;
	/** 助功 */
	private float ast;
	/** 抢断*/
	private float stl;
	/** 盖帽*/
	private float blk;
	/** 失误*/
	private float to;
	/** 时间*/
	private float min;
	/** 犯规*/
	private float pf;
	/** 技能效率*/
	private int skill;
	/** 进攻和*/
	private int attrCap;
	/** 防守和*/
	private int guaCap;
	/** 总能力*/
	private int cap;
	/** */
	private int beforeCap;
	/**
	 * 新秀数据，格式：2017_2_30  年份_轮数_顺位
	 */
	private String draft;
	
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public int getBeforeCap() {
		return beforeCap;
	}
	public void setBeforeCap(int beforeCap) {
		this.beforeCap = beforeCap;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public void setBeforePrice(int beforePrice) {
		this.beforePrice = beforePrice;
	}
	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}
	public void setInjured(int injured) {
		this.injured = injured;
	}
	public void setFgm(int fgm) {
		this.fgm = fgm;
	}
	public void setFtm(int ftm) {
		this.ftm = ftm;
	}
	public void setPts(int pts) {
		this.pts = pts;
	}
	public void setThreePm(int threePm) {
		this.threePm = threePm;
	}
	public void setOreb(int oreb) {
		this.oreb = oreb;
	}
	public void setDreb(int dreb) {
		this.dreb = dreb;
	}
	public void setAst(int ast) {
		this.ast = ast;
	}
	public void setStl(int stl) {
		this.stl = stl;
	}
	public void setBlk(int blk) {
		this.blk = blk;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public void setPf(int pf) {
		this.pf = pf;
	}
	public void setAttrCap(int attrCap) {
		this.attrCap = attrCap;
	}
	public void setGuaCap(int guaCap) {
		this.guaCap = guaCap;
	}
	public void setCap(int cap) {
		this.cap = cap;
	}
	public int getTeamId() {
		return teamId;
	}
	public String getName() {
		return name;
	}
	public String getShortName() {
		return shortName;
	}
	public String getPosition() {
		return position;
	}
	public String getGrade() {
		return grade;
	}
	public int getPrice() {
		return price;
	}
	public int getBeforePrice() {
		return beforePrice;
	}
	public int getPlayerType() {
		return playerType;
	}
	public int getInjured() {
		return injured;
	}
	public int getPlayerId() {
		return playerId;
	}
	
	public float getFgm() {
		return fgm;
	}
	public void setFgm(float fgm) {
		this.fgm = fgm;
	}
	public float getFtm() {
		return ftm;
	}
	public void setFtm(float ftm) {
		this.ftm = ftm;
	}
	public float getPts() {
		return pts;
	}
	public void setPts(float pts) {
		this.pts = pts;
	}
	public float getThreePm() {
		return threePm;
	}
	public void setThreePm(float threePm) {
		this.threePm = threePm;
	}

    public float getThreePa() {
        return threePa;
    }

    public void setThreePa(float threePa) {
        this.threePa = threePa;
    }

    public float getOreb() {
		return oreb;
	}
	public void setOreb(float oreb) {
		this.oreb = oreb;
	}
	public float getDreb() {
		return dreb;
	}
	public void setDreb(float dreb) {
		this.dreb = dreb;
	}
	public float getAst() {
		return ast;
	}
	public void setAst(float ast) {
		this.ast = ast;
	}
	public float getStl() {
		return stl;
	}
	public void setStl(float stl) {
		this.stl = stl;
	}
	public float getBlk() {
		return blk;
	}
	public void setBlk(float blk) {
		this.blk = blk;
	}
	public float getTo() {
		return to;
	}
	public void setTo(float to) {
		this.to = to;
	}
	public float getMin() {
		return min;
	}
	public void setMin(float min) {
		this.min = min;
	}
	public float getPf() {
		return pf;
	}
	public void setPf(float pf) {
		this.pf = pf;
	}
	public int getAttrCap() {
		return attrCap;
	}
	public int getGuaCap() {
		return guaCap;
	}
	public int getCap() {
		return cap;
	}
	public int getSkill() {
		return skill;
	}
	public void setSkill(int skill) {
		this.skill = skill;
	}
	public String getDraft() {
		return draft;
	}
	public void setDraft(String draft) {
		this.draft = draft;
	}
	
	
}

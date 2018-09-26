package com.ftkj.domain.data;

import java.io.Serializable;

public class NBAPlayerDetail implements Serializable{
	
	private static final long serialVersionUID = -3241726939891887806L;
	
	public static final String POS_C="C";
	public static final String POS_PF="PF";
	public static final String POS_SF="SF";
	public static final String POS_SG="SG";
	public static final String POS_PG="PG";
	
	private int playerId;
	private int espnId;
	private int teamId;
	private String name;
	private String ename;
	private String shortName;
	private String shortNameTw;
	private String shortNameEn;	
	private int number ;
	private String position;
	private String height ="";
	private String weight ="";
	private String school = "";
	private String birthday ="";
	private String nation ="";
	private String draft ="";
	private String salary;
	private String contract;
	private int price;
	private int beforePrice;
	private String grade;
	private int playerType;	
	private int injured;
	private int lowPrice;
	private int cap;
	private int beforeCap;
	private int attr;
	private int beforeAttr;
	private String grades;
	private String contractEn;
	private String contractTr;
	private String contractTw;
	private int plus = 0;
	
	/**0:中文版,1:英文版,2:土耳其版,3:台湾版*/
	public String getContract(int charset){
		String result="";
		if(charset==1) //英文版
			result= getContractEn()!=null?getContractEn():"";
		else  if(charset==0)//中文版
			result= getContract()!=null?getContract():"";
		else if(charset==2)
			result = getContractTr()!=null?getContractTr():"";
		else if(charset==3)
			result = getContractTw()!=null?getContractTw():"";
		return result;
	}	
	
	//状态常量
	public static int getPlusStat(int plus){
		if(plus<=-12)
			return -2;
		else if(plus>-12 && plus<=-5)
			return -1;
		else if(plus>5 && plus<=15)
			return 1;
		else if(plus>15)
			return 2;
		return 0;
	}
	
	public String getContractTw() {
		return contractTw;
	}
	public void setContractTw(String contractTw) {
		this.contractTw = contractTw;
	}
	public int getPlus() {
		return plus;
	}
	public void setPlus(int plus) {
		this.plus = plus;
	}
	public String getContractTr() {
		return contractTr;
	}
	public void setContractTr(String contractTr) {
		this.contractTr = contractTr;
	}
	public String getContractEn() {
		return contractEn;
	}
	public void setContractEn(String contractEn) {
		this.contractEn = contractEn;
	}
	public String getGrades() {
		return grades;
	}
	public void setGrades(String grades) {
		this.grades = grades;
	}
	public String getShortNameEn() {
		return shortNameEn;
	}
	public void setShortNameEn(String shortNameEn) {
		this.shortNameEn = shortNameEn;
	}
	public NBAPlayerDetail() {}
	
	public NBAPlayerDetail(int playerId,int teamId){
		this.playerId = playerId;
		this.teamId = teamId;
	}	
	public int getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(int lowPrice) {
		this.lowPrice = lowPrice;
	}
	public String getShortName() {
		if(shortName==null)return name;
		return shortName;
	}
	public int getInjured() {
		return injured;
	}	
	public String getShortNameTw() {
		return shortNameTw;
	}
	public void setShortNameTw(String shortNameTw) {
		this.shortNameTw = shortNameTw;
	}
	public void setInjured(int injured) {
		this.injured = injured;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public int getPlayerId() {
		return playerId;
	}	
	public int getPlayerType() {
		return playerType;
	}
	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getEspnId() {
		return espnId;
	}
	public void setEspnId(int espnId) {
		this.espnId = espnId;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getDraft() {
		return draft;
	}
	public int getDraftInt(){
		try{
			return new Integer(draft);
		}catch(Exception ex){
			return 0;
		}
	}
	public void setDraft(String draft) {
		this.draft = draft;
	}
	
	public String getSalary() {
		return salary;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getBeforePrice() {
		return beforePrice;
	}
	public void setBeforePrice(int beforePrice) {
		this.beforePrice = beforePrice;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
	    if(grade.equals("M"))grade="X";
		this.grade = grade;
	}
	public static String getPOS_C() {
		return POS_C;
	}
	public static String getPOS_PF() {
		return POS_PF;
	}
	public static String getPOS_SF() {
		return POS_SF;
	}
	public static String getPOS_SG() {
		return POS_SG;
	}
	public static String getPOS_PG() {
		return POS_PG;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}	
	public int getCap() {
		return cap;
	}
	public void setCap(int cap) {
		this.cap = cap;
	}
	
	public void setBeforeCap(int beforeCap) {
		this.beforeCap = beforeCap;
	}
	public int getBeforeCap() {
		if(beforeCap==0 && cap>0)return cap;
		return beforeCap;
	}
	
	public int getAttr() {
		return attr;
	}
	public void setAttr(int attr) {
		this.attr = attr;
	}
	
	public int getBeforeAttr() {
		if(beforeAttr==0 && attr>0)return attr;
		return beforeAttr;
	}
	public void setBeforeAttr(int beforeAttr) {
		this.beforeAttr = beforeAttr;
	}
		
	
}

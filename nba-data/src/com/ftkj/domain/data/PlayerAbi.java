package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

public class PlayerAbi implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2295103603837264721L;

	//public static final double OVERALL_WEIGHT[]={0.849,0.051,0.10};//2017-10-21
	//public static final double OVERALL_WEIGHT[]={0.84,0.06,0.10};//2014-11-14
	//public static final double OVERALL_WEIGHT[]={0.83,0.07,0.10};//2013-11-19 *
	//public static final double OVERALL_WEIGHT[]={0.82,0.08,0.10};//2014-11-25
	//public static final double OVERALL_WEIGHT[]={0.81,0.09,0.10};//2012-12-04 *
	//public static final double OVERALL_WEIGHT[]={0.80,0.10,0.10};//2014-12-05
	public static final double OVERALL_WEIGHT[]={0.79,0.11,0.10,0.3};//2014-12-16
	//public static final double OVERALL_WEIGHT[]={0.78,0.12,0.10};//2014-12-23
	//public static final double OVERALL_WEIGHT[]={0.77,0.13,0.10};//2014-12-30
	//public static final double OVERALL_WEIGHT[]={0.76,0.14,0.10};//2015-01-09
	//public static final double OVERALL_WEIGHT[]={0.75,0.15,0.10};//2015-01-20
	//public static final double OVERALL_WEIGHT[]={0.74,0.16,0.10};//2015-01-27
		
	public static final int RECENT_NUMBER=1;
	
	public static final double V_PTS[] 			= {3.1329,8.719};
	public static final double V_PTS1[] 			= {4.166,8.719};
	public static final double V_PTS2[] 			= {2.2727,8.719};
	public static final double V_PTS3[] 			= {1.4285,8.719};
	public static final double V_THREE_PM[] 	= {145.4545,1.135};
	public static final double V_REB[] 			= {23.4782,8.671};
	public static final double V_DREB[] 			= {8.5918,8.671};
	public static final double V_AST[] 			= {9.6511,6.137};
	public static final double V_STL[]  		= {50,12.367};
	public static final double V_BLK[] 			= {39.20705,15.412};
	public static final double V_TO[] 			= {17.6583,39.753};
	public static final double V_MIN[]			= {3.1329,46.677};
	public static final double V_PF[]			= {25.8992,79.927};
	public static final double V_FGM[]  		= {100,17.991};
	public static final double V_FTM[]  		= {100,18.393};
//	public static final double V_PTS[] 			= {2.866,8.719};
//	public static final double V_THREE_PM[] 	= {27.745,1.135};
//	public static final double V_REB[] 			= {7.109,8.671};
//	public static final double V_AST[] 			= {8.325,6.137};
//	public static final double V_STL[]  		= {29.211,12.367};
//	public static final double V_BLK[] 			= {28.196,15.412};
//	public static final double V_TO[] 			= {12.049,39.753};
//	public static final double V_MIN[]			= {1.091,46.677};
//	public static final double V_PF[]			= {-10.346,79.927};
//	public static final double V_FGM[]  		= {100,17.991};
//	public static final double V_FTM[]  		= {100,18.393};
	
	private static Map<Integer,double[]> weightMap;
	
	static {
		weightMap = Maps.newConcurrentMap();
		weightMap.put(0, new double[]{0,0,0,0,0,0,0});
		weightMap.put(1, new double[]{0.1026,0,0,0,0,0,0.8974});
		weightMap.put(2, new double[]{0.1026,0.0324,0,0,0,0,0.865});
		weightMap.put(3, new double[]{0.1026,0.0648,0,0,0,0,0.8326});
		weightMap.put(4, new double[]{0.1026,0.0972,0,0,0,0,0.8002});
		weightMap.put(5, new double[]{0.1026,0.1296,0,0,0,0,0.7678});
		weightMap.put(6, new double[]{0.1026,0.162,0,0,0,0,0.7354});
		weightMap.put(7, new double[]{0.1026,0.1944,0,0,0,0,0.703});
		weightMap.put(8, new double[]{0.1026,0.2268,0,0,0,0,0.6706});
		weightMap.put(9, new double[]{0.1026,0.2592,0,0,0,0,0.6382});
		weightMap.put(10, new double[]{0.1026,0.2916,0,0,0,0,0.6058});
		weightMap.put(11, new double[]{0.1026,0.3239,0,0,0,0,0.5735});
		weightMap.put(12, new double[]{0.1026,0.3239,0.0147,0,0,0,0.5588});
		weightMap.put(13, new double[]{0.1026,0.3239,0.0294,0,0,0,0.5441});
		weightMap.put(14, new double[]{0.1026,0.3239,0.0441,0,0,0,0.5294});
		weightMap.put(15, new double[]{0.1026,0.3239,0.0588,0,0,0,0.5147});
		weightMap.put(16, new double[]{0.1026,0.3239,0.0734,0,0,0,0.5001});
		weightMap.put(17, new double[]{0.1026,0.3239,0.0881,0,0,0,0.4854});
		weightMap.put(18, new double[]{0.1026,0.3239,0.1028,0,0,0,0.4707});
		weightMap.put(19, new double[]{0.1026,0.3239,0.1175,0,0,0,0.456});
		weightMap.put(20, new double[]{0.1026,0.3239,0.1322,0,0,0,0.4413});
		weightMap.put(21, new double[]{0.1026,0.3239,0.1469,0,0,0,0.4266});
		weightMap.put(22, new double[]{0.1026,0.3239,0.1469,0.0104,0,0,0.4162});
		weightMap.put(23, new double[]{0.1026,0.3239,0.1469,0.0208,0,0,0.4058});
		weightMap.put(24, new double[]{0.1026,0.3239,0.1469,0.0313,0,0,0.3953});
		weightMap.put(25, new double[]{0.1026,0.3239,0.1469,0.0417,0,0,0.3849});
		weightMap.put(26, new double[]{0.1026,0.3239,0.1469,0.0521,0,0,0.3745});
		weightMap.put(27, new double[]{0.1026,0.3239,0.1469,0.0625,0,0,0.3641});
		weightMap.put(28, new double[]{0.1026,0.3239,0.1469,0.0729,0,0,0.3537});
		weightMap.put(29, new double[]{0.1026,0.3239,0.1469,0.0834,0,0,0.3432});
		weightMap.put(30, new double[]{0.1026,0.3239,0.1469,0.0938,0,0,0.3328});
		weightMap.put(31, new double[]{0.1026,0.3239,0.1469,0.1042,0,0,0.3224});
		weightMap.put(32, new double[]{0.1026,0.3239,0.1469,0.1042,0.0083,0,0.3141});
		weightMap.put(33, new double[]{0.1026,0.3239,0.1469,0.1042,0.0166,0,0.3058});
		weightMap.put(34, new double[]{0.1026,0.3239,0.1469,0.1042,0.0249,0,0.2975});
		weightMap.put(35, new double[]{0.1026,0.3239,0.1469,0.1042,0.0332,0,0.2892});
		weightMap.put(36, new double[]{0.1026,0.3239,0.1469,0.1042,0.0415,0,0.2809});
		weightMap.put(37, new double[]{0.1026,0.3239,0.1469,0.1042,0.0498,0,0.2726});
		weightMap.put(38, new double[]{0.1026,0.3239,0.1469,0.1042,0.0581,0,0.2643});
		weightMap.put(39, new double[]{0.1026,0.3239,0.1469,0.1042,0.0664,0,0.256});
		weightMap.put(40, new double[]{0.1026,0.3239,0.1469,0.1042,0.0747,0,0.2477});
		weightMap.put(41, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0,0.2394});
		weightMap.put(42, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.007,0.2324});
		weightMap.put(43, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.014,0.2254});
		weightMap.put(44, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.021,0.2184});
		weightMap.put(45, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.028,0.2114});
		weightMap.put(46, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.035,0.2044});
		weightMap.put(47, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.042,0.1974});
		weightMap.put(48, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.049,0.1904});
		weightMap.put(49, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.056,0.1834});
		weightMap.put(50, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.063,0.1764});
		weightMap.put(51, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.07,0.1694});
		weightMap.put(52, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.0761,0.1633});
		weightMap.put(53, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.0822,0.1572});
		weightMap.put(54, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.0883,0.1511});
		weightMap.put(55, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.0944,0.145});
		weightMap.put(56, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1005,0.1389});
		weightMap.put(57, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1066,0.1328});
		weightMap.put(58, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1127,0.1267});
		weightMap.put(59, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1188,0.1206});
		weightMap.put(60, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1249,0.1145});
		weightMap.put(61, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.131,0.1084});
		weightMap.put(62, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1364,0.103});
		weightMap.put(63, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1418,0.0976});
		weightMap.put(64, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1473,0.0921});
		weightMap.put(65, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1527,0.0867});
		weightMap.put(66, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1582,0.0812});
		weightMap.put(67, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1636,0.0758});
		weightMap.put(68, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.169,0.0704});
		weightMap.put(69, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1745,0.0649});
		weightMap.put(70, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1799,0.0595});
		weightMap.put(71, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1854,0.054});
		weightMap.put(72, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1903,0.0491});
		weightMap.put(73, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.1952,0.0442});
		weightMap.put(74, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.2001,0.0393});
		weightMap.put(75, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.205,0.0344});
		weightMap.put(76, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.2099,0.0295});
		weightMap.put(77, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.2148,0.0246});
		weightMap.put(78, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.2197,0.0197});
		weightMap.put(79, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.2246,0.0148});
		weightMap.put(80, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.2295,0.0099});
		weightMap.put(81, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.2345,0.0049});
		weightMap.put(82, new double[]{0.1026,0.3239,0.1469,0.1042,0.083,0.2394,0});

	}
	private int playerId;	
	private int fgm;	
	private int ftm;
	private int pts;	
	private int threePm;
	private int ast;
	private int oreb;	
	private int dreb;	
	private int stl;
	private int blk;
	private int to;	
	private int pf;
	private int min;
	private int attrAbi;
	private int guaAbi;
	private int abi;
	
	public PlayerAbi(){	}

	public static double[] getOverAllWeight(int id){
		if(id <=0){
			return weightMap.get(0);
		}
		double[] result = weightMap.get(id);
		if(result==null){
			result = weightMap.get(82);
		}
		return result;
	}
	
	
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
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

	public void setAst(int ast) {
		this.ast = ast;
	}

	public void setOreb(int oreb) {
		this.oreb = oreb;
	}

	public void setDreb(int dreb) {
		this.dreb = dreb;
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

	public void setPf(int pf) {
		this.pf = pf;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setAttrAbi(int attrAbi) {
		this.attrAbi = attrAbi;
	}

	public void setGuaAbi(int guaAbi) {
		this.guaAbi = guaAbi;
	}

	public int getFgm() {
		return fgm;
	}


	public int getFtm() {
		return ftm;
	}


	public int getPts() {
		return pts;
	}

	public int getThreePm() {
		return threePm;
	}	

	public int getAst() {
		return ast;
	}

	public int getOreb() {
		return oreb;
	}

	public int getDreb() {
		return dreb;
	}

	public int getStl() {
		return stl;
	}

	public int getBlk() {
		return blk;
	}

	public int getTo() {
		return to;
	}

	public int getPf() {
		return pf;
	}

	public int getMin() {
		return min;
	}

	public int getAttrAbi() {
		return attrAbi;
	}

	public int getGuaAbi() {
		return guaAbi;
	}
	public int getReb(){
		return oreb+dreb;
	}
	
	public int getAbi() {
		return abi;
	}

	public void setAbi(int abi) {
		this.abi = abi;
	}

	@Override
	public String toString() {
		return "PlayerAbi [abi=" + abi + ", ast=" + ast + ", attrAbi="
				+ attrAbi + ", blk=" + blk + ", dreb=" + dreb + ", fgm=" + fgm
				+ ", ftm=" + ftm + ", guaAbi=" + guaAbi + ", min=" + min
				+ ", oreb=" + oreb + ", pf=" + pf + ", playerId=" + playerId
				+ ", pts=" + pts + ", stl=" + stl + ", threePm=" + threePm
				+ ", to=" + to + "]";
	}

}

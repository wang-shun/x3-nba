package com.ftkj.manager.team;

import com.ftkj.server.GameSource;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年5月8日
 *
 */
public class TeamNode implements Serializable{
	private static final long serialVersionUID = 1L;
	private long teamId;
	private String nodeName;
	private String args;
	
	public TeamNode(long teamId, String nodeName) {
		super();
		this.teamId = teamId;
		this.nodeName = nodeName;
	}
	
	public TeamNode(long teamId){
		this.teamId = teamId;
		this.nodeName = GameSource.serverName;
	}
	
	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public static String nodeName(TeamNode tn){
        return tn != null ? tn.getNodeName() : "";
    }

    @Override
	public int hashCode() {
		return (int)GameSource.getUserId(this.teamId);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TeamNode){
			return ((TeamNode)obj).getTeamId()==this.teamId;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "TeamNode [teamId=" + teamId + ", nodeName=" + nodeName + ", args=" + args + "]";
	}
	
//	public static void main(String[] args) {
//		Set<TeamNode> nset = Sets.newHashSet();
//		nset.add(new TeamNode(112233, "xx"));
//		nset.add(new TeamNode(112234, "aa"));
//		nset.add(new TeamNode(112235, "bb"));
//		nset.add(new TeamNode(112233, "cc"));
//		System.err.println(nset.size());
//	}
	
}

package com.ftkj.jredis;

public class Page implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7822962813479388407L;
	
	private int start;//开始坐标
	private int end;//结束坐标
	private int pageNo;//第几页
	private int pageSize;//共几页
	private int size;//一页条数
	
	public Page(int pageNo,int size){
		if(pageNo<1)pageNo=1;
		this.pageNo = pageNo;
		this.size = size;
		start = (pageNo-1)*size;
		end   = start + size -1;
	}
	
	public void setResultSize(int len){
		//System.out.println("#====="+len+"=="+end);
		if (len % size == 0)
			this.pageSize = len/size;
		else
			this.pageSize =  len/size + 1;
		if(end>=len)end = len-1;
		//System.out.println("*====="+len+"=="+end);
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "Page [end=" + end + ", pageNo=" + pageNo + ", pageSize="
				+ pageSize + ", size=" + size + ", start=" + start + "]";
	}
	
}

package com.ftkj.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 分页工具类.
 *
 * @author luch
 */
public class Page implements Iterable<Integer>, Iterator<Integer>, Serializable {
    private static final long serialVersionUID = 8097917817525068152L;
    /** 总记录条数 */
    private final long totalCount;
    /** 每页大小 */
    private final int pageSize;
    /** 总页数 */
    private final int pageCount;
    /** 当前第几页 */
    private int pageNo;

    /**
     * @param totalCount 总记录条数
     * @param pageSize   每页大小
     */
    public Page(long totalCount, int pageSize) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageCount = calcPageCount(totalCount, pageSize);
    }

    /**
     * @param totalCount 总记录条数
     * @param pageSize   每页大小
     */
    public Page(long totalCount, int pageSize, int pageNo) {
        this(totalCount, pageSize);
        if (pageNo > pageCount) {
            pageNo = pageCount;
        }
        if (pageNo < 0) {
            pageNo = 0;
        }
        this.pageNo = pageNo;
    }

    private int calcPageCount(long totalCount, int pageSize) {
        if (totalCount % pageSize == 0) {
            return (int) (totalCount / pageSize);
        } else {
            return (int) (totalCount / pageSize) + 1;
        }
    }

    /** 总记录条数 */
    public long getTotalCount() {
        return totalCount;
    }

    /** 每页大小 */
    public int getPageSize() {
        return pageSize;
    }

    /** 每页大小 */
    public int pageSize() {
        return getPageSize();
    }

    /** 总页数 */
    public int getPageCount() {
        return pageCount;
    }

    /** 初始行的偏移量为0. */
    public int getPageStart() {
        return getOffset();
    }

    /** 初始行的偏移量为0. */
    public int pageStart() {
        return getOffset();
    }

    public int pageEnd() {
        if (pageNo == pageCount) {
            return (int) totalCount;
        }
        return getOffset() + pageSize - 1;
    }

    /** 当前页数记录条数 */
    public int currPageRowCount() {
        if (pageNo == pageCount) {
            return (int) (totalCount - getOffset());
        }
        return pageSize;
    }

    /** 初始行的偏移量为0. */
    public int getOffset() {
        return (pageNo - 1) * pageSize;
    }

    public Iterator<Integer> iterator() {
        return this;
    }

    /** 是否还有下一页 */
    public boolean hasNext() {
        return pageNo < pageCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    /** 返回下一页 */
    public Integer next() {
        if (pageNo > pageCount) {
            throw new NoSuchElementException();
        }
        pageNo += 1;
        return pageNo;
    }

    public static void main(String[] args) {
        Page page = new Page(21, 10);
        for (int pageNo : page) {
            System.out.println("page No. " + pageNo +
                    " offset " + page.getOffset() +
                    " end " + page.pageEnd() +
                    " size " + page.pageSize);
        }
        page = new Page(21, 10);
        System.out.println("======");
        while (page.hasNext()) {
            int pageNo = page.next();
            System.out.println("page No. " + pageNo + "/" + page.pageCount +
                    " offset " + page.getOffset() +
                    " end " + page.pageEnd() +
                    " size " + page.pageSize);
        }
    }
}

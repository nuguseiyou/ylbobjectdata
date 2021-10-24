package com.bjpowernode.vo;

/**
 * vo : value object (view object)
 */
public class PageInfo {

    private int pageNo;
    private int pageSize;
    private int totalPages;
    private int totalRecords;

    public PageInfo() {
        pageNo = 1;
        pageSize = 9;
        totalPages = 0;
        totalRecords = 0;
    }

    public PageInfo(int pageNo, int pageSize, int totalRecords) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
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

    public int getTotalPages() {
        // totalRecords:总记录数
        // totalPages:总页数
        if (totalRecords % pageSize == 0) {
            totalPages = totalRecords / pageSize;
        } else {
            totalPages = totalRecords / pageSize + 1;
        }
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
}

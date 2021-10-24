package com.nuguseiyou.vo;

/**
 * 2021/9/14
 */
public class Pageinfo {
    private int pageNo;
    private int pageSize;
    private int totalPages;
    private int totalRecords;

    public Pageinfo(int pageNo, int pageSize, int totalRecords) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
    }

    public Pageinfo() {
        pageNo = 1;
        pageSize = 9;
        totalPages = 0;
        totalRecords = 0;
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
        if(totalRecords % pageSize == 0){
            totalPages = totalRecords / pageSize;
        }else{
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

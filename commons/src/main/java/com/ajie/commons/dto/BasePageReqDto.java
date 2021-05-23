package com.ajie.commons.dto;

public class BasePageReqDto extends BaseReqDto {
    /**
     * 当前页
     */
    private int currentPage = 1;
    /**
     * 每页条数，默认10条
     */
    private int pageSize = 10;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}

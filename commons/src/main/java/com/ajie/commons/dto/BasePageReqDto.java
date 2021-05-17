package com.ajie.commons.dto;

public class BasePageReqDto extends BaseReqDto {
    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 每页条数
     */
    private int pageSize;

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

package com.ajie.commons.dto;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 */
public class PageDto<T> {
    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 总条数
     */
    private long total;
    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 数据
     */
    private List<T> data;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public static <T> PageDto empty() {
        PageDto<T> dto = new PageDto<>();
        dto.setData(Collections.emptyList());
        return dto;
    }
}


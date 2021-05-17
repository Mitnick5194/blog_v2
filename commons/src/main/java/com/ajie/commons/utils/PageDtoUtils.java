package com.ajie.commons.utils;

import com.ajie.commons.dto.PageDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class PageDtoUtils {

    public static <T> PageDto toPageDto(IPage<T> page) {
        PageDto dto = new PageDto();
        if (null == page || CollectionUtils.isEmpty(page.getRecords())) {
            dto.setData(Collections.emptyList());
            return dto;
        }
        dto.setData(page.getRecords());
        dto.setCurrentPage((int) page.getCurrent());
        dto.setPageSize((int) page.getSize());
        dto.setTotal(page.getTotal());
        return dto;
    }
}

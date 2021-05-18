package com.ajie.commons.utils;

import com.ajie.commons.dto.PageDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import sun.applet.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageDtoUtils {

    /**
     * 将Page转换成PageDto
     *
     * @param page
     * @param <T>
     * @return
     */
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

    /**
     * 将Page转换成PageDto并将结果集替换成trans
     *
     * @param page
     * @param trans 替换结果
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T, S> PageDto toPageDto(IPage<T> page, List<S> trans) {
        PageDto dto = new PageDto();
        if (null == page || CollectionUtils.isEmpty(page.getRecords())) {
            dto.setData(Collections.emptyList());
            return dto;
        }
        trans.stream();
        dto.setCurrentPage((int) page.getCurrent());
        dto.setPageSize((int) page.getSize());
        dto.setTotal(page.getTotal());
        dto.setData(trans);
        return dto;
    }

    /**
     * 将Page转换成PageDto并将结果集替换成function的返回结果
     *
     * @param page
     * @param function
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T, S> PageDto toPageDto(IPage<T> page, Function<T, S> function) {
        PageDto dto = new PageDto();
        if (null == page || CollectionUtils.isEmpty(page.getRecords())) {
            dto.setData(Collections.emptyList());
            return dto;
        }
        dto.setCurrentPage((int) page.getCurrent());
        dto.setPageSize((int) page.getSize());
        dto.setTotal(page.getTotal());
        List<T> records = page.getRecords();
        List<S> trans = records.stream().map(s -> {
            return function.apply(s);
        }).collect(Collectors.toList());
        dto.setData(trans);
        return dto;
    }

    public static void main(String[] args) {
        List<Long> list = new ArrayList<>();
        list.add(123423L);
        Page page = new Page();
        page.setRecords(list);
        PageDto pageDto = toPageDto(page, (x) -> {
            return "abc" + String.valueOf(x);
        });
        List<String> data = pageDto.getData();
        System.out.println(data.get(0));
    }
}

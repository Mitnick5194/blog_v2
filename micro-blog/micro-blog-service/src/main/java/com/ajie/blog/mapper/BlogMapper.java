package com.ajie.blog.mapper;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.po.BlogPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogMapper extends BaseMapper<BlogPO> {

    @Select({"<script>",
            "select * from mb_blog",
            "where del=0 ",
            "<if test='dto.keyword!=null'>  and title like CONCAT('%',#{dto.keyword},'%')",
            "UNION select * from mb_blog where del=0 and content like CONCAT('%',#{dto.keyword},'%')",
            "</if>",
            "</script>"})
    IPage<BlogPO> queryByPage(@Param("page") IPage<BlogPO> page, @Param("dto") BlogQueryReqDto dto);

    @Select("select * from tb_blog")
    List<BlogPO> queryOldData();
}
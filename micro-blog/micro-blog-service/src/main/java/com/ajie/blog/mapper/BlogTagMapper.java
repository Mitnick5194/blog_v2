package com.ajie.blog.mapper;

import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.dto.TagDto;
import com.ajie.blog.api.po.BlogPO;
import com.ajie.blog.api.po.BlogTagPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTagPO> {

    @Select({"<script>",
            "select t.id as id,t.tag_name as tag , count(b.tag_id) as blogCount from mb_tag t ",
            "left join mb_blog_tag b ",
            "on t.id = b.tag_id ",
            "where t.del=0 and b.del=0",
            "GROUP BY b.tag_id ORDER BY blogCount DESC ",
            "</script>"})
    IPage<TagDto> queryTag(@Param("page") IPage<TagDto> page);
}
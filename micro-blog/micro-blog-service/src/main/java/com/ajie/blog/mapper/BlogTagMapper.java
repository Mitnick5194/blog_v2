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
   /* select t.id as id,t.tag_name as tag , count(tb.tag_id) as blogCount from mb_tag t
    join
            (select _tb.tag_id from mb_blog_tag as _tb
                    join mb_blog as _b
                    on _tb.blog_id = _b.id and (_b.type = 1 or (_b.type = 0 and _b.user_id = 1395288739512655872))  and _tb.del = 0 and _tb.del=0) tb
    on t.id = tb.tag_id
    where t.del=0
    GROUP BY tb.tag_id  ORDER BY blogCount desc limit 10*/

    /**
     * @param page
     * @param userId 当前登录者，如果有传，则把当前登录者状态为私有的博文一同查出来
     * @return
     */
    @Select({"<script>",
            "select t.id as id,t.tag_name as tag , count(tb.tag_id) as blogCount from mb_tag as t ",
            "join",
            "(select _tb.tag_id from mb_blog_tag as _tb ",
            "join ",
            "mb_blog as _b ",
            "on _tb.blog_id = _b.id and (_b.type = 1 ",
            "<if  test='userId!=null'>",
            "    or (_b.type=2  and _b.user_id=#{userId}) " ,
            "</if>",
            ")  ",
            "and _b.del = 0 and _tb.del=0) as tb ",
            " on t.id = tb.tag_id ",
            "where t.del=0",
            "GROUP BY tb.tag_id HAVING blogCount > 0 ORDER BY blogCount desc",
            "</script>"})
    IPage<TagDto> queryTag(@Param("page") IPage<TagDto> page, @Param("userId") Long userId);
}
package com.ajie.blog.migrate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MigrateMapper {

    @Select("select * from tb_blog")
    List<OldBlogPO> selectBlog();

    @Select("select * from tb_label")
    List<OldTagPO> selectTag();

    @Select("select * from tb_comment")
    List<OldCommentPO> selectComment();
}

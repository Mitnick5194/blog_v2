package com.ajie.blog.mapper;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.po.BlogPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogMapper extends BaseMapper<BlogPO> {

    /**
     * 分页查询博文
     *
     * @param page
     * @param dto
     * @param blogIds
     * @param userId  当前登录者，如果有传，则把当前登录者状态为私有的博文一同查出来
     * @return
     */
    @Select({"<script>",
            "(select * from mb_blog",
            "where del=0 and type=1",
            "<if test='blogIds != null and blogIds.size > 0'> ",
            " and id in ",
            "  <foreach collection='blogIds' index='index' item='item' open='(' separator=',' close=')'> ",
            "    #{item}",
            "  </foreach> ",
            "</if>",
            "<if test='dto.keyword==null'> order by create_time desc)</if>",
            "<if test='dto.keyword!=null'>",
            "   and title like CONCAT('%',#{dto.keyword},'%') order by create_time desc )",
            "   UNION ",
            "   (select * from mb_blog where del=0 ",
            "   <if test='blogIds != null and blogIds.size > 0'> ",
            "       and id in ",
            "       <foreach collection='blogIds' index='index' item='item' open='(' separator=',' close=')'> ",
            "           #{item}",
            "       </foreach> ",
            "   </if>",
            "and content like CONCAT('%',#{dto.keyword},'%') order by create_time desc )",
            "</if>",
            "<if test='userId != null'>",
            "   UNION ",
            "   (select * from mb_blog where del=0 and type=0 and user_id=#{userId}",
            "   <if test='blogIds != null and blogIds.size > 0'> ",
            "       and id in ",
            "       <foreach collection='blogIds' index='index' item='item' open='(' separator=',' close=')'> ",
            "           #{item}",
            "       </foreach> ",
            "   </if>",
            "   <if test='dto.keyword==null'> order by create_time desc )</if>",
            "   <if test='dto.keyword!=null'>",
            "       and title like CONCAT('%',#{dto.keyword},'%') order by create_time desc )",
            "       UNION ",
            "       (select * from mb_blog where del=0",
            "       <if test='blogIds != null and blogIds.size > 0'> ",
            "           and id in ",
            "           <foreach collection='blogIds' index='index' item='item' open='(' separator=',' close=')'> ",
            "               #{item}",
            "           </foreach> ",
            "       </if>",
            "       and content like CONCAT('%',#{dto.keyword},'%') order by create_time desc)",
            "   </if>",
            "</if>",
            "</script>"})
    IPage<BlogRespDto> queryByPage(@Param("page") IPage<BlogPO> page, @Param("dto") BlogQueryReqDto dto, @Param("blogIds") List<Long> blogIds, @Param("userId") Long userId);


    @Select("select * from tb_blog")
    List<BlogPO> queryOldData();
}
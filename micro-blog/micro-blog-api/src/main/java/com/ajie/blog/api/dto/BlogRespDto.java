package com.ajie.blog.api.dto;

import com.ajie.blog.account.api.dto.AccountRespDto;
import com.ajie.blog.api.po.BlogPO;
import com.ajie.blog.api.po.TagPO;
import com.ajie.commons.dto.BaseReqDto;
import com.ajie.commons.dto.BaseRespDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 响应信息
 */
@ApiModel(value = "BlogRespDto", description = "博文")
@Getter
@Setter
public class BlogRespDto extends BlogDto {
    /**
     * 标签
     */
    @ApiModelProperty(value = "标签组")
    private List<TagDto> tagList;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;
    /**
     * 用户头像链接
     */
    @ApiModelProperty(value = "用户头像链接")
    private String userHeaderUrl;

    @ApiModelProperty(value = "阅读数")
    private int readCount;

    @ApiModelProperty(value = "评论数")
    private int commentCount;


    public void build(BlogPO blog) {
        BeanUtils.copyProperties(blog, this);
    }

    public void build(AccountRespDto account) {
        this.setUserHeaderUrl(account.getHeaderUrl());
        this.setUserName(account.getAccountName());
        this.setUserId(account.getId());
    }

    public void build(List<TagPO> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            tagList = Collections.emptyList();
            return;
        }
        List<TagDto> list = tags.stream().map(s -> {
            TagDto t = new TagDto();
            BeanUtils.copyProperties(s, t);
            return t;
        }).collect(Collectors.toList());
        this.tagList = list;
    }

}

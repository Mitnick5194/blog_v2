package com.ajie.blog.api.po;

import com.ajie.commons.po.BasePO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import java.math.BigDecimal;

@Getter
@Setter
@TableName("mb_tag")
public class TagPO extends BasePO {
    /**
     * 标签名
     */

    private String tagName;
}
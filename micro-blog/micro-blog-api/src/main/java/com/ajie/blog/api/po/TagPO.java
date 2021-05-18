package com.ajie.blog.api.po;

import com.ajie.commons.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import java.math.BigDecimal;

@Getter
@Setter
public class TagPO extends BasePO {
    /**
     * 标签名
     */

    private String tagName;
}
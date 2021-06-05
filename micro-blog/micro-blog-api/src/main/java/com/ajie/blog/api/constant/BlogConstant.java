package com.ajie.blog.api.constant;

public interface BlogConstant {

    /**
     * 状态:1正常
     */
    public static final Integer STATUS_NORMAL = 1;
    /**
     * 状态:2草稿
     */
    public static final Integer STATUS_DRAFF = 2;

    /**
     * 类型:1正常
     */
    public static final Integer TYPE_NORMAL = 1;
    /**
     * 类型: 2草稿
     */
    public static final Integer TYPE_PRIVATE = 2;

    /**
     * 阅读数redis缓存key，hash结构，hash的key为博客的ID
     */
    static final String READ_COUNT_KEY = "RC-BLOG";

    /**
     * 评论数redis缓存key,hash结构，hash的key为博客的ID
     */
    static final String COMMENT_COUNT_KEY = "CC-BLOG";

}

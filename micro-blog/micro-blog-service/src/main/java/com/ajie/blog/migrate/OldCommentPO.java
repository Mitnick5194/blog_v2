package com.ajie.blog.migrate;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("tb_comment")
public class OldCommentPO {
    private Integer id;
    private Integer blogId;
    private Integer userId;
    private String content;
}

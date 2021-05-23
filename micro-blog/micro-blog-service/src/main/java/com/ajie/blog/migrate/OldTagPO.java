package com.ajie.blog.migrate;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("tb_label")
public class OldTagPO {
    private Integer id;
    private String name;
}

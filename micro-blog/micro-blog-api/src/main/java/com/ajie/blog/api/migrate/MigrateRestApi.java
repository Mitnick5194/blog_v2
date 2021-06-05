package com.ajie.blog.api.migrate;

import com.ajie.blog.api.dto.MigrateDto;
import com.ajie.commons.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 数据迁移
 */
@FeignClient(name = "migrateRestApi",url = "http://www.nzjie.cn")
public interface MigrateRestApi {

    @PostMapping("loadblogs")
    RestResponse<List<MigrateDto>> loadBlog();
}

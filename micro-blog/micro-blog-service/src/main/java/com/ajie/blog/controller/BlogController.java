package com.ajie.blog.controller;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogReqDto;
import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.dto.DraftBlogReqDto;
import com.ajie.blog.api.rest.BlogRestApi;
import com.ajie.blog.config.Properties;
import com.ajie.blog.migrate.MigrateService;
import com.ajie.blog.service.BlogService;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.DateUtil;
import com.ajie.commons.utils.FileUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * 博文请求入口
 */
@Api(tags = "博文模块")
@RequestMapping("/micro-blog/v2/blog")
@RestController
//@CrossOrigin
public class BlogController implements BlogRestApi {
    private Logger logger = LoggerFactory.getLogger(BlogController.class);


    @Resource
    private BlogService blogService;

    @Resource
    private MigrateService migrateService;

    @Override
    public RestResponse<Long> save(BlogReqDto blog) {
        Long data = blogService.save(blog);
        return RestResponse.success(data);
    }

    @Override
    public RestResponse<Long> saveDraft(DraftBlogReqDto blog) {
        return RestResponse.success(blogService.saveDraft(blog));
    }

    @Override
    public RestResponse<Integer> deleteById(Long id) {
        return RestResponse.success(blogService.deleteById(id));
    }

    @Override
    public RestResponse<PageDto<List<BlogRespDto>>> queryByPage(BlogQueryReqDto dto) {
        return RestResponse.success(blogService.queryByPage(dto));
    }

    @Override
    public RestResponse<BlogRespDto> queryBlogById(Long id) {
        return RestResponse.success(blogService.queryBlogById(id));
    }

    @Override
    public RestResponse<Integer> togglePrivate(Long id, Integer type) {
        return RestResponse.success(blogService.togglePrivate(id, type));
    }

    /**
     * 数据迁移
     *
     * @param userId
     * @return
     */
    @GetMapping("migrate")
    public RestResponse<Integer> migrate(@RequestParam(required = false, name = "userId") String userId, @RequestParam("password") String password) {
        return RestResponse.success(migrateService.migrate(userId, password));
    }

    /**
     * 富文本的图片上传
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/imgupload")
    public void imgupload(@RequestParam("upload") MultipartFile file,
                          HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取get_image.html的请求地址
        String backUrl = request.getHeader("Origin") + Properties.frontGetImageHtml;
        try {
            //图片存放路径
            String imageFolder = Properties.uploadFileDir;
            LocalDate localDate = LocalDate.now();
            StringBuilder sb = new StringBuilder();
            sb.append(imageFolder);
            if (!imageFolder.endsWith(File.separator)) {
                sb.append(File.separator);
            }
            sb.append(localDate.getYear());
            sb.append(DateUtil.prettyNum(localDate.getMonthValue()));
            sb.append(DateUtil.prettyNum(localDate.getDayOfMonth()));
            FileUtils.createFolderIfNotExist(imageFolder);
            //根据时间戳创建新的文件名，这样即便是第二次上传相同名称的文件，也不会把第一次的文件覆盖了
            String fileName = "BL-" + System.currentTimeMillis() + file.getOriginalFilename();
            File destFile = new File(imageFolder, fileName);
            file.transferTo(destFile);
            String callback = request.getParameter("CKEditorFuncNum");
            String imageUrl = Properties.uploadFileUrl + fileName;
            response.sendRedirect(backUrl + "?ImageUrl=" + imageUrl + "&CKEditorFuncNum=" + callback);
        } catch (Exception e) {
            logger.error("图片上传失败");
            response.sendRedirect(backUrl + "?error=" + e.getMessage());
        }
    }

}

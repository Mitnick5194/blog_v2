package com.ajie.blog.migrate;

import com.ajie.blog.api.constant.BlogConstant;
import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.dto.MigrateDto;
import com.ajie.blog.api.migrate.MigrateRestApi;
import com.ajie.blog.api.po.BlogPO;
import com.ajie.blog.api.po.BlogTagPO;
import com.ajie.blog.api.po.CommentPO;
import com.ajie.blog.api.po.TagPO;
import com.ajie.blog.mapper.BlogMapper;
import com.ajie.blog.mapper.BlogTagMapper;
import com.ajie.blog.mapper.CommentMapper;
import com.ajie.blog.mapper.TagMapper;
import com.ajie.commons.RestResponse;
import com.ajie.commons.aop.AbstractMapperAspect;
import com.ajie.commons.exception.MicroCommonException;
import com.ajie.commons.utils.ApiUtil;
import com.ajie.commons.utils.HtmlFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 旧数据迁移
 */
@Service
public class MigrateService {
    @Resource
    private MigrateMapper mapper;
    @Resource
    private BlogMapper blogMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private BlogTagMapper blogTagMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MigrateRestApi migrateRestApi;

    public int migrate(String createPerson, String password) {
        //标记忽略字段填充（时间字段创建人字段）
        System.setProperty(AbstractMapperAspect.IGNORE_FILL, AbstractMapperAspect.IGNORE_FILL);
        if (StringUtils.isBlank(password) || !"xylx".equals(password)) {
            throw new MicroCommonException(-1, "非法操作");
        }
        List<OldBlogPO> oldBlogPOS = mapper.selectBlog();
        List<OldCommentPO> oldCommentPOS = mapper.selectComment();
        List<BlogPO> blogs = new ArrayList<>();
        //旧博客和新的id映射
        Map<Integer, Long> map = new HashMap<>();
        for (OldBlogPO b : oldBlogPOS) {
            BlogPO p = new BlogPO();
            BeanUtils.copyProperties(b, p);
            p.setUpdateTime(b.getCreateTime());
            p.setAbstractContent(handleAbstractContent(b.getContent()));
            p.setUserId(Long.valueOf(createPerson));
            p.setType(1);
            p.setDel(0);
            p.setCreatePerson(createPerson);
            p.setUpdatePerson(createPerson);
            blogMapper.insert(p);
            blogs.add(p);
            map.put(b.getId(), p.getId());
        }
        //博客ID和博客映射
        Map<Long, BlogPO> idMap = blogs.stream().collect(Collectors.toMap(BlogPO::getId, Function.identity()));
        handleComment(createPerson, oldCommentPOS, map, idMap);
        handleTag(oldBlogPOS, map, idMap);
        handleReadCount(map, idMap);
        return oldBlogPOS.size();
    }

    /**
     * 处理评论
     *
     * @param createPerson
     * @param oldCommentPOS
     * @param map           旧博客id和新博客id映射
     * @param idMap         新博客的id和实体映射
     */
    private void handleComment(String createPerson, List<OldCommentPO> oldCommentPOS, Map<Integer, Long> map, Map<Long, BlogPO> idMap) {
        //统计博客评论数用
        Map<Long, List<CommentPO>> blogCommentMap = new HashMap<>();
        for (OldCommentPO b : oldCommentPOS) {
            Integer blogId = b.getBlogId();
            Long aLong = map.get(blogId);
            if (null == aLong) {
                continue;//被刪了
            }
            BlogPO blogPO = idMap.get(aLong);
            CommentPO p = new CommentPO();
            p.setUserId(Long.valueOf(createPerson));
            p.setContent(b.getContent());
            p.setBlogId(aLong);
            p.setUpdateTime(blogPO.getCreateTime());
            p.setCreateTime(blogPO.getCreateTime());
            p.setCreatePerson(createPerson);
            p.setUpdatePerson(createPerson);
            p.setDel(0);
            commentMapper.insert(p);
            List<CommentPO> commentPOS = blogCommentMap.get(aLong);
            if (null == commentPOS) {
                commentPOS = new ArrayList<>();
                blogCommentMap.put(aLong, commentPOS);
            }
            commentPOS.add(p);
        }
        //评论数
        Iterator<Map.Entry<Long, List<CommentPO>>> iterator = blogCommentMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<CommentPO>> next = iterator.next();
            Long key = next.getKey();
            List<CommentPO> value = next.getValue();
            //评论数
            stringRedisTemplate.opsForHash().increment(BlogConstant.COMMENT_COUNT_KEY, String.valueOf(key), Long.valueOf(value.size()));
        }
    }

    private void handleTag(List<OldBlogPO> oldBlogPOS, Map<Integer, Long> map, Map<Long, BlogPO> idMap) {
        Map<Long, List<Long>> blogTagMap = new HashMap<>();
        //新标签名和id映射
        Map<String, Long> tagNameIdMap = new HashMap<>();
        //新标签ID和标签名映射
        Map<Long, String> idTagNameMap = new HashMap<>();
        for (OldBlogPO b : oldBlogPOS) {
            String labelStrs = b.getLabelStrs();
            if (StringUtils.isBlank(labelStrs)) {
                continue;
            }
            Long aLong = map.get(b.getId());
            BlogPO blogPO = idMap.get(aLong);
            String[] labels = labelStrs.split("\\,");
            for (String l : labels) {
                Long tagId = tagNameIdMap.get(l);
                if (null == tagId) {
                    //还没有保存，保存一下
                    TagPO t = new TagPO();
                    t.setTagName(l);
                    t.setDel(0);
                    t.setCreateTime(blogPO.getCreateTime());
                    t.setUpdateTime(blogPO.getUpdateTime());
                    t.setUpdatePerson(blogPO.getUpdatePerson());
                    t.setCreatePerson(blogPO.getCreatePerson());
                    tagMapper.insert(t);
                    tagNameIdMap.put(l, t.getId());
                    idTagNameMap.put(t.getId(), l);
                    tagId = t.getId();
                }
                List<Long> longs = blogTagMap.get(aLong);
                if (null == longs) {
                    longs = new ArrayList<>();
                    blogTagMap.put(aLong, longs);
                }
                longs.add(tagId);
            }
        }
        Iterator<Map.Entry<Long, List<Long>>> it = blogTagMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<Long>> next = it.next();
            Long key = next.getKey();
            BlogPO blog = idMap.get(key);
            List<Long> value = next.getValue();
            for (Long id : value) {
                BlogTagPO po = new BlogTagPO();
                po.setBlogId(key);
                po.setTagId(id);
                String s = idTagNameMap.get(id);
                po.setTagName(s);
                po.setCreatePerson(blog.getCreatePerson());
                po.setUpdateTime(blog.getUpdateTime());
                po.setCreatePerson(blog.getUpdatePerson());
                po.setUpdatePerson(blog.getUpdatePerson());
                po.setDel(0);
                blogTagMapper.insert(po);
            }
        }
    }

    /**
     * 阅读数，通过链接请求阿里云服务器获取
     *
     * @param map   旧博客id和新博客id映射
     * @param idMap 新博客的id和实体映射
     */
    private void handleReadCount(Map<Integer, Long> map, Map<Long, BlogPO> idMap) {
        RestResponse<List<MigrateDto>> data = migrateRestApi.loadBlog();
        List<MigrateDto> list = ApiUtil.checkAndGetData(data);
        for (MigrateDto item : list) {
            Long newId = map.get(item.getId());
            BlogPO blogPO = idMap.get(newId);
            if (null != blogPO) {
                stringRedisTemplate.opsForHash().increment(BlogConstant.READ_COUNT_KEY, String.valueOf(blogPO.getId()), item.getReadNum());
            }
        }


    }


    /**
     * 摘要部分去除html标签（因为截取一段内容，可能前面有标签，但是结束标签没有被包含，会导致页面标签混乱）<br>
     * 摘要保留200字
     *
     * @param content
     */
    private String handleAbstractContent(String content) {
        // 先取400
        if (content.length() > 400) {
            content = content.substring(0, 399);
        }
        StringBuilder sb = new StringBuilder();
        // 过滤完整的标签组
        content = HtmlFilter.filterHtml(content, sb);
        if (content.length() > 200) {
            content = content.substring(0, 200);// 只显示200个字
        }
        // 结束可能是<div class='' 需要手动处理一下
        for (int i = content.length() - 1, j = 0; i >= 0; i--) {
            if (++j == 50) { // 只检查后50个字符
                break;
            }
            char ch = content.charAt(i);
            if (ch == HtmlFilter.MARK_LEFT) {
                content = content.substring(0, i - 1);
                break;
            }
        }
        return content;
    }

    /**
     * 处理内容，将里面的链接替换掉
     *
     * @return
     */
    private static String convertContent(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        //http://www.nzjie.cn/static/images/blog/20210314/BI-ANsx7T48YwkPaDbDx1f1615699940238.PNG
        //http://static.image.qyun.nzjie.cn/micro-blog/BL-1622024181961a59c089047554c17d726ca3f6da1a792.png
        content = content.replaceAll("http://www.nzjie.cn/static/images/blog", "http://static.image.qyun.nzjie.cn/micro-blog");
        return content;
    }

    public static void main(String[] args) {
        String content = "我是图片哈哈http://www.nzjie.cn/static/images/blog/20210314/BI-ANsx7T48YwkPaDbDx1f1615699940238.PNG图片是我嘻嘻";
        String s = convertContent(content);
        System.out.println(s);
    }

}

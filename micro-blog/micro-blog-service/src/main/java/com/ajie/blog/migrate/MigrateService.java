package com.ajie.blog.migrate;

import com.ajie.blog.api.po.BlogPO;
import com.ajie.blog.api.po.BlogTagPO;
import com.ajie.blog.api.po.CommentPO;
import com.ajie.blog.api.po.TagPO;
import com.ajie.blog.mapper.BlogMapper;
import com.ajie.blog.mapper.BlogTagMapper;
import com.ajie.blog.mapper.CommentMapper;
import com.ajie.blog.mapper.TagMapper;
import com.ajie.commons.aop.AbstractMapperAspect;
import com.ajie.commons.utils.HtmlFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public int migrate(String createPerson) {
        String property = System.getProperty(AbstractMapperAspect.IGNORE_FILL);
        if (null == property) {
            property = "";
        }
        try {
            System.setProperty(AbstractMapperAspect.IGNORE_FILL, AbstractMapperAspect.IGNORE_FILL);
            List<OldBlogPO> oldBlogPOS = mapper.selectBlog();
            List<OldCommentPO> oldCommentPOS = mapper.selectComment();
            List<BlogPO> blogs = new ArrayList<>();
            //评论博客中间表
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
            Map<Long, BlogPO> idMap = blogs.stream().collect(Collectors.toMap(BlogPO::getId, Function.identity()));
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
            }
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
            return oldBlogPOS.size();
        } finally {
            System.setProperty(AbstractMapperAspect.IGNORE_FILL, property);
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

}

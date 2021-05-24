package com.ajie.blog.service.impl;

import com.ajie.blog.account.api.dto.AccountRespDto;
import com.ajie.blog.account.api.rest.AccountRestApi;
import com.ajie.blog.api.dto.*;
import com.ajie.blog.api.enums.BlogExceptionEmun;
import com.ajie.blog.api.po.BlogPO;
import com.ajie.blog.api.po.BlogTagPO;
import com.ajie.blog.api.po.DraftBlogPO;
import com.ajie.blog.exception.BlogException;
import com.ajie.blog.mapper.BlogMapper;
import com.ajie.blog.mapper.BlogTagMapper;
import com.ajie.blog.mapper.DraftBlogMapper;
import com.ajie.blog.service.BlogService;
import com.ajie.blog.service.TagService;
import com.ajie.commons.constant.TableConstant;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService, TableConstant {
    private Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);
    @Resource
    private BlogMapper blogMapper;
    @Resource
    private DraftBlogMapper draftBlogMapper;
    @Resource
    private BlogTagMapper blogTagMapper;
    @Resource
    private TagService tagService;
    @Resource
    private AccountRestApi accountRestApi;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(BlogReqDto dto) {
        ParamCheck.assertNull(dto.getTitle(), BlogException.paramError("标题"));
        ParamCheck.assertNull(dto.getContent(), BlogException.paramError("内容"));
        ParamCheck.assertNull(dto.getTagList(), BlogException.paramError("标签"));
        BlogPO po = new BlogPO();
        BeanUtils.copyProperties(dto, po);
        po.setUserId(UserInfoUtil.getUserId());
        //处理摘要
        String ac = handleAbstractContent(dto.getContent());
        po.setAbstractContent(ac);
        blogMapper.insert(po);
        handleTag(dto.getTagList(), po.getId(), false);
        return po.getId();
    }

    /**
     * 处理标签
     *
     * @param tagList    标签
     * @param blogId     博客id
     * @param delBlogTag 是否需要删除中间表
     */
    private void handleTag(List<TagDto> tagList, Long blogId, boolean delBlogTag) {
        //保存标签
        tagService.createTags(tagList);
        if (delBlogTag) {
            //删除中间表
            BlogTagPO blogTag = new BlogTagPO();
            blogTag.setBlogId(blogId);
            List<BlogTagPO> pos = blogTagMapper.selectList(blogTag.wrap(BlogTagPO.class));
            for (BlogTagPO p : pos) {
                blogTagMapper.deleteById(p.getId());
            }
        }
        //保存中间表
        BlogTagPO blogTag = new BlogTagPO();
        blogTag.setBlogId(blogId);
        for (TagDto t : tagList) {
            blogTag.setTagId(t.getId());
            blogTagMapper.insert(blogTag);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer update(BlogReqDto dto) {
        ParamCheck.assertNull(dto.getId(), BlogException.paramError("文章ID"));
        ParamCheck.assertNull(dto.getTitle(), BlogException.paramError("标题"));
        ParamCheck.assertNull(dto.getContent(), BlogException.paramError("内容"));
        ParamCheck.assertNull(dto.getTagList(), BlogException.paramError("标签"));
        BlogPO po = new BlogPO();
        BeanUtils.copyProperties(dto, po);
        //处理摘要
        String ac = handleAbstractContent(dto.getContent());
        po.setAbstractContent(ac);
        int ret = blogMapper.updateById(po);
        handleTag(dto.getTagList(), po.getId(), true);
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveDraft(BlogReqDto blog) {
        List<TagDto> tagList = blog.getTagList();
        if (CollectionUtils.isNotEmpty(tagList)) {
            //保存标签
            tagService.createTags(tagList);
        }
        DraftBlogPO po = new DraftBlogPO();
        Long id = blog.getId();
        if (null == id) {
            //新建草稿
            BeanUtils.copyProperties(blog, po);
            draftBlogMapper.insert(po);
            return po.getId();
        }
        //查询是否有该草稿
        po.setRefBlogId(blog.getId());
        DraftBlogPO draft = draftBlogMapper.selectOne(po.wrap(DraftBlogPO.class));
        if (null != draft) {
            //有了直接更新
            BeanUtils.copyProperties(blog, draft);
            draftBlogMapper.updateById(draft);
            return draft.getId();
        }
        //没有，新建
        BeanUtils.copyProperties(blog, po);
        draftBlogMapper.insert(po);
        return po.getId();
    }

    @Override
    public Integer deleteById(Long id) {
        BlogPO blogPo = blogMapper.selectById(id);
        if (null == blogPo) {
            throw BlogException.of(BlogExceptionEmun.PARAM_ERROR.getCode(), "找不到文章：" + id);
        }
        BlogPO po = new BlogPO();
        po.setId(id);
        po.setDel(DEL);
        int ret = blogMapper.updateById(po);
        return ret;
    }

    @Override
    public PageDto<List<BlogRespDto>> queryByPage(BlogQueryReqDto dto) {
        if (dto.isDraft()) {
            return queryDraft(dto);
        }
        Page<BlogPO> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        List<Long> blogIds = null;
        if (CollectionUtils.isNotEmpty(dto.getTagList())) {
            //查询标签中间表
            BlogTagPO blogTag = new BlogTagPO();
            QueryWrapper<BlogTagPO> wrap = new QueryWrapper<>();
            wrap.in("tag_id", StringUtils.join(dto.getTagList(), ","));
            List<BlogTagPO> btp = blogTagMapper.selectList(wrap);
            blogIds = btp.stream().map(BlogTagPO::getBlogId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(blogIds)) {
                return PageDto.empty();
            }
        }
        IPage<BlogRespDto> blogPoPage = blogMapper.queryByPage(page, dto, blogIds, UserInfoUtil.getUserId());
        List<BlogRespDto> records = blogPoPage.getRecords();
        fillAccountInfo(records);
        PageDto<List<BlogRespDto>> result = PageDtoUtil.toPageDto(blogPoPage);
        return result;
    }

    private void fillAccountInfo(List<BlogRespDto> records) {
        List<Long> userIds = records.stream().map(BlogRespDto::getUserId).collect(Collectors.toList());
        List<AccountRespDto> accountList = ApiUtil.checkAndGetData(accountRestApi.queryAccountInfo(userIds));
        if (CollectionUtils.isNotEmpty(accountList)) {
            Map<Long, AccountRespDto> map = accountList.stream().collect(Collectors.toMap(AccountRespDto::getId, Function.identity()));
            for (BlogRespDto item : records) {
                AccountRespDto accountRespDto = map.get(item.getUserId());
                if (null == accountRespDto) {
                    continue;
                }
                item.build(accountRespDto);
            }
        }
    }

    private PageDto<List<BlogRespDto>> queryDraft(BlogQueryReqDto dto) {
        Page page = new Page(dto.getCurrentPage(), dto.getPageSize());
        DraftBlogPO po = new DraftBlogPO();
        po.setUserId(UserInfoUtil.getUserId());
        QueryWrapper<DraftBlogPO> wrap = po.wrap(DraftBlogPO.class);
        if (StringUtils.isNotBlank(dto.getKeyword())) {
            wrap.like("content", dto.getKeyword());
        }
        IPage iPage = draftBlogMapper.selectPage(page, wrap);
        return PageDtoUtil.toPageDto(iPage, (s) -> {
            BlogRespDto t = new BlogRespDto();
            BeanUtils.copyProperties(s, t);
            return t;
        });
    }

    @Override
    public BlogRespDto queryBlogById(Long id) {
        BlogPO blogPO = blogMapper.selectById(id);
        if (null == blogPO) {
            return null;
        }

        BlogRespDto dto = new BlogRespDto();
        dto.build(blogPO);
        fillTag(dto);
        fillAccountInfo(Collections.singletonList(dto));
        return dto;
    }

    private void fillTag(BlogRespDto blog) {
        //查询标签
        BlogTagPO po = new BlogTagPO();
        po.setBlogId(blog.getId());
        List<BlogTagPO> pos = blogTagMapper.selectList(po.wrap(BlogTagPO.class));
        if (CollectionUtils.isEmpty(pos)) {
            return;
        }
        List<TagDto> tags = new ArrayList<>();
        for (BlogTagPO t : pos) {
            TagDto dto = new TagDto();
            dto.setTag(t.getTagName());
            tags.add(dto);
        }
        blog.setTagList(tags);
    }

    public int migrate() {
        List<BlogPO> blogPOs = blogMapper.selectList(new QueryWrapper<>());
        //List<BlogPO> blogPOs = blogMapper.queryOldData();
        List<BlogPO> list = blogPOs.stream().map(s -> {
            BlogPO t = new BlogPO();
            BeanUtils.copyProperties(s, t);
            t.setAbstractContent(handleAbstractContent(s.getContent()));
            return t;
        }).collect(Collectors.toList());
        for (BlogPO item : list) {
            blogMapper.updateById(item);
        }
        return blogPOs.size();
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

    public static void main(String[] args) {
        BlogPO po = new BlogPO();
        po.setId(1L);
        po.setTitle("abc");
        po.setType(2);
        QueryWrapper<BlogPO> wrap = po.wrap(BlogPO.class);
        System.out.println(wrap);

        QueryWrapper wrap2 = new QueryWrapper();
        wrap2.eq("id", 234234l);
        System.out.println(wrap2);
    }
}

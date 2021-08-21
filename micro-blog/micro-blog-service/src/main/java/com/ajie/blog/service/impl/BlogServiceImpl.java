package com.ajie.blog.service.impl;

import com.ajie.blog.account.api.dto.AccountRespDto;
import com.ajie.blog.account.api.rest.AccountRestApi;
import com.ajie.blog.api.constant.BlogConstant;
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
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class BlogServiceImpl implements BlogService, TableConstant, BlogConstant {
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
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long save(BlogReqDto dto) {
        ParamCheck.assertNull(dto.getTitle(), BlogException.paramError("标题"));
        ParamCheck.assertNull(dto.getContent(), BlogException.paramError("内容"));
        ParamCheck.assertNull(dto.getTagList(), BlogException.paramError("标签"));

        BlogPO po = new BlogPO();
        BeanUtils.copyProperties(dto, po);
        po.setUserId(UserInfoUtil.getUserId());
        //处理摘要
        String ac = handleAbstractContent(dto.getContent());
        po.setAbstractContent(ac);
        Long id = dto.getId();
        //如果是草稿，也是不能查出来的
        BlogPO blogPO = blogMapper.selectById(id);
        if (null != blogPO) {
            //更新
            blogMapper.updateById(po);
        } else {
            //如果是数据库已存在的草稿发布，那么参数里的id是会有值的，但是是草稿的id，所以这里置空一下
            po.setId(null);
            blogMapper.insert(po);
        }
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
        if (CollectionUtils.isEmpty(tagList)) {
            return;
        }
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
            blogTag.setTagName(t.getTag());
            blogTagMapper.insert(blogTag);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveDraft(DraftBlogReqDto blog) {
        DraftBlogPO po = new DraftBlogPO();
        //查询是否有该草稿
        Long id = blog.getId();
        BeanUtils.copyProperties(blog, po, "id");
        boolean isUpdate = false;
        if (null == id) {
            //新建时保存草稿
            //没有，新建
            draftBlogMapper.insert(po);
        } else {
            isUpdate = saveDraftByExistId(po, blog);
        }
        //保存标签，因为标签列表会联表查询mb_blog_tag中间表并过滤数量为0的，所以保存草稿时将标签插入了也没事
        handleTag(blog.getTagList(), po.getId(), isUpdate);
        return po.getId();
    }

    /**
     * @param po
     * @param dto
     * @return 是否更新
     */
    private boolean saveDraftByExistId(DraftBlogPO po, DraftBlogReqDto dto) {
        //有id，有两种情况，一种id是草稿的id（编辑草稿），一种是博文id（编辑时保存为草稿）
        //1尝试查询草稿，看看有没有
        Long id = dto.getId();
        DraftBlogPO draftBlogPO = draftBlogMapper.selectById(id);
        if (null != draftBlogPO) {
            //更新草稿
            po.setId(id);
            draftBlogMapper.updateById(po);
            return true;
        }
        //尝试使用博文id查询草稿
        DraftBlogPO query = new DraftBlogPO();
        query.setRefBlogId(id);
        draftBlogPO = draftBlogMapper.selectOne(query.wrap(DraftBlogPO.class));
        if (null == draftBlogPO) {
            //没有，该博文第一次保存草稿
            po.setRefBlogId(id);
            draftBlogMapper.insert(po);
            return false;
        }
        //该文章对应的草稿已经存在
        if (dto.isForceSave()) {
            //强制保存
            po.setId(draftBlogPO.getId());
            draftBlogMapper.updateById(po);
            return true;
        }
        throw new BlogException(BlogExceptionEmun.RECORD_EXIST.getCode(), "草稿已存在");
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
            wrap.in("tag_id", dto.getTagList().toArray());
            List<BlogTagPO> btp = blogTagMapper.selectList(wrap);
            blogIds = btp.stream().map(BlogTagPO::getBlogId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(blogIds)) {
                return PageDto.empty();
            }
        }
        IPage<BlogRespDto> blogPoPage = blogMapper.queryByPage(page, dto, blogIds, UserInfoUtil.getUserId());
        List<BlogRespDto> records = blogPoPage.getRecords();
        try {
            fillAccountInfo(records);
            fillCount(records);
        } catch (Exception e) {//不要影响主业务
            logger.warn("填充信息失败", e);
        }

        PageDto<List<BlogRespDto>> result = PageDtoUtil.toPageDto(blogPoPage);
        return result;
    }

    private void fillAccountInfo(List<BlogRespDto> records) {
        List<Long> userIds = records.stream().map(BlogRespDto::getUserId).distinct().collect(Collectors.toList());
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

    /**
     * 填充阅读数和评论数
     */
    private void fillCount(List<BlogRespDto> records) {
        for (BlogRespDto dto : records) {
            String rc = (String) stringRedisTemplate.opsForHash().get(READ_COUNT_KEY, String.valueOf(dto.getId()));
            if (StringUtils.isNotBlank(rc)) {
                dto.setReadCount(Integer.valueOf(rc));
            }
            String cc = (String) stringRedisTemplate.opsForHash().get(COMMENT_COUNT_KEY, String.valueOf(dto.getId()));
            if (StringUtils.isNotBlank(cc)) {
                dto.setCommentCount(Integer.valueOf(cc));
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
        //是否私有博客
        if (blogPO.getType() == TYPE_PRIVATE && (!blogPO.getUserId().equals(UserInfoUtil.getUserId()))) {
            return null;
        }
        BlogRespDto dto = new BlogRespDto();
        dto.build(blogPO);
        fillTag(dto);
        try {
            //阅读数+1
            long rc = stringRedisTemplate.opsForHash().increment(READ_COUNT_KEY, String.valueOf(dto.getId()), 1L);
            dto.setReadCount((int) rc);
            fillAccountInfo(Collections.singletonList(dto));
        } catch (Exception e) {
            //不要影响主业务
            logger.warn("获取用户信息失败", e);
        }
        return dto;
    }

    @Override
    public Integer togglePrivate(Long id, Integer type) {
        BlogPO blogPO = blogMapper.selectById(id);
        if (null == blogPO) {
            throw BlogException.of(BlogExceptionEmun.PARAM_ERROR.getCode(), "找不到文章：" + id);
        }
        if (null == type || (type != 1 && type != 2)) {
            throw BlogException.paramError(String.valueOf(type));
        }
        Integer t = blogPO.getType();
        if (t == type) {
            return 0;
        }
        blogPO.setType(type);
        return blogMapper.updateById(blogPO);
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

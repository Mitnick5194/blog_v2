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
        ParamCheck.assertNull(dto.getTitle(), BlogException.paramError("??????"));
        ParamCheck.assertNull(dto.getContent(), BlogException.paramError("??????"));
        ParamCheck.assertNull(dto.getTagList(), BlogException.paramError("??????"));

        BlogPO po = new BlogPO();
        BeanUtils.copyProperties(dto, po);
        po.setUserId(UserInfoUtil.getUserId());
        //????????????
        String ac = handleAbstractContent(dto.getContent());
        po.setAbstractContent(ac);
        Long id = dto.getId();
        //??????????????????????????????????????????
        BlogPO blogPO = blogMapper.selectById(id);
        if (null != blogPO) {
            //??????
            blogMapper.updateById(po);
        } else {
            //???????????????????????????????????????????????????????????????id????????????????????????????????????id???????????????????????????
            po.setId(null);
            blogMapper.insert(po);
        }
        handleTag(dto.getTagList(), po.getId(), null != blogPO);
        return po.getId();
    }

    /**
     * ????????????
     *
     * @param tagList    ??????
     * @param blogId     ??????id
     * @param delBlogTag ??????????????????????????????????????????????????????
     */
    private void handleTag(List<TagDto> tagList, Long blogId, boolean delBlogTag) {
        if (CollectionUtils.isEmpty(tagList)) {
            return;
        }
        //????????????
        tagService.createTags(tagList);
        if (delBlogTag) {
            //???????????????
            BlogTagPO blogTag = new BlogTagPO();
            blogTag.setBlogId(blogId);
            List<BlogTagPO> pos = blogTagMapper.selectList(blogTag.wrap(BlogTagPO.class));
            for (BlogTagPO p : pos) {
                blogTagMapper.deleteById(p.getId());
            }
        }
        //???????????????
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
        //????????????????????????
        Long id = blog.getId();
        BeanUtils.copyProperties(blog, po, "id");
        boolean isUpdate = false;
        if (null == id) {
            //?????????????????????
            //???????????????
            draftBlogMapper.insert(po);
        } else {
            isUpdate = saveDraftByExistId(po, blog);
        }
        //????????????????????????????????????????????????mb_blog_tag???????????????????????????0??????????????????????????????????????????????????????
        handleTag(blog.getTagList(), po.getId(), isUpdate);
        return po.getId();
    }

    /**
     * @param po
     * @param dto
     * @return ????????????
     */
    private boolean saveDraftByExistId(DraftBlogPO po, DraftBlogReqDto dto) {
        //???id???????????????????????????id????????????id????????????????????????????????????id??????????????????????????????
        //1????????????????????????????????????
        Long id = dto.getId();
        DraftBlogPO draftBlogPO = draftBlogMapper.selectById(id);
        if (null != draftBlogPO) {
            //????????????
            po.setId(id);
            draftBlogMapper.updateById(po);
            return true;
        }
        //??????????????????id????????????
        DraftBlogPO query = new DraftBlogPO();
        query.setRefBlogId(id);
        draftBlogPO = draftBlogMapper.selectOne(query.wrap(DraftBlogPO.class));
        if (null == draftBlogPO) {
            //???????????????????????????????????????
            po.setRefBlogId(id);
            draftBlogMapper.insert(po);
            return false;
        }
        //????????????????????????????????????
        if (dto.isForceSave()) {
            //????????????
            po.setId(draftBlogPO.getId());
            draftBlogMapper.updateById(po);
            return true;
        }
        throw new BlogException(BlogExceptionEmun.RECORD_EXIST.getCode(), "???????????????");
    }

    @Override
    public Integer deleteById(Long id) {
        BlogPO blogPo = blogMapper.selectById(id);
        if (null == blogPo) {
            throw BlogException.of(BlogExceptionEmun.PARAM_ERROR.getCode(), "??????????????????" + id);
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
            //?????????????????????
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
        } catch (Exception e) {//?????????????????????
            logger.warn("??????????????????", e);
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
     * ???????????????????????????
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
        //??????????????????
        if (blogPO.getType() == TYPE_PRIVATE && (!blogPO.getUserId().equals(UserInfoUtil.getUserId()))) {
            return null;
        }
        BlogRespDto dto = new BlogRespDto();
        dto.build(blogPO);
        fillTag(dto);
        try {
            //?????????+1
            long rc = stringRedisTemplate.opsForHash().increment(READ_COUNT_KEY, String.valueOf(dto.getId()), 1L);
            dto.setReadCount((int) rc);
            fillAccountInfo(Collections.singletonList(dto));
        } catch (Exception e) {
            //?????????????????????
            logger.warn("????????????????????????", e);
        }
        return dto;
    }

    @Override
    public Integer togglePrivate(Long id, Integer type) {
        BlogPO blogPO = blogMapper.selectById(id);
        if (null == blogPO) {
            throw BlogException.of(BlogExceptionEmun.PARAM_ERROR.getCode(), "??????????????????" + id);
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
        //????????????
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
     * ??????????????????html??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????<br>
     * ????????????200???
     *
     * @param content
     */
    private String handleAbstractContent(String content) {
        // ??????400
        if (content.length() > 400) {
            content = content.substring(0, 399);
        }
        StringBuilder sb = new StringBuilder();
        // ????????????????????????
        content = HtmlFilter.filterHtml(content, sb);
        if (content.length() > 200) {
            content = content.substring(0, 200);// ?????????200??????
        }
        // ???????????????<div class='' ????????????????????????
        for (int i = content.length() - 1, j = 0; i >= 0; i--) {
            if (++j == 50) { // ????????????50?????????
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

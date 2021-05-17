package com.ajie.commons.po;

import com.ajie.commons.utils.UserInfoUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础持久PO
 */
public class BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人
     */
    private String createPerson;
    /**
     * 更新人
     */
    private String updatePerson;

    /**
     * 逻辑删除标志，1删除，0未删除
     */
    private int del = 0;

    /**
     * 填充公共字段，一般在插入前一步执行
     */
    public void createFill() {
        Long userId = UserInfoUtil.get().getId();
        if (null != userId) {
            this.createPerson = String.valueOf(userId);
            this.updatePerson = String.valueOf(userId);
        }
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public void updateFill() {
        this.updatePerson = String.valueOf(UserInfoUtil.get().getId());
        this.updateTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }
}

package com.ipeaksoft.moneyday.core.entity;

import java.util.Date;

public class CommPlayback {
    private Integer id;

    private String subject;

    private Date playCreateTime;

    private String url;

    private Integer webinarId;

    private Integer duration;

    private String webinarSubject;

    private Integer status;

    private Integer isDefault;

    private Date createtime;

    private Date updatetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getPlayCreateTime() {
        return playCreateTime;
    }

    public void setPlayCreateTime(Date playCreateTime) {
        this.playCreateTime = playCreateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWebinarId() {
        return webinarId;
    }

    public void setWebinarId(Integer webinarId) {
        this.webinarId = webinarId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getWebinarSubject() {
        return webinarSubject;
    }

    public void setWebinarSubject(String webinarSubject) {
        this.webinarSubject = webinarSubject;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}
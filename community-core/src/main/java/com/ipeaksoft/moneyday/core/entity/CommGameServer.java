package com.ipeaksoft.moneyday.core.entity;

public class CommGameServer {
    private Integer id;

    private Long startTime;

    private Integer oaAppId;

    private String serCode;

    private String serName;

    private String serDesc;

    private Byte status;

    private Byte isDelete;

    private Long createTime;

    private Long updateTime;

    private Integer parentId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Integer getOaAppId() {
        return oaAppId;
    }

    public void setOaAppId(Integer oaAppId) {
        this.oaAppId = oaAppId;
    }

    public String getSerCode() {
        return serCode;
    }

    public void setSerCode(String serCode) {
        this.serCode = serCode;
    }

    public String getSerName() {
        return serName;
    }

    public void setSerName(String serName) {
        this.serName = serName;
    }

    public String getSerDesc() {
        return serDesc;
    }

    public void setSerDesc(String serDesc) {
        this.serDesc = serDesc;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
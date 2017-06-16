package com.ipeaksoft.moneyday.core.entity;

import java.util.Date;

public class CommMemCash {
    private Long id;

    private String orderid;

    private String description;

    private String openid;

    private String realName;

    private Integer amount;

    private Integer totalcredits;

    private String status;

    private Date operateTime;

    private String operateResult;

    private Integer operator;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getTotalcredits() {
        return totalcredits;
    }

    public void setTotalcredits(Integer totalcredits) {
        this.totalcredits = totalcredits;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateResult() {
        return operateResult;
    }

    public void setOperateResult(String operateResult) {
        this.operateResult = operateResult;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
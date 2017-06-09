package com.ipeaksoft.moneyday.core.entity;

public class CommMemOrder {
    private Integer id;

    private Integer platId;

    private Integer groleId;

    private Integer ip;

    private Long time;

    private String deviceId;

    private Byte from;

    private String orderId;

    private String payway;

    private Double realAmount;

    private Double amount;

    private Double gmCnt;

    private Byte status;

    private Double rebateCnt;

    private String roleId;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlatId() {
        return platId;
    }

    public void setPlatId(Integer platId) {
        this.platId = platId;
    }

    public Integer getGroleId() {
        return groleId;
    }

    public void setGroleId(Integer groleId) {
        this.groleId = groleId;
    }

    public Integer getIp() {
        return ip;
    }

    public void setIp(Integer ip) {
        this.ip = ip;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Byte getFrom() {
        return from;
    }

    public void setFrom(Byte from) {
        this.from = from;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayway() {
        return payway;
    }

    public void setPayway(String payway) {
        this.payway = payway;
    }

    public Double getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(Double realAmount) {
        this.realAmount = realAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getGmCnt() {
        return gmCnt;
    }

    public void setGmCnt(Double gmCnt) {
        this.gmCnt = gmCnt;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Double getRebateCnt() {
        return rebateCnt;
    }

    public void setRebateCnt(Double rebateCnt) {
        this.rebateCnt = rebateCnt;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
package com.ipeaksoft.moneyday.core.entity;

import java.util.Date;

public class CommUserDay {
    private Integer id;

    private Integer userid;

    private Integer todayregisternum;

    private Integer todayrechargenum;

    private Double todaycommission;

    private Date time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getTodayregisternum() {
        return todayregisternum;
    }

    public void setTodayregisternum(Integer todayregisternum) {
        this.todayregisternum = todayregisternum;
    }

    public Integer getTodayrechargenum() {
        return todayrechargenum;
    }

    public void setTodayrechargenum(Integer todayrechargenum) {
        this.todayrechargenum = todayrechargenum;
    }

    public Double getTodaycommission() {
        return todaycommission;
    }

    public void setTodaycommission(Double todaycommission) {
        this.todaycommission = todaycommission;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
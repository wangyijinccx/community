package com.ipeaksoft.moneyday.core.entity;

public class CommHost {
    private Integer id;

    private String account;

    private String pass;

    private String mobile;

    private String subject;

    private String introduction;

    private String imgUrl;

    private Integer onlinestatus;

    private Integer webinarId;

    private String nickname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getOnlinestatus() {
        return onlinestatus;
    }

    public void setOnlinestatus(Integer onlinestatus) {
        this.onlinestatus = onlinestatus;
    }

    public Integer getWebinarId() {
        return webinarId;
    }

    public void setWebinarId(Integer webinarId) {
        this.webinarId = webinarId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
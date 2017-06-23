package com.ipeaksoft.moneyday.core.enums;
public enum MessageEnum {
	trading("交易消息"),
	tradingpromate("玩家%s在%s,为您提供一笔%s元的收益"),
	tradingmaster("徒弟%s，为您提供一笔%s元的收益"),
	tradinghost("徒弟%s，为您提供一笔%s元的收益"),
    system("系统消息"),
    template1("您有一笔%s元的收益到账"),
    template2("您成功提现%s元"),
    template3("身份认证成功")
    ;

    private final String key;

    private MessageEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
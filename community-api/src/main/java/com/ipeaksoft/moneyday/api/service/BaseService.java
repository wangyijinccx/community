package com.ipeaksoft.moneyday.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final Integer withdrawalNumber = 3;
}
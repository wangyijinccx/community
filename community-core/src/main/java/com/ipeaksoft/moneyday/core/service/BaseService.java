package com.ipeaksoft.moneyday.core.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final Integer withdrawalNumber = 3;
	public static final BigDecimal promoterCommission = new BigDecimal(0.3);
	public static final BigDecimal masterCommission = new BigDecimal(0.03);
	public static final BigDecimal hostCommission = new BigDecimal(0.01);

	// protected Logger logger;
	// protected String classname;
	//
	// public BaseService() {
	// classname = getClass().getName();
	// logger = LoggerFactory.getLogger(classname);
	// }

}
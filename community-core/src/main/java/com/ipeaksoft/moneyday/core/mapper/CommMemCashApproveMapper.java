package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommMemCashApprove;

public interface CommMemCashApproveMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CommMemCashApprove record);

    int insertSelective(CommMemCashApprove record);

    CommMemCashApprove selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CommMemCashApprove record);

    int updateByPrimaryKey(CommMemCashApprove record);
}
package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Address;
public interface AddressService{


    int deleteByPrimaryKey(Integer id);

    int insert(Address record);

    int insertSelective(Address record);

    Address selectByPrimaryKey(Integer id);

    Address selectByFaultConfigId(Integer id);

    int updateByPrimaryKeySelective(Address record);

    int updateByPrimaryKey(Address record);

}

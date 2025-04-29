package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Address record);

    int insertSelective(Address record);

    Address selectByPrimaryKey(Integer id);

    Address selectByFaultConfigId(Integer id);

    int updateByPrimaryKeySelective(Address record);

    int updateByPrimaryKey(Address record);
}
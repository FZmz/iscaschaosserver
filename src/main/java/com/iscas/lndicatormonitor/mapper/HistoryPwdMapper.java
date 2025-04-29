package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.HistoryPwd;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryPwdMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HistoryPwd record);

    int insertSelective(HistoryPwd record);

    HistoryPwd selectByPrimaryKey(Integer id);

    List<HistoryPwd> selectLastFiveByUserId(Integer userId);

    int updateByPrimaryKeySelective(HistoryPwd record);

    int updateByPrimaryKey(HistoryPwd record);
}
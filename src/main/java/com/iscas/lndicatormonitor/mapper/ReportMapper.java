package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper extends BaseMapper<Report> {
    int deleteByPrimaryKey(Integer id);

    int insert(Report record);

    int insertSelective(Report record);

    Report selectByPrimaryKey(Integer id);
    List<Report> selectAll();
    int updateByPrimaryKeySelective(Report record);

    int updateByPrimaryKey(Report record);
    int checkRecordIdExists(Integer recordId );
}
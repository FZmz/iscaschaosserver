package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Report;
import com.iscas.lndicatormonitor.domain.Statebound;

import java.util.List;

public interface ReportService extends IService<Report> {


    int deleteByPrimaryKey(Integer id);

    int insert(Report record);

    int insertSelective(Report record);

    Report selectByPrimaryKey(Integer id);

    List<Report> selectAll();
    int updateByPrimaryKeySelective(Report record);

    int updateByPrimaryKey(Report record);

    int checkRecordIdExists(int recordId);


}

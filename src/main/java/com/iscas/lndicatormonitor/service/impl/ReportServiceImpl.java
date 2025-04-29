package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Statebound;
import com.iscas.lndicatormonitor.mapper.StateboundMapper;
import com.iscas.lndicatormonitor.service.StateboundService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.mapper.ReportMapper;
import com.iscas.lndicatormonitor.domain.Report;
import com.iscas.lndicatormonitor.service.ReportService;

import java.util.List;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService  {

    @Resource
    private ReportMapper reportMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return reportMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Report record) {
        return reportMapper.insert(record);
    }

    @Override
    public int insertSelective(Report record) {
        return reportMapper.insertSelective(record);
    }

    @Override
    public Report selectByPrimaryKey(Integer id) {
        return reportMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Report> selectAll() {
        return reportMapper.selectAll();
    }

    @Override
    public int updateByPrimaryKeySelective(Report record) {
        return reportMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Report record) {
        return reportMapper.updateByPrimaryKey(record);
    }

    @Override
    public int checkRecordIdExists(int recordId) {
        return reportMapper.checkRecordIdExists(recordId);
    }

}

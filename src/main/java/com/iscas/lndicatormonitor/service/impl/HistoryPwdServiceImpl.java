package com.iscas.lndicatormonitor.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.mapper.HistoryPwdMapper;
import com.iscas.lndicatormonitor.domain.HistoryPwd;
import com.iscas.lndicatormonitor.service.HistoryPwdService;

import java.util.List;

@Service
public class HistoryPwdServiceImpl implements HistoryPwdService{

    @Resource
    private HistoryPwdMapper historyPwdMapper;


    @Override
    public List<HistoryPwd> selectLastFiveByUserId(Integer userId) {
        List<HistoryPwd> historyPwdList = historyPwdMapper.selectLastFiveByUserId(userId);
        return historyPwdList;
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return historyPwdMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(HistoryPwd record) {
        return historyPwdMapper.insert(record);
    }

    @Override
    public int insertSelective(HistoryPwd record) {
        return historyPwdMapper.insertSelective(record);
    }

    @Override
    public HistoryPwd selectByPrimaryKey(Integer id) {
        return historyPwdMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(HistoryPwd record) {
        return historyPwdMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(HistoryPwd record) {
        return historyPwdMapper.updateByPrimaryKey(record);
    }

}

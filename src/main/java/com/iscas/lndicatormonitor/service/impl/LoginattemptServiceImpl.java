package com.iscas.lndicatormonitor.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.mapper.LoginattemptMapper;
import com.iscas.lndicatormonitor.domain.Loginattempt;
import com.iscas.lndicatormonitor.service.LoginattemptService;
@Service
public class LoginattemptServiceImpl implements LoginattemptService{

    @Resource
    private LoginattemptMapper loginattemptMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return loginattemptMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Loginattempt record) {
        return loginattemptMapper.insert(record);
    }

    @Override
    public int insertSelective(Loginattempt record) {
        return loginattemptMapper.insertSelective(record);
    }

    @Override
    public Loginattempt selectByPrimaryKey(Integer id) {
        return loginattemptMapper.selectByPrimaryKey(id);
    }

    @Override
    public Loginattempt selectByUserId(Integer userId) {
        return loginattemptMapper.selectByUserId(userId);
    }

    @Override
    public int updateByPrimaryKeySelective(Loginattempt record) {
        return loginattemptMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Loginattempt record) {
        return loginattemptMapper.updateByPrimaryKey(record);
    }

}

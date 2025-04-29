package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.IndexRecommend.Faults;
import com.iscas.lndicatormonitor.service.FaultsService;
import com.iscas.lndicatormonitor.mapper.FaultsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author mj
* @description 针对表【faults】的数据库操作Service实现
* @createDate 2024-12-02 16:44:17
*/
@Service
public class FaultsServiceImpl extends ServiceImpl<FaultsMapper, Faults>
    implements FaultsService{
    @Autowired
    private  FaultsMapper faultsMapper;

    @Override
    public Faults getFaultByName(String faultName) {
        // 使用 MyBatis-Plus 提供的 QueryWrapper 简化查询
        return faultsMapper.selectOne(new QueryWrapper<Faults>().eq("fault_name", faultName));
    }
}





package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Nodeagent;
import com.iscas.lndicatormonitor.domain.Selector;

public interface SelectorService{


    int deleteByPrimaryKey(Integer id);

    int insert(Selector record);

    int insertSelective(Selector record);

    Selector selectByPrimaryKey(Integer id);

    Selector selectByFaultConfigKey(Integer faultConfigId);
    int updateByPrimaryKeySelective(Selector record);

    int updateByPrimaryKey(Selector record);

    /**
    * @author mj
    * @description 针对表【nodeagent】的数据库操作Service
    * @createDate 2024-10-17 15:37:46
    */
    interface NodeagentService extends IService<Nodeagent> {

    }
}

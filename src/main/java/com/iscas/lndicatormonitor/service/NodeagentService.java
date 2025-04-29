package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Nodeagent;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author mj
* @description 针对表【nodeagent】的数据库操作Service
* @createDate 2024-10-22 22:01:06
*/
public interface NodeagentService extends IService<Nodeagent> {

    /**
     * 根据 agentName 获取 Nodeagent 详细信息
     *
     * @param agentName 唯一的 agentName
     * @return Nodeagent 对象，包含详细信息
     */
    Nodeagent getNodeagentByName(String agentName);

}

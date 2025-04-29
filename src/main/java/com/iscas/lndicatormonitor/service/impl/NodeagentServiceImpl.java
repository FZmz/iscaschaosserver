package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Nodeagent;
import com.iscas.lndicatormonitor.mapper.NodeagentMapper;
import com.iscas.lndicatormonitor.service.NodeagentService;
import org.springframework.stereotype.Service;

/**
* @author mj
* @description 针对表【nodeagent】的数据库操作Service实现
* @createDate 2024-10-22 22:01:06
*/
@Service
public class NodeagentServiceImpl extends ServiceImpl<NodeagentMapper, Nodeagent>
    implements NodeagentService{

    @Override
    public Nodeagent getNodeagentByName(String agentName) {
        return baseMapper.selectByAgentName(agentName);
    }
}





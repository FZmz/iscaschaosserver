package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Statebound;
import com.iscas.lndicatormonitor.mapper.StateboundMapper;
import com.iscas.lndicatormonitor.service.StateboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author mj
* @description 针对表【stateBound】的数据库操作Service实现
* @createDate 2024-10-22 22:55:13
*/
@Service
public class StateboundServiceImpl extends ServiceImpl<StateboundMapper, Statebound> implements StateboundService {

    @Autowired
    private StateboundMapper stateboundMapper;

    /**
     * 根据 boundId 查询 Statebound 列表
     *
     * @param boundId 绑定ID
     * @return 查询到的 Statebound 实体对象列表
     */
    public List<Statebound> getStateboundsByBoundId(String boundId) {
        QueryWrapper<Statebound> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bound_id", boundId); // 使用字段名和查询条件
        return stateboundMapper.selectList(queryWrapper);
    }

    /**
     * 从查询结果中过滤出所有 steadyId
     *
     * @param boundId 绑定ID
     * @return 查询到的 steadyId 列表
     */
    public List<String> getSteadyIdsByBoundId(String boundId) {
        // 查询符合条件的 Statebound 列表
        List<Statebound> statebounds = getStateboundsByBoundId(boundId);
        // 使用 Java Stream API 提取 steadyId
        return statebounds.stream()
                .map(Statebound::getSteadyId) // 提取 steadyId 字段
                .filter(steadyId -> steadyId != null && !steadyId.isEmpty()) // 过滤掉 null 和空值
                .distinct() // 去重
                .collect(Collectors.toList()); // 收集到列表
    }
}





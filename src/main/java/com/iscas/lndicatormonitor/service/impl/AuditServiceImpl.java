package com.iscas.lndicatormonitor.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import com.iscas.lndicatormonitor.mapper.ApplicationMapper;
import com.iscas.lndicatormonitor.mapper.AuditMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Application;
import com.iscas.lndicatormonitor.domain.Audit;
import com.iscas.lndicatormonitor.dto.AuditQueryCriteria;
import com.iscas.lndicatormonitor.service.AuditService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuditServiceImpl extends ServiceImpl<AuditMapper, Audit> implements AuditService{

    @Resource
    private AuditMapper auditMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return auditMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Audit record) {
        return auditMapper.insert(record);
    }

    @Override
    public int insertSelective(Audit record) {
        return auditMapper.insertSelective(record);
    }

    @Override
    public List<Audit> selectAllAudit() {
        return auditMapper.selectAllAudit();
    }

    @Override
    public Audit selectByPrimaryKey(Integer id) {
        return auditMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Audit record) {
        return auditMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Audit record) {
        return auditMapper.updateByPrimaryKey(record);
    }

    @Override
    public IPage<Audit> getAuditLogsByPage(
            Integer page, 
            Integer pageSize, 
            String startTime, 
            String endTime, 
            String username, 
            String operationType, 
            String operationName) {
        
        // 创建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Audit> pageObj = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
        
        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Audit> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        // 添加时间范围条件
        if (startTime != null && !startTime.isEmpty()) {
            queryWrapper.ge(Audit::getOperateTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            queryWrapper.le(Audit::getOperateTime, endTime);
        }
        
        // 添加用户名条件
        if (username != null && !username.isEmpty()) {
            queryWrapper.like(Audit::getUsername, username);
        }
        
        // 添加操作类型条件
        if (operationType != null && !operationType.isEmpty()) {
            queryWrapper.eq(Audit::getOperateResult, operationType);
        }
        
        // 添加操作名称条件
        if (operationName != null && !operationName.isEmpty()) {
            queryWrapper.like(Audit::getOperateName, operationName);
        }
        
        // 按操作时间降序排序
        queryWrapper.orderByDesc(Audit::getOperateTime);
        
        // 执行分页查询
        return page(pageObj, queryWrapper);
    }

    @Override
    public IPage<Audit> getAuditLogsByPage(AuditQueryCriteria criteria) {
        if (criteria == null) {
            criteria = new AuditQueryCriteria();
        }
        
        return getAuditLogsByPage(
            criteria.getPage(),
            criteria.getPageSize(),
            criteria.getStartTime(),
            criteria.getEndTime(),
            criteria.getUsername(),
            criteria.getOperationType(),
            criteria.getOperationName()
        );
    }
    
    @Override
    public Set<String> getAllOperationTypes() {
        // 获取所有审计日志
        List<Audit> allAudits = selectAllAudit();
        
        // 使用Stream API提取所有不为空的操作名称并去重
        Set<String> operationTypes = allAudits.stream()
            .map(Audit::getOperateName)
            .filter(name -> name != null && !name.isEmpty())
            .collect(Collectors.toSet());
        
        return operationTypes;
    }
}

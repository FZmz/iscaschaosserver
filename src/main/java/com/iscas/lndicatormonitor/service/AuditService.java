package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Audit;
import com.iscas.lndicatormonitor.dto.AuditQueryCriteria;

import java.util.List;
import java.util.Set;

public interface AuditService extends IService<Audit>{
    int deleteByPrimaryKey(Integer id);

    int insert(Audit record);

    int insertSelective(Audit record);

    List<Audit> selectAllAudit();
    Audit selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Audit record);

    int updateByPrimaryKey(Audit record);

    /**
     * 根据条件分页查询审计日志
     * @param page 页码
     * @param pageSize 每页大小
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param username 用户名称
     * @param operationType 操作类型
     * @param operationName 操作名称
     * @return 分页审计日志列表
     */
    IPage<Audit> getAuditLogsByPage(
            Integer page, 
            Integer pageSize, 
            String startTime, 
            String endTime, 
            String username, 
            String operationType, 
            String operationName);
    
    /**
     * 根据查询条件对象分页查询审计日志
     * @param criteria 查询条件对象
     * @return 分页审计日志列表
     */
    IPage<Audit> getAuditLogsByPage(AuditQueryCriteria criteria);
    
    /**
     * 获取所有不同的操作类型
     * @return 操作类型集合
     */
    Set<String> getAllOperationTypes();
}

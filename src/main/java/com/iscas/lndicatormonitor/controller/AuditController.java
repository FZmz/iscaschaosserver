package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Audit;
import com.iscas.lndicatormonitor.dto.AuditQueryCriteria;
import com.iscas.lndicatormonitor.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    AuditService auditService;
    
    @GetMapping("/getAllAudit")
    @OperationLog("查询审计日志")
    public CustomResult getAll(){
        List<Audit> auditList = auditService.selectAllAudit();
        return new CustomResult(20000,"获取成功",auditList);
    }

    @GetMapping("/delAuditById")
    @OperationLog("删除审计日志")
    public CustomResult delAuditById(int id){
        try{
            auditService.deleteByPrimaryKey(id);
            return new CustomResult(20000,"删除成功",null);
        }catch (Exception e){
            return new CustomResult(40000,"删除失败",null);
        }
    }
    
    
    @PostMapping("/listByPage")
    @OperationLog("分页查询审计日志")
    public CustomResult queryAuditLogs(@RequestBody AuditQueryCriteria criteria) {
        try {
            IPage<Audit> auditPage = auditService.getAuditLogsByPage(criteria);
            return new CustomResult(20000, "查询成功", auditPage);
        } catch (Exception e) {
            return new CustomResult(40000, "查询失败: " + null, null);
        }
    }
    
    @GetMapping("/operationTypes")
    @OperationLog("获取所有审计操作类型")
    public CustomResult getAllOperationTypes() {
        try {
            Set<String> operationTypes = auditService.getAllOperationTypes();
            return new CustomResult(20000, "获取操作类型成功", operationTypes);
        } catch (Exception e) {
            return new CustomResult(40000, "获取操作类型失败: " + null, null);
        }
    }
}

package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Audit;
import com.iscas.lndicatormonitor.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/operate")
public class OperateController {
    @Autowired
    AuditService auditService;

    @PostMapping("/addRecord")
    @OperationLog("新增操作日志")
    public CustomResult addRecord(@RequestBody Audit audit){
        int result = auditService.insert(audit);
        if (result > 0){
            return new CustomResult(20000,"新增成功!",null);
        }else {
            return new CustomResult(40000,"新增失败!",null);
        }
    }

    @GetMapping("/delRecord")
    @OperationLog("删除操作日志")
    public CustomResult delRecord(int recordId){
        int result = auditService.deleteByPrimaryKey(recordId);
        if (result > 0){
            return new CustomResult(20000,"删除成功!",null);
        }else {
            return new CustomResult(40000,"删除失败!",null);
        }
    }
}

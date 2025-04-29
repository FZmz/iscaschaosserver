package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.recordv2.ChaosExerciseRecords;
import com.iscas.lndicatormonitor.dto.recordv2.ChaosExeRecordsDTO;
import com.iscas.lndicatormonitor.dto.recordv2.RecordListDTO;
import com.iscas.lndicatormonitor.dto.recordv2.Recordv2QueryCriteria;
import com.iscas.lndicatormonitor.dto.recordv2.WorkflowSummaryDTO;

import java.util.Map;

/**
* @author mj
* @description 针对表【chaos_exercise_records(混沌工程演练记录表)】的数据库操作Service
* @createDate 2025-01-19 22:41:43
*/
public interface ChaosExerciseRecordsService extends IService<ChaosExerciseRecords> {

    ChaosExeRecordsDTO getBasicInfo(Integer recordId);

    WorkflowSummaryDTO getWorkflowInfoByRecordId(Integer recordId);

    IPage<RecordListDTO> queryRecordList(Recordv2QueryCriteria criteria);

    Map<Integer, Long> countByRecordStatus();
}

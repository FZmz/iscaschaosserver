package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.scenario.FaultScenario;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author mj
* @description 针对表【fault_scenario(故障场景表)】的数据库操作Mapper
* @createDate 2025-01-22 23:10:23
* @Entity generator.domain.FaultScenario
*/
public interface FaultScenarioMapper extends BaseMapper<FaultScenario> {

    @Select("<script>SELECT DISTINCT scenario_id FROM fault_scenario_tags_relation WHERE tags_id IN <foreach collection='tagIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<Integer> getScenarioIdsByTagIds(@Param("tagIds") List<Integer> tagIds);
}





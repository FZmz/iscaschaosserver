package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.scenario.ScenarioTagsCorrelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author mj
* @description 针对表【scenario_tags_correlation】的数据库操作Mapper
* @createDate 2025-01-23 11:18:46
* @Entity generator.domain.ScenarioTagsCorrelation
*/
public interface ScenarioTagsCorrelationMapper extends BaseMapper<ScenarioTagsCorrelation> {
    @Select("<script>SELECT DISTINCT scenario_id FROM scenario_tags_correlation WHERE tags_id IN <foreach collection='tagIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<Integer> getScenarioIdsByTagIds(@Param("tagIds") List<Integer> tagIds);

}
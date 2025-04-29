package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Nodeagent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author mj
* @description 针对表【nodeagent】的数据库操作Mapper
* @createDate 2024-10-17 15:39:24
* @Entity com.iscas.lndicatormonitor.domain.Nodeagent
*/
@Mapper
public interface NodeagentMapper extends BaseMapper<Nodeagent> {

    @Select("SELECT * FROM nodeagent WHERE agent_name = #{agentName} AND is_delete = 0")
    Nodeagent selectByAgentName(@Param("agentName") String agentName);
}





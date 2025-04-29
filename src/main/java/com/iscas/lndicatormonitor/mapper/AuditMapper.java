package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Audit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuditMapper  extends BaseMapper<Audit> {
    int deleteByPrimaryKey(Integer id);

    int insert(Audit record);

    int insertSelective(Audit record);

    Audit selectByPrimaryKey(Integer id);

    List<Audit> selectAllAudit();
    int updateByPrimaryKeySelective(Audit record);

    int updateByPrimaryKey(Audit record);
}
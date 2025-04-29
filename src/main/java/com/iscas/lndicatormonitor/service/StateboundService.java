package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Statebound;

import java.util.List;

/**
* @author mj
* @description 针对表【stateBound】的数据库操作Service
* @createDate 2024-10-22 22:55:13
*/
public interface StateboundService extends IService<Statebound> {

    List<Statebound> getStateboundsByBoundId(String boundId);

    List<String> getSteadyIdsByBoundId(String boundId);


}

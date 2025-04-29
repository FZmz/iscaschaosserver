package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Load;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.LoadApi;
import com.iscas.lndicatormonitor.domain.LoadScript;
import com.iscas.lndicatormonitor.dto.load.LoadDTO;

import java.util.List;

/**
* @author mj
* @description 针对表【load】的数据库操作Service
* @createDate 2024-10-17 14:43:52
*/
public interface LoadService extends IService<Load> {
    boolean saveLoadDefinition(LoadDTO loadDTO);

    boolean updateLoadDefinition(LoadDTO loadDTO);

    List<LoadScript> getLoadScriptsByLoadId(String loadId);

    List<LoadApi> getLoadApisByLoadId(String loadId);

    boolean removeLoadAndRelatedDataById(String id);
}

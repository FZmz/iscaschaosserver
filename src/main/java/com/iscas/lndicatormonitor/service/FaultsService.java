package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.IndexRecommend.Faults;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author mj
* @description 针对表【faults】的数据库操作Service
* @createDate 2024-12-02 16:44:17
*/
public interface FaultsService extends IService<Faults> {

    Faults getFaultByName(String faultName);
}

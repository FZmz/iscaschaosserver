package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Service;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author mj
* @description 针对表【service】的数据库操作Service
* @createDate 2024-10-17 19:08:23
*/
public interface ServiceService extends IService<Service> {

    List<Service> getByApplicationId(String applicationId);
}

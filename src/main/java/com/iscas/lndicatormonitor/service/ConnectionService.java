package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Connection;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author mj
* @description 针对表【connection】的数据库操作Service
* @createDate 2024-10-17 19:08:23
*/

public interface ConnectionService extends IService<Connection> {

    List<Connection> getByApplicationId(String applicationId);
}

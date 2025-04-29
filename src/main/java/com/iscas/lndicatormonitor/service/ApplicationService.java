package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Application;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author mj
* @description 针对表【application】的数据库操作Service
* @createDate 2024-10-17 14:43:52
*/
public interface ApplicationService extends IService<Application> {
    
    /**
     * 新增应用
     * @param application 应用信息
     * @return CustomResult
     */
    CustomResult addApplication(Application application);

    /**
     * 更新应用
     * @param application 应用信息
     * @return CustomResult
     */
    CustomResult updateApplication(Application application);
}

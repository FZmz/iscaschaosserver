package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Application;
import com.iscas.lndicatormonitor.service.ApplicationService;
import com.iscas.lndicatormonitor.mapper.ApplicationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author mj
* @description 针对表【application】的数据库操作Service实现
* @createDate 2024-10-17 14:43:52
*/
@Slf4j
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application>
    implements ApplicationService {

    @Override
    public CustomResult addApplication(Application application) {
        try {
            // 1. 参数校验
            if (application == null) {
                return CustomResult.fail("应用信息不能为空");
            }
            if (!StringUtils.hasText(application.getName())) {
                return CustomResult.fail("应用名称不能为空");
            }

            // 2. 检查名称是否重复
            long count = this.lambdaQuery()
                    .eq(Application::getName, application.getName())
                    .count();
            
            if (count > 0) {
                return CustomResult.fail("应用名称已存在，请重新输入");
            }

            // 3. 保存应用
            boolean saved = this.save(application);
            if (!saved) {
                return CustomResult.fail("保存应用失败");
            }

            return CustomResult.ok(application.getId());
        } catch (Exception e) {
            log.error("新增应用失败", e);
            return CustomResult.fail("新增应用失败");
        }
    }

    @Override
    public CustomResult updateApplication(Application application) {
        try {
            // 1. 参数校验
            if (application == null || application.getId() == null) {
                return CustomResult.fail("应用信息不完整");
            }
            if (!StringUtils.hasText(application.getName())) {
                return CustomResult.fail("应用名称不能为空");
            }

            // 2. 检查名称是否重复（排除自身）
            long count = this.lambdaQuery()
                    .eq(Application::getName, application.getName())
                    .ne(Application::getId, application.getId())
                    .count();
            
            if (count > 0) {
                return CustomResult.fail("应用名称已存在，请重新输入");
            }

            // 3. 更新应用
            boolean updated = this.updateById(application);
            if (!updated) {
                return CustomResult.fail("更新应用失败");
            }

            return CustomResult.ok();
        } catch (Exception e) {
            log.error("更新应用失败，id={}", application.getId(), e);
            return CustomResult.fail("更新应用失败: " + e.getMessage());
        }
    }
}





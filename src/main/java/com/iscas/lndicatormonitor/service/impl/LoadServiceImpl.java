package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Load;
import com.iscas.lndicatormonitor.domain.LoadApi;
import com.iscas.lndicatormonitor.domain.LoadScript;
import com.iscas.lndicatormonitor.dto.load.LoadDTO;
import com.iscas.lndicatormonitor.service.LoadApiService;
import com.iscas.lndicatormonitor.service.LoadService;
import com.iscas.lndicatormonitor.mapper.LoadMapper;
import com.iscas.lndicatormonitor.service.LoadscriptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mj
 * @description 针对表【load】的数据库操作Service实现
 * @createDate 2024-10-17 14:43:52
 */
@Service
public class LoadServiceImpl extends ServiceImpl<LoadMapper, Load> implements LoadService {

    @Autowired
    private LoadscriptService loadscriptService;

    @Autowired
    private LoadApiService loadApiService;

    @Override
    public boolean saveLoadDefinition(LoadDTO loadDTO) {
        // 转换 LoadDTO 为 Load
        Load load = new Load();
        BeanUtils.copyProperties(loadDTO, load);

        // 保存 Load 对象
        load.setIsDelete(0);
        boolean loadResult = this.save(load);

        // 保存 LoadScript 列表
        if (loadDTO.getType() == 1) {
            for (LoadScript loadscript : loadDTO.getLoadScriptList()) {
                loadscript.setLoadId(load.getId());  // 关联负载ID
                loadscript.setIsDelete(0);
                loadscript.setCreateTime(loadDTO.getCreatTime());
                loadscriptService.save(loadscript);
            }
        }

        // 保存 LoadApi 列表
        if (loadDTO.getType() == 2) {
            for (LoadApi loadapi : loadDTO.getLoadApiList()) {
                loadapi.setLoadId(load.getId());  // 关联负载ID
                loadapi.setIsDelete(0);
                loadapi.setCreatedTime(loadDTO.getCreatTime());
                loadApiService.save(loadapi);
            }
        }

        return loadResult;
    }

    @Override
    public boolean updateLoadDefinition(LoadDTO loadDTO) {
        // 转换 LoadDTO 为 Load
        Load load = new Load();
        BeanUtils.copyProperties(loadDTO, load);

        // 更新 Load 对象
        boolean loadResult = this.updateById(load);

        // 更新 LoadScript 列表
        if (loadDTO.getLoadScriptList() != null) {
            for (LoadScript loadscript : loadDTO.getLoadScriptList()) {
                loadscript.setLoadId(loadDTO.getId());  // 关联负载ID
                loadscriptService.updateById(loadscript);
            }
        }

        // 更新 LoadApi 列表
        if (loadDTO.getLoadApiList() != null) {
            for (LoadApi loadapi : loadDTO.getLoadApiList()) {
                loadapi.setLoadId(loadDTO.getId());  // 关联负载ID
                loadApiService.updateById(loadapi);
            }
        }

        return loadResult;
    }

    @Override
    public boolean removeLoadAndRelatedDataById(String loadId) {
//        // 删除相关的脚本
//        QueryWrapper<LoadScript> scriptWrapper = new QueryWrapper<>();
//        scriptWrapper.eq("load_id", loadId);
//        loadscriptService.remove(scriptWrapper);
//
//        // 删除相关的 API
//        QueryWrapper<LoadApi> apiWrapper = new QueryWrapper<>();
//        apiWrapper.eq("load_id", loadId);
//        loadApiService.remove(apiWrapper);

        // 更新相关的脚本，将 idelete 字段设为 1
        UpdateWrapper<LoadScript> scriptWrapper = new UpdateWrapper<>();
        scriptWrapper.eq("load_id", loadId)
                .set("is_delete", 1);
        loadscriptService.update(scriptWrapper);

// 更新相关的 API，将 idelete 字段设为 1
        UpdateWrapper<LoadApi> apiWrapper = new UpdateWrapper<>();
        apiWrapper.eq("load_id", loadId)
                .set("is_delete", 1);
        loadApiService.update(apiWrapper);

        // 删除负载本身
        UpdateWrapper<Load> loadWrapper = new UpdateWrapper<>();
        loadWrapper.eq("id", loadId)
                .set("is_delete", 1);
        return  this.update(loadWrapper);
    }

    @Override
    public List<LoadScript> getLoadScriptsByLoadId(String loadId) {
        return loadscriptService.list(new QueryWrapper<LoadScript>().eq("load_id", loadId));
    }

    @Override
    public List<LoadApi> getLoadApisByLoadId(String loadId) {
        return loadApiService.list(new QueryWrapper<LoadApi>().eq("load_id", loadId));
    }
}



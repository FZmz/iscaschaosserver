package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.SteadyStateRepository;
import com.iscas.lndicatormonitor.mapper.SteadyStateRepositoryMapper;
import com.iscas.lndicatormonitor.service.SteadyStateRepositoryService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author mj
* @description 针对表【steady_state_repository】的数据库操作Service实现
* @createDate 2025-01-13 11:09:39
*/
@Service
public class SteadyStateRepositoryServiceImpl extends ServiceImpl<SteadyStateRepositoryMapper, SteadyStateRepository>
    implements SteadyStateRepositoryService {

}





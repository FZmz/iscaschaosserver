package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Application;
import com.iscas.lndicatormonitor.domain.Statebound;
import com.iscas.lndicatormonitor.domain.SteadyStateRepository;
import com.iscas.lndicatormonitor.domain.Steadystate;
import com.iscas.lndicatormonitor.dto.steadystate.SteadyResultDTO;
import com.iscas.lndicatormonitor.mapper.ApplicationMapper;
import com.iscas.lndicatormonitor.mapper.StateboundMapper;
import com.iscas.lndicatormonitor.mapper.SteadyStateRepositoryMapper;
import com.iscas.lndicatormonitor.service.SteadystateService;
import com.iscas.lndicatormonitor.mapper.SteadystateMapper;
import com.iscas.lndicatormonitor.strategy.steatystate.IMetricStrategy;
import com.iscas.lndicatormonitor.strategy.steatystate.MetricStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
* @author mj
* @description 针对表【steadystate】的数据库操作Service实现
* @createDate 2025-01-13 13:52:34
*/
@Slf4j
@Service
public class SteadystateServiceImpl extends ServiceImpl<SteadystateMapper, Steadystate>
        implements SteadystateService {

    @Autowired
    private StateboundMapper stateBoundMapper;
    @Autowired
    private SteadystateMapper steadystateMapper;
    @Autowired
    private SteadyStateRepositoryMapper steadyStateRepositoryMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationMapper applicationMapper;

    @Value("${coroot.url}")
    private String corootUrl;

    @Value("${coroot.cookie}")
    private String cookie;

    @Value("${coroot.projectId}")
    private String projectId;

    private static final Logger logger = Logger.getLogger(SteadystateServiceImpl.class);

    @Override
    public List<Map<String, Object>> getMetricsByPlanId(String planId, Long from, Long to) {
        logger.info(String.format("开始处理 getMetricsByPlanId, 参数: planId=%s, from=%d, to=%d", planId, from, to));
        List<Statebound> stateboundList = stateBoundMapper.selectList(
                new QueryWrapper<Statebound>()
                        .eq("bound_id", planId)
                        .eq("bound_type", 0) // 0 表示 workflow
        );
        logger.info(String.format("查询到 %d 个 Statebound 记录", stateboundList.size()));
        return processStateBoundList(stateboundList, from, to);
    }

    @Override
    public List<Map<String, Object>> getMetricsByFaultConfigId(String faultConfigId, Long from, Long to) {
        logger.info(String.format("开始处理 getMetricsByFaultConfigId, 参数: faultConfigId=%s, from=%d, to=%d", faultConfigId, from, to));
        List<Statebound> stateboundList = stateBoundMapper.selectList(
                new QueryWrapper<Statebound>()
                        .eq("bound_id", faultConfigId)
                        .eq("bound_type", 1) // 1 表示 faultConfig
        );
        logger.info(String.format("查询到 %d 个 Statebound 记录", stateboundList.size()));
        return processStateBoundList(stateboundList, from, to);
    }

    private List<Map<String, Object>> processStateBoundList(List<Statebound> stateboundList, Long from, Long to) {
        logger.info(String.format("开始处理 Statebound 列表，共 %d 个记录", stateboundList.size()));
        List<Map<String, Object>> result = new ArrayList<>();

        for (Statebound sb : stateboundList) {
            logger.info(String.format("处理 Statebound: id=%s, boundType=%d, boundId=%s", sb.getId(), sb.getBoundType(), sb.getBoundId()));

            // 查询 Steadystate
            Steadystate steadystate = steadystateMapper.selectById(sb.getSteadyId());
            if (steadystate == null) {
                logger.warn(String.format("未找到对应的 Steadystate, id=%s", sb.getSteadyId()));
                continue;
            }

            SteadyStateRepository repository = steadyStateRepositoryMapper.selectOne(
                    new QueryWrapper<SteadyStateRepository>()
                            .eq("id", steadystate.getSteadyStateId())
            );
            if (repository == null) {
                logger.warn(String.format("未找到对应的 SteadyStateRepository, steadyStateId=%s", steadystate.getSteadyStateId()));
                continue;
            }

            String namespace = getNamespaceBySteadyState(steadystate);
            String targetService = steadystate.getTargetService();
            String url = String.format("%s%s/app/%s:%s:%s?from=%d&to=%d",
                    corootUrl, projectId, namespace, "Deployment", targetService, from, to);
            logger.info(String.format("构建的请求 URL: %s", url));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, this.cookie);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

                if (responseEntity.getStatusCode() != HttpStatus.OK) {
                    logger.error(String.format("请求失败，状态码: %s", responseEntity.getStatusCode()));
                    continue;
                }

                String responseBody = responseEntity.getBody();
                logger.debug(String.format("收到响应: %s", responseBody));

                JsonNode rootNode = objectMapper.readTree(responseBody);

                // 使用策略计算指标
                IMetricStrategy strategy = MetricStrategyFactory.getStrategy(repository.getType());
                Object actualValue = strategy.compute(repository.getType(), repository.getSteadyStateName(), rootNode);

                // 对比期望值与实际值
                String comparator = steadystate.getComparator();
                boolean isSatisfied = compareValues(steadystate.getValue(), actualValue, comparator);
                String steadyUnit = repository.getSteadyStateUnit();

                // 构造 SteadyResultDTO
                SteadyResultDTO steadyResultDTO = new SteadyResultDTO();
                steadyResultDTO.setExpectedValue(steadystate.getValue());
                steadyResultDTO.setActualValue(actualValue);
                steadyResultDTO.setSteadyUnit(steadyUnit);
                steadyResultDTO.setComparator(comparator);
                steadyResultDTO.setIsSteadySatisfied(isSatisfied);

                logger.info(String.format("稳态结果: %s", steadyResultDTO));

                // 添加到返回结果
                Map<String, Object> mapForOneSteady = new HashMap<>();
                mapForOneSteady.put("steadyStateName", repository.getSteadyStateName());
                mapForOneSteady.put("metrics", steadyResultDTO);
                result.add(mapForOneSteady);

            } catch (Exception e) {
                logger.error("请求或处理指标失败", e);
            }
        }

        logger.info("Statebound 列表处理完成");
        return result;
    }

    private boolean compareValues(Object expectedValue, Object actualValue, String comparator) {
        if (expectedValue == null || actualValue == null || comparator == null) {
            logger.info("比较参数为空");
        }

        double expected = ((Number) expectedValue).doubleValue();
        double actual;
        if (actualValue instanceof Number) {
            actual = ((Number) actualValue).doubleValue();
        } else if (actualValue instanceof String) {
            try {
                actual = Double.parseDouble((String) actualValue);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("无法将 actualValue 转换为 double 类型: " + actualValue, e);
            }
        } else {
            throw new IllegalArgumentException("actualValue 类型不受支持: " + actualValue.getClass());
        }
        switch (comparator) {
            case ">":
                return actual > expected;
            case ">=":
                return actual >= expected;
            case "<":
                return actual < expected;
            case "<=":
                return actual <= expected;
            case "==":
                return actual == expected;
            case "!=":
                return actual != expected;
            default:
                logger.error("未知的比较符: " + comparator);
                return false;
        }
    }

    public String getNamespaceBySteadyState(Steadystate steadystate) {
        if (steadystate == null || steadystate.getApplicationId() == null) {
            throw new IllegalArgumentException("Steadystate 或 ApplicationId 不能为空");
        }

        Application application = applicationMapper.selectOne(
                new QueryWrapper<Application>()
                        .eq("id", steadystate.getApplicationId())
                        .eq("isdelete", 0) // 确保未被逻辑删除
        );

        if (application == null) {
            throw new RuntimeException(String.format("未找到对应的 Application，ID=%s", steadystate.getApplicationId()));
        }

        logger.info(String.format("查询到 Application: id=%s, namespace=%s",
                application.getId(), application.getNamespace()));
        return application.getNamespace();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomResult saveSteadystate(Steadystate entity) {
        try {
            // 1. 参数校验
            if (entity == null) {
                return CustomResult.fail("稳态数据不能为空");
            }
            if (StringUtils.isEmpty(entity.getName())) {
                return CustomResult.fail("稳态名称不能为空");
            }

            // 2. 检查名称是否重复
            Long count = this.lambdaQuery()
                    .eq(Steadystate::getName, entity.getName())
                    .eq(Steadystate::getApplicationId, entity.getApplicationId())  // 同一个应用下的稳态名称不能重复
                    .count();
            
            if (count > 0) {
                return CustomResult.fail("稳态名称已存在，请重新输入");
            }

            // 3. 设置创建时间和更新时间
            Date now = new Date();
            entity.setCreateTime(now);
            entity.setUpdateTime(now);

            // 4. 执行保存
            boolean saved = super.save(entity);
            
            if (saved) {
                return CustomResult.ok(entity.getId());
            } else {
                return CustomResult.fail("保存稳态数据失败");
            }
        } catch (Exception e) {
            log.error("保存稳态数据异常", e);
            return CustomResult.fail("保存稳态数据异常: " + e.getMessage());
        }
    }
}
//package com.iscas.lndicatormonitor.apitest;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.iscas.lndicatormonitor.dto.WorkflowDTO;
//import com.iscas.lndicatormonitor.dto.planv2.AddPlanv2DTO;
//import com.iscas.lndicatormonitor.dto.planv2.Planv2DTO;
//import com.iscas.lndicatormonitor.dto.planv2.QueryCriteria;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.Date;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//class Planv2ControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @Transactional
//    void testGetPlanList() throws Exception {
//        // 1. 准备测试数据 - 先添加一个计划
//        AddPlanv2DTO addPlanDTO = new AddPlanv2DTO();
//
//        // 设置 planv2DTO
//        Planv2DTO planv2DTO = new Planv2DTO();
//        planv2DTO.setName("测试计划");
//        planv2DTO.setSceneDesc("测试场景");
//        planv2DTO.setExpection("测试预期");
//        planv2DTO.setPlanType(1);
//        planv2DTO.setCreatorId(1001);
//        addPlanDTO.setPlanv2DTO(planv2DTO);
//
//        // 设置 workflowDTO
//        WorkflowDTO workflowDTO = new WorkflowDTO();
//        workflowDTO.setContent("测试内容");
//        workflowDTO.setNodes("node1,node2");
//        addPlanDTO.setWorkflowDTO(workflowDTO);
//
//        // 设置故障配置和稳态ID
//        addPlanDTO.setFaultConfigIdList(new int[]{1, 2});
//        addPlanDTO.setSteadyIdList(Arrays.asList("steady1", "steady2"));
//
//        // 先添加计划
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/planv2/addPlan")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(addPlanDTO)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(20000));
//
//        // 2. 测试列表查询 - 无条件查询
//        QueryCriteria criteria = new QueryCriteria();
//        criteria.setPageNum(1);
//        criteria.setPageSize(10);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/planv2/list")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(criteria)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(20000))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.records").isArray())
//                .andReturn();
//
//        // 3. 测试带条件的查询
//        QueryCriteria criteriaWithCondition = new QueryCriteria();
//        criteriaWithCondition.setName("测试");
//        criteriaWithCondition.setCreator("green");
//        criteriaWithCondition.setPlanType("1");
//        criteriaWithCondition.setOrderByTime("DESC");
//        criteriaWithCondition.setPageNum(1);
//        criteriaWithCondition.setPageSize(10);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/planv2/list")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(criteriaWithCondition)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(20000))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.records[0].name").value("测试计划"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.records[0].planType").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.records[0].faultNum").value(2));
//
//        // 4. 测试时间区间查询
//        QueryCriteria criteriaWithTime = new QueryCriteria();
//        criteriaWithTime.setStartTime(new Date(System.currentTimeMillis() - 86400000)); // 24小时前
//        criteriaWithTime.setEndTime(new Date());
//        criteriaWithTime.setPageNum(1);
//        criteriaWithTime.setPageSize(10);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/planv2/list")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(criteriaWithTime)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(20000));
//
//        // 5. 测试分页
//        QueryCriteria criteriaPage2 = new QueryCriteria();
//        criteriaPage2.setPageNum(2);
//        criteriaPage2.setPageSize(5);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/planv2/list")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(criteriaPage2)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.current").value(2))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size").value(5));
//    }
//}
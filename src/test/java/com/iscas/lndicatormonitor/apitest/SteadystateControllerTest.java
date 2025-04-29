//package com.iscas.lndicatormonitor.apitest;
//
//import com.iscas.lndicatormonitor.domain.Steadystate;
//import com.iscas.lndicatormonitor.service.SteadystateService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.util.Date;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)  // 禁用Spring Security过滤器
//public class SteadystateControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private SteadystateService steadystateService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private Steadystate testSteadystate;
//
//    @BeforeEach
//    void setUp() {
//        // 创建测试数据
//        testSteadystate = new Steadystate();
//        testSteadystate.setId("1");
//        testSteadystate.setApplicationId("app1");
//        testSteadystate.setSteadyStateId(1);
//        testSteadystate.setName("测试稳态");
//        testSteadystate.setCreateTime(new Date());
//        testSteadystate.setUpdateTime(new Date());
//        testSteadystate.setIsDelete(0);
//        testSteadystate.setValue(100);
//    }
//
//    @Test
//    public void testAddSteadystate() throws Exception {
//        when(steadystateService.save(any(Steadystate.class))).thenReturn(true);
//
//        mockMvc.perform(post("/steadystate/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testSteadystate)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000));
//    }
//
//    @Test
//    public void testDeleteSteadystate() throws Exception {
//        when(steadystateService.removeById("1")).thenReturn(true);
//
//        mockMvc.perform(delete("/steadystate/delete")
//                        .param("id", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000));
//    }
//
//    @Test
//    public void testUpdateSteadystate() throws Exception {
//        when(steadystateService.updateById(any(Steadystate.class))).thenReturn(true);
//
//        mockMvc.perform(put("/steadystate/update")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testSteadystate)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000));
//    }
//
//    @Test
//    public void testGetSteadystateById() throws Exception {
//        when(steadystateService.getById("1")).thenReturn(testSteadystate);
//
//        mockMvc.perform(get("/steadystate/get")
//                        .param("id", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000))
//                .andExpect(jsonPath("$.data.id").value("1"))
//                .andExpect(jsonPath("$.data.name").value("测试稳态"));
//    }
//
//    @Test
//    public void testListSteadystates() throws Exception {
//        Page<Steadystate> page = new Page<>(1, 10);
//        page.setRecords(java.util.Collections.singletonList(testSteadystate));
//        page.setTotal(1);
//
//        when(steadystateService.page(any(Page.class))).thenReturn(page);
//
//        mockMvc.perform(get("/steadystate/list")
//                        .param("page", "1")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000))
//                .andExpect(jsonPath("$.data.records[0].id").value("1"))
//                .andExpect(jsonPath("$.data.total").value(1));
//    }
//
//    @Test
//    public void testListAllByType() throws Exception {
//        Page<Steadystate> page = new Page<>(1, 10);
//        page.setRecords(java.util.Collections.singletonList(testSteadystate));
//        page.setTotal(1);
//
//        when(steadystateService.page(any(Page.class))).thenReturn(page);
//
//        mockMvc.perform(get("/steadystate/listAllByType")
//                        .param("type", "0")
//                        .param("page", "1")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000))
//                .andExpect(jsonPath("$.data.records[0].id").value("1"))
//                .andExpect(jsonPath("$.data.total").value(1));
//    }
//
//    // 测试失败场景
//    @Test
//    public void testAddSteadystateFail() throws Exception {
//        when(steadystateService.save(any(Steadystate.class))).thenReturn(false);
//
//        mockMvc.perform(post("/steadystate/add")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testSteadystate)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(50000))
//                .andExpect(jsonPath("$.msg").value("新增失败"));
//    }
//
//    @Test
//    public void testGetSteadystateByIdNotFound() throws Exception {
//        when(steadystateService.getById("999")).thenReturn(null);
//
//        mockMvc.perform(get("/steadystate/get")
//                        .param("id", "999"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(50000))
//                .andExpect(jsonPath("$.msg").value("未找到对应稳态信息"));
//    }
//}
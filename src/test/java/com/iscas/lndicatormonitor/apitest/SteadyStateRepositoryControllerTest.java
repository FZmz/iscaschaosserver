//package com.iscas.lndicatormonitor.apitest;
//
//import com.iscas.lndicatormonitor.domain.SteadyStateRepository;
//import com.iscas.lndicatormonitor.service.SteadyStateRepositoryService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.http.MediaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.util.Arrays;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)  // 禁用所有Spring Security过滤器
//public class SteadyStateRepositoryControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private SteadyStateRepositoryService stateRepositoryService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void testGetAllIds() throws Exception {
//        // 准备测试数据
//        when(stateRepositoryService.list())
//                .thenReturn(Arrays.asList(
//                        createSteadyState(1, "test1"),
//                        createSteadyState(2, "test2")
//                ));
//
//        // 执行测试
//        mockMvc.perform(get("/steadyStateRepository/ids"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000))
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data[0]").value(1))
//                .andExpect(jsonPath("$.data[1]").value(2));
//    }
//
//    @Test
//    public void testGetById() throws Exception {
//        // 准备测试数据
//        SteadyStateRepository repository = createSteadyState(1, "test1");
//        when(stateRepositoryService.getById(1)).thenReturn(repository);
//
//        // 执行测试
//        mockMvc.perform(get("/steadyStateRepository/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000))
//                .andExpect(jsonPath("$.data.id").value(1))
//                .andExpect(jsonPath("$.data.steadyStateName").value("test1"));
//    }
//
//    @Test
//    public void testAdd() throws Exception {
//        // 准备测试数据
//        SteadyStateRepository repository = createSteadyState(1, "test1");
//        when(stateRepositoryService.save(any(SteadyStateRepository.class))).thenReturn(true);
//
//        // 执行测试
//        mockMvc.perform(post("/steadyStateRepository")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(repository)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000));
//    }
//
//    @Test
//    public void testDeleteById() throws Exception {
//        // 准备测试数据
//        when(stateRepositoryService.removeById(1)).thenReturn(true);
//
//        // 执行测试
//        mockMvc.perform(delete("/steadyStateRepository/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(20000));
//    }
//
//    // 测试失败场景 - 获取不存在的记录
//    @Test
//    public void testGetByIdNotFound() throws Exception {
//        when(stateRepositoryService.getById(999)).thenReturn(null);
//
//        mockMvc.perform(get("/steadyStateRepository/999"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(50000))
//                .andExpect(jsonPath("$.msg").value("未找到对应记录"));
//    }
//
//    // 辅助方法 - 创建测试数据
//    private SteadyStateRepository createSteadyState(Integer id, String name) {
//        SteadyStateRepository repository = new SteadyStateRepository();
//        repository.setId(id);
//        repository.setSteadyStateName(name);
//        repository.setHandler("testHandler");
//        repository.setType(1);
//        repository.setIsDelete(0);
//        repository.setSteadyStateUnit("testUnit");
//        return repository;
//    }
//}
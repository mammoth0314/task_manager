package com.qiang.taskmanager.controller;

import com.github.pagehelper.PageInfo;
import com.qiang.taskmanager.common.Result;
import com.qiang.taskmanager.entity.Task;
import com.qiang.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("测试任务1");
        //task1.setDescription("这是第一个测试任务");
        task1.setStatus("进行中");

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("测试任务2");
        //task2.setDescription("这是第二个测试任务");
        task2.setStatus("已完成");
    }

    @Test
    void testGetAllTasks() throws Exception {
        // 准备数据
        List<Task> tasks = Arrays.asList(task1, task2);

        // 模拟行为
        when(taskService.findAll()).thenReturn(tasks);

        // 执行测试并验证
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("测试任务1"))
                .andExpect(jsonPath("$.data[1].id").value(2));

        // 验证服务调用
        verify(taskService, times(1)).findAll();
    }

    @Test
    void testGetTaskById_Success() throws Exception {
        // 模拟行为
        when(taskService.findById(1L)).thenReturn(task1);

        // 执行测试
        mockMvc.perform(get("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("测试任务1"));

        // 验证服务调用
        verify(taskService, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        // 模拟行为 - 返回null或抛出异常
        when(taskService.findById(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(taskService, times(1)).findById(999L);
    }

    @Test
    void testCreateTask() throws Exception {
        String taskJson = """
        {
            "title": "新任务",
            "description": "新任务描述",
            "status": "待开始"
        }
        """;

        // 执行测试
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        // 验证服务调用
        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void testUpdateTask() throws Exception {
        String taskJson = """
        {
            "title": "更新后的任务",
            "description": "更新后的描述",
            "status": "已完成"
        }
        """;

        // 执行测试
        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        // 验证服务调用
        verify(taskService, times(1)).updateTask(any(Task.class));
    }

    @Test
    void testDeleteTask() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        // 验证服务调用
        verify(taskService, times(1)).deleteById(1L);
    }

    @Test
    void testGetTasksWithPage() throws Exception {
        // 准备数据
        PageInfo<Task> pageInfo = new PageInfo<>(Arrays.asList(task1, task2));
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(10);
        pageInfo.setTotal(2);

        // 模拟行为
        when(taskService.findTasksWithPage(1, 10)).thenReturn(pageInfo);

        // 执行测试
        mockMvc.perform(get("/tasks/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.list.length()").value(2));

        // 验证服务调用
        verify(taskService, times(1)).findTasksWithPage(1, 10);
    }

    @Test
    void testSearchTasksByTitle() throws Exception {
        // 准备数据
        PageInfo<Task> pageInfo = new PageInfo<>(Arrays.asList(task1));
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(10);
        pageInfo.setTotal(1);

        // 模拟行为
        when(taskService.findTasksByTitleWithPage("测试", 1, 10)).thenReturn(pageInfo);

        // 执行测试
        mockMvc.perform(get("/tasks/search/title")
                        .param("title", "测试")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("测试任务1"));

        // 验证服务调用
        verify(taskService, times(1)).findTasksByTitleWithPage("测试", 1, 10);
    }

    @Test
    void testSearchTasksByStatus() throws Exception {
        // 准备数据
        PageInfo<Task> pageInfo = new PageInfo<>(Arrays.asList(task2));
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(10);
        pageInfo.setTotal(1);

        // 模拟行为
        when(taskService.findTasksByStatusWithPage("已完成", 1, 10)).thenReturn(pageInfo);

        // 执行测试
        mockMvc.perform(get("/tasks/search/status")
                        .param("status", "已完成")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].status").value("已完成"));

        // 验证服务调用
        verify(taskService, times(1)).findTasksByStatusWithPage("已完成", 1, 10);
    }

    @Test
    void testGetTasksWithPage_DefaultParameters() throws Exception {
        // 准备数据
        PageInfo<Task> pageInfo = new PageInfo<>(Arrays.asList(task1, task2));

        // 模拟行为
        when(taskService.findTasksWithPage(1, 10)).thenReturn(pageInfo);

        // 执行测试（不传参数，使用默认值）
        mockMvc.perform(get("/tasks/page")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证服务调用（使用默认参数）
        verify(taskService, times(1)).findTasksWithPage(1, 10);
    }
}
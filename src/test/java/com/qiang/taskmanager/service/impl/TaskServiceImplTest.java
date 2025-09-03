package com.qiang.taskmanager.service.impl;

import com.github.pagehelper.PageInfo;
import com.qiang.taskmanager.entity.Task;
import com.qiang.taskmanager.mapper.TaskMapper;
import com.qiang.taskmanager.exception.TaskNotFoundException;
import com.qiang.taskmanager.exception.TaskOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task testTask;
    private List<Task> testTasks;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("测试任务");
        testTask.setStatus("pending");
        testTask.setCreatedAt(LocalDateTime.now());

        testTasks = Arrays.asList(
                createTask(1L, "任务1", "pending"),
                createTask(2L, "任务2", "in_progress"),
                createTask(3L, "任务3", "completed")
        );
    }

    private Task createTask(Long id, String title, String status) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setStatus(status);
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }

    @Test
    void findAll_ShouldReturnTaskList_WhenTasksExist() {
        // Arrange
        when(taskMapper.findAll()).thenReturn(testTasks);

        // Act
        List<Task> result = taskService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(taskMapper, times(1)).findAll();
    }

    @Test
    void findAll_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        when(taskMapper.findAll()).thenThrow(new RuntimeException("数据库错误"));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> taskService.findAll());

        assertEquals("获取任务列表失败", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void findById_ShouldReturnTask_WhenTaskExists() {
        // Arrange
        when(taskMapper.findById(1L)).thenReturn(testTask);

        // Act
        Task result = taskService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试任务", result.getTitle());
        verify(taskMapper, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowTaskNotFoundException_WhenTaskNotExists() {
        // Arrange
        when(taskMapper.findById(999L)).thenReturn(null);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.findById(999L));

        assertEquals("任务不存在，ID: 999", exception.getMessage());
        verify(taskMapper, times(1)).findById(999L);
    }

    @Test
    void findById_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        when(taskMapper.findById(1L)).thenThrow(new RuntimeException("数据库错误"));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> taskService.findById(1L));

        assertEquals("查询任务失败，ID: 1", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void createTask_ShouldCreateTaskSuccessfully() {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("新任务");
        newTask.setStatus("pending");

        doNothing().when(taskMapper).insert(any(Task.class));

        // Act
        taskService.createTask(newTask);

        // Assert
        verify(taskMapper, times(1)).insert(any(Task.class));
        assertNotNull(newTask.getCreatedAt());
    }

    @Test
    void createTask_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("新任务");

        doThrow(new RuntimeException("插入错误")).when(taskMapper).insert(any(Task.class));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> taskService.createTask(newTask));

        assertEquals("创建任务失败", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void updateTask_ShouldUpdateTask_WhenTaskExists() {
        // Arrange
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("更新后的任务");

        when(taskMapper.findById(1L)).thenReturn(testTask);
        doNothing().when(taskMapper).update(any(Task.class));

        // Act
        taskService.updateTask(updatedTask);

        // Assert
        verify(taskMapper, times(1)).findById(1L);
        verify(taskMapper, times(1)).update(updatedTask);
    }

    @Test
    void updateTask_ShouldThrowTaskNotFoundException_WhenTaskNotExists() {
        // Arrange
        Task updatedTask = new Task();
        updatedTask.setId(999L);

        when(taskMapper.findById(999L)).thenReturn(null);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTask(updatedTask));

        assertEquals("任务不存在，无法更新，ID: 999", exception.getMessage());
        verify(taskMapper, times(1)).findById(999L);
        verify(taskMapper, never()).update(any(Task.class));
    }

    @Test
    void deleteById_ShouldDeleteTask_WhenTaskExists() {
        // Arrange
        when(taskMapper.findById(1L)).thenReturn(testTask);
        doNothing().when(taskMapper).deleteById(1L);

        // Act
        taskService.deleteById(1L);

        // Assert
        verify(taskMapper, times(1)).findById(1L);
        verify(taskMapper, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowTaskNotFoundException_WhenTaskNotExists() {
        // Arrange
        when(taskMapper.findById(999L)).thenReturn(null);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.deleteById(999L));

        assertEquals("任务不存在，无法删除，ID: 999", exception.getMessage());
        verify(taskMapper, times(1)).findById(999L);
        verify(taskMapper, never()).deleteById(anyLong());
    }

    @Test
    void findTasksWithPage_ShouldReturnPagedTasks() {
        // Arrange
        when(taskMapper.findAll()).thenReturn(testTasks);

        // Act
        PageInfo<Task> result = taskService.findTasksWithPage(1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getList().size());
        verify(taskMapper, times(1)).findAll();
    }

    @Test
    void findTasksByTitleWithPage_ShouldReturnPagedTasks() {
        // Arrange
        List<Task> filteredTasks = Arrays.asList(testTasks.get(0), testTasks.get(1));
        // 使用 eq() 来精确匹配参数，或者使用 anyString() 来匹配任何字符串
        when(taskMapper.findByTitleLike(eq("测试"))).thenReturn(filteredTasks);

        // Act
        PageInfo<Task> result = taskService.findTasksByTitleWithPage("测试", 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        verify(taskMapper, times(1)).findByTitleLike("测试");
    }

    @Test
    void findTasksByStatusWithPage_ShouldReturnPagedTasks() {
        // Arrange
        List<Task> pendingTasks = Collections.singletonList(testTasks.get(0));
        when(taskMapper.findByStatus("pending")).thenReturn(pendingTasks);

        // Act
        PageInfo<Task> result = taskService.findTasksByStatusWithPage("pending", 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("pending", result.getList().get(0).getStatus());
        verify(taskMapper, times(1)).findByStatus("pending");
    }

    @Test
    void findTasksWithPage_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        when(taskMapper.findAll()).thenThrow(new RuntimeException("分页查询错误"));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> taskService.findTasksWithPage(1, 10));

        assertEquals("分页查询任务失败", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void findTasksByTitleWithPage_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        when(taskMapper.findByTitleLike(anyString())).thenThrow(new RuntimeException("查询错误"));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> taskService.findTasksByTitleWithPage("测试", 1, 10));

        assertEquals("根据标题分页查询任务失败", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void findTasksByStatusWithPage_ShouldThrowTaskOperationException_WhenMapperFails() {
        // Arrange
        when(taskMapper.findByStatus(anyString())).thenThrow(new RuntimeException("查询错误"));

        // Act & Assert
        TaskOperationException exception = assertThrows(TaskOperationException.class,
                () -> taskService.findTasksByStatusWithPage("pending", 1, 10));

        assertEquals("根据状态分页查询任务失败", exception.getMessage());
        assertNotNull(exception.getCause());
    }
}
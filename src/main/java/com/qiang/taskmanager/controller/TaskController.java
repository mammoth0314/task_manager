package com.qiang.taskmanager.controller;

import com.github.pagehelper.PageInfo;
import com.qiang.taskmanager.common.Result;
import com.qiang.taskmanager.entity.Task;
import com.qiang.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@Slf4j
@Tag(name = "任务管理接口")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 获取所有任务
    @GetMapping
    @Operation(summary = "获取所有任务")
    public Result<List<Task>> getAllTasks() {
        log.info("获取所有任务");
        return Result.success(taskService.findAll());
    }

    // 根据ID获取任务
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取任务")
    public Result<Task> getTaskById(@PathVariable Long id) {
        log.info("根据ID获取任务,ID为:{}", id);
        return Result.success(taskService.findById(id));
    }

    // 创建新任务
    @PostMapping
    @Operation(summary = "创建新任务")
    public Result createTask(@RequestBody Task task) {
        log.info("创建新任务");
        taskService.createTask(task);
        return Result.success();
    }

    // 更新任务
    @PutMapping("/{id}")
    @Operation(summary = "更新任务")
    public Result updateTask(@PathVariable Long id, @RequestBody Task task) {
        log.info("更新任务,ID为：{}", id);
        task.setId(id);
        taskService.updateTask(task);
        return Result.success();
    }

    // 删除任务
    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务")
    public Result deleteTask(@PathVariable Long id) {
        log.info("删除任务");
        taskService.deleteById(id);
        return Result.success();
    }

    // 分页查询所有任务
    @GetMapping("/page")
    @Operation(summary = "分页查询所有任务")
    public Result<PageInfo<Task>> getTasksWithPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("分页查询所有任务，页码：{}，每页数量：{}", pageNum, pageSize);
        PageInfo<Task> pageInfo = taskService.findTasksWithPage(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    // 根据标题模糊查询任务（分页）
    @GetMapping("/search/title")
    @Operation(summary = "根据标题模糊查询任务")
    public Result<PageInfo<Task>> searchTasksByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("根据标题模糊查询任务，标题：{}，页码：{}，每页数量：{}", title, pageNum, pageSize);
        PageInfo<Task> pageInfo = taskService.findTasksByTitleWithPage(title, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    // 根据状态查询任务（分页）
    @GetMapping("/search/status")
    @Operation(summary = "根据状态查询任务")
    public Result<PageInfo<Task>> searchTasksByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("根据状态查询任务，状态：{}，页码：{}，每页数量：{}", status, pageNum, pageSize);
        PageInfo<Task> pageInfo = taskService.findTasksByStatusWithPage(status, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}

package com.qiang.taskmanager.service;

import com.github.pagehelper.PageInfo;
import com.qiang.taskmanager.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> findAll();
    Task findById(Long id);
    void createTask(Task task);
    void updateTask(Task task);
    void deleteById(Long id);

    // 分页查询所有任务
    PageInfo<Task> findTasksWithPage(int pageNum, int pageSize);

    // 根据标题模糊查询任务（分页）
    PageInfo<Task> findTasksByTitleWithPage(String title, int pageNum, int pageSize);

    // 根据状态查询任务（分页）
    PageInfo<Task> findTasksByStatusWithPage(String status, int pageNum, int pageSize);
}
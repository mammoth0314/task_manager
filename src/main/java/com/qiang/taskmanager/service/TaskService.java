package com.qiang.taskmanager.service;

import com.qiang.taskmanager.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> findAll();
    Task findById(Long id);
    void createTask(Task task);
    void updateTask(Task task);
    void deleteById(Long id);
}

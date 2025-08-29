package com.qiang.taskmanager.service.impl;

import com.qiang.taskmanager.entity.Task;
import com.qiang.taskmanager.mapper.TaskMapper;
import com.qiang.taskmanager.service.TaskService;
import com.qiang.taskmanager.exception.TaskNotFoundException;
import com.qiang.taskmanager.exception.TaskOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<Task> findAll() throws TaskOperationException {
        try {
            return taskMapper.findAll();
        } catch (Exception e) {
            throw new TaskOperationException("获取任务列表失败", e);
        }
    }

    @Override
    public Task findById(Long id) throws TaskNotFoundException, TaskOperationException {
        try {
            Task task = taskMapper.findById(id);
            if (task == null) {
                throw new TaskNotFoundException("任务不存在，ID: " + id);
            }
            return task;
        } catch (TaskNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskOperationException("查询任务失败，ID: " + id, e);
        }
    }

    @Override
    public void createTask(Task task) throws TaskOperationException {
        try {
            task.setCreatedAt(LocalDateTime.now());
            taskMapper.insert(task);
        } catch (Exception e) {
            throw new TaskOperationException("创建任务失败", e);
        }
    }

    @Override
    public void updateTask(Task task) throws TaskNotFoundException, TaskOperationException {
        try {
            Task existingTask = taskMapper.findById(task.getId());
            if (existingTask == null) {
                throw new TaskNotFoundException("任务不存在，无法更新，ID: " + task.getId());
            }
            taskMapper.update(task);
        } catch (TaskNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskOperationException("更新任务失败，ID: " + task.getId(), e);
        }
    }

    @Override
    public void deleteById(Long id) throws TaskNotFoundException, TaskOperationException {
        try {
            Task task = taskMapper.findById(id);
            if (task == null) {
                throw new TaskNotFoundException("任务不存在，无法删除，ID: " + id);
            }
            taskMapper.deleteById(id);
        } catch (TaskNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskOperationException("删除任务失败，ID: " + id, e);
        }
    }
}

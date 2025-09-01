package com.qiang.taskmanager.exception;

import com.qiang.taskmanager.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理任务未找到异常
     * @param ex 任务未找到异常
     * @return 响应结果
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Result<Void>> handleTaskNotFoundException(TaskNotFoundException ex) {
        Result<Void> result = Result.error(404, ex.getMessage());
        return ResponseEntity.status(404).body(result);
    }

    /**
     * 处理任务操作异常
     * @param ex 任务操作异常
     * @return 响应结果
     */
    @ExceptionHandler(TaskOperationException.class)
    public ResponseEntity<Result<Void>> handleTaskOperationException(TaskOperationException ex) {
        Result<Void> result = Result.error(500, ex.getMessage());
        return ResponseEntity.status(500).body(result);
    }

    /**
     * 处理HTTP消息不可读异常
     * @param ex HTTP消息不可读异常
     * @return 响应结果
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Result<Void> result = Result.error(400, "请求数据格式错误: " + ex.getMessage());
        return ResponseEntity.status(400).body(result);
    }

    /**
     * 处理通用异常
     * @param ex 通用异常
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleGenericException(Exception ex) {
        Result<Void> result = Result.error(500, "服务器内部错误: " + ex.getMessage());
        return ResponseEntity.status(500).body(result);
    }
}
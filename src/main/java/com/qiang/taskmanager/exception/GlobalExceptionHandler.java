package com.qiang.taskmanager.common;

import com.qiang.taskmanager.exception.TaskNotFoundException;
import com.qiang.taskmanager.exception.TaskOperationException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Result<Void>> handleTaskNotFoundException(TaskNotFoundException ex) {
        Result<Void> result = Result.error(404, ex.getMessage());
        return ResponseEntity.status(404).body(result);
    }

    @ExceptionHandler(TaskOperationException.class)
    public ResponseEntity<Result<Void>> handleTaskOperationException(TaskOperationException ex) {
        Result<Void> result = Result.error(500, ex.getMessage());
        return ResponseEntity.status(500).body(result);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Result<Void> result = Result.error(400, "请求数据格式错误: " + ex.getMessage());
        return ResponseEntity.status(400).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleGenericException(Exception ex) {
        Result<Void> result = Result.error(500, "服务器内部错误: " + ex.getMessage());
        return ResponseEntity.status(500).body(result);
    }
}

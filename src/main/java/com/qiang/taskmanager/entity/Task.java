package com.qiang.taskmanager.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private String title;
    private String status;
    private LocalDateTime createdAt;
}

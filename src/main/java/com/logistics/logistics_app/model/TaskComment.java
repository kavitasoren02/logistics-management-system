package com.logistics.logistics_app.model;

import lombok.Data;

@Data
public class TaskComment {
    private Long id;
    private Long taskId;
    private String comment;
    private Long userId;
    private Long timestamp;
}

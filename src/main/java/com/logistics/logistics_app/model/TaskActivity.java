package com.logistics.logistics_app.model;

import com.logistics.logistics_app.model.enums.ActivityType;
import lombok.Data;

@Data
public class TaskActivity {
    private Long id;
    private Long taskId;
    private ActivityType activityType;
    private String description;
    private Long userId;
    private Long timestamp;
}

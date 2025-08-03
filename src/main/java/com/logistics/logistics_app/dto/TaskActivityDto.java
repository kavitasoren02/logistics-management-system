package com.logistics.logistics_app.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistics.logistics_app.model.enums.ActivityType;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskActivityDto {
    private Long id;
    private Long taskId;
    private ActivityType activityType;
    private String description;
    private Long userId;
    private Long timestamp;
}

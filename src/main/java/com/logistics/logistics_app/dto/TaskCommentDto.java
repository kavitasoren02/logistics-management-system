package com.logistics.logistics_app.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskCommentDto {
    private Long id;
    private Long taskId;
    private String comment;
    private Long userId;
    private Long timestamp;
}

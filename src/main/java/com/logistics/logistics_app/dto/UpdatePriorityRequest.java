package com.logistics.logistics_app.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistics.logistics_app.model.enums.Priority;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdatePriorityRequest {
    private Long taskId;
    private Priority priority;
}

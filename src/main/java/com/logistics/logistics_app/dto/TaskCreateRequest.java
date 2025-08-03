package com.logistics.logistics_app.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistics.logistics_app.model.enums.Priority;
import com.logistics.logistics_app.model.enums.ReferenceType;
import com.logistics.logistics_app.model.enums.Task;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskCreateRequest {
    private List<RequestItem> requests;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RequestItem {
        private Long referenceId;
        private ReferenceType referenceType;
        private Task task;
        private Long assigneeId;
        private Priority priority;
        private Long taskDeadlineTime;
    }
}

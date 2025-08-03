package com.logistics.logistics_app.model;

import com.logistics.logistics_app.model.enums.Priority;
import com.logistics.logistics_app.model.enums.ReferenceType;
import com.logistics.logistics_app.model.enums.Task;
import com.logistics.logistics_app.model.enums.TaskStatus;
import lombok.Data;

@Data
public class TaskManagement {
    private Long id;
    private Long referenceId;
    private ReferenceType referenceType;
    private Task task;
    private String description;
    private TaskStatus status;
    private Long assigneeId; // Simplified from Entity for this assignment
    private Long taskDeadlineTime;
    private Priority priority;
    // FEATURE: Add creation time for smart daily view
    private Long createdTime;
}

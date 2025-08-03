package com.logistics.logistics_app.service;

import com.logistics.logistics_app.dto.*;
import com.logistics.logistics_app.model.enums.Priority;

import java.util.List;

public interface TaskManagementService {
    List<TaskManagementDto> createTasks(TaskCreateRequest request);
    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
    String assignByReference(AssignByReferenceRequest request);
    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
    TaskManagementDto findTaskById(Long id);

    // FEATURE: New methods for priority management
    TaskManagementDto updateTaskPriority(Long taskId, Priority priority);
    List<TaskManagementDto> getTasksByPriority(Priority priority);

    // FEATURE: New methods for comments and activity history
    TaskDetailsDto getTaskDetails(Long taskId);
    String addComment(Long taskId, AddCommentRequest request);
}

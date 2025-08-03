package com.logistics.logistics_app.repository;


import com.logistics.logistics_app.model.TaskManagement;
import com.logistics.logistics_app.model.enums.Priority;
import com.logistics.logistics_app.model.enums.ReferenceType;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Optional<TaskManagement> findById(Long id);
    TaskManagement save(TaskManagement task);
    List<TaskManagement> findAll();
    List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType);
    List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds);
    // FEATURE: New method for priority filtering
    List<TaskManagement> findByPriority(Priority priority);
}

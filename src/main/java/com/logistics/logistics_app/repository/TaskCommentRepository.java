package com.logistics.logistics_app.repository;

import com.logistics.logistics_app.model.TaskComment;

import java.util.List;

public interface TaskCommentRepository {
    TaskComment save(TaskComment comment);
    List<TaskComment> findByTaskIdOrderByTimestamp(Long taskId);
}

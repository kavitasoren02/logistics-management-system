package com.logistics.logistics_app.repository;


import com.logistics.logistics_app.model.TaskActivity;

import java.util.List;

public interface TaskActivityRepository {
    TaskActivity save(TaskActivity activity);
    List<TaskActivity> findByTaskIdOrderByTimestamp(Long taskId);
}

package com.logistics.logistics_app.repository;

import com.logistics.logistics_app.model.TaskActivity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskActivityRepository implements TaskActivityRepository {
    private final Map<Long, TaskActivity> activityStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public TaskActivity save(TaskActivity activity) {
        if (activity.getId() == null) {
            activity.setId(idCounter.incrementAndGet());
        }
        if (activity.getTimestamp() == null) {
            activity.setTimestamp(System.currentTimeMillis());
        }
        activityStore.put(activity.getId(), activity);
        return activity;
    }

    @Override
    public List<TaskActivity> findByTaskIdOrderByTimestamp(Long taskId) {
        return activityStore.values().stream()
                .filter(activity -> activity.getTaskId().equals(taskId))
                .sorted((a1, a2) -> Long.compare(a1.getTimestamp(), a2.getTimestamp()))
                .collect(Collectors.toList());
    }
}

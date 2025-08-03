package com.logistics.logistics_app.repository;

import com.logistics.logistics_app.model.TaskComment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskCommentRepository implements TaskCommentRepository {
    private final Map<Long, TaskComment> commentStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public TaskComment save(TaskComment comment) {
        if (comment.getId() == null) {
            comment.setId(idCounter.incrementAndGet());
        }
        if (comment.getTimestamp() == null) {
            comment.setTimestamp(System.currentTimeMillis());
        }
        commentStore.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public List<TaskComment> findByTaskIdOrderByTimestamp(Long taskId) {
        return commentStore.values().stream()
                .filter(comment -> comment.getTaskId().equals(taskId))
                .sorted((c1, c2) -> Long.compare(c1.getTimestamp(), c2.getTimestamp()))
                .collect(Collectors.toList());
    }
}

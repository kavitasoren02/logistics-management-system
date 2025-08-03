package com.logistics.logistics_app.service.impl;

import com.logistics.logistics_app.dto.*;
import com.logistics.logistics_app.exception.ResourceNotFoundException;
import com.logistics.logistics_app.mapper.ITaskManagementMapper;
import com.logistics.logistics_app.model.TaskActivity;
import com.logistics.logistics_app.model.TaskComment;
import com.logistics.logistics_app.model.TaskManagement;
import com.logistics.logistics_app.model.enums.ActivityType;
import com.logistics.logistics_app.model.enums.Priority;
import com.logistics.logistics_app.model.enums.Task;
import com.logistics.logistics_app.model.enums.TaskStatus;
import com.logistics.logistics_app.repository.TaskActivityRepository;
import com.logistics.logistics_app.repository.TaskCommentRepository;
import com.logistics.logistics_app.repository.TaskRepository;
import com.logistics.logistics_app.service.TaskManagementService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {
    private final TaskRepository taskRepository;
    private final TaskActivityRepository activityRepository;
    private final TaskCommentRepository commentRepository;
    private final ITaskManagementMapper taskMapper;

    public TaskManagementServiceImpl(TaskRepository taskRepository,
                                     TaskActivityRepository activityRepository,
                                     TaskCommentRepository commentRepository,
                                     ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.activityRepository = activityRepository;
        this.commentRepository = commentRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();

        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");

            TaskManagement savedTask = taskRepository.save(newTask);
            createdTasks.add(savedTask);

            // FEATURE: Log task creation activity
            logActivity(savedTask.getId(), ActivityType.CREATED,
                    "Task created: " + savedTask.getTask().getView(), null);
        }

        return taskMapper.modelListToDtoList(createdTasks);
    }

    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();

        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

            TaskStatus oldStatus = task.getStatus();

            if (item.getTaskStatus() != null && !item.getTaskStatus().equals(oldStatus)) {
                task.setStatus(item.getTaskStatus());
                // FEATURE: Log status change activity
                logActivity(task.getId(), ActivityType.STATUS_CHANGED,
                        "Status changed from " + oldStatus + " to " + item.getTaskStatus(), null);
            }

            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }

            updatedTasks.add(taskRepository.save(task));
        }

        return taskMapper.modelListToDtoList(updatedTasks);
    }

    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository
                .findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            // BUG FIX #1: Fix task reassignment logic
            if (!tasksOfType.isEmpty()) {
                // Assign the first task to the new assignee
                TaskManagement taskToReassign = tasksOfType.get(0);
                Long oldAssigneeId = taskToReassign.getAssigneeId();
                taskToReassign.setAssigneeId(request.getAssigneeId());
                taskRepository.save(taskToReassign);

                // FEATURE: Log reassignment activity
                logActivity(taskToReassign.getId(), ActivityType.REASSIGNED,
                        "Task reassigned from assignee " + oldAssigneeId + " to " + request.getAssigneeId(), null);

                // Cancel all other duplicate tasks
                for (int i = 1; i < tasksOfType.size(); i++) {
                    TaskManagement taskToCancel = tasksOfType.get(i);
                    taskToCancel.setStatus(TaskStatus.CANCELLED);
                    taskRepository.save(taskToCancel);

                    // FEATURE: Log cancellation activity
                    logActivity(taskToCancel.getId(), ActivityType.STATUS_CHANGED,
                            "Task cancelled due to reassignment", null);
                }
            } else {
                // Create a new task if none exist
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                newTask.setDescription("Task created via assign-by-reference");
                newTask.setPriority(Priority.MEDIUM); // Default priority
                newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000); // 1 day from now

                TaskManagement savedTask = taskRepository.save(newTask);

                // FEATURE: Log task creation activity
                logActivity(savedTask.getId(), ActivityType.CREATED,
                        "Task created via assign-by-reference", null);
            }
        }

        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

        // BUG FIX #2: Filter out cancelled tasks and implement smart daily view
        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED) // Fix: Filter out cancelled tasks
                .filter(task -> {
                    // FEATURE: Smart daily task view implementation
                    long taskCreatedTime = task.getCreatedTime() != null ? task.getCreatedTime() : task.getTaskDeadlineTime();

                    // Tasks that were created within the date range
                    boolean createdInRange = taskCreatedTime >= request.getStartDate() &&
                            taskCreatedTime <= request.getEndDate();

                    // OR active tasks that were created before the range but are still open
                    boolean activeBeforeRange = taskCreatedTime < request.getStartDate() &&
                            (task.getStatus() == TaskStatus.ASSIGNED ||
                                    task.getStatus() == TaskStatus.STARTED);

                    return createdInRange || activeBeforeRange;
                })
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filteredTasks);
    }

    // FEATURE: Update task priority
    @Override
    public TaskManagementDto updateTaskPriority(Long taskId, Priority priority) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        Priority oldPriority = task.getPriority();
        task.setPriority(priority);
        TaskManagement savedTask = taskRepository.save(task);

        // Log priority change activity
        logActivity(taskId, ActivityType.PRIORITY_CHANGED,
                "Priority changed from " + oldPriority + " to " + priority, null);

        return taskMapper.modelToDto(savedTask);
    }

    // FEATURE: Get tasks by priority
    @Override
    public List<TaskManagementDto> getTasksByPriority(Priority priority) {
        List<TaskManagement> tasks = taskRepository.findByPriority(priority);
        // Filter out cancelled tasks
        List<TaskManagement> activeTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
                .collect(Collectors.toList());
        return taskMapper.modelListToDtoList(activeTasks);
    }

    // FEATURE: Get task details with activity history and comments
    @Override
    public TaskDetailsDto getTaskDetails(Long taskId) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        TaskDetailsDto taskDetails = taskMapper.modelToDetailsDto(task);

        // Get activities and comments
        List<TaskActivity> activities = activityRepository.findByTaskIdOrderByTimestamp(taskId);
        List<TaskComment> comments = commentRepository.findByTaskIdOrderByTimestamp(taskId);

        taskDetails.setActivities(taskMapper.activityListToDtoList(activities));
        taskDetails.setComments(taskMapper.commentListToDtoList(comments));

        return taskDetails;
    }

    // FEATURE: Add comment to task
    @Override
    public String addComment(Long taskId, AddCommentRequest request) {
        // Verify task exists
        taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        TaskComment comment = new TaskComment();
        comment.setTaskId(taskId);
        comment.setComment(request.getComment());
        comment.setUserId(request.getUserId());
        commentRepository.save(comment);

        // Log comment activity
        logActivity(taskId, ActivityType.COMMENT_ADDED,
                "Comment added by user " + request.getUserId(), request.getUserId());

        return "Comment added successfully";
    }

    // FEATURE: Helper method to log activities
    private void logActivity(Long taskId, ActivityType activityType, String description, Long userId) {
        TaskActivity activity = new TaskActivity();
        activity.setTaskId(taskId);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setUserId(userId);
        activityRepository.save(activity);
    }
}

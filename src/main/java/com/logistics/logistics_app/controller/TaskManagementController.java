package com.logistics.logistics_app.controller;

import com.logistics.logistics_app.dto.*;
import com.logistics.logistics_app.model.enums.Priority;
import com.logistics.logistics_app.model.response.Response;
import com.logistics.logistics_app.service.TaskManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {
    private final TaskManagementService taskManagementService;

    public TaskManagementController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @GetMapping("/{id}")
    public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
        return new Response<>(taskManagementService.findTaskById(id));
    }

    @PostMapping("/create")
    public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
        return new Response<>(taskManagementService.createTasks(request));
    }

    @PostMapping("/update")
    public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
        return new Response<>(taskManagementService.updateTasks(request));
    }

    @PostMapping("/assign-by-ref")
    public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
        return new Response<>(taskManagementService.assignByReference(request));
    }

    @PostMapping("/fetch-by-date/v2")
    public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
        return new Response<>(taskManagementService.fetchTasksByDate(request));
    }

    // FEATURE: New endpoint to update task priority
    @PutMapping("/{id}/priority")
    public Response<TaskManagementDto> updateTaskPriority(@PathVariable Long id,
                                                          @RequestBody UpdatePriorityRequest request) {
        return new Response<>(taskManagementService.updateTaskPriority(id, request.getPriority()));
    }

    // FEATURE: New endpoint to get tasks by priority
    @GetMapping("/priority/{priority}")
    public Response<List<TaskManagementDto>> getTasksByPriority(@PathVariable Priority priority) {
        return new Response<>(taskManagementService.getTasksByPriority(priority));
    }

    // FEATURE: New endpoint to get task details with history and comments
    @GetMapping("/{id}/details")
    public Response<TaskDetailsDto> getTaskDetails(@PathVariable Long id) {
        return new Response<>(taskManagementService.getTaskDetails(id));
    }

    // FEATURE: New endpoint to add comments to tasks
    @PostMapping("/{id}/comments")
    public Response<String> addComment(@PathVariable Long id, @RequestBody AddCommentRequest request) {
        return new Response<>(taskManagementService.addComment(id, request));
    }
}

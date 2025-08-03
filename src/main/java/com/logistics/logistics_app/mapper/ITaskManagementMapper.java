package com.logistics.logistics_app.mapper;

import com.logistics.logistics_app.dto.TaskActivityDto;
import com.logistics.logistics_app.dto.TaskCommentDto;
import com.logistics.logistics_app.dto.TaskDetailsDto;
import com.logistics.logistics_app.dto.TaskManagementDto;
import com.logistics.logistics_app.model.TaskActivity;
import com.logistics.logistics_app.model.TaskComment;
import com.logistics.logistics_app.model.TaskManagement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ITaskManagementMapper {
    ITaskManagementMapper INSTANCE = Mappers.getMapper(ITaskManagementMapper.class);

    TaskManagementDto modelToDto(TaskManagement model);
    TaskManagement dtoToModel(TaskManagementDto dto);
    List<TaskManagementDto> modelListToDtoList(List<TaskManagement> models);

    // Fix: Ignore the activities and comments fields since they're not in TaskManagement
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "comments", ignore = true)
    TaskDetailsDto modelToDetailsDto(TaskManagement model);

    TaskActivityDto activityToDto(TaskActivity activity);
    TaskCommentDto commentToDto(TaskComment comment);
    List<TaskActivityDto> activityListToDtoList(List<TaskActivity> activities);
    List<TaskCommentDto> commentListToDtoList(List<TaskComment> comments);
}

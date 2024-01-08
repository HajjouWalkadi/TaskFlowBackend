package com.example.taskflowbrief.mapper;


import com.example.taskflowbrief.model.dto.TaskDTO;
import com.example.taskflowbrief.model.dto.response.TaskResponseDTO;
import com.example.taskflowbrief.model.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);
//    @Mapping(target = "createdBy", source = "createdBy")
//    @Mapping(target = "assignedTo", source = "assignedTo")
    TaskResponseDTO entityToDto(Task task);
    Task dtoToEntity(TaskDTO taskDTO);

}

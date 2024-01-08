package com.example.taskflowbrief.mapper;


import com.example.taskflowbrief.model.dto.UserDTO;
import com.example.taskflowbrief.model.dto.response.UserResponseDTO;
import com.example.taskflowbrief.model.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

//    @Mappings({
//            @Mapping(target = "createdTasks", source = "createdTasks"),
//            @Mapping(target = "assignedTasks", source = "assignedTasks"),
//            @Mapping(target = "tokenDemands", source = "tokenDemands")
//    })
    UserResponseDTO entityToDto(User user);

    User dtoToEntity(UserDTO userDTO);

    List<UserResponseDTO> entitiesToDtos(List<User> users);
    List<User> dtosToEntities(List<UserDTO> userDTOs);
}

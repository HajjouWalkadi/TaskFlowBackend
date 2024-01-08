package com.example.taskflowbrief.mapper;

import com.example.taskflowbrief.model.dto.TagDTO;
import com.example.taskflowbrief.model.dto.response.TagResponseDTO;
import com.example.taskflowbrief.model.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);
    TagResponseDTO entityToDto(Tag tag);
    Tag dtoToEntity(TagDTO tagDTO);
}

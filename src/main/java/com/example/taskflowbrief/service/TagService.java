package com.example.taskflowbrief.service;

import com.example.taskflowbrief.model.dto.TagDTO;
import com.example.taskflowbrief.model.dto.response.TagResponseDTO;

import java.util.List;

public interface TagService {
    TagResponseDTO createTag(TagDTO tagDTO);
    TagResponseDTO updateTag(Long tagId, TagDTO tagDTO);
    void deleteTag(Long tagId);
    TagResponseDTO getTagById(Long tagId);
    List<TagResponseDTO> getAllTags();
}

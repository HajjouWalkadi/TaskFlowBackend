package com.example.taskflowbrief.service.impl;

import com.example.taskflowbrief.mapper.TagMapper;
import com.example.taskflowbrief.model.dto.TagDTO;
import com.example.taskflowbrief.model.dto.response.TagResponseDTO;
import com.example.taskflowbrief.model.entities.Tag;
import com.example.taskflowbrief.repository.TagRepository;
import com.example.taskflowbrief.service.TagService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public TagResponseDTO createTag(TagDTO tagDTO) {
        try {
            Tag tag = tagMapper.dtoToEntity(tagDTO);
            Tag savedTag = tagRepository.save(tag);
            return tagMapper.entityToDto(savedTag);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while creating tag", e);
        }
    }

    @Override
    public TagResponseDTO updateTag(Long tagId, TagDTO tagDTO) {
        try {
            Optional<Tag> optionalTag = tagRepository.findById(tagId);
            if (optionalTag.isPresent()) {
                Tag tag = optionalTag.get();
                tag.setName(tagDTO.getName());
                Tag updatedTag = tagRepository.save(tag);
                return tagMapper.entityToDto(updatedTag);
            } else {
                throw new ValidationException("Tag not found with ID: " + tagId);
            }
        } catch (Exception e) {
            // Handle specific exceptions or log the error
            throw new RuntimeException("Error occurred while updating tag", e);
        }
    }

    @Override
    public void deleteTag(Long tagId) {
        try {
            Optional<Tag> optionalTag = tagRepository.findById(tagId);
            if (optionalTag.isPresent()) {
                tagRepository.deleteById(tagId);
            } else {
                throw new ValidationException("Tag not found with ID: " + tagId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while deleting tag", e);
        }
    }

    @Override
    public TagResponseDTO getTagById(Long tagId) {
        try {
            Optional<Tag> optionalTag = tagRepository.findById(tagId);
            if (optionalTag.isPresent()) {
                Tag tag = optionalTag.get();
                return tagMapper.entityToDto(tag);
            } else {
                throw new ValidationException("Tag not found with ID: " + tagId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching tag by ID", e);
        }
    }

    @Override
    public List<TagResponseDTO> getAllTags() {
        try {
            List<Tag> tags = tagRepository.findAll();
            return tags.stream()
                    .map(tagMapper::entityToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching all tags", e);
        }
    }
}

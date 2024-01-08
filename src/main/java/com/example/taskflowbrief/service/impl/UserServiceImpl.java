// UserServiceImpl.java
package com.example.taskflowbrief.service.impl;


import com.example.taskflowbrief.mapper.UserMapper;
import com.example.taskflowbrief.model.dto.UserDTO;
import com.example.taskflowbrief.model.dto.response.UserResponseDTO;
import com.example.taskflowbrief.model.entities.User;
import com.example.taskflowbrief.repository.UserRepository;
import com.example.taskflowbrief.service.UserService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDTO createUser(UserDTO userDTO) {
        User user = userMapper.dtoToEntity(userDTO);
        user.setAdditionalTokens(2);
        User savedUser = userRepository.save(user);
        return userMapper.entityToDto(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUserName(userDTO.getUserName());
            user.setEmail(userDTO.getEmail());
            user.setAdditionalTokens(2);
            user.setPassword(userDTO.getPassword());
            user.setTelephone(userDTO.getTelephone());
            user.setRole(userDTO.getRole());
            User updatedUser = userRepository.save(user);
            return userMapper.entityToDto(updatedUser);
        } else {
            throw new ValidationException("User not found with ID: " + userId);
        }
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new ValidationException("User not found with ID: " + userId);
        }
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return userMapper.entityToDto(user);
        } else {
            throw new ValidationException("User not found with ID: " + userId);
        }
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }
}

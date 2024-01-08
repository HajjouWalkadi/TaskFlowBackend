package com.example.taskflowbrief.service;

import com.example.taskflowbrief.model.dto.UserDTO;
import com.example.taskflowbrief.model.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService{
    UserResponseDTO createUser(UserDTO userDTO);
    UserResponseDTO updateUser(Long userId, UserDTO userDTO);
    void deleteUser(Long userId);
    UserResponseDTO getUserById(Long userId);
    List<UserResponseDTO> getAllUsers();
}

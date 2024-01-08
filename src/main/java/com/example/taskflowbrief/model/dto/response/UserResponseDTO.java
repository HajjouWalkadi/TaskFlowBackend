package com.example.taskflowbrief.model.dto.response;

import com.example.taskflowbrief.enums.Role;
import com.example.taskflowbrief.model.dto.TaskDTO;
import com.example.taskflowbrief.model.dto.TokenDemandDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String userName;
    private String email;
    private String telephone;
    private Role role;
    private List<TaskDTO> createdTasks;
    private List<TaskDTO> assignedTasks;
    private List<TokenDemandDTO> tokenDemands;
}


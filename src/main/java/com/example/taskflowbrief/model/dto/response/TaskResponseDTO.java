package com.example.taskflowbrief.model.dto.response;

import com.example.taskflowbrief.enums.TaskStatus;
import com.example.taskflowbrief.model.dto.TagDTO;
import com.example.taskflowbrief.model.dto.TokenDemandDTO;
import com.example.taskflowbrief.model.dto.UserDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private UserDTO createdBy;
    private UserDTO assignedTo;
    private TaskStatus status;
    private List<TagDTO> tags;
    private List<TokenDemandDTO> tokenDemands;
}

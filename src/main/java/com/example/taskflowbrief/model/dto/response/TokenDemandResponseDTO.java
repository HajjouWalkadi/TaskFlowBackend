package com.example.taskflowbrief.model.dto.response;

import com.example.taskflowbrief.enums.RequestStatus;
import com.example.taskflowbrief.enums.TokenType;
import com.example.taskflowbrief.model.dto.TaskDTO;
import com.example.taskflowbrief.model.dto.UserDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDemandResponseDTO {
    private Long id;
    private LocalDateTime demandDate;
    private RequestStatus status;
    private TokenType type;
    private UserDTO user;
    private TaskDTO task;
}

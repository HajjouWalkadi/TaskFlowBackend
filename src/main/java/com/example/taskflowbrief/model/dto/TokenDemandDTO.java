package com.example.taskflowbrief.model.dto;

import com.example.taskflowbrief.enums.RequestStatus;
import com.example.taskflowbrief.enums.TokenType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDemandDTO {
    private LocalDateTime demandDate;

    private RequestStatus status;

    @NotNull(message = "Type cannot be null")
    private TokenType type;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Task ID cannot be null")
    @Positive(message = "Task ID must be positive")
    private Long taskId;
}

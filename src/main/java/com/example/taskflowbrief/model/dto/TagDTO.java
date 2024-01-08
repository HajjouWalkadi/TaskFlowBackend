package com.example.taskflowbrief.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDTO {
    @NotBlank(message = "Name is required")
    private String name;
}

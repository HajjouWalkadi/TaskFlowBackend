package com.example.taskflowbrief.service;

import com.example.taskflowbrief.model.dto.TokenDemandDTO;
import com.example.taskflowbrief.model.dto.response.TaskResponseDTO;
import com.example.taskflowbrief.model.dto.response.TokenDemandResponseDTO;


import java.util.List;

public interface TokenDemandService {
    TokenDemandResponseDTO requestToken(TokenDemandDTO tokenDemandDTO);
    TaskResponseDTO replaceTask(Long assignedToUserId, Long tokenDemandId, Long managerId);
    List<TokenDemandResponseDTO> getAllTokenDemands();
}

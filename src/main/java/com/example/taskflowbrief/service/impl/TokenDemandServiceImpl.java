package com.example.taskflowbrief.service.impl;

import com.example.taskflowbrief.enums.RequestStatus;
import com.example.taskflowbrief.enums.Role;
import com.example.taskflowbrief.enums.TaskStatus;
import com.example.taskflowbrief.enums.TokenType;
import com.example.taskflowbrief.mapper.TaskMapper;
import com.example.taskflowbrief.mapper.TokenDemandMapper;
import com.example.taskflowbrief.model.dto.TokenDemandDTO;
import com.example.taskflowbrief.model.dto.response.TaskResponseDTO;
import com.example.taskflowbrief.model.dto.response.TokenDemandResponseDTO;
import com.example.taskflowbrief.model.entities.Task;
import com.example.taskflowbrief.model.entities.TokenDemand;
import com.example.taskflowbrief.model.entities.User;
import com.example.taskflowbrief.repository.TaskRepository;
import com.example.taskflowbrief.repository.TokenDemandRepository;
import com.example.taskflowbrief.repository.UserRepository;
import com.example.taskflowbrief.service.TaskService;
import com.example.taskflowbrief.service.TokenDemandService;
import com.example.taskflowbrief.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenDemandServiceImpl implements TokenDemandService {

    private final TokenDemandRepository tokenDemandRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TokenDemandMapper tokenDemandMapper;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final TaskService taskService;

    @Override
    public TokenDemandResponseDTO requestToken(TokenDemandDTO tokenDemandDTO) {
        User requestingUser = getUserById(tokenDemandDTO.getUserId());
        Task requestedTask = getTaskById(tokenDemandDTO.getTaskId());

        if (requestingUser.getRole() != Role.USER) {
            throw new ValidationException("Only users with the role USER can request a token.");
        }

        if (!isUserAssignedToTask(requestingUser, requestedTask)) {
            throw new ValidationException("The requesting user is not assigned to the specified task.");
        }

        if (tokenDemandRepository.existsByTask(requestedTask)) {
            throw new ValidationException("You have already requested a token for the specified task.");
        }

        if (requestedTask.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new ValidationException("Token can only be requested for tasks with status IN_PROGRESS.");
        }

        if (tokenDemandDTO.getType() == TokenType.DELETE) {
            validateDeletionTokens(requestingUser.getId());
        }

        if (tokenDemandDTO.getType() == TokenType.REPLACEMENT) {
            validateModificationTokens(requestingUser.getId());
        }

        TokenDemand tokenDemand = tokenDemandMapper.dtoToEntity(tokenDemandDTO);
        tokenDemand.setUser(requestingUser);
        tokenDemand.setTask(requestedTask);
        tokenDemand.setDemandDate(LocalDateTime.now());
        tokenDemand.setType(tokenDemandDTO.getType());
        tokenDemand.setStatus(RequestStatus.PENDING);
        TokenDemand savedTokenDemand = tokenDemandRepository.save(tokenDemand);

        return tokenDemandMapper.entityToDto(savedTokenDemand);
    }

    private boolean isUserAssignedToTask(User user, Task task) {
        return user.equals(task.getAssignedTo());
    }


    private void validateDeletionTokens(Long userId) {
        int monthlyDeletionTokens = tokenDemandRepository.countMonthlyDeletionTokens(
                userId,
                TokenType.DELETE,
                LocalDate.now()
        );

        if (monthlyDeletionTokens >= 1) {
            throw new ValidationException("You have already requested a deletion token this month.");
        }
    }

    private void validateModificationTokens(Long userId) {
        int monthlyModificationTokens = tokenDemandRepository.countMonthlyModificationTokens(
                userId,
                TokenType.REPLACEMENT,
                LocalDate.now()
        );

        if (monthlyModificationTokens >= 2) {
            throw new ValidationException("You have already requested a modification token this month.");
        }
    }


    @Override
    @Transactional
    public TaskResponseDTO replaceTask(Long assignedToUserId, Long tokenDemandId, Long managerId) {
        TokenDemand tokenDemand = tokenDemandRepository.getById(tokenDemandId);
        if (tokenDemand == null) {
            throw new ValidationException("Token not found with ID: " + tokenDemandId);
        }

        if (getUserById(managerId).getRole() != Role.MANAGER) {
            throw new ValidationException("Only users with the role MANAGER can replace tasks.");
        }

        if (tokenDemand.getType() != TokenType.REPLACEMENT) {
            throw new ValidationException("Invalid token type for task replacement.");
        }

        if (tokenDemand.getStatus() != RequestStatus.APPROVED) {
            throw new ValidationException("Token is not approved for task replacement.");
        }

        Task taskToReplace = tokenDemand.getTask();

        if (taskToReplace.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new ValidationException("Only tasks in progress can be replaced.");
        }

        if (taskToReplace.getAssignedTo().getId().equals(assignedToUserId)) {
            throw new ValidationException("Invalid assignment. The task to replace must not be assigned to the same user.");
        }

        taskToReplace.setAssignedTo(getUserById(assignedToUserId));
        taskRepository.save(taskToReplace);

        tokenDemand.setStatus(RequestStatus.CHANGED);
        tokenDemandRepository.save(tokenDemand);

        return taskMapper.entityToDto(taskToReplace);
    }


    private boolean isManagerResponseOverdue(TokenDemand tokenDemand) {
        LocalDateTime requestDate = tokenDemand.getDemandDate();
        LocalDateTime deadline = requestDate.plusHours(12);
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(deadline);
    }

    @Override
    public List<TokenDemandResponseDTO> getAllTokenDemands() {
        List<TokenDemand> tokenDemands = tokenDemandRepository.findAll();
        return tokenDemands.stream()
                .map(tokenDemandMapper::entityToDto)
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found with ID: " + userId));
    }

    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ValidationException("Task with ID " + taskId + " not found."));
    }


}

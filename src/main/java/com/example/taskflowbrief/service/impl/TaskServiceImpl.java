package com.example.taskflowbrief.service.impl;

import com.example.taskflowbrief.enums.RequestStatus;
import com.example.taskflowbrief.enums.Role;
import com.example.taskflowbrief.enums.TaskStatus;
import com.example.taskflowbrief.mapper.TaskMapper;
import com.example.taskflowbrief.model.dto.TaskDTO;
import com.example.taskflowbrief.model.dto.TokenDemandDTO;
import com.example.taskflowbrief.model.dto.UserDTO;
import com.example.taskflowbrief.model.dto.response.TaskResponseDTO;
import com.example.taskflowbrief.model.entities.Tag;
import com.example.taskflowbrief.model.entities.Task;
import com.example.taskflowbrief.model.entities.TokenDemand;
import com.example.taskflowbrief.model.entities.User;
import com.example.taskflowbrief.repository.TagRepository;
import com.example.taskflowbrief.repository.TaskRepository;
import com.example.taskflowbrief.repository.TokenDemandRepository;
import com.example.taskflowbrief.repository.UserRepository;
import com.example.taskflowbrief.service.TaskService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TokenDemandRepository tokenDemandRepository;

    @Override
    public TaskResponseDTO createTask(TaskDTO taskDTO) {
        validateTaskDTO(taskDTO);

        Task task = taskMapper.dtoToEntity(taskDTO);
        task.setCreatedBy(getUserById(taskDTO.getCreatedByUserId()));
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStartDate(taskDTO.getStartDate());
        task.setDueDate(taskDTO.getDueDate());
        task.setAssignedTo(getUserById(taskDTO.getAssignedToUserId()));
        task.setTags(getTagsByIds(taskDTO.getTagIds()));
        task.setStatus(TaskStatus.IN_PROGRESS);
        Task savedTask = taskRepository.save(task);
        return taskMapper.entityToDto(savedTask);
    }

    @Override
    public TaskResponseDTO updateTask(Long taskId, TaskDTO taskDTO, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ValidationException("Task not found with ID: " + taskId));

        if (!isAdminOrCreator(userId, task.getCreatedBy())) {
            throw new ValidationException("User does not have permission to update this task.");
        }

        boolean hasChangedTokenDemand = tokenDemandRepository.existsByTaskAndStatus(task, RequestStatus.CHANGED);

        if (hasChangedTokenDemand) {
            throw new ValidationException("Task cannot be updated as it has a corresponding TokenDemand with status CHANGED.");
        }

        validateTaskDTO(taskDTO);

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStartDate(taskDTO.getStartDate());
        task.setDueDate(taskDTO.getDueDate());
        task.setStatus(taskDTO.getStatus());
        task.setAssignedTo(getUserById(taskDTO.getAssignedToUserId()));
        task.setTags(getTagsByIds(taskDTO.getTagIds()));

        Task updatedTask = taskRepository.save(task);
        return taskMapper.entityToDto(updatedTask);
    }



    public void deleteTask(Long taskId, Long userId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();

            if (isAdminOrCreator(userId, task.getCreatedBy())) {
                boolean hasChangedTokenDemand = tokenDemandRepository.existsByTaskAndStatus(task, RequestStatus.CHANGED);

                if (hasChangedTokenDemand) {
                    throw new ValidationException("Cannot delete the task because it has a TokenDemand with status CHANGED.");
                }

                taskRepository.deleteById(taskId);
            } else {
                throw new ValidationException("User does not have permission to delete this task. You can send a token to delete this task!!");
            }
        } else {
            throw new ValidationException("Task not found with ID: " + taskId);
        }
    }


    private boolean isAdminOrCreator(Long userId, User createdBy) {
        User user = getUserById(userId);

        return user.getRole() == Role.MANAGER || createdBy.equals(user);
    }

    @Override
    public TaskResponseDTO getTaskById(Long taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            TaskResponseDTO taskResponseDTO = taskMapper.entityToDto(task);
            taskResponseDTO.setAssignedTo(mapUserToUserDTO(task.getAssignedTo()));
            taskResponseDTO.setCreatedBy(mapUserToUserDTO(task.getCreatedBy()));
            return taskResponseDTO;
        } else {
            throw new ValidationException("Task not found with ID: " + taskId);
        }
    }

    @Override
    public List<TaskResponseDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> {
                    TaskResponseDTO taskResponseDTO = taskMapper.entityToDto(task);
                    return taskResponseDTO;
                })
                .toList();
    }

    private void validateTaskDTO(TaskDTO taskDTO) {
        LocalDate currentDate = LocalDate.now();

        if (taskDTO.getStartDate().isBefore(currentDate)) {
            throw new ValidationException("Task start date cannot be in the past.");
        }

        if (taskDTO.getDueDate().isBefore(taskDTO.getStartDate())) {
            throw new ValidationException("Task due date cannot be in the past.");
        }

        if (taskDTO.getStartDate().isAfter(currentDate.plusDays(3))) {
            throw new ValidationException("Task must be scheduled at least 3 days in advance.");
        }

        getTagsByIds(taskDTO.getTagIds());

        if (taskDTO.getTagIds().size() < 2) {
            throw new ValidationException("At least two tags are required for the task.");
        }

        getUserById(taskDTO.getCreatedByUserId());
        getUserById(taskDTO.getAssignedToUserId());

        if (getUserById(taskDTO.getCreatedByUserId()).getRole() == Role.USER) {
            if (taskDTO.getCreatedByUserId() != taskDTO.getAssignedToUserId()) {
                throw new ValidationException("User with role USER can only assign tasks to themselves.");
            }
        }
    }

    @Override
    @Transactional
    public void updateTaskStatusToDone(Long taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();

            if (task.getDueDate().isBefore(LocalDate.now())) {
                throw new ValidationException("Task cannot be marked as DONE after the due date has passed.");
            }

            if (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.UNCOMPLETED) {
                throw new ValidationException("Task is already marked as DONE or is UNCOMPLETED.");
            }

            taskRepository.updateTaskStatusToDone(taskId, LocalDate.now());
        } else {
            throw new ValidationException("Task not found with ID: " + taskId);
        }
    }

    @Override
    @Transactional
    public void updateOverdueTasksStatus() {
        List<Task> overdueTasks = taskRepository.findLateTasks(LocalDate.now());

        for (Task task : overdueTasks) {
            if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                task.setStatus(TaskStatus.UNCOMPLETED);
            }
        }
    }

    private List<Tag> getTagsByIds(List<Long> tagIds) {
        List<Tag> existingTags = tagRepository.findAllById(tagIds);
        if (existingTags.size() != tagIds.size()) {
            throw new ValidationException("One or more tags do not exist.");
        }
        return existingTags;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found with ID: " + userId));
    }

    private UserDTO mapUserToUserDTO(User user) {
        if (user != null) {
            return UserDTO.builder()
                    .userName(user.getUserName())
                    .email(user.getEmail())
                    .telephone(user.getTelephone())
                    .role(user.getRole())
                    .build();
        }
        return null;
    }

    private List<TokenDemandDTO> mapTokenToDTO(List<TokenDemand> tokenDemands) {
        return tokenDemands.stream()
                .map(this::mapTokenDemandToDTO)
                .collect(Collectors.toList());
    }

    private TokenDemandDTO mapTokenDemandToDTO(TokenDemand tokenDemand) {
        return TokenDemandDTO.builder()
                .demandDate(tokenDemand.getDemandDate())
                .status(tokenDemand.getStatus())
                .type(tokenDemand.getType())
                .build();
    }
}

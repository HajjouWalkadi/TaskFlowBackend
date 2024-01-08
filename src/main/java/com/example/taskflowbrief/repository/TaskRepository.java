package com.example.taskflowbrief.repository;


import com.example.taskflowbrief.model.entities.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.status = 'COMPLETED' WHERE t.id = :taskId AND t.dueDate >= :currentDate AND t.status = 'IN_PROGRESS'")
    void updateTaskStatusToDone(@Param("taskId") Long taskId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate")
    List<Task> findLateTasks(@Param("currentDate") LocalDate currentDate);
}

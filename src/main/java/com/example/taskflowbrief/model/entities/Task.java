package com.example.taskflowbrief.model.entities;


import com.example.taskflowbrief.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "text")
    private String description;


    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToMany
    @JoinTable(name = "task_tag",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TokenDemand> tokenDemands;

}
//give me a form json for postman to save a task in the database
// {
//     "title": "task 1",
//     "description": "description 1",
//     "startDate": "2021-09-01",
//     "dueDate": "2021-09-01",
//     "createdByUserId": 1,
//     "assignedToUserId": 1,
//     "status": "IN_PROGRESS",
//     "tagIds": [1, 2]
// }
//give me a form json for postman to save a task in the database
// {



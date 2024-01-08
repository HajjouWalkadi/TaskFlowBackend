package com.example.taskflowbrief.repository;


import com.example.taskflowbrief.model.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

}

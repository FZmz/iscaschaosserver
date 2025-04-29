package com.iscas.lndicatormonitor.repository;

import com.iscas.lndicatormonitor.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

    Page<Task> findAll(Pageable pageable);
}

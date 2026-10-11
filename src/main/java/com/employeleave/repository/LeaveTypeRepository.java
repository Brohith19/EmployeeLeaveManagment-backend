package com.employeleave.repository;

import com.employeleave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    boolean existsByName(String name);
}
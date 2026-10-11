package com.employeleave.repository;

import com.employeleave.entity.Employee;
import com.employeleave.entity.LeaveApproval;
import com.employeleave.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveApprovalRepository extends JpaRepository<LeaveApproval, Long> {

    Optional<LeaveApproval> findByLeaveRequest(LeaveRequest leaveRequest);

    List<LeaveApproval> findByManager(Employee manager);

    List<LeaveApproval> findByAction(String action);
}
package com.employeleave.repository;

import com.employeleave.entity.Employee;
import com.employeleave.entity.LeaveRequest;
import com.employeleave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(Employee employee);

    List<LeaveRequest> findByStatus(String status);

    List<LeaveRequest> findByEmployeeAndStatus(
            Employee employee,
            String status
    );

    List<LeaveRequest> findByLeaveType(LeaveType leaveType);

    List<LeaveRequest> findByStartDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsByEmployeeAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Employee employee,
            String status,
            LocalDate endDate,
            LocalDate startDate
    );
}
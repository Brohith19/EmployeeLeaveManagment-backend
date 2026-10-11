package com.employeleave.repository;

import com.employeleave.entity.Employee;
import com.employeleave.entity.LeaveBalance;
import com.employeleave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByEmployee(Employee employee);

    Optional<LeaveBalance> findByEmployeeAndLeaveTypeAndYear(
            Employee employee,
            LeaveType leaveType,
            Integer year
    );

    List<LeaveBalance> findByYear(Integer year);
}
package com.employeleave.service;

import com.employeleave.entity.Employee;
import com.employeleave.entity.LeaveBalance;
import com.employeleave.entity.LeaveType;
import com.employeleave.repository.EmployeeRepository;
import com.employeleave.repository.LeaveBalanceRepository;
import com.employeleave.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveBalanceService(
            LeaveBalanceRepository leaveBalanceRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository) {

        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public LeaveBalance createLeaveBalance(
            Long employeeId,
            Long leaveTypeId,
            Integer totalDays,
            Integer year) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        if (leaveBalanceRepository
                .findByEmployeeAndLeaveTypeAndYear(employee, leaveType, year)
                .isPresent()) {

            throw new RuntimeException(
                    "Leave balance already exists for this employee, leave type and year"
            );
        }

        if (totalDays == null || totalDays < 0) {
            throw new RuntimeException("Total days must be zero or greater");
        }

        LeaveBalance balance = new LeaveBalance();

        balance.setEmployee(employee);
        balance.setLeaveType(leaveType);
        balance.setTotalDays(totalDays);
        balance.setUsedDays(0);
        balance.setRemainingDays(totalDays);
        balance.setYear(year);

        return leaveBalanceRepository.save(balance);
    }

    public List<LeaveBalance> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll();
    }

    public Optional<LeaveBalance> getLeaveBalanceById(Long id) {
        return leaveBalanceRepository.findById(id);
    }

    public List<LeaveBalance> getBalancesByEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return leaveBalanceRepository.findByEmployee(employee);
    }

    public List<LeaveBalance> getBalancesByUsername(String username) {

        Employee employee = employeeRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return leaveBalanceRepository.findByEmployee(employee);
    }

    public List<LeaveBalance> getBalancesByYear(Integer year) {
        return leaveBalanceRepository.findByYear(year);
    }

    public LeaveBalance updateLeaveBalance(
            Long id,
            Integer totalDays,
            Integer usedDays,
            Integer year) {

        LeaveBalance balance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        if (totalDays == null || totalDays < 0) {
            throw new RuntimeException("Total days must be zero or greater");
        }

        if (usedDays == null || usedDays < 0) {
            throw new RuntimeException("Used days must be zero or greater");
        }

        if (usedDays > totalDays) {
            throw new RuntimeException("Used days cannot exceed total days");
        }

        balance.setTotalDays(totalDays);
        balance.setUsedDays(usedDays);
        balance.setRemainingDays(totalDays - usedDays);
        balance.setYear(year);

        return leaveBalanceRepository.save(balance);
    }

    public LeaveBalance deductLeaveDays(Long id, Integer days) {

        LeaveBalance balance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        if (days == null || days <= 0) {
            throw new RuntimeException("Days must be greater than zero");
        }

        if (balance.getRemainingDays() < days) {
            throw new RuntimeException("Insufficient leave balance");
        }

        balance.setUsedDays(balance.getUsedDays() + days);
        balance.setRemainingDays(balance.getRemainingDays() - days);

        return leaveBalanceRepository.save(balance);
    }

    public void deleteLeaveBalance(Long id) {

        if (!leaveBalanceRepository.existsById(id)) {
            throw new RuntimeException("Leave balance not found");
        }

        leaveBalanceRepository.deleteById(id);
    }
}
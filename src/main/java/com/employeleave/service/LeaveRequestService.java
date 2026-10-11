package com.employeleave.service;

import com.employeleave.entity.Employee;
import com.employeleave.entity.LeaveBalance;
import com.employeleave.entity.LeaveRequest;
import com.employeleave.entity.LeaveType;
import com.employeleave.repository.EmployeeRepository;
import com.employeleave.repository.LeaveBalanceRepository;
import com.employeleave.repository.LeaveRequestRepository;
import com.employeleave.repository.LeaveTypeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveRequestService(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository,
            LeaveBalanceRepository leaveBalanceRepository) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    // CREATE LEAVE REQUEST
    @Transactional
    public LeaveRequest createLeaveRequest(
            Long employeeId,
            Long leaveTypeId,
            LocalDate startDate,
            LocalDate endDate,
            String reason) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Leave reason is required");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Leave type not found"));

        int numberOfDays = Math.toIntExact(
                ChronoUnit.DAYS.between(startDate, endDate) + 1);

        int year = startDate.getYear();

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeAndLeaveTypeAndYear(
                        employee, leaveType, year)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Leave balance not found for this employee and year"));

        if (balance.getRemainingDays() < numberOfDays) {
            throw new IllegalArgumentException("Insufficient leave balance");
        }

        boolean overlappingLeave =
                leaveRequestRepository
                        .existsByEmployeeAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                employee, "PENDING", endDate, startDate)
                || leaveRequestRepository
                        .existsByEmployeeAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                employee, "APPROVED", endDate, startDate);

        if (overlappingLeave) {
            throw new IllegalArgumentException(
                    "Leave request overlaps with an existing leave");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setNumberOfDays(numberOfDays);
        leaveRequest.setReason(reason);
        leaveRequest.setStatus("PENDING");
        leaveRequest.setManagerComment(null);

        return leaveRequestRepository.save(leaveRequest);
    }

    // GET ALL LEAVE REQUESTS
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    // GET LEAVE REQUEST BY ID
    public Optional<LeaveRequest> getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id);
    }

    // GET ALL REQUESTS FOR AN EMPLOYEE
    public List<LeaveRequest> getRequestsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found"));

        return leaveRequestRepository.findByEmployee(employee);
    }

    // GET ALL REQUESTS FOR THE AUTHENTICATED USERNAME
    public List<LeaveRequest> getRequestsByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        Employee employee = employeeRepository.findByUserUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No employee found for username: " + username));

        return leaveRequestRepository.findByEmployee(employee);
    }

    // GET REQUESTS BY STATUS
    public List<LeaveRequest> getRequestsByStatus(String status) {
        return leaveRequestRepository.findByStatus(status);
    }

    // GET PENDING REQUESTS BY EMPLOYEE
    public List<LeaveRequest> getPendingRequestsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found"));

        return leaveRequestRepository.findByEmployeeAndStatus(
                employee, "PENDING");
    }

    // GET PENDING REQUESTS BY USERNAME
    public List<LeaveRequest> getPendingRequestsByUsername(String username) {
        Employee employee = employeeRepository.findByUserUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found"));

        return leaveRequestRepository.findByEmployeeAndStatus(
                employee, "PENDING");
    }

    // DELETE A PENDING REQUEST
    @Transactional
    public void deleteLeaveRequest(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Leave request not found"));

        if (!"PENDING".equalsIgnoreCase(leaveRequest.getStatus())) {
            throw new IllegalArgumentException(
                    "Only pending leave requests can be deleted");
        }

        leaveRequestRepository.delete(leaveRequest);
    }
}
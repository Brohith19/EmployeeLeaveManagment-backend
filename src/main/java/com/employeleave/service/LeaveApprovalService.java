
package com.employeleave.service;

import com.employeleave.entity.Employee;
import com.employeleave.entity.LeaveApproval;
import com.employeleave.entity.LeaveBalance;
import com.employeleave.entity.LeaveRequest;
import com.employeleave.entity.Notification;

import com.employeleave.repository.EmployeeRepository;
import com.employeleave.repository.LeaveApprovalRepository;
import com.employeleave.repository.LeaveBalanceRepository;
import com.employeleave.repository.LeaveRequestRepository;
import com.employeleave.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveApprovalService {

    private final LeaveApprovalRepository leaveApprovalRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final NotificationRepository notificationRepository;

    public LeaveApprovalService(
            LeaveApprovalRepository leaveApprovalRepository,
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            LeaveBalanceRepository leaveBalanceRepository,
            NotificationRepository notificationRepository) {

        this.leaveApprovalRepository = leaveApprovalRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public LeaveApproval approveLeave(
            Long leaveRequestId,
            Long managerId,
            String comment) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() ->
                        new RuntimeException("Leave request not found"));

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() ->
                        new RuntimeException("Manager not found"));

        validateManager(leaveRequest, manager);

        if (!"PENDING".equals(leaveRequest.getStatus())) {
            throw new RuntimeException(
                    "Only pending leave requests can be approved");
        }

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeAndLeaveTypeAndYear(
                        leaveRequest.getEmployee(),
                        leaveRequest.getLeaveType(),
                        leaveRequest.getStartDate().getYear())
                .orElseThrow(() ->
                        new RuntimeException("Leave balance not found"));

        if (balance.getRemainingDays() < leaveRequest.getNumberOfDays()) {
            throw new RuntimeException("Insufficient leave balance");
        }

        balance.setUsedDays(
                balance.getUsedDays() + leaveRequest.getNumberOfDays());

        balance.setRemainingDays(
                balance.getRemainingDays() - leaveRequest.getNumberOfDays());

        leaveBalanceRepository.save(balance);

        leaveRequest.setStatus("APPROVED");
        leaveRequest.setManagerComment(comment);
        leaveRequestRepository.save(leaveRequest);

        LeaveApproval approval = new LeaveApproval();
        approval.setLeaveRequest(leaveRequest);
        approval.setManager(manager);
        approval.setAction("APPROVED");
        approval.setComment(comment);
        approval.setActionDate(LocalDateTime.now());

        LeaveApproval savedApproval =
                leaveApprovalRepository.save(approval);

        String message = "Your leave request #" + leaveRequest.getId()
                + " (" + leaveRequest.getLeaveType().getName()
                + ") has been APPROVED.";

        if (comment != null && !comment.trim().isEmpty()) {
            message += " Manager comment: " + comment.trim();
        }

        createEmployeeNotification(
                leaveRequest.getEmployee(),
                "Leave Request Approved",
                message);

        return savedApproval;
    }

    @Transactional
    public LeaveApproval rejectLeave(
            Long leaveRequestId,
            Long managerId,
            String comment) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() ->
                        new RuntimeException("Leave request not found"));

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() ->
                        new RuntimeException("Manager not found"));

        validateManager(leaveRequest, manager);

        if (!"PENDING".equals(leaveRequest.getStatus())) {
            throw new RuntimeException(
                    "Only pending leave requests can be rejected");
        }

        leaveRequest.setStatus("REJECTED");
        leaveRequest.setManagerComment(comment);
        leaveRequestRepository.save(leaveRequest);

        LeaveApproval approval = new LeaveApproval();
        approval.setLeaveRequest(leaveRequest);
        approval.setManager(manager);
        approval.setAction("REJECTED");
        approval.setComment(comment);
        approval.setActionDate(LocalDateTime.now());

        LeaveApproval savedApproval =
                leaveApprovalRepository.save(approval);

        String message = "Your leave request #" + leaveRequest.getId()
                + " (" + leaveRequest.getLeaveType().getName()
                + ") has been REJECTED.";

        if (comment != null && !comment.trim().isEmpty()) {
            message += " Manager comment: " + comment.trim();
        }

        createEmployeeNotification(
                leaveRequest.getEmployee(),
                "Leave Request Rejected",
                message);

        return savedApproval;
    }

    private void createEmployeeNotification(
            Employee employee,
            String title,
            String message) {

        Notification notification = new Notification();
        notification.setEmployee(employee);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    private void validateManager(
            LeaveRequest leaveRequest,
            Employee manager) {

        Employee assignedManager = leaveRequest.getEmployee().getManager();

        if (assignedManager == null) {
            throw new RuntimeException(
                    "No manager assigned to this employee");
        }

        if (!assignedManager.getId().equals(manager.getId())) {
            throw new RuntimeException(
                    "You are not authorized to approve this leave request");
        }
    }

    public List<LeaveApproval> getAllApprovals() {
        return leaveApprovalRepository.findAll();
    }

    public Optional<LeaveApproval> getApprovalById(Long id) {
        return leaveApprovalRepository.findById(id);
    }

    public Optional<LeaveApproval> getApprovalByLeaveRequest(
            Long leaveRequestId) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() ->
                        new RuntimeException("Leave request not found"));

        return leaveApprovalRepository.findByLeaveRequest(leaveRequest);
    }

    public List<LeaveApproval> getApprovalsByManager(Long managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() ->
                        new RuntimeException("Manager not found"));

        return leaveApprovalRepository.findByManager(manager);
    }

    public List<LeaveApproval> getApprovalsByAction(String action) {
        return leaveApprovalRepository.findByAction(action);
    }
}

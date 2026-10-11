package com.employeleave.controller;

import com.employeleave.entity.LeaveRequest;
import com.employeleave.service.LeaveRequestService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    // CREATE LEAVE REQUEST
    @PostMapping
    public ResponseEntity<?> createLeaveRequest(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String reason) {

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            LeaveRequest saved = leaveRequestService.createLeaveRequest(
                    employeeId,
                    leaveTypeId,
                    start,
                    end,
                    reason
            );

            return ResponseEntity.ok(saved);

        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Leave request failed");
            error.put("message",
                    ex.getMessage() == null ? "Unknown backend error" : ex.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET ALL LEAVE REQUESTS
    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    // GET ALL LEAVE REQUESTS FOR THE AUTHENTICATED EMPLOYEE
    @GetMapping("/mine")
    public ResponseEntity<?> getMyLeaveRequests(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "Authentication required")
            );
        }

        try {
            String username = authentication.getName();

            return ResponseEntity.ok(
                    leaveRequestService.getRequestsByUsername(username)
            );
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unable to retrieve your leave requests");
            error.put("message",
                    ex.getMessage() == null ? "Unknown error" : ex.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET LEAVE REQUEST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequestById(@PathVariable Long id) {
        return leaveRequestService.getLeaveRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET REQUESTS BY EMPLOYEE
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getRequestsByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                leaveRequestService.getRequestsByEmployee(employeeId)
        );
    }

    // GET PENDING REQUESTS BY EMPLOYEE
    @GetMapping("/employee/{employeeId}/pending")
    public ResponseEntity<List<LeaveRequest>> getPendingRequestsByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                leaveRequestService.getPendingRequestsByEmployee(employeeId)
        );
    }

    // GET PENDING REQUESTS FOR A USERNAME
    @GetMapping("/my-pending")
    public ResponseEntity<List<LeaveRequest>> getMyPendingRequests(
            @RequestParam String username) {
        return ResponseEntity.ok(
                leaveRequestService.getPendingRequestsByUsername(username)
        );
    }

    // GET REQUESTS BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveRequest>> getRequestsByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(
                leaveRequestService.getRequestsByStatus(status)
        );
    }

    // DELETE A PENDING REQUEST
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Long id) {
        try {
            leaveRequestService.deleteLeaveRequest(id);

            return ResponseEntity.ok(
                    Map.of("message", "Leave request deleted successfully")
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            ex.getMessage() == null ? "Delete failed" : ex.getMessage())
            );
        }
    }
}
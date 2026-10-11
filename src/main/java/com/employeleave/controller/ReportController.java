package com.employeleave.controller;

import com.employeleave.entity.LeaveRequest;
import com.employeleave.service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final LeaveRequestService leaveRequestService;

    public ReportController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping("/leave-summary")
    public ResponseEntity<Map<String, Object>> getLeaveSummary() {

        List<LeaveRequest> allRequests =
                leaveRequestService.getAllLeaveRequests();

        long pending = allRequests.stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()))
                .count();

        long approved = allRequests.stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus()))
                .count();

        long rejected = allRequests.stream()
                .filter(r -> "REJECTED".equalsIgnoreCase(r.getStatus()))
                .count();

        Map<String, Object> report = new LinkedHashMap<>();

        report.put("totalRequests", allRequests.size());
        report.put("pendingRequests", pending);
        report.put("approvedRequests", approved);
        report.put("rejectedRequests", rejected);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeReport(
            @PathVariable Long employeeId) {

        List<LeaveRequest> requests =
                leaveRequestService.getRequestsByEmployee(employeeId);

        long pending = requests.stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()))
                .count();

        long approved = requests.stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus()))
                .count();

        long rejected = requests.stream()
                .filter(r -> "REJECTED".equalsIgnoreCase(r.getStatus()))
                .count();

        int totalLeaveDays = requests.stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus()))
                .mapToInt(LeaveRequest::getNumberOfDays)
                .sum();

        Map<String, Object> report = new LinkedHashMap<>();

        report.put("employeeId", employeeId);
        report.put("totalRequests", requests.size());
        report.put("pendingRequests", pending);
        report.put("approvedRequests", approved);
        report.put("rejectedRequests", rejected);
        report.put("approvedLeaveDays", totalLeaveDays);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getStatusReport(
            @PathVariable String status) {

        List<LeaveRequest> requests =
                leaveRequestService.getRequestsByStatus(status);

        Map<String, Object> report = new LinkedHashMap<>();

        report.put("status", status.toUpperCase());
        report.put("count", requests.size());
        report.put("requests", requests);

        return ResponseEntity.ok(report);
    }
}
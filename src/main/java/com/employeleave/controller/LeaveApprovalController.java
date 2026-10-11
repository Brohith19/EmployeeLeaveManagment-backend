package com.employeleave.controller;

import com.employeleave.entity.LeaveApproval;
import com.employeleave.service.LeaveApprovalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-approvals")
public class LeaveApprovalController {

    private final LeaveApprovalService leaveApprovalService;

    public LeaveApprovalController(LeaveApprovalService leaveApprovalService) {
        this.leaveApprovalService = leaveApprovalService;
    }

    @PutMapping("/{leaveRequestId}/approve")
    public ResponseEntity<LeaveApproval> approveLeave(
            @PathVariable Long leaveRequestId,
            @RequestParam Long managerId,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                leaveApprovalService.approveLeave(
                        leaveRequestId,
                        managerId,
                        comment
                )
        );
    }

    @PutMapping("/{leaveRequestId}/reject")
    public ResponseEntity<LeaveApproval> rejectLeave(
            @PathVariable Long leaveRequestId,
            @RequestParam Long managerId,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                leaveApprovalService.rejectLeave(
                        leaveRequestId,
                        managerId,
                        comment
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<LeaveApproval>> getAllApprovals() {
        return ResponseEntity.ok(
                leaveApprovalService.getAllApprovals()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveApproval> getApprovalById(
            @PathVariable Long id) {

        return leaveApprovalService.getApprovalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/request/{leaveRequestId}")
    public ResponseEntity<LeaveApproval> getApprovalByLeaveRequest(
            @PathVariable Long leaveRequestId) {

        return leaveApprovalService
                .getApprovalByLeaveRequest(leaveRequestId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<LeaveApproval>> getApprovalsByManager(
            @PathVariable Long managerId) {

        return ResponseEntity.ok(
                leaveApprovalService.getApprovalsByManager(managerId)
        );
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<LeaveApproval>> getApprovalsByAction(
            @PathVariable String action) {

        return ResponseEntity.ok(
                leaveApprovalService.getApprovalsByAction(action)
        );
    }
}
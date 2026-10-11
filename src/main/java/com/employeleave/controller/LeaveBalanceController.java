package com.employeleave.controller;

import com.employeleave.entity.LeaveBalance;
import com.employeleave.service.LeaveBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balances")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    public LeaveBalanceController(LeaveBalanceService leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    @PostMapping
    public ResponseEntity<LeaveBalance> createLeaveBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam Integer totalDays,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                leaveBalanceService.createLeaveBalance(
                        employeeId, leaveTypeId, totalDays, year
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<LeaveBalance>> getAllLeaveBalances() {
        return ResponseEntity.ok(
                leaveBalanceService.getAllLeaveBalances()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveBalance> getLeaveBalanceById(
            @PathVariable Long id) {

        return leaveBalanceService.getLeaveBalanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalance>> getBalancesByEmployee(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                leaveBalanceService.getBalancesByEmployee(employeeId)
        );
    }

    @GetMapping("/my-balance")
    public ResponseEntity<List<LeaveBalance>> getMyBalance(
            @RequestParam String username) {

        return ResponseEntity.ok(
                leaveBalanceService.getBalancesByUsername(username)
        );
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<LeaveBalance>> getBalancesByYear(
            @PathVariable Integer year) {

        return ResponseEntity.ok(
                leaveBalanceService.getBalancesByYear(year)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveBalance> updateLeaveBalance(
            @PathVariable Long id,
            @RequestParam Integer totalDays,
            @RequestParam Integer usedDays,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                leaveBalanceService.updateLeaveBalance(
                        id, totalDays, usedDays, year
                )
        );
    }

    @PutMapping("/{id}/deduct")
    public ResponseEntity<LeaveBalance> deductLeaveDays(
            @PathVariable Long id,
            @RequestParam Integer days) {

        return ResponseEntity.ok(
                leaveBalanceService.deductLeaveDays(id, days)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeaveBalance(
            @PathVariable Long id) {

        leaveBalanceService.deleteLeaveBalance(id);

        return ResponseEntity.ok(
                "Leave balance deleted successfully"
        );
    }
}
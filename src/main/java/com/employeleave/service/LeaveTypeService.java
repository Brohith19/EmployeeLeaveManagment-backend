package com.employeleave.service;

import com.employeleave.entity.LeaveType;
import com.employeleave.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public LeaveType createLeaveType(LeaveType leaveType) {
        if (leaveTypeRepository.existsByName(leaveType.getName())) {
            throw new RuntimeException("Leave type already exists");
        }

        return leaveTypeRepository.save(leaveType);
    }

    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    public Optional<LeaveType> getLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id);
    }

    public LeaveType updateLeaveType(Long id, LeaveType updatedLeaveType) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        leaveType.setName(updatedLeaveType.getName());
        leaveType.setDescription(updatedLeaveType.getDescription());
        leaveType.setDefaultDays(updatedLeaveType.getDefaultDays());
        leaveType.setPaid(updatedLeaveType.getPaid());
        leaveType.setStatus(updatedLeaveType.getStatus());

        return leaveTypeRepository.save(leaveType);
    }

    public void deleteLeaveType(Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new RuntimeException("Leave type not found");
        }

        leaveTypeRepository.deleteById(id);
    }
}
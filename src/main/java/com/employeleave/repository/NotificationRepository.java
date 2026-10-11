
package com.employeleave.repository;

import com.employeleave.entity.Employee;
import com.employeleave.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Get all notifications for an employee
    List<Notification> findByEmployee(Employee employee);

    // Get unread notifications for an employee
    List<Notification> findByEmployeeAndIsRead(
            Employee employee,
            Boolean isRead
    );

    // Get notifications for an employee, newest first
    List<Notification> findByEmployeeIdOrderByCreatedAtDesc(
            Long employeeId
    );

    // Get unread notifications, newest first
    List<Notification> findByEmployeeIdAndIsReadFalseOrderByCreatedAtDesc(
            Long employeeId
    );
}


package com.employeleave.service;

import com.employeleave.entity.Employee;
import com.employeleave.entity.Notification;
import com.employeleave.repository.EmployeeRepository;
import com.employeleave.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            EmployeeRepository employeeRepository) {
        this.notificationRepository = notificationRepository;
        this.employeeRepository = employeeRepository;
    }

    // Used internally when a manager approves or rejects a request.
    public Notification createNotification(
            Long employeeId, String title, String message) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        Notification notification = new Notification();
        notification.setEmployee(employee);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    // Always derive the employee from the authenticated username.
    public List<Notification> getMyNotifications(String username) {
        Employee employee = getEmployeeForUsername(username);

        return notificationRepository
                .findByEmployeeIdOrderByCreatedAtDesc(employee.getId());
    }

    public List<Notification> getMyUnreadNotifications(String username) {
        Employee employee = getEmployeeForUsername(username);

        return notificationRepository
                .findByEmployeeIdAndIsReadFalseOrderByCreatedAtDesc(
                        employee.getId());
    }

    public long getMyUnreadCount(String username) {
        return getMyUnreadNotifications(username).size();
    }

    @Transactional
    public Notification markMyNotificationAsRead(
            Long notificationId, String username) {

        Employee employee = getEmployeeForUsername(username);

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));

        if (!notification.getEmployee().getId().equals(employee.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You cannot access another employee's notification");
        }

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    private Employee getEmployeeForUsername(String username) {
        return employeeRepository.findByUserUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee profile not found for current user"));
    }
}

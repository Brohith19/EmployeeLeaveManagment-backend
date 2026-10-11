
package com.employeleave.controller;

import com.employeleave.entity.Notification;
import com.employeleave.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Notification>> getMyNotifications(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        authentication.getName()));
    }

    @GetMapping("/mine/unread")
    public ResponseEntity<List<Notification>> getMyUnreadNotifications(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications(
                        authentication.getName()));
    }

    @GetMapping("/mine/unread-count")
    public ResponseEntity<Map<String, Long>> getMyUnreadCount(
            Authentication authentication) {

        return ResponseEntity.ok(
                Map.of("count",
                        notificationService.getMyUnreadCount(
                                authentication.getName())));
    }

    @PutMapping("/mine/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.markMyNotificationAsRead(
                        id, authentication.getName()));
    }
}

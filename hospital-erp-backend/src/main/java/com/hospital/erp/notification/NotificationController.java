package com.hospital.erp.notification;

import com.hospital.erp.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ApiResponse<List<Notification>> notifications() {
        return ApiResponse.ok(notificationService.mine(), "Notifications loaded");
    }

    @GetMapping("/notifications/unread-count")
    public ApiResponse<Map<String, Long>> unreadCount() {
        return ApiResponse.ok(Map.of("count", notificationService.unreadCount()), "Unread count loaded");
    }

    @PutMapping("/notifications/{id}/read")
    public ApiResponse<Notification> markRead(@PathVariable Long id) {
        return ApiResponse.ok(notificationService.markRead(id), "Notification marked as read");
    }
}

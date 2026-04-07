package com.hospital.erp.notification;

import com.hospital.erp.common.enums.NotificationType;
import com.hospital.erp.user.CurrentUserService;
import com.hospital.erp.user.User;
import com.hospital.erp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void notifyCenter(Long centerId, NotificationType type, String title, String message, Long referenceId, String referenceType) {
        List<User> users = userRepository.findByCenter_IdAndActiveTrue(centerId);
        users.forEach(user -> create(user, type, title, message, referenceId, referenceType));
    }

    @Transactional
    public Notification create(User user, NotificationType type, String title, String message, Long referenceId, String referenceType) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    public List<Notification> mine() {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(currentUserService.get().getId());
    }

    public long unreadCount() {
        return notificationRepository.countByUser_IdAndReadFalse(currentUserService.get().getId());
    }

    @Transactional
    public Notification markRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}

package com.hospital.erp.user;

import com.hospital.erp.user.dto.NotificationDeliveryResponse;
import com.hospital.erp.user.dto.VerificationCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final NotificationLogRepository notificationLogRepository;

    @Value("${app.notifications.email-delivery:LOG_ONLY}")
    private String emailDeliveryMode;

    @Value("${app.notifications.sms-delivery:LOG_ONLY}")
    private String smsDeliveryMode;

    @Transactional
    public List<NotificationDeliveryResponse> sendWelcome(User user, String temporaryPassword, boolean sendEmail, boolean sendSms) {
        List<NotificationDeliveryResponse> deliveries = new ArrayList<>();
        String message = """
                Welcome to Apana Swastha Kendra.
                Username: %s
                Temporary password: %s
                Please sign in and complete your profile verification.
                """.formatted(user.getEmail(), temporaryPassword);

        if (sendEmail) {
            deliveries.add(deliver(user, NotificationChannel.EMAIL, user.getEmail(), "Welcome to Apana Swastha Kendra", message, emailDeliveryMode));
        }
        if (sendSms) {
            deliveries.add(deliver(user, NotificationChannel.SMS, user.getPhone(), "Registration complete", message, smsDeliveryMode));
        }
        return deliveries;
    }

    @Transactional
    public VerificationCodeResponse sendVerificationCode(User user, VerificationChannel channel, String destination, String code, LocalDateTime expiresAt) {
        NotificationChannel notificationChannel = channel == VerificationChannel.EMAIL ? NotificationChannel.EMAIL : NotificationChannel.SMS;
        String mode = channel == VerificationChannel.EMAIL ? emailDeliveryMode : smsDeliveryMode;
        String message = "Your verification code is %s. It expires at %s.".formatted(code, expiresAt);
        NotificationDeliveryResponse delivery = deliver(user, notificationChannel, destination, "Verification code", message, mode);
        String previewCode = "LOG_ONLY".equalsIgnoreCase(mode) ? code : null;
        return new VerificationCodeResponse(
                channel.name(),
                maskDestination(destination),
                delivery.status(),
                previewCode,
                expiresAt,
                delivery.message()
        );
    }

    private NotificationDeliveryResponse deliver(
            User user,
            NotificationChannel channel,
            String destination,
            String subject,
            String message,
            String mode
    ) {
        if (destination == null || destination.isBlank()) {
            return saveLog(user, channel, "N/A", subject, message, NotificationStatus.SKIPPED,
                    "Skipped because the destination is not available.");
        }

        log.info("Notification [{}] to {} via {}: {}", channel, destination, mode, message);
        return saveLog(user, channel, destination, subject, message, NotificationStatus.LOGGED,
                "Notification saved to audit log. Configure a real provider later to send externally.");
    }

    private NotificationDeliveryResponse saveLog(
            User user,
            NotificationChannel channel,
            String destination,
            String subject,
            String message,
            NotificationStatus status,
            String note
    ) {
        NotificationLog logEntry = new NotificationLog();
        logEntry.setUser(user);
        logEntry.setChannel(channel);
        logEntry.setRecipient(destination);
        logEntry.setSubject(subject);
        logEntry.setMessage(message);
        logEntry.setStatus(status);
        notificationLogRepository.save(logEntry);
        return new NotificationDeliveryResponse(channel.name(), maskDestination(destination), status.name(), note);
    }

    private String maskDestination(String destination) {
        if (destination == null || destination.isBlank() || "N/A".equals(destination)) {
            return "Not set";
        }
        int visible = Math.min(3, destination.length());
        return destination.substring(0, visible) + "*".repeat(Math.max(0, destination.length() - visible));
    }
}

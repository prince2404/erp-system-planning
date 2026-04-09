package com.hospital.erp.user.dto;

public record NotificationDeliveryResponse(
        String channel,
        String destination,
        String status,
        String message
) {
}

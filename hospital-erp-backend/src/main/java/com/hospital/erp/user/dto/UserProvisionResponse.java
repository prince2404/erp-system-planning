package com.hospital.erp.user.dto;

import java.util.List;

public record UserProvisionResponse(
        UserResponse user,
        String temporaryPassword,
        List<String> permissionKeys,
        List<NotificationDeliveryResponse> notifications
) {
}

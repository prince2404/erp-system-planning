package com.hospital.erp.user;

import com.hospital.erp.common.AppException;
import com.hospital.erp.common.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RankGuard {
    public void assertCanCreate(User creator, Role targetRole) {
        if (creator == null) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Creator authentication is required");
        }
        if (targetRole.getRank() <= creator.getRank()) {
            throw new AppException(HttpStatus.FORBIDDEN, "You can only create users below your hierarchy rank");
        }
    }

    public void assertCanManage(User actor, User target) {
        if (actor == null) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        if (target.getRank() <= actor.getRank()) {
            throw new AppException(HttpStatus.FORBIDDEN, "You can only manage users below your hierarchy rank");
        }
    }
}

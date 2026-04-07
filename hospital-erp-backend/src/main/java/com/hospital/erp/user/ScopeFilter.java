package com.hospital.erp.user;

import com.hospital.erp.common.enums.ScopeType;
import org.springframework.stereotype.Component;

@Component
public class ScopeFilter {
    public boolean isSystem(User user) {
        return user != null && user.getScopeType() == ScopeType.SYSTEM;
    }

    public boolean canAccessScopedEntity(User user, ScopeType scopeType, Long scopeId) {
        if (user == null || scopeId == null) {
            return false;
        }
        if (isSystem(user)) {
            return true;
        }
        return user.getScopeType() == scopeType && scopeId.equals(user.getScopeId());
    }

    public Long scopedCenterId(User user, Long requestedCenterId) {
        if (isSystem(user)) {
            return requestedCenterId;
        }
        if (user != null && user.getScopeType() == ScopeType.CENTER) {
            return user.getScopeId();
        }
        return requestedCenterId;
    }
}

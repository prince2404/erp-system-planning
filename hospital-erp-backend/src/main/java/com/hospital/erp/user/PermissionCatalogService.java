package com.hospital.erp.user;

import com.hospital.erp.common.enums.Role;
import com.hospital.erp.common.enums.ScopeType;
import com.hospital.erp.user.dto.PermissionCatalogGroupResponse;
import com.hospital.erp.user.dto.PermissionCatalogItemResponse;
import com.hospital.erp.user.dto.UserManagementOptionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionCatalogService {
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    public static String permissionKey(String module, String action) {
        return module.toLowerCase() + "." + action.toLowerCase();
    }

    @Transactional
    public void ensureCatalog() {
        for (PermissionDefinition definition : DEFINITIONS) {
            permissionRepository.findByModuleAndAction(definition.module(), definition.action())
                    .ifPresentOrElse(existing -> {
                        existing.setDescription(definition.description());
                        permissionRepository.save(existing);
                    }, () -> {
                        Permission permission = new Permission();
                        permission.setModule(definition.module());
                        permission.setAction(definition.action());
                        permission.setDescription(definition.description());
                        permissionRepository.save(permission);
                    });
        }
    }

    public UserManagementOptionsResponse managementOptions(User actor) {
        List<String> manageableRoles = Arrays.stream(Role.values())
                .filter(role -> role.getRank() > actor.getRank())
                .map(Role::name)
                .toList();

        Map<String, String> defaultScopeByRole = new LinkedHashMap<>();
        Map<String, List<String>> defaultPermissionKeysByRole = new LinkedHashMap<>();
        for (Role role : Role.values()) {
            defaultScopeByRole.put(role.name(), defaultScope(role).name());
            defaultPermissionKeysByRole.put(role.name(), new ArrayList<>(defaultPermissionKeys(role)));
        }

        return new UserManagementOptionsResponse(
                manageableRoles,
                defaultScopeByRole,
                defaultPermissionKeysByRole,
                groupedCatalog()
        );
    }

    public ScopeType defaultScope(Role role) {
        return switch (role) {
            case SUPER_ADMIN, ADMIN -> ScopeType.SYSTEM;
            case STATE_MANAGER -> ScopeType.STATE;
            case DISTRICT_MANAGER -> ScopeType.DISTRICT;
            case BLOCK_MANAGER, ASSOCIATE -> ScopeType.BLOCK;
            case PATIENT -> ScopeType.SELF;
            default -> ScopeType.CENTER;
        };
    }

    public Set<String> defaultPermissionKeys(Role role) {
        if (role == Role.SUPER_ADMIN) {
            return allPermissionKeys();
        }
        return DEFINITIONS.stream()
                .filter(definition -> definition.defaultRoles().contains(role))
                .map(definition -> permissionKey(definition.module(), definition.action()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> effectivePermissionKeys(User user) {
        if (user == null) {
            return Set.of();
        }
        if (user.getRole() == Role.SUPER_ADMIN) {
            return allPermissionKeys();
        }
        Set<String> assigned = userPermissionRepository.findByUser_Id(user.getId()).stream()
                .map(up -> permissionKey(up.getPermission().getModule(), up.getPermission().getAction()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!assigned.isEmpty()) {
            return assigned;
        }
        return defaultPermissionKeys(user.getRole());
    }

    public boolean canGrant(User actor, String key) {
        if (actor.getRole() == Role.SUPER_ADMIN || actor.getRole() == Role.ADMIN) {
            return true;
        }
        return effectivePermissionKeys(actor).contains(key);
    }

    public List<Permission> resolveRequestedPermissions(Role role, Set<String> requestedKeys) {
        Set<String> normalizedKeys = (requestedKeys == null || requestedKeys.isEmpty())
                ? defaultPermissionKeys(role)
                : requestedKeys.stream()
                        .filter(value -> value != null && !value.isBlank())
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        return normalizedKeys.stream()
                .map(this::findPermissionByKey)
                .sorted(Comparator.comparing(Permission::getModule).thenComparing(Permission::getAction))
                .toList();
    }

    public List<PermissionCatalogGroupResponse> groupedCatalog() {
        Map<String, Permission> permissionsByKey = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        permission -> permissionKey(permission.getModule(), permission.getAction()),
                        permission -> permission,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        Map<String, List<PermissionCatalogItemResponse>> groups = new LinkedHashMap<>();
        for (PermissionDefinition definition : DEFINITIONS) {
            Permission permission = permissionsByKey.get(permissionKey(definition.module(), definition.action()));
            PermissionCatalogItemResponse item = new PermissionCatalogItemResponse(
                    permission != null ? permission.getId() : null,
                    permissionKey(definition.module(), definition.action()),
                    definition.label(),
                    definition.description(),
                    definition.defaultRoles().stream().map(Role::name).toList()
            );
            groups.computeIfAbsent(definition.module(), key -> new ArrayList<>()).add(item);
        }

        List<PermissionCatalogGroupResponse> response = new ArrayList<>();
        for (Map.Entry<String, List<PermissionCatalogItemResponse>> entry : groups.entrySet()) {
            response.add(new PermissionCatalogGroupResponse(entry.getKey(), moduleLabel(entry.getKey()), entry.getValue()));
        }
        return response;
    }

    private Permission findPermissionByKey(String key) {
        String[] parts = key.split("\\.", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid permission key: " + key);
        }
        return permissionRepository.findByModuleAndAction(parts[0].toUpperCase(), parts[1].toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + key));
    }

    private Set<String> allPermissionKeys() {
        return DEFINITIONS.stream()
                .map(definition -> permissionKey(definition.module(), definition.action()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static String moduleLabel(String module) {
        return switch (module) {
            case "USERS" -> "Users";
            case "GEOGRAPHY" -> "Geography";
            case "CENTERS" -> "Centers";
            case "ASSOCIATES" -> "Associates";
            case "FAMILIES" -> "Families";
            case "HEALTH_CARDS" -> "Health Cards";
            case "WALLET" -> "Wallet";
            case "SALES" -> "Sales";
            case "INVENTORY" -> "Inventory";
            case "VENDORS" -> "Vendors";
            case "DOCTORS" -> "Doctors";
            case "OPD" -> "OPD";
            case "HR" -> "HR";
            case "COMMISSIONS" -> "Commissions";
            case "REPORTS" -> "Reports";
            case "NOTIFICATIONS" -> "Notifications";
            case "PROFILE" -> "Profile";
            default -> module;
        };
    }

    private static PermissionDefinition def(String module, String action, String label, String description, Role... roles) {
        return new PermissionDefinition(module, action, label, description, Set.of(roles));
    }

    private record PermissionDefinition(String module, String action, String label, String description, Set<Role> defaultRoles) {
    }

    private static final List<PermissionDefinition> DEFINITIONS = List.of(
            def("USERS", "VIEW", "View users", "Can view user accounts and their current access.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("USERS", "CREATE", "Create users", "Can create staff, manager, and associate accounts.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("USERS", "EDIT", "Edit users", "Can update user details, scope, and activation state.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("USERS", "DISABLE", "Disable users", "Can disable inactive or invalid user accounts.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("USERS", "RESET_PASSWORD", "Reset passwords", "Can reset temporary passwords for users in scope.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("USERS", "ASSIGN_ROLE", "Assign roles", "Can assign a role during user creation or update.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("USERS", "ASSIGN_PERMISSIONS", "Assign permissions", "Can toggle permission access for a user.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),

            def("GEOGRAPHY", "VIEW", "View geography", "Can view states, districts, and blocks.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("GEOGRAPHY", "CREATE", "Create geography", "Can create new states, districts, or blocks.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER),

            def("CENTERS", "VIEW", "View centers", "Can view center details and operational status.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER, Role.DOCTOR, Role.PHARMACIST, Role.RECEPTIONIST),
            def("CENTERS", "CREATE", "Create centers", "Can create and activate new centers.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER),
            def("CENTERS", "EDIT", "Edit centers", "Can update center profile, contact details, and hierarchy.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER),
            def("CENTERS", "ASSIGN_STAFF", "Assign center staff", "Can map users and operating roles to a center.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("CENTERS", "ACTIVATE", "Activate centers", "Can activate or deactivate center operations.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER),

            def("ASSOCIATES", "VIEW", "View associates", "Can see enrolled and assigned associates.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("ASSOCIATES", "CREATE", "Create associates", "Can onboard new associates.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("ASSOCIATES", "EDIT", "Edit associates", "Can update associate details.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("ASSOCIATES", "MAP_CENTER", "Map associates to center", "Can map an associate to a center or block network.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),

            def("FAMILIES", "VIEW", "View families", "Can view enrolled families and their members.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.ASSOCIATE, Role.RECEPTIONIST, Role.DOCTOR, Role.PHARMACIST),
            def("FAMILIES", "CREATE", "Enroll families", "Can enroll new families.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.ASSOCIATE, Role.RECEPTIONIST),
            def("FAMILIES", "EDIT", "Edit families", "Can update family demographic information.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.ASSOCIATE, Role.RECEPTIONIST),
            def("FAMILIES", "VERIFY", "Verify families", "Can verify enrollment and identity details.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST),
            def("FAMILIES", "ADD_MEMBERS", "Add family members", "Can add or update family members.", Role.ADMIN, Role.CENTER_MANAGER, Role.ASSOCIATE, Role.RECEPTIONIST),

            def("HEALTH_CARDS", "GENERATE", "Generate cards", "Can generate digital family health cards.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST),
            def("HEALTH_CARDS", "VIEW", "View cards", "Can view card details and QR-linked data.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST, Role.ASSOCIATE, Role.DOCTOR, Role.PHARMACIST),
            def("HEALTH_CARDS", "PRINT", "Print cards", "Can print or reprint family cards.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST),
            def("HEALTH_CARDS", "BLOCK", "Block cards", "Can block a lost or misused card.", Role.ADMIN, Role.CENTER_MANAGER),
            def("HEALTH_CARDS", "REISSUE", "Reissue cards", "Can reissue a health card after verification.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST),

            def("WALLET", "VIEW", "View wallet", "Can view wallet balances.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.RECEPTIONIST, Role.ASSOCIATE),
            def("WALLET", "TOPUP", "Top up wallet", "Can add balance to a family or business wallet.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST),
            def("WALLET", "DEBIT", "Debit wallet", "Can debit from a wallet during approved flows.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST, Role.PHARMACIST),
            def("WALLET", "CREDIT", "Credit wallet", "Can credit payouts, commissions, or adjustments.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER),
            def("WALLET", "REFUND", "Refund wallet", "Can process wallet refunds.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST),
            def("WALLET", "VIEW_TRANSACTIONS", "View wallet transactions", "Can view wallet transaction history.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.RECEPTIONIST),

            def("SALES", "CREATE", "Create sales", "Can create medicine or product sales.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST, Role.RECEPTIONIST),
            def("SALES", "VIEW", "View sales", "Can view sales and receipts.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST, Role.RECEPTIONIST, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER),
            def("SALES", "CANCEL", "Cancel sales", "Can cancel or reverse sales before settlement.", Role.ADMIN, Role.CENTER_MANAGER),
            def("SALES", "RECEIPT", "Generate receipts", "Can print or re-send a receipt.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST, Role.RECEPTIONIST),

            def("INVENTORY", "VIEW", "View inventory", "Can view medicine and product stock.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST, Role.DOCTOR, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER),
            def("INVENTORY", "CREATE_ITEM", "Create inventory item", "Can add new inventory items.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST),
            def("INVENTORY", "UPDATE_STOCK", "Update stock", "Can receive or adjust stock.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST),
            def("INVENTORY", "DISPENSE", "Dispense items", "Can dispense medicines and products.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST),
            def("INVENTORY", "LOW_STOCK_VIEW", "View low stock alerts", "Can view low-stock alerts.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER),

            def("VENDORS", "VIEW", "View vendors", "Can browse vendors and procurement records.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST),
            def("VENDORS", "CREATE", "Create vendors", "Can create vendors.", Role.ADMIN, Role.CENTER_MANAGER),
            def("VENDORS", "PURCHASE_ORDER_CREATE", "Create purchase orders", "Can create purchase orders.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST),
            def("VENDORS", "PURCHASE_ORDER_APPROVE", "Approve purchase orders", "Can approve purchase orders.", Role.ADMIN, Role.CENTER_MANAGER),
            def("VENDORS", "GOODS_RECEIVE_CREATE", "Receive goods", "Can receive procured goods into stock.", Role.ADMIN, Role.CENTER_MANAGER, Role.PHARMACIST),

            def("DOCTORS", "VIEW", "View doctors", "Can view doctor profiles and availability.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST, Role.DOCTOR, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER),
            def("DOCTORS", "CREATE_PROFILE", "Create doctor profiles", "Can create or update doctor profiles.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),

            def("OPD", "CREATE_VISIT", "Create OPD visit", "Can register an OPD visit.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST),
            def("OPD", "VIEW_QUEUE", "View OPD queue", "Can view the center OPD queue.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST, Role.DOCTOR),
            def("OPD", "UPDATE_STATUS", "Update OPD status", "Can advance queue status and visit stages.", Role.ADMIN, Role.CENTER_MANAGER, Role.RECEPTIONIST, Role.DOCTOR),
            def("OPD", "PRESCRIPTION_CREATE", "Create prescription", "Can create a prescription for a visit.", Role.ADMIN, Role.DOCTOR),

            def("HR", "ATTENDANCE_VIEW", "View attendance", "Can view attendance records.", Role.ADMIN, Role.HR_MANAGER, Role.CENTER_MANAGER),
            def("HR", "ATTENDANCE_MARK", "Mark attendance", "Can mark attendance.", Role.ADMIN, Role.HR_MANAGER, Role.CENTER_MANAGER),
            def("HR", "LEAVE_APPLY", "Apply leave", "Can apply for leave as a user.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER, Role.DOCTOR, Role.PHARMACIST, Role.RECEPTIONIST, Role.ASSOCIATE, Role.CENTER_STAFF),
            def("HR", "LEAVE_APPROVE", "Approve leave", "Can approve leave requests.", Role.ADMIN, Role.HR_MANAGER, Role.CENTER_MANAGER),
            def("HR", "PAYROLL_RUN", "Run payroll", "Can generate payroll and payouts.", Role.ADMIN, Role.HR_MANAGER),
            def("HR", "STAFF_PROFILE_MANAGE", "Manage staff profile", "Can update staff employment details.", Role.ADMIN, Role.HR_MANAGER, Role.CENTER_MANAGER),

            def("COMMISSIONS", "VIEW", "View commissions", "Can view commission statements.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.ASSOCIATE),
            def("COMMISSIONS", "CALCULATE", "Calculate commissions", "Can calculate commission distribution.", Role.ADMIN),
            def("COMMISSIONS", "APPROVE", "Approve commissions", "Can approve commission settlements.", Role.ADMIN),
            def("COMMISSIONS", "WALLET_CREDIT", "Credit commission wallet", "Can credit wallet after commission approval.", Role.ADMIN),

            def("REPORTS", "DASHBOARD_VIEW", "View dashboard", "Can view live dashboard KPIs.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("REPORTS", "CENTER_VIEW", "View center reports", "Can view center-level reports.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),
            def("REPORTS", "BLOCK_VIEW", "View block reports", "Can view block-level reports.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER),
            def("REPORTS", "DISTRICT_VIEW", "View district reports", "Can view district-level reports.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER),
            def("REPORTS", "STATE_VIEW", "View state reports", "Can view state-level reports.", Role.ADMIN, Role.STATE_MANAGER),
            def("REPORTS", "EXPORT", "Export reports", "Can export report data.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER),

            def("NOTIFICATIONS", "SEND_EMAIL", "Send email", "Can trigger operational email notifications.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER),
            def("NOTIFICATIONS", "SEND_SMS", "Send SMS", "Can trigger operational SMS notifications.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER),

            def("PROFILE", "VIEW_SELF", "View own profile", "Can view own profile details.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER, Role.DOCTOR, Role.PHARMACIST, Role.RECEPTIONIST, Role.ASSOCIATE, Role.CENTER_STAFF, Role.PATIENT),
            def("PROFILE", "EDIT_SELF", "Edit own profile", "Can edit own profile details.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER, Role.DOCTOR, Role.PHARMACIST, Role.RECEPTIONIST, Role.ASSOCIATE, Role.CENTER_STAFF, Role.PATIENT),
            def("PROFILE", "VERIFY_EMAIL", "Verify email", "Can verify own email address.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER, Role.DOCTOR, Role.PHARMACIST, Role.RECEPTIONIST, Role.ASSOCIATE, Role.CENTER_STAFF, Role.PATIENT),
            def("PROFILE", "VERIFY_PHONE", "Verify phone", "Can verify own phone number.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER, Role.DOCTOR, Role.PHARMACIST, Role.RECEPTIONIST, Role.ASSOCIATE, Role.CENTER_STAFF, Role.PATIENT),
            def("PROFILE", "MANAGE_BANK_DETAILS", "Manage bank details", "Can update own bank and payout details.", Role.ADMIN, Role.STATE_MANAGER, Role.DISTRICT_MANAGER, Role.BLOCK_MANAGER, Role.CENTER_MANAGER, Role.HR_MANAGER, Role.DOCTOR, Role.PHARMACIST, Role.RECEPTIONIST, Role.ASSOCIATE, Role.CENTER_STAFF)
    );
}

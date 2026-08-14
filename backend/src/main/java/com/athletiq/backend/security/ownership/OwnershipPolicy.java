package com.athletiq.backend.security.ownership;

public final class OwnershipPolicy {

    private OwnershipPolicy() {
    }

    public static boolean canAccess(
            Long authenticatedUserId,
            Long ownerId,
            OwnershipRule rule,
            boolean staffAccess
    ) {
        if (authenticatedUserId == null || rule == null) {
            return false;
        }

        if (rule == OwnershipRule.OWNER_ONLY) {
            return ownerId != null && authenticatedUserId.equals(ownerId);
        }

        if (rule == OwnershipRule.STAFF_ACCESS) {
            return staffAccess ||
                    (ownerId != null && authenticatedUserId.equals(ownerId));
        }

        if (rule == OwnershipRule.ORGANIZER_ACCESS ||
                rule == OwnershipRule.ADMIN_ACCESS) {
            return ownerId != null && authenticatedUserId.equals(ownerId);
        }

        return false;
    }

    public static void requireAccess(
            Long authenticatedUserId,
            Long ownerId,
            OwnershipRule rule,
            boolean staffAccess
    ) {
        if (!canAccess(authenticatedUserId, ownerId, rule, staffAccess)) {
            throw new SecurityAccessDeniedException(
                    "User is not authorized to access this resource"
            );
        }
    }
}
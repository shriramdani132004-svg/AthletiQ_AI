package com.athletiq.backend.security.ownership;

import java.util.Objects;

public final class OwnershipGuard {

    private OwnershipGuard() {
    }

    public static void requireOwner(Long authenticatedUserId, Long ownerId) {
        if (authenticatedUserId == null || ownerId == null ||
                !Objects.equals(authenticatedUserId, ownerId)) {
            throw new SecurityAccessDeniedException(
                    "Authenticated user does not own this resource"
            );
        }
    }

    public static void requireAccess(
            Long authenticatedUserId,
            Long ownerId,
            OwnershipRule rule,
            boolean staffAccess
    ) {
        if (rule == null) {
            throw new SecurityAccessDeniedException("Ownership rule is required");
        }

        if (rule == OwnershipRule.OWNER_ONLY) {
            requireOwner(authenticatedUserId, ownerId);
            return;
        }

        if (rule == OwnershipRule.STAFF_ACCESS && staffAccess) {
            return;
        }

        requireOwner(authenticatedUserId, ownerId);
    }
}
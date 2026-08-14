package com.athletiq.backend.security.authorization;

import com.athletiq.backend.security.auth.entity.Role;
import java.util.EnumSet;
import java.util.Set;

public final class RolePermissions {

    private RolePermissions() {}

    public static Set<Permission> permissionsFor(Role role) {
        return switch (role) {
            case SUPER_ADMIN -> EnumSet.allOf(Permission.class);

            case ORGANIZER -> EnumSet.of(
                Permission.EVENT_CREATE,
                Permission.EVENT_READ,
                Permission.EVENT_UPDATE,
                Permission.EVENT_DELETE,
                Permission.APPLICATION_READ,
                Permission.APPLICATION_EVALUATE,
                Permission.APPLICATION_SHORTLIST,
                Permission.PLAYER_SELECT,
                Permission.STAFF_MANAGE,
                Permission.EVENT_DASHBOARD_READ
            );

            case STAFF -> EnumSet.of(
                Permission.EVENT_READ,
                Permission.APPLICATION_READ,
                Permission.APPLICATION_EVALUATE,
                Permission.EVENT_DASHBOARD_READ
            );

            case PLAYER -> EnumSet.of(
                Permission.EVENT_READ,
                Permission.APPLICATION_CREATE,
                Permission.APPLICATION_READ_OWN,
                Permission.APPLICATION_UPDATE_OWN
            );
        };
    }

    public static boolean hasPermission(Role role, Permission permission) {
        return permissionsFor(role).contains(permission);
    }
}
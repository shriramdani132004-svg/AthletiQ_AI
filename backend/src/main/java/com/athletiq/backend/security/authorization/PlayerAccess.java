package com.athletiq.backend.security.authorization;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZER', 'STAFF', 'PLAYER')")
public @interface PlayerAccess {
}
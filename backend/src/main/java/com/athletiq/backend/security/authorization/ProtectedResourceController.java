package com.athletiq.backend.security.authorization;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProtectedResourceController {

    @GetMapping("/admin/dashboard")
    @AdminOnly
    public ResponseEntity<?> adminDashboard() {
        return ResponseEntity.ok(Map.of("access", "SUPER_ADMIN", "status", "AUTHORIZED"));
    }

    @GetMapping("/organizer/profile")
    @OrganizerAccess
    public ResponseEntity<?> organizerProfile() {
        return ResponseEntity.ok(Map.of("access", "ORGANIZER", "status", "AUTHORIZED"));
    }

    @GetMapping("/staff/dashboard")
    @StaffAccess
    public ResponseEntity<?> staffDashboard() {
        return ResponseEntity.ok(Map.of("access", "STAFF", "status", "AUTHORIZED"));
    }

    @GetMapping("/player/profile")
    @PlayerAccess
    public ResponseEntity<?> playerProfile() {
        return ResponseEntity.ok(Map.of("access", "PLAYER", "status", "AUTHORIZED"));
    }
}

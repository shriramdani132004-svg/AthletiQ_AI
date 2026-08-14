package com.athletiq.backend.profile.controller;

import com.athletiq.backend.profile.dto.OrganizationInfoRequest;
import com.athletiq.backend.profile.dto.OrganizationInfoResponse;
import com.athletiq.backend.profile.service.OrganizationInfoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZER')")
@RequestMapping("/api/v1/profile/organization")
public class OrganizationInfoController {

    private final OrganizationInfoService organizationInfoService;

    public OrganizationInfoController(OrganizationInfoService organizationInfoService) {
        this.organizationInfoService = organizationInfoService;
    }

    @GetMapping
    public OrganizationInfoResponse getOrganizationInfo(Authentication authentication) {
        return organizationInfoService.getOrganizationInfo(authentication.getName());
    }

    @PutMapping
    public OrganizationInfoResponse updateOrganizationInfo(Authentication authentication, @RequestBody OrganizationInfoRequest request) {
        return organizationInfoService.updateOrganizationInfo(authentication.getName(), request);
    }
}
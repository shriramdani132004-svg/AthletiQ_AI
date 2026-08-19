package com.athletiq.backend.application.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.athletiq.backend.application.entity.PlayerResponseStatus;
import com.athletiq.backend.application.service.PlayerResponseService;

@RestController
@RequestMapping("/api/public/player-response")
public class PlayerResponseController {

    private final PlayerResponseService responseService;

    public PlayerResponseController(
            PlayerResponseService responseService
    ) {
        this.responseService =
                responseService;
    }

    @GetMapping("/{token}/accept")
    public ResponseEntity<Map<String, Object>> accept(
            @PathVariable String token
    ) {
        return ResponseEntity.ok(
                responseService.respond(
                        token,
                        PlayerResponseStatus.ACCEPTED
                )
        );
    }

    @GetMapping("/{token}/decline")
    public ResponseEntity<Map<String, Object>> decline(
            @PathVariable String token
    ) {
        return ResponseEntity.ok(
                responseService.respond(
                        token,
                        PlayerResponseStatus.DECLINED
                )
        );
    }
}
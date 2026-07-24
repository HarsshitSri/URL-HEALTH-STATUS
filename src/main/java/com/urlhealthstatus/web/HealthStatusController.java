package com.urlhealthstatus.web;

import com.urlhealthstatus.dto.HealthStatusResponse;
import com.urlhealthstatus.service.HealthStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthStatusController {

    private final HealthStatusService healthStatusService;

    public HealthStatusController(HealthStatusService healthStatusService) {
        this.healthStatusService = healthStatusService;
    }

    @GetMapping("/health-status")
    public HealthStatusResponse healthStatus(@RequestParam(value = "url", required = false) String url) {
        return healthStatusService.audit(url);
    }
}

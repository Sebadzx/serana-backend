package com.upc.serana.controller;

import com.upc.serana.dto.DashboardBienestarDTO;
import com.upc.serana.service.DashboardBienestarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bienestar")
@RequiredArgsConstructor
public class DashboardBienestarController {

    private final DashboardBienestarService dashboardBienestarService;

    @GetMapping("/metricas-globales")
    public ResponseEntity<DashboardBienestarDTO> obtenerMetricas() {
        DashboardBienestarDTO dto = dashboardBienestarService.obtenerMetricasGlobales();
        return ResponseEntity.ok(dto);
    }
}

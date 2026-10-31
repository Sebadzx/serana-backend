package com.arquiweb.grupo4.serana.controllers;

import com.arquiweb.grupo4.serana.dtos.DashboardBienestarDTO;
import com.arquiweb.grupo4.serana.services.DashboardBienestarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bienestar")
@CrossOrigin(origins = "*")
public class DashboardBienestarController {

    private final DashboardBienestarService dashboardBienestarService;

    public DashboardBienestarController(DashboardBienestarService dashboardBienestarService) {
        this.dashboardBienestarService = dashboardBienestarService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyAuthority('ROLE_BIENESTAR', 'ROLE_ADMIN')")
    public ResponseEntity<DashboardBienestarDTO> obtenerResumenDashboard() {
        DashboardBienestarDTO dto = dashboardBienestarService.obtenerResumenDashboard();
        return ResponseEntity.ok(dto);
    }

    // US-16: Reporte Consolidado en Formato JSON
    @GetMapping("/reportes/consolidado")
    @PreAuthorize("hasAnyAuthority('ROLE_BIENESTAR', 'ROLE_ADMIN')")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> obtenerReporteConsolidado() {
        return ResponseEntity.ok(dashboardBienestarService.obtenerReporteConsolidado());
    }

    // US-16: Exportación de Reporte para Microsoft Excel (CSV con UTF-8 BOM)
    @GetMapping("/reportes/exportar-excel")
    @PreAuthorize("hasAnyAuthority('ROLE_BIENESTAR', 'ROLE_ADMIN')")
    public ResponseEntity<byte[]> exportarReporteExcel() {
        byte[] csvBytes = dashboardBienestarService.exportarReporteExcelCsv();
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte_ansiedad_digital_bienestar.csv\"")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csvBytes);
    }

    // US-19: Pipeline automatizado de reentrenamiento del modelo Random Forest
    @PostMapping("/mlops/reentrenar")
    @PreAuthorize("hasAnyAuthority('ROLE_BIENESTAR', 'ROLE_ADMIN')")
    public ResponseEntity<java.util.Map<String, Object>> reentrenarModelo() {
        return ResponseEntity.ok(dashboardBienestarService.reentrenarModelo());
    }
}


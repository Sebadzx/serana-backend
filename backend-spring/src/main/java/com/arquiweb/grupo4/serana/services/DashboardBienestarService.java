package com.arquiweb.grupo4.serana.services;

import com.arquiweb.grupo4.serana.dtos.DashboardBienestarDTO;
import java.util.List;
import java.util.Map;

public interface DashboardBienestarService {
    DashboardBienestarDTO obtenerResumenDashboard();
    List<Map<String, Object>> obtenerReporteConsolidado();
    byte[] exportarReporteExcelCsv();
    // US-19: Pipeline automatizado de reentrenamiento del modelo Random Forest
    Map<String, Object> reentrenarModelo();
}


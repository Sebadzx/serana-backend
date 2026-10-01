import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface DashboardBienestarDTO {
  totalEstudiantesEvaluados: number;
  casosCriticos: number;
  distribucionAnsiedad: { [key: string]: number };
  porcentajeCasosRiesgo: number;
  fechaActualizacion: string;
}

export interface ReporteConsolidadoDTO {
  idEvaluacion: number;
  codigoEstudiante: string;
  fechaEvaluacion: string;
  carrera?: string;
  ciclo?: number;
  puntuacionGad7: number;
  puntuacionPss10: number;
  tiempoPantallaHoras: number;
  habitoSuenoHoras: number;
  nivelAnsiedad: string;
  scoreProbabilidad: number;
  esCasoCritico: boolean;
  justificacion: string;
}

export interface SolicitudARCOAdminDTO {
  id: number;
  codigoUsuario: string;
  nombreUsuario: string;
  tipoSolicitud: string;
  estado: string;
  detalleSolicitud: string;
  fechaSolicitud: string;
  fechaRespuesta?: string;
  respuestaAdmin?: string;
}

@Injectable({
  providedIn: 'root'
})
export class BienestarService {
  private apiUrl = 'http://13.218.166.49:8080/api/bienestar';
  private arcoUrl = 'http://13.218.166.49:8080/api/arco';

  constructor(private http: HttpClient) {}

  obtenerResumenDashboard(): Observable<DashboardBienestarDTO> {
    return this.http.get<DashboardBienestarDTO>(`${this.apiUrl}/dashboard`);
  }

  obtenerReporteConsolidado(): Observable<ReporteConsolidadoDTO[]> {
    return this.http.get<ReporteConsolidadoDTO[]>(`${this.apiUrl}/reportes/consolidado`);
  }

  descargarReporteExcel(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/reportes/exportar-excel`, {
      responseType: 'blob'
    });
  }

  obtenerSolicitudesARCO(): Observable<SolicitudARCOAdminDTO[]> {
    return this.http.get<SolicitudARCOAdminDTO[]>(`${this.arcoUrl}/admin/solicitudes`);
  }

  responderSolicitudARCO(id: number, dto: { estado: string; respuestaAdmin: string }): Observable<any> {
    return this.http.put(`${this.arcoUrl}/admin/solicitudes/${id}/responder`, dto);
  }

  // US-19: Pipeline automatizado de reentrenamiento del modelo Random Forest
  reentrenarModelo(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/mlops/reentrenar`, {});
  }
}


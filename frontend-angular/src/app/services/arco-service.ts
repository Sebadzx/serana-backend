import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface SolicitudARCODTO {
  id?: number;
  tipoSolicitud: string;
  detalleSolicitud: string;
  estado?: string;
  fechaSolicitud?: string;
  fechaRespuesta?: string;
  respuestaAdmin?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ArcoService {
  private apiUrl = 'http://13.218.166.49:8080/api/arco';

  constructor(private http: HttpClient) {}

  enviarSolicitud(datos: { tipoSolicitud: string; detalleSolicitud: string }): Observable<SolicitudARCODTO> {
    return this.http.post<SolicitudARCODTO>(`${this.apiUrl}/solicitudes`, datos);
  }

  listarMisSolicitudes(): Observable<SolicitudARCODTO[]> {
    return this.http.get<SolicitudARCODTO[]>(`${this.apiUrl}/mis-solicitudes`);
  }
}

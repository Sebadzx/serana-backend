import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ConsentimientoDTO {
  versionTerminos: string;
  ipAddress?: string;
}

export interface EstadoConsentimientoDTO {
  firmado: boolean;
  fechaFirma?: string;
  version?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConsentimientoService {
  private apiUrl = 'http://13.218.166.49:8080/api/consentimiento';

  constructor(private http: HttpClient) {}

  obtenerEstado(): Observable<EstadoConsentimientoDTO> {
    return this.http.get<EstadoConsentimientoDTO>(`${this.apiUrl}/estado`);
  }

  firmar(dto: ConsentimientoDTO): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/firmar`, dto);
  }
}

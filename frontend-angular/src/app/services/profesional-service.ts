import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Profesional } from '../models/profesional';

// Consume el recurso real "Personal Médico" (ProfesionalMedico) del backend.
@Injectable({
  providedIn: 'root',
})
export class ProfesionalService {
  ruta_servidor: string = "http://13.218.166.49:8080/arqui_serana";
  recurso: string = "profesionales";

  constructor(private http: HttpClient) {}

  listAll(): Observable<Profesional[]> {
    return this.http.get<Profesional[]>(this.ruta_servidor + "/" + this.recurso);
  }

  findById(id: number): Observable<Profesional> {
    return this.http.get<Profesional>(this.ruta_servidor + "/" + this.recurso + "/" + id);
  }

  // Punto 4: registro en Personal Médico del profesional autenticado (puede no existir aún)
  findByUsuarioId(usuarioId: number): Observable<Profesional> {
    return this.http.get<Profesional>(this.ruta_servidor + "/" + this.recurso + "/usuario/" + usuarioId);
  }

  update(profesional: any): Observable<any> {
    return this.http.put<any>(this.ruta_servidor + "/" + this.recurso, profesional);
  }
}

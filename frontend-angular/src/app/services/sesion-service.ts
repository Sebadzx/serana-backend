import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Sesion } from '../models/sesion';

@Injectable({
  providedIn: 'root',
})
export class SesionService {
  ruta_servidor: string = "http://13.218.166.49:8080/arqui_serana";
  recurso: string = "sesiones";

  constructor(private http: HttpClient) {}

  // Punto 5: reserva una sesión sobre un horario disponible
  reservar(sesion: any): Observable<any> {
    return this.http.post<any>(this.ruta_servidor + "/" + this.recurso, sesion);
  }

  // Punto 6: sesiones del profesional autenticado
  listarPorProfesional(profesionalId: number): Observable<Sesion[]> {
    return this.http.get<Sesion[]>(this.ruta_servidor + "/" + this.recurso + "/profesional/" + profesionalId);
  }
}

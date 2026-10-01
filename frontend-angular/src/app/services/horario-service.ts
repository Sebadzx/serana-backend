import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Horario } from '../models/horario';

@Injectable({
  providedIn: 'root',
})
export class HorarioService {
  ruta_servidor: string = "http://13.218.166.49:8080/arqui_serana";
  recurso: string = "horarios";

  constructor(private http: HttpClient) {}

  // Punto 3 y 4: registra un horario del profesional autenticado
  crear(horario: any): Observable<any> {
    return this.http.post<any>(this.ruta_servidor + "/" + this.recurso, horario);
  }

  // Todos los horarios registrados por el profesional (gestión propia)
  listarPorProfesional(profesionalId: number): Observable<Horario[]> {
    return this.http.get<Horario[]>(this.ruta_servidor + "/" + this.recurso + "/profesional/" + profesionalId);
  }

  // Horarios disponibles y futuros de un profesional (para que el paciente reserve)
  listarDisponiblesPorProfesional(profesionalId: number): Observable<Horario[]> {
    return this.http.get<Horario[]>(this.ruta_servidor + "/" + this.recurso + "/disponibles/profesional/" + profesionalId);
  }
}

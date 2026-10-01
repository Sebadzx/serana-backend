import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface RecomendacionICBTDTO {
  id: number;
  titulo: string;
  moduloICBT: string;
  descripcion: string;
  completada: boolean;
}

export interface EvaluacionResponseDTO {
  idEvaluacion: number;
  fechaEvaluacion: string;
  nivelAnsiedadPredicho: string;
  scoreProbabilidad: number;
  esCasoCritico: boolean;
  justificacionCli: string;
  estresoresDetectados: string;
  polaridadSentimiento: number;
  recomendacionesICBT: RecomendacionICBTDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class EvaluacionService {
  private apiUrl = 'http://13.218.166.49:8080/api/evaluaciones';

  constructor(private http: HttpClient) {}

  paso1Psicometrico(datos: {
    nivelEstres: number;
    nivelAnsiedad: number;
    puntuacionGad7: number;
    puntuacionPss10: number;
  }): Observable<{ evaluacionId: number; estado: string; mensaje: string; siguientePaso: string }> {
    return this.http.post<{ evaluacionId: number; estado: string; mensaje: string; siguientePaso: string }>(
      `${this.apiUrl}/paso1-psicometrico`,
      datos
    );
  }

  paso2Habitos(evaluacionId: number, datos: {
    habitoSuenoHoras: number;
    tiempoPantallaHoras: number;
  }): Observable<any> {
    return this.http.put<any>(
      `${this.apiUrl}/${evaluacionId}/paso2-habitos`,
      datos
    );
  }

  paso3TextoLibre(evaluacionId: number, datos: {
    textoLibre: string;
  }): Observable<EvaluacionResponseDTO> {
    return this.http.post<EvaluacionResponseDTO>(
      `${this.apiUrl}/${evaluacionId}/paso3-texto-libre`,
      datos
    );
  }

  listarMisEvaluaciones(): Observable<EvaluacionResponseDTO[]> {
    return this.http.get<EvaluacionResponseDTO[]>(`${this.apiUrl}/mis-evaluaciones`);
  }
}

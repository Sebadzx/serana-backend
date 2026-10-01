import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RecursoEducativo } from '../models/recurso-educativo';

@Injectable({
  providedIn: 'root',
})
export class RecursoEducativoService {
  ruta_servidor: string = "http://13.218.166.49:8080/arqui_serana";
  recurso: string = "recursos_educativos";
  
  constructor(private http: HttpClient) {}

  listAll() {
    return this.http.get<RecursoEducativo[]>(this.ruta_servidor + "/" + this.recurso);
  }

  add(recursoEducativo: RecursoEducativo) {
    return this.http.post<RecursoEducativo>(this.ruta_servidor + "/" + this.recurso, recursoEducativo);
  }
    
  delete(id: number) {
    return this.http.delete<any>(this.ruta_servidor + "/" + this.recurso + "/" + id.toString());
  }
}

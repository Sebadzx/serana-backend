import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ConfiguracionService {
  ruta_servidor: string = "http://13.218.166.49:8080/arqui_serana";
  recurso: string = "configuraciones";

  constructor(private http: HttpClient) {}

  findByUsuarioId(usuarioId: number) {
    return this.http.get<any>(`${this.ruta_servidor}/${this.recurso}/usuario/${usuarioId}`);
  }

  add(config: any) {
    return this.http.post<any>(`${this.ruta_servidor}/${this.recurso}`, config);
  }

  update(config: any) {
    return this.http.put<any>(`${this.ruta_servidor}/${this.recurso}`, config);
  }
}

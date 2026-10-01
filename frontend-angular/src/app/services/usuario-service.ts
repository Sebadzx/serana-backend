import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UsuarioDTO } from '../models/usuarioDTO';
import { TokenDTO } from '../models/tokenDTO';
import { Usuario } from '../models/usuario';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  ruta_servidor: string = "http://13.218.166.49:8080/api/auth";
  
  constructor(private http: HttpClient) {}

  login(loginDTO: any) {
    const payload = {
      email: loginDTO.email || loginDTO.correo || "",
      password: loginDTO.password || loginDTO.contrasenia || ""
    };
    return this.http.post<any>(this.ruta_servidor + "/login", payload).pipe(
      tap((data: any) => {
        if (data.token) {
          localStorage.setItem("jwtToken", data.token);
          localStorage.setItem("id", data.id ? data.id.toString() : "");
          localStorage.setItem("codigoUniversitario", data.codigoUniversitario || "");
          localStorage.setItem("nombreCompleto", data.nombreCompleto || "");
          localStorage.setItem("email", data.email || "");
          localStorage.setItem("authorities", JSON.stringify(data.roles || []));
        }
      })
    );
  }

  registro(registroDTO: any) {
    return this.http.post<any>(this.ruta_servidor + "/registro", registroDTO);
  }

  add(usuario: any) {
    return this.registro(usuario);
  }

  listAll() {
    return this.http.get<any[]>("http://13.218.166.49:8080/api/evaluaciones/mis-evaluaciones");
  }

  delete(id: number) {
    return this.http.delete("http://13.218.166.49:8080/api/arco/solicitudes/" + id);
  }
  logout() {
    localStorage.clear();
  }
  getIdLogeado(){
    return localStorage.getItem("id");
  }
  getAuthoritiesLogeado(){
    return localStorage.getItem("authorities");
  }
  getJwtTokenLogeado(){
    return localStorage.getItem("jwtToken");
  }
}
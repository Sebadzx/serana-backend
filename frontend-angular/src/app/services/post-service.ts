import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  ruta_servidor: string = "http://13.218.166.49:8080/arqui_serana";
  recurso: string = "posts";

  constructor(private http: HttpClient) {}

  listAll() {
    return this.http.get<any[]>(this.ruta_servidor + "/" + this.recurso);
  }

  add(post: any) {
    return this.http.post<any>(this.ruta_servidor + "/" + this.recurso, post);
  }
}

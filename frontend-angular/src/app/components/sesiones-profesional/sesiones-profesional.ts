import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UsuarioService } from '../../services/usuario-service';
import { LanguageService } from '../../services/language-service';
import { ProfesionalService } from '../../services/profesional-service';
import { SesionService } from '../../services/sesion-service';
import { Sesion } from '../../models/sesion';

// Punto 6: el botón "Sesiones" del módulo del Profesional muestra únicamente
// una tabla con las sesiones del profesional autenticado, con el mismo diseño del resto del sitio.
@Component({
  selector: 'app-sesiones-profesional',
  standalone: false,
  templateUrl: './sesiones-profesional.html',
  styleUrl: './sesiones-profesional.css',
})
export class SesionesProfesional implements OnInit {
  sesiones: Sesion[] = [];
  cargando: boolean = true;
  sinPerfilProfesional: boolean = false;

  constructor(
    private usuarioService: UsuarioService,
    public langService: LanguageService,
    private profesionalService: ProfesionalService,
    private sesionService: SesionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarSesiones();
  }

  cargarSesiones(): void {
    const usuarioId = Number(this.usuarioService.getIdLogeado());
    this.cargando = true;

    this.profesionalService.findByUsuarioId(usuarioId).subscribe({
      next: (profesional) => {
        this.sesionService.listarPorProfesional(profesional.id).subscribe({
          next: (data) => {
            this.sesiones = data;
            this.cargando = false;
          },
          error: (err) => {
            console.log('Error al cargar sesiones:', err);
            this.cargando = false;
          }
        });
      },
      error: () => {
        // Aún no tiene registro en Personal Médico (no ha registrado ningún horario todavía)
        this.sinPerfilProfesional = true;
        this.cargando = false;
      }
    });
  }

  formatHora(hora: string): string {
    return hora ? hora.substring(0, 5) : '';
  }

  getInicioUrl(): string {
    return '/dashboard-profesional';
  }

  logout(): void {
    this.usuarioService.logout();
    this.router.navigate(['/login']);
  }
}

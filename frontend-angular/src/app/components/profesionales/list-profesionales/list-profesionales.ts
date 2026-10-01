import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProfesionalService } from '../../../services/profesional-service';
import { Profesional } from '../../../models/profesional';
import { LanguageService } from '../../../services/language-service';
import { UsuarioService } from '../../../services/usuario-service';

@Component({
  selector: 'app-list-profesionales',
  standalone: false,
  templateUrl: './list-profesionales.html',
  styleUrl: './list-profesionales.css',
})
export class ListProfesionales implements OnInit {
  profesionales: Profesional[] = [];
  profesionalesFiltrados: Profesional[] = [];
  terminoBusqueda: string = '';
  cargando: boolean = true;

  // El backend (Personal Médico) no almacena foto; se asigna una imagen de forma consistente por id.
  private avatarPool: string[] = [
    'avatar-sandra.jpg', 'avatar-carlos.jpg', 'avatar-sofia.jpg',
    'avatar-john.jpg', 'avatar-profesional.jpg', 'avatar-david.jpg', 'avatar-doctor.jpg'
  ];

  constructor(
    private profesionalService: ProfesionalService,
    private router: Router,
    public langService: LanguageService,
    private usuarioService: UsuarioService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarProfesionales();
  }

  cargarProfesionales(): void {
    this.cargando = true;
    this.profesionalService.listAll().subscribe({
      next: (data) => {
        this.profesionales = data;
        this.profesionalesFiltrados = [...data];
        this.cargando = false;
      },
      error: (err) => {
        console.log('Error al cargar profesionales:', err);
        this.cargando = false;
      }
    });
  }

  buscar(): void {
    const termino = this.terminoBusqueda.trim().toLowerCase();
    if (!termino) {
      this.profesionalesFiltrados = [...this.profesionales];
      return;
    }
    this.profesionalesFiltrados = this.profesionales.filter(p =>
      p.nombres?.toLowerCase().includes(termino) ||
      p.apellidos?.toLowerCase().includes(termino) ||
      p.especialidad?.toLowerCase().includes(termino) ||
      p.lugarTrabajo?.toLowerCase().includes(termino)
    );
  }

  filtroProximamente(nombreFiltro: string): void {
    this.snackBar.open(`${nombreFiltro}: ${this.langService.translate('prof.filtro_desarrollo')}`, '', { duration: 2000 });
  }

  nombreCompleto(prof: Profesional): string {
    return `${prof.nombres ?? ''} ${prof.apellidos ?? ''}`.trim();
  }

  getAvatar(id: number): string {
    return this.avatarPool[id % this.avatarPool.length];
  }

  verPerfil(id: number): void {
    this.router.navigate(['/profesionales', id]);
  }

  agendarSesion(id: number): void {
    this.router.navigate(['/profesionales', id]);
  }

  getInicioUrl(): string {
    const role = this.usuarioService.getAuthoritiesLogeado();
    if (role && role.includes('ROLE_PROFESIONAL')) {
      return '/dashboard-profesional';
    }
    if (role && role.includes('ROLE_PACIENTE')) {
      return '/dashboard-paciente';
    }
    return '/comunidad';
  }

  getSesionesUrl(): string {
    const role = this.usuarioService.getAuthoritiesLogeado();
    if (role && role.includes('ROLE_PROFESIONAL')) {
      return '/sesiones';
    }
    return '/profesionales';
  }

  logout(): void {
    this.usuarioService.logout();
    this.router.navigate(['/login']);
  }
}

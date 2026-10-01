import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCalendarCellClassFunction } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProfesionalService } from '../../../services/profesional-service';
import { HorarioService } from '../../../services/horario-service';
import { SesionService } from '../../../services/sesion-service';
import { Profesional } from '../../../models/profesional';
import { Horario } from '../../../models/horario';
import { LanguageService } from '../../../services/language-service';
import { UsuarioService } from '../../../services/usuario-service';

@Component({
  selector: 'app-perfil-profesional',
  standalone: false,
  templateUrl: './perfil-profesional.html',
  styleUrl: './perfil-profesional.css',
})
export class PerfilProfesional implements OnInit {
  profesional?: Profesional;
  cargando: boolean = true;

  // Punto 5: los horarios mostrados son siempre reales y disponibles (ya filtrados por el backend)
  horariosDisponibles: Horario[] = [];
  horariosDelDia: Horario[] = [];
  selectedDate: Date | null = null;
  horarioSeleccionado: Horario | null = null;
  reservando: boolean = false;

  private avatarPool: string[] = [
    'avatar-sandra.jpg', 'avatar-carlos.jpg', 'avatar-sofia.jpg',
    'avatar-john.jpg', 'avatar-profesional.jpg', 'avatar-david.jpg', 'avatar-doctor.jpg'
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private profesionalService: ProfesionalService,
    private horarioService: HorarioService,
    private sesionService: SesionService,
    public langService: LanguageService,
    private usuarioService: UsuarioService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.cargando = true;
    this.profesionalService.findById(id).subscribe({
      next: (data) => {
        this.profesional = data;
        this.cargando = false;
        this.cargarHorariosDisponibles(id);
      },
      error: (err) => {
        console.log('Error al cargar el profesional:', err);
        this.cargando = false;
      }
    });
  }

  cargarHorariosDisponibles(profesionalId: number): void {
    this.horarioService.listarDisponiblesPorProfesional(profesionalId).subscribe({
      next: (horarios) => {
        this.horariosDisponibles = horarios;
        this.preseleccionarPrimeraFecha();
      },
      error: (err) => console.log('Error al cargar horarios disponibles:', err)
    });
  }

  nombreCompleto(): string {
    if (!this.profesional) return '';
    return `${this.profesional.nombres ?? ''} ${this.profesional.apellidos ?? ''}`.trim();
  }

  getAvatar(): string {
    return this.profesional ? this.avatarPool[this.profesional.id % this.avatarPool.length] : this.avatarPool[0];
  }

  preseleccionarPrimeraFecha(): void {
    if (this.horariosDisponibles.length === 0) return;
    const primera = this.horariosDisponibles[0];
    this.selectedDate = new Date(primera.fecha + 'T00:00:00');
    this.horariosDelDia = this.horariosDisponibles.filter(h => h.fecha === primera.fecha);
  }

  // Resalta en el calendario los días que tienen al menos un horario disponible
  dateClass: MatCalendarCellClassFunction<Date> = (cellDate, view) => {
    if (view === 'month') {
      const iso = this.toIsoDate(cellDate);
      const disponible = this.horariosDisponibles.some(h => h.fecha === iso);
      return disponible ? 'dia-disponible' : '';
    }
    return '';
  };

  onDateSelected(date: Date | null): void {
    this.selectedDate = date;
    this.horarioSeleccionado = null;
    const iso = date ? this.toIsoDate(date) : null;
    this.horariosDelDia = iso ? this.horariosDisponibles.filter(h => h.fecha === iso) : [];
  }

  seleccionarHorario(horario: Horario): void {
    this.horarioSeleccionado = horario;
  }

  formatHora(hora: string): string {
    return hora ? hora.substring(0, 5) : '';
  }

  toIsoDate(date: Date): string {
    return date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0');
  }

  // Punto 5: la reserva se hace únicamente sobre un horario disponible real
  reservarSesion(): void {
    if (!this.profesional || !this.horarioSeleccionado) {
      this.snackBar.open(this.langService.translate('prof.selecciona_horario'), '', { duration: 2500 });
      return;
    }

    const usuarioId = Number(this.usuarioService.getIdLogeado());
    this.reservando = true;

    this.sesionService.reservar({
      idUsuarioPaciente: usuarioId,
      idHorario: this.horarioSeleccionado.id,
      tipoSesion: 'Virtual',
      comentario: ''
    }).subscribe({
      next: (sesion) => {
        this.reservando = false;
        this.router.navigate(['/reserva', this.profesional!.id], {
          queryParams: { sesionId: sesion.id, fecha: sesion.fecha, hora: sesion.hora }
        });
      },
      error: (err) => {
        this.reservando = false;
        this.snackBar.open(
          err.error?.message || 'No se pudo reservar la sesión. Inténtalo de nuevo.',
          '',
          { duration: 3000 }
        );
      }
    });
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

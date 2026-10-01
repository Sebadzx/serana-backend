import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UsuarioService } from '../../services/usuario-service';
import { LanguageService } from '../../services/language-service';
import { ProfesionalService } from '../../services/profesional-service';
import { Profesional } from '../../models/profesional';
import { MatSnackBar } from '@angular/material/snack-bar';

// Punto 1: Duplicado exacto del diseño de DashboardProfesional, usado como
// Dashboard del Paciente. La fila de "Especialistas Destacados" se conecta al
// Personal Médico real para permitir ver perfil / agendar sesión.
@Component({
  selector: 'app-dashboard-paciente',
  standalone: false,
  templateUrl: './dashboard-paciente.html',
  styleUrl: './dashboard-paciente.css',
})
export class DashboardPaciente implements OnInit {
  currentUser: any = null;

  // Mocked data based on mockup (idéntico al Dashboard Profesional original)
  citasHoy: number = 3;
  mensajesPendientes: number = 2;

  proximasSesiones = [
    { hora: '09:00 AM', paciente: 'Dra. Laura Martínez' },
    { hora: '11:30 AM', paciente: 'Dr. Carlos Reyes' }
  ];

  pacientesRecientes = [
    { nombre: 'Sofía G.', ultimaSesion: '15 Nov', avatar: 'avatar-sofia.jpg' },
    { nombre: 'Carlos R.', ultimaSesion: '12 Nov', avatar: 'avatar-carlos.jpg' }
  ];

  recursosCompartidos = [
    { nombre: 'Guía de Relajación.pdf', tipo: 'pdf' },
    { nombre: 'Diario de Gratitud.docx', tipo: 'doc' }
  ];

  // Especialistas Destacados: ahora se alimentan del Personal Médico real
  profesionalesDestacados: { id: number, nombre: string, cargo: string, centro: string, avatar: string }[] = [];

  private avatarPool: string[] = [
    'avatar-sandra.jpg', 'avatar-carlos.jpg', 'avatar-sofia.jpg',
    'avatar-john.jpg', 'avatar-profesional.jpg', 'avatar-david.jpg', 'avatar-doctor.jpg'
  ];

  constructor(
    private usuarioService: UsuarioService,
    public langService: LanguageService,
    private profesionalService: ProfesionalService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.cargarDatosUsuario();
    this.cargarProfesionalesDestacados();
  }

  cargarDatosUsuario() {
    const id = this.usuarioService.getIdLogeado();
    if (id) {
      const url = `${this.usuarioService.ruta_servidor}/usuarios/${Number(id)}`;
      fetch(url, {
        headers: {
          'Authorization': 'Bearer ' + this.usuarioService.getJwtTokenLogeado()
        }
      })
      .then(res => res.json())
      .then(data => {
        this.currentUser = data;
      })
      .catch(err => {
        console.log("Error al cargar datos del paciente:", err);
        this.currentUser = {
          nombres: 'Paciente',
          apellidos: 'Serana',
          correo: 'paciente@serana.pe'
        };
      });
    }
  }

  cargarProfesionalesDestacados(): void {
    this.profesionalService.listAll().subscribe({
      next: (data: Profesional[]) => {
        this.profesionalesDestacados = data.slice(0, 2).map(p => ({
          id: p.id,
          nombre: `${p.nombres ?? ''} ${p.apellidos ?? ''}`.trim(),
          cargo: p.especialidad,
          centro: p.lugarTrabajo,
          avatar: this.avatarPool[p.id % this.avatarPool.length]
        }));
      },
      error: (err) => console.log('Error al cargar profesionales destacados:', err)
    });
  }

  unirseLlamada(paciente: string) {
    this.snackBar.open(`Iniciando videollamada con ${paciente}...`, "", { duration: 3000 });
  }

  ejecutarAccion(accion: string) {
    this.snackBar.open(`Abriendo herramienta: ${accion}`, "", { duration: 2000 });
  }

  verRecurso(nombre: string) {
    this.snackBar.open(`Descargando recurso: ${nombre}`, "", { duration: 2000 });
  }

  verPerfilEspecialista(id: number) {
    this.router.navigate(['/profesionales', id]);
  }

  agendarSesionEspecialista(id: number) {
    this.router.navigate(['/profesionales', id]);
  }

  logout() {
    this.usuarioService.logout();
    window.location.href = '/login';
  }
}

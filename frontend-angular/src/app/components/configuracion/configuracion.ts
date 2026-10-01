import { Component, OnInit } from '@angular/core';
import { ConfiguracionService } from '../../services/configuracion-service';
import { UsuarioService } from '../../services/usuario-service';
import { LanguageService } from '../../services/language-service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-configuracion',
  standalone: false,
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.css',
})
export class Configuracion implements OnInit {
  config: any = {
    id: 0,
    tema: 'CLARO',
    idioma: 'Español (España)',
    notificacionHabilitada: true,
    idUsuario: 0
  };

  // Local simulated settings
  pushNotifications: boolean = false;
  anonymousByDefault: boolean = false;
  
  currentUser: any = null;

  constructor(
    private configuracionService: ConfiguracionService,
    private usuarioService: UsuarioService,
    public langService: LanguageService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.cargarDatosUsuario();
    this.cargarConfiguracion();
    this.anonymousByDefault = localStorage.getItem('anonimoPorDefecto') === 'true';
    this.pushNotifications = localStorage.getItem('pushNotifications') === 'true';
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
        console.log("Error al cargar datos del usuario:", err);
      });
    }
  }

  getCreatorAvatarUrl(): string {
    const role = this.usuarioService.getAuthoritiesLogeado();
    if (role && role.includes('ROLE_ADMIN')) {
      return 'avatar-admin.jpg';
    }
    if (role && role.includes('ROLE_PROFESIONAL')) {
      return 'avatar-profesional.jpg';
    }
    return 'avatar-paciente.jpg';
  }

  cargarConfiguracion() {
    const usuarioId = Number(this.usuarioService.getIdLogeado());
    this.configuracionService.findByUsuarioId(usuarioId).subscribe({
      next: (data) => {
        if (data) {
          this.config = data;
          // Normalizar idioma si es código corto
          if (this.config.idioma === 'ES') {
            this.config.idioma = 'Español (España)';
          }
          this.langService.setLanguage(this.config.idioma);
          // Guardar y aplicar tema actual
          localStorage.setItem('tema', this.config.tema);
          if (this.config.tema === 'OSCURO') {
            document.body.classList.add('dark-mode');
          } else {
            document.body.classList.remove('dark-mode');
          }
        } else {
          this.crearConfiguracionDefecto(usuarioId);
        }
      },
      error: (err) => {
        console.log("Configuración no encontrada o error, creando por defecto...", err);
        this.crearConfiguracionDefecto(usuarioId);
      }
    });
  }

  crearConfiguracionDefecto(usuarioId: number) {
    const defaultPayload = {
      id: 0,
      tema: 'CLARO',
      idioma: 'Español (España)',
      notificacionHabilitada: true,
      idUsuario: usuarioId
    };

    this.configuracionService.add(defaultPayload).subscribe({
      next: (data) => {
        this.config = data;
        this.langService.setLanguage(this.config.idioma);
      },
      error: (err) => {
        console.log("Error al crear configuración por defecto:", err);
      }
    });
  }

  toggleModoOscuro(event: any) {
    this.config.tema = event.checked ? 'OSCURO' : 'CLARO';
    localStorage.setItem('tema', this.config.tema);
    
    if (this.config.tema === 'OSCURO') {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }

    this.guardarConfiguracion();
    this.snackBar.open(`Modo oscuro ${event.checked ? 'activado' : 'desactivado'}`, "", { duration: 2000 });
  }

  cambiarIdioma(newIdioma: string) {
    this.config.idioma = newIdioma;
    this.langService.setLanguage(newIdioma);
    this.guardarConfiguracion();
    this.snackBar.open(`Idioma cambiado a: ${newIdioma}`, "", { duration: 2000 });
  }

  toggleAlertasCorreo(event: any) {
    this.config.notificacionHabilitada = event.checked;
    this.guardarConfiguracion();
    this.snackBar.open(`Alertas por correo ${event.checked ? 'activadas' : 'desactivadas'}`, "", { duration: 2000 });
  }

  togglePush(event: any) {
    this.pushNotifications = event.checked;
    localStorage.setItem('pushNotifications', event.checked ? 'true' : 'false');
    this.snackBar.open(`Notificaciones push ${event.checked ? 'activadas' : 'desactivadas'}`, "", { duration: 2000 });
  }

  toggleAnonimoDefecto(event: any) {
    this.anonymousByDefault = event.checked;
    localStorage.setItem('anonimoPorDefecto', event.checked ? 'true' : 'false');
    this.snackBar.open(`Publicación anónima por defecto ${event.checked ? 'activada' : 'desactivada'}`, "", { duration: 2000 });
  }

  guardarConfiguracion() {
    // Normalizar idioma a código corto para enviar al backend si fuese necesario
    const payload = {
      ...this.config,
      idioma: this.config.idioma === 'Español (España)' ? 'ES' : this.config.idioma
    };

    this.configuracionService.update(payload).subscribe({
      next: (data) => {
        console.log("Configuración actualizada:", data);
      },
      error: (err) => {
        console.log("Error al guardar configuración:", err);
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

  getTranslation(key: string): string {
    return this.langService.translate('config.' + key);
  }

  ejecutarAccion(accion: string) {
    this.snackBar.open(`Redirigiendo a: ${accion}`, "", { duration: 2000 });
  }
}

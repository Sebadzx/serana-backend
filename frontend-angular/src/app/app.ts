import { Component, OnInit, signal } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { UsuarioService } from './services/usuario-service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('serana-front');
  showCabecera: boolean = false;

  constructor(private usuarioService: UsuarioService, private router: Router) {}

  ngOnInit(): void {
    // Aplicar tema guardado en localStorage al arrancar la app
    const savedTheme = localStorage.getItem('tema');
    if (savedTheme === 'OSCURO') {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }

    // Escuchar cambios de ruta para decidir si mostrar la cabecera
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const url = event.urlAfterRedirects || event.url || '';
      this.updateCabeceraVisibility(url);
    });

    // Ejecutar verificación inicial
    this.updateCabeceraVisibility(this.router.url);
  }

  updateCabeceraVisibility(url: string): void {
    const isPublicPage = url === '/home' || 
                         url === '/login' || 
                         url === '/' || 
                         url === '/usuarios/add-usuarios' ||
                         url === '/dashboard-profesional' ||
                         url === '/configuracion' ||
                         url.includes('/home') || 
                         url.includes('/login') ||
                         url.includes('/dashboard-profesional') ||
                         url.includes('/configuracion') ||
                         url.includes('/usuarios/add-usuarios');
    const isLogged = this.usuarioService.getJwtTokenLogeado() !== null;
    this.showCabecera = isLogged && !isPublicPage;
  }
}

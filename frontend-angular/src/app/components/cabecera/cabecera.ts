import { Component, HostListener } from '@angular/core';
import { UsuarioService } from '../../services/usuario-service';
import { LanguageService } from '../../services/language-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cabecera',
  standalone: false,
  templateUrl: './cabecera.html',
  styleUrl: './cabecera.css',
})
export class Cabecera {
  isDropdownOpen = false;

  constructor (
    private usuarioService: UsuarioService,
    public langService: LanguageService,
    private router: Router
  ){}

  // Se reevalúa en cada ciclo de detección de cambios (incluida cada navegación)
  getSearchPlaceholder(): string {
    if (this.router.url.includes('/profesionales')) {
      return this.langService.translate('header.search_professionals');
    }
    return this.langService.translate('header.search_community');
  }

  toggleDropdown(event: Event) {
    event.stopPropagation();
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  getAvatarUrl(): string {
    const role = this.usuarioService.getAuthoritiesLogeado() || '';
    const email = localStorage.getItem('email') || '';
    const nombre = (localStorage.getItem('nombreCompleto') || '').toLowerCase();

    if (role.includes('ROLE_ADMIN') || email.includes('admin')) {
      return 'avatar-admin.jpg';
    }
    if (role.includes('ROLE_BIENESTAR') || role.includes('ROLE_PROFESIONAL') || email.includes('bienestar')) {
      return 'avatar-profesional.jpg';
    }

    // Estudiante: Si es Juan Perez o perfil masculino, usar avatar masculino
    if (nombre.includes('maria') || nombre.includes('ana') || nombre.includes('sofia') || nombre.includes('lucia') || nombre.includes('camila')) {
      return 'avatar-paciente.jpg';
    }
    return 'avatar-david.jpg';
  }

  get nombreUsuario(): string {
    return localStorage.getItem('nombreCompleto') || 'Estudiante';
  }

  get emailUsuario(): string {
    return localStorage.getItem('email') || '';
  }

  get rolLabel(): string {
    const auth = this.usuarioService.getAuthoritiesLogeado() || '';
    if (auth.includes('ROLE_ADMIN')) return 'Administrador';
    if (auth.includes('ROLE_BIENESTAR')) return 'Bienestar Universitario';
    if (auth.includes('ROLE_PROFESIONAL')) return 'Especialista';
    return 'Estudiante UPC';
  }

  get esBienestarOAdmin(): boolean {
    const auth = this.usuarioService.getAuthoritiesLogeado() || '';
    return auth.includes('ROLE_ADMIN') || auth.includes('ROLE_BIENESTAR');
  }

  navegarInicio(): void {
    if (this.esBienestarOAdmin) {
      this.router.navigate(['/dashboard-profesional']);
    } else {
      this.router.navigate(['/mis-evaluaciones']);
    }
  }

  logout() {      
    this.usuarioService.logout();
    this.router.navigate(['/login']);
  }

  @HostListener('document:click')
  onDocumentClick() {
    this.isDropdownOpen = false;
  }
}

import { CanActivateFn, Router } from '@angular/router';
import { UsuarioService } from '../services/usuario-service';
import { inject } from '@angular/core';

export const consultarGuard: CanActivateFn = (route, state) => {
  const usuarioService = inject(UsuarioService);
  const router = inject(Router);
  const jwt = usuarioService.getJwtTokenLogeado();
  const authorities = usuarioService.getAuthoritiesLogeado();

  if (jwt && authorities) {
    if (
      authorities.includes("ROLE_ESTUDIANTE") || 
      authorities.includes("ROLE_BIENESTAR") || 
      authorities.includes("ROLE_ADMIN") ||
      authorities.includes("ROLE_PACIENTE") ||
      authorities.includes("ROLE_PROFESIONAL")
    ) {
      return true;
    }
  }

  router.navigate(['/login']);
  return false;
};

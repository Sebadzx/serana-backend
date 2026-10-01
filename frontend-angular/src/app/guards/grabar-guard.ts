import { CanActivateFn } from '@angular/router';
import { UsuarioService } from '../services/usuario-service';
import { inject } from '@angular/core';

export const grabarGuard: CanActivateFn = (route, state) => {
    const usuarioService = inject(UsuarioService);
  let authorities = usuarioService.getAuthoritiesLogeado();

  if(authorities){
    if(authorities.indexOf("ROLE_ADMIN")>=0 )
      {
      return true;
    }
  }
  return false;
};

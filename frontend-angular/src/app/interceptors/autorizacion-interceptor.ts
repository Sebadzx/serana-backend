import { HttpInterceptorFn } from '@angular/common/http';
import { UsuarioService } from '../services/usuario-service';
import { inject } from '@angular/core';

export const autorizacionInterceptor: HttpInterceptorFn = (req, next) => {
  const usuarioService = inject(UsuarioService);
  let jwtToken = usuarioService.getJwtTokenLogeado();
  if (jwtToken && jwtToken !== "" && !req.url.includes('/api/auth/')) {
    let cloneRequest = req.clone({
      headers: req.headers.set("Authorization", "Bearer " + jwtToken)
    });
    return next(cloneRequest);
  }
  return next(req);
};


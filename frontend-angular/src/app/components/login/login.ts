import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { UsuarioService } from '../../services/usuario-service';
import { UsuarioDTO } from '../../models/usuarioDTO';
import { TokenDTO } from '../../models/tokenDTO';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {


  loginForm!: FormGroup;

  constructor(
    private usuarioService: UsuarioService,
    private snackBar: MatSnackBar,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(){
    this.loginForm = this.formBuilder.group(
      {
      correo: ['', [Validators.required]],
      contrasenia: ['', [Validators.required]],
    }
  );
  }

    login() {
      const loginDTO = {
        email: this.loginForm.get('correo')?.value.trim(),
        password: this.loginForm.get('contrasenia')?.value
      };

      this.usuarioService.login(loginDTO).subscribe({
        next: (data: any) => {
          const roles = data.roles || [];
          if (roles.includes('ROLE_ADMIN') || roles.includes('ROLE_BIENESTAR')) {
            this.router.navigate(['/dashboard-profesional']);
          } else {
            this.router.navigate(['/consentimiento']);
          }
          this.snackBar.open(`¡Bienvenido a SERANA, ${data.nombreCompleto || 'Usuario'}!`, 'Cerrar', { duration: 3000 });
        },
        error: (err: any) => {
          console.error('Error al iniciar sesión:', err);
          const errorMsg = err.error?.message || 'Correo o contraseña incorrectos.';
          this.snackBar.open('Error al iniciar sesión: ' + errorMsg, 'Cerrar', { duration: 3500 });
        }
      });
    }

}



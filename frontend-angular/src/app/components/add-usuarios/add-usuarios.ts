import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UsuarioService } from '../../services/usuario-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-usuarios',
  standalone: false,
  templateUrl: './add-usuarios.html',
  styleUrl: './add-usuarios.css',
})
export class AddUsuarios implements OnInit { 
  
  saveForm!: FormGroup;
  cargando: boolean = false;

  constructor (
    private usuarioService: UsuarioService,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private router: Router
  ) {}

  ngOnInit() {
    this.saveForm = this.formBuilder.group({
      codigoUniversitario: ['', [Validators.required, Validators.pattern(/^[uU]?[0-9]{8,10}$/)]],
      nombreCompleto: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmarPassword: ['', [Validators.required]],
      aceptoConsentimiento: [false, [Validators.requiredTrue]]
    });
  }

  formularioValido(): boolean {
    if (!this.saveForm.valid) {
      this.snackBar.open('Por favor completa todos los campos requeridos correctamente.', 'Cerrar', { duration: 2500 });
      return false;
    }
    
    if (this.saveForm.get('password')?.value !== this.saveForm.get('confirmarPassword')?.value) {
      this.snackBar.open('Las contraseñas no coinciden.', 'Cerrar', { duration: 2500 });
      return false;
    }

    return true;
  }

  grabar() {
    if (!this.formularioValido()) return;

    this.cargando = true;
    const registroDTO = {
      codigoUniversitario: this.saveForm.get('codigoUniversitario')?.value.trim(),
      nombreCompleto: this.saveForm.get('nombreCompleto')?.value.trim(),
      email: this.saveForm.get('email')?.value.trim().toLowerCase(),
      password: this.saveForm.get('password')?.value,
      aceptoConsentimiento: this.saveForm.get('aceptoConsentimiento')?.value,
      ipAddress: '127.0.0.1',
      versionTerminos: 'v1.0'
    };

    this.usuarioService.registro(registroDTO).subscribe({
      next: (data: any) => {
        this.cargando = false;
        this.snackBar.open('¡Registro exitoso en SERANA! Ahora puedes iniciar sesión.', 'Aceptar', { duration: 3500 });
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        this.cargando = false;
        console.error('Error al registrar estudiante:', err);
        let errorMsg = 'Error al procesar el registro institucional.';
        if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor backend (puerto 8080 apagado). Por favor inicia Spring Boot.';
        } else if (err.error?.message) {
          errorMsg = err.error.message;
        } else if (err.error?.error) {
          errorMsg = err.error.error;
        }
        this.snackBar.open(errorMsg, 'Cerrar', { duration: 5000 });
      }
    });
  }
}

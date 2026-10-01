import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { MaterialModule } from './modules/material/material-module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Cabecera } from './components/cabecera/cabecera';
import { Login } from './components/login/login';
import { ListUsuarios } from './components/usuarios/list-usuarios/list-usuarios';
import { AddUsuarios } from './components/add-usuarios/add-usuarios';
import { autorizacionInterceptor } from './interceptors/autorizacion-interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ListRecursosEducativos } from './components/recursos-educativos/list-recursos-educativos/list-recursos-educativos';
import { Home } from './components/home/home';
import { Comunidad } from './components/comunidad/comunidad';
import { DashboardProfesional } from './components/dashboard-profesional/dashboard-profesional';
import { Configuracion } from './components/configuracion/configuracion';
import { DashboardPaciente } from './components/dashboard-paciente/dashboard-paciente';
import { SesionesProfesional } from './components/sesiones-profesional/sesiones-profesional';
import { ListProfesionales } from './components/profesionales/list-profesionales/list-profesionales';
import { PerfilProfesional } from './components/profesionales/perfil-profesional/perfil-profesional';
import { ReservaPlaceholder } from './components/profesionales/reserva-placeholder/reserva-placeholder';

import { ConsentimientoComponent } from './components/consentimiento/consentimiento';
import { EvaluacionComponent } from './components/evaluacion/evaluacion';
import { MisEvaluacionesComponent } from './components/mis-evaluaciones/mis-evaluaciones';

@NgModule({
  declarations: [App, Cabecera, Login, ListUsuarios, AddUsuarios, ListRecursosEducativos, Home, Comunidad, DashboardProfesional, Configuracion, ListProfesionales, PerfilProfesional, ReservaPlaceholder, DashboardPaciente, SesionesProfesional, ConsentimientoComponent, EvaluacionComponent, MisEvaluacionesComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MaterialModule,
    ReactiveFormsModule, // para [formGroup]
    FormsModule, // si usas ngModel
    MatFormFieldModule, // para <mat-form-field> y <mat-error>
    MatInputModule,
    MatButtonModule, // para <button mat-button>
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([autorizacionInterceptor])),
  ],
  bootstrap: [App],
})
export class AppModule {}

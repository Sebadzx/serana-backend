import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ListUsuarios } from './components/usuarios/list-usuarios/list-usuarios';
import { AddUsuarios } from './components/add-usuarios/add-usuarios';
import { Login } from './components/login/login';
import { consultarGuard } from './guards/consultar-guard';
import { grabarGuard } from './guards/grabar-guard';
import { ListRecursosEducativos } from './components/recursos-educativos/list-recursos-educativos/list-recursos-educativos';
import { Home } from './components/home/home';
import { Comunidad } from './components/comunidad/comunidad';
import { DashboardProfesional } from './components/dashboard-profesional/dashboard-profesional';
import { Configuracion } from './components/configuracion/configuracion';
import { ListProfesionales } from './components/profesionales/list-profesionales/list-profesionales';
import { PerfilProfesional } from './components/profesionales/perfil-profesional/perfil-profesional';
import { ReservaPlaceholder } from './components/profesionales/reserva-placeholder/reserva-placeholder';
import { DashboardPaciente } from './components/dashboard-paciente/dashboard-paciente';
import { SesionesProfesional } from './components/sesiones-profesional/sesiones-profesional';

import { ConsentimientoComponent } from './components/consentimiento/consentimiento';
import { EvaluacionComponent } from './components/evaluacion/evaluacion';
import { MisEvaluacionesComponent } from './components/mis-evaluaciones/mis-evaluaciones';

const routes: Routes = [
  { path: "", redirectTo: "home", pathMatch: "full" },
  { path: "home", component: Home }, 
  { path: "login", component: Login },
  { path: "consentimiento", component: ConsentimientoComponent, canActivate: [consultarGuard] },
  { path: "evaluacion", component: EvaluacionComponent, canActivate: [consultarGuard] },
  { path: "mis-evaluaciones", component: MisEvaluacionesComponent, canActivate: [consultarGuard] },
  { path: "comunidad", component: Comunidad, canActivate: [consultarGuard] },
  { path: "dashboard-profesional", component: DashboardProfesional, canActivate: [consultarGuard] },
  { path: "dashboard-paciente", component: DashboardPaciente, canActivate: [consultarGuard] },
  { path: "sesiones", component: SesionesProfesional, canActivate: [consultarGuard] },
  { path: "configuracion", component: Configuracion, canActivate: [consultarGuard] },
  { path: "usuarios/list-usuarios", component: ListUsuarios, canActivate: [consultarGuard] },
  { path: "usuarios/add-usuarios", component: AddUsuarios },
  { path: "recursos-educativos/list-recursos-educativos", component: ListRecursosEducativos, canActivate: [consultarGuard] },
  { path: "list-recursos-educativos", redirectTo: "recursos-educativos/list-recursos-educativos", pathMatch: "full" },
  { path: "profesionales", component: ListProfesionales, canActivate: [consultarGuard] },
  { path: "profesionales/:id", component: PerfilProfesional, canActivate: [consultarGuard] },
  { path: "reserva/:id", component: ReservaPlaceholder, canActivate: [consultarGuard] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfesionalService } from '../../../services/profesional-service';
import { Profesional } from '../../../models/profesional';
import { LanguageService } from '../../../services/language-service';

// Punto 5: pantalla de confirmación real, mostrada tras crear la Sesion en el backend.
@Component({
  selector: 'app-reserva-placeholder',
  standalone: false,
  templateUrl: './reserva-placeholder.html',
  styleUrl: './reserva-placeholder.css',
})
export class ReservaPlaceholder implements OnInit {
  profesional?: Profesional;
  profesionalId!: number;
  sesionId: string | null = null;
  fecha: string | null = null;
  hora: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private profesionalService: ProfesionalService,
    public langService: LanguageService
  ) {}

  ngOnInit(): void {
    this.profesionalId = Number(this.route.snapshot.paramMap.get('id'));
    this.sesionId = this.route.snapshot.queryParamMap.get('sesionId');
    this.fecha = this.route.snapshot.queryParamMap.get('fecha');
    this.hora = this.route.snapshot.queryParamMap.get('hora');

    this.profesionalService.findById(this.profesionalId).subscribe({
      next: (data) => this.profesional = data
    });
  }

  nombreCompleto(): string {
    if (!this.profesional) return '';
    return `${this.profesional.nombres ?? ''} ${this.profesional.apellidos ?? ''}`.trim();
  }

  formatHora(hora: string): string {
    return hora ? hora.substring(0, 5) : '';
  }

  volverAlPerfil(): void {
    this.router.navigate(['/profesionales', this.profesionalId]);
  }
}

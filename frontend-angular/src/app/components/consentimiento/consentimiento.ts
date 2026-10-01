import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ConsentimientoService } from '../../services/consentimiento-service';
import { ArcoService, SolicitudARCODTO } from '../../services/arco-service';

@Component({
  selector: 'app-consentimiento',
  standalone: false,
  templateUrl: './consentimiento.html',
  styleUrl: './consentimiento.css'
})
export class ConsentimientoComponent implements OnInit {
  tabActiva: 'consentimiento' | 'arco' = 'consentimiento';

  // Consentimiento
  acepto: boolean = false;
  cargando: boolean = false;
  yaFirmado: boolean = false;

  // Derechos ARCO (US-18)
  tipoSolicitud: string = 'CANCELACION';
  detalleSolicitud: string = '';
  enviandoArco: boolean = false;
  solicitudesArco: SolicitudARCODTO[] = [];
  cargandoSolicitudes: boolean = false;

  constructor(
    private consentimientoService: ConsentimientoService,
    private arcoService: ArcoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.verificarEstado();
    this.cargarSolicitudesArco();
  }

  verificarEstado(): void {
    this.consentimientoService.obtenerEstado().subscribe({
      next: (res) => {
        if (res && res.firmado) {
          this.yaFirmado = true;
          this.acepto = true;
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Verificación de consentimiento:', err);
        this.cdr.detectChanges();
      }
    });
  }

  cambiarTab(tab: 'consentimiento' | 'arco'): void {
    this.tabActiva = tab;
    if (tab === 'arco') {
      this.cargarSolicitudesArco();
    }
    this.cdr.detectChanges();
  }

  firmarConsentimiento(): void {
    if (!this.acepto) {
      this.snackBar.open('Debes marcar la casilla para confirmar tu consentimiento.', 'Cerrar', { duration: 2500 });
      return;
    }

    this.cargando = true;
    this.cdr.detectChanges();

    this.consentimientoService.firmar({
      versionTerminos: 'v1.0-Ley29733',
      ipAddress: '127.0.0.1'
    }).subscribe({
      next: () => {
        this.cargando = false;
        this.yaFirmado = true;
        this.snackBar.open('Consentimiento firmado digitalmente con éxito (Ley N° 29733).', 'Aceptar', { duration: 3000 });
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error al firmar consentimiento:', err);
        const msg = err.error?.message || 'Error al procesar la firma del consentimiento.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
        this.cdr.detectChanges();
      }
    });
  }

  irAEvaluacion(): void {
    this.router.navigate(['/evaluacion']);
  }

  cargarSolicitudesArco(): void {
    this.cargandoSolicitudes = true;
    this.cdr.detectChanges();
    this.arcoService.listarMisSolicitudes().subscribe({
      next: (data) => {
        this.solicitudesArco = data || [];
        this.cargandoSolicitudes = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar solicitudes ARCO:', err);
        this.cargandoSolicitudes = false;
        this.cdr.detectChanges();
      }
    });
  }

  enviarSolicitudArco(): void {
    if (!this.detalleSolicitud || this.detalleSolicitud.trim().length < 10) {
      this.snackBar.open('Por favor ingresa un detalle o justificación de al menos 10 caracteres.', 'Cerrar', { duration: 3000 });
      return;
    }

    this.enviandoArco = true;
    this.cdr.detectChanges();

    this.arcoService.enviarSolicitud({
      tipoSolicitud: this.tipoSolicitud,
      detalleSolicitud: this.detalleSolicitud.trim()
    }).subscribe({
      next: (solicitudCreada) => {
        this.enviandoArco = false;
        this.detalleSolicitud = '';
        this.snackBar.open(`Solicitud de ${this.tipoSolicitud} registrada con ID #${solicitudCreada.id}. Será procesada conforme a Ley.`, 'Aceptar', { duration: 4000 });
        this.cargarSolicitudesArco();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.enviandoArco = false;
        console.error('Error al enviar solicitud ARCO:', err);
        const msg = err.error?.message || 'No se pudo registrar la solicitud ARCO.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
        this.cdr.detectChanges();
      }
    });
  }
}


import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { EvaluacionService, EvaluacionResponseDTO, RecomendacionICBTDTO } from '../../services/evaluacion-service';
import { ArcoService, SolicitudARCODTO } from '../../services/arco-service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-mis-evaluaciones',
  standalone: false,
  templateUrl: './mis-evaluaciones.html',
  styleUrl: './mis-evaluaciones.css'
})
export class MisEvaluacionesComponent implements OnInit {
  evaluaciones: EvaluacionResponseDTO[] = [];
  cargando: boolean = true;
  errorCarga: string | null = null;
  columnasMostradas: string[] = ['id', 'fecha', 'nivel', 'probabilidad', 'alerta', 'estresores', 'acciones'];

  // Informe Personalizado (US-14)
  evaluacionSeleccionada: EvaluacionResponseDTO | null = null;
  mostrarModalInforme: boolean = false;

  // Derechos ARCO (US-18)
  mostrarModalARCO: boolean = false;
  enviandoARCO: boolean = false;
  tipoSolicitudARCO: string = 'CANCELACION';
  detalleSolicitudARCO: string = 'Solicito la supresión y eliminación definitiva de mis registros de evaluación y datos personales (Derecho de Cancelación / Olvido, Art. 18 de la Ley N° 29733).';
  misSolicitudesARCO: SolicitudARCODTO[] = [];

  constructor(
    private evaluacionService: EvaluacionService,
    private arcoService: ArcoService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar
  ) {}

  verInforme(ev: EvaluacionResponseDTO): void {
    this.evaluacionSeleccionada = ev;
    this.mostrarModalInforme = true;
    this.cdr.detectChanges();
  }

  cerrarModalInforme(): void {
    this.mostrarModalInforme = false;
    this.evaluacionSeleccionada = null;
    this.cdr.detectChanges();
  }

  imprimirInforme(): void {
    window.print();
  }

  // US-16: Exportación de todo el Historial a PDF
  exportarHistorialPDF(): void {
    window.print();
  }

  // Imprimir informe directamente desde la fila
  imprimirInformeDirecto(ev: EvaluacionResponseDTO): void {
    this.verInforme(ev);
    setTimeout(() => {
      window.print();
    }, 300);
  }

  // US-14 & US-16: Exportación de Informe Individual a Excel / CSV (UTF-8 BOM)
  exportarInformeExcel(evParam?: EvaluacionResponseDTO): void {
    const ev = evParam || this.evaluacionSeleccionada;
    if (!ev) return;

    const bom = '\uFEFF';
    let csv = bom + 'REPORTE CLINICO DE ANSIEDAD DIGITAL Y SALUD EMOCIONAL (US-14)\n';
    csv += `ID Evaluacion;${ev.idEvaluacion}\n`;
    csv += `Fecha Evaluacion;${new Date(ev.fechaEvaluacion).toLocaleString()}\n`;
    csv += `Nivel de Ansiedad Predicho;${ev.nivelAnsiedadPredicho}\n`;
    csv += `Confianza del Modelo Predictivo;${Math.round((ev.scoreProbabilidad || 0) * 100)}%\n`;
    csv += `Caso Critico Detectado;${ev.esCasoCritico ? 'SI' : 'NO'}\n`;
    csv += `Estresores Detectados (NLP);"${(ev.estresoresDetectados || 'Sobrecarga virtual').replace(/"/g, '""')}"\n`;
    csv += `Polaridad Sentimiento;${ev.polaridadSentimiento || 0}\n`;
    csv += `Justificacion Clinica;"${(ev.justificacionCli || '').replace(/"/g, '""')}"\n\n`;

    csv += 'FACTORES DE MAYOR INFLUENCIA (FEATURE IMPORTANCE)\n';
    csv += 'Factor Clave;Nivel de Influencia Estimado\n';
    csv += 'Escala TS4US (Tecnoestres Universitario);Factor de mayor influencia\n';
    csv += 'Escala ASAIDAS (Ansiedad por Inteligencia Artificial);Factor de influencia moderado\n';
    csv += 'Horas frente a pantallas y descanso;Indicadores conductuales\n';

    csv += '\nMODULOS DE INTERVENCION PSICOEDUCATIVA iCBT RECOMENDADOS\n';
    csv += 'Modulo iCBT;Titulo de la Intervencion;Descripcion;Estado\n';
    if (ev.recomendacionesICBT && ev.recomendacionesICBT.length > 0) {
      ev.recomendacionesICBT.forEach((r: RecomendacionICBTDTO) => {
        csv += `"${r.moduloICBT}";"${r.titulo.replace(/"/g, '""')}";"${r.descripcion.replace(/"/g, '""')}";"${r.completada ? 'Completado' : 'Pendiente'}"\n`;
      });
    }

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `informe_ansiedad_evaluacion_${ev.idEvaluacion}_${new Date().toISOString().slice(0, 10)}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    this.snackBar.open('Informe exportado a Excel exitosamente (US-14 y US-16).', 'Aceptar', { duration: 3000 });
  }

  // US-16: Exportación de todo el Historial de Evaluaciones del Estudiante a Excel (.csv con UTF-8 BOM)
  exportarHistorialExcel(): void {
    if (!this.evaluaciones || this.evaluaciones.length === 0) {
      this.snackBar.open('No tienes evaluaciones registradas para exportar.', 'Cerrar', { duration: 3000 });
      return;
    }

    const bom = '\uFEFF';
    let csv = bom + 'ID_EVALUACION;FECHA;NIVEL_ANSIEDAD;CONFIANZA_IA;CASO_CRITICO;ESTRESORES_NLP;POLARIDAD;INTERVENCIONES_ICBT\n';
    this.evaluaciones.forEach(ev => {
      const modulos = ev.recomendacionesICBT ? ev.recomendacionesICBT.map(r => r.moduloICBT).join(' | ') : 'iCBT General';
      csv += `${ev.idEvaluacion};`;
      csv += `"${new Date(ev.fechaEvaluacion).toLocaleString()}";`;
      csv += `"${ev.nivelAnsiedadPredicho}";`;
      csv += `"${Math.round((ev.scoreProbabilidad || 0) * 100)}%";`;
      csv += `"${ev.esCasoCritico ? 'SI' : 'NO'}";`;
      csv += `"${(ev.estresoresDetectados || '').replace(/"/g, '""')}";`;
      csv += `"${ev.polaridadSentimiento || 0}";`;
      csv += `"${modulos.replace(/"/g, '""')}"\n`;
    });

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `historial_evaluaciones_${new Date().toISOString().slice(0, 10)}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    this.snackBar.open('Historial completo exportado a Excel exitosamente (US-16).', 'Aceptar', { duration: 3000 });
  }


  ngOnInit(): void {
    this.cargarEvaluaciones();
    this.cargarMisSolicitudesARCO();
  }

  cargarEvaluaciones(): void {
    this.cargando = true;
    this.errorCarga = null;
    this.cdr.detectChanges();

    this.evaluacionService.listarMisEvaluaciones().subscribe({
      next: (data) => {
        this.evaluaciones = data || [];
        this.cargando = false;
        this.errorCarga = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar historial:', err);
        this.cargando = false;
        if (err.status === 401 || err.status === 403) {
          this.errorCarga = 'Tu sesión ha expirado o no tienes autorización. Por favor inicia sesión nuevamente.';
          this.snackBar.open('Sesión expirada. Redirigiendo al login...', 'Cerrar', { duration: 3000 });
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 1500);
        } else {
          this.errorCarga = 'No se pudo conectar con el servidor para obtener las evaluaciones.';
          this.snackBar.open('Error al cargar evaluaciones.', 'Reintentar', { duration: 4000 })
            .onAction().subscribe(() => this.cargarEvaluaciones());
        }
        this.cdr.detectChanges();
      }
    });
  }

  nuevaEvaluacion(): void {
    this.router.navigate(['/evaluacion']);
  }

  // Métodos ARCO (US-18)
  abrirModalARCO(): void {
    this.mostrarModalARCO = true;
    this.cargarMisSolicitudesARCO();
    this.cdr.detectChanges();
  }

  cerrarModalARCO(): void {
    this.mostrarModalARCO = false;
    this.cdr.detectChanges();
  }

  cargarMisSolicitudesARCO(): void {
    this.arcoService.listarMisSolicitudes().subscribe({
      next: (data) => {
        this.misSolicitudesARCO = data || [];
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Mis solicitudes ARCO:', err);
      }
    });
  }

  enviarSolicitudARCO(): void {
    if (!this.detalleSolicitudARCO.trim()) {
      this.snackBar.open('Por favor ingresa el fundamento de tu solicitud.', 'Cerrar', { duration: 2500 });
      return;
    }

    this.enviandoARCO = true;
    this.arcoService.enviarSolicitud({
      tipoSolicitud: this.tipoSolicitudARCO,
      detalleSolicitud: this.detalleSolicitudARCO.trim()
    }).subscribe({
      next: () => {
        this.enviandoARCO = false;
        this.snackBar.open('Solicitud ARCO registrada exitosamente (Ley N° 29733).', 'Aceptar', { duration: 3500 });
        this.cargarMisSolicitudesARCO();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.enviandoARCO = false;
        console.error('Error al registrar solicitud ARCO:', err);
        const msg = err.error?.message || 'Error al procesar la solicitud ARCO.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
        this.cdr.detectChanges();
      }
    });
  }
}

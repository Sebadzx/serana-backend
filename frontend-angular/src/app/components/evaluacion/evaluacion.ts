import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { EvaluacionService, EvaluacionResponseDTO } from '../../services/evaluacion-service';

@Component({
  selector: 'app-evaluacion',
  standalone: false,
  templateUrl: './evaluacion.html',
  styleUrl: './evaluacion.css'
})
export class EvaluacionComponent implements OnInit {
  pasoActual: number = 1; // 1: Psicométrico, 2: Hábitos, 3: Texto Libre, 4: Resultado
  cargando: boolean = false;
  evaluacionId: number | null = null;
  resultadoFinal: EvaluacionResponseDTO | null = null;

  // Formulario Paso 1: Psicométrico
  formPaso1!: FormGroup;
  // Formulario Paso 2: Hábitos
  formPaso2!: FormGroup;
  // Formulario Paso 3: Texto Libre
  formPaso3!: FormGroup;

  // Reactivos TS4US (Escala de Tecnoestrés Universitario)
  preguntasTS4US = [
    { id: 1, texto: 'Siento que las plataformas y herramientas virtuales de la universidad saturan mi día a día.' },
    { id: 2, texto: 'Las notificaciones y entregas virtuales interrumpen constantemente mi tiempo de descanso personal.' },
    { id: 3, texto: 'Experimento fatiga visual o cansancio físico excesivo tras largas jornadas de estudio virtual.' },
    { id: 4, texto: 'Siento la presión o necesidad de responder mensajes académicos de forma inmediata.' },
    { id: 5, texto: 'Me cuesta concentrarme debido a la multitarea constante entre pestañas y aplicaciones.' }
  ];

  // Reactivos ASAIDAS (Ansiedad e Inteligencia Artificial en Estudiantes)
  preguntasASAIDAS = [
    { id: 1, texto: 'Siento la necesidad compulsiva de consultar herramientas de IA para realizar mis tareas.' },
    { id: 2, texto: 'Experimento ansiedad o inseguridad cuando debo rendir evaluaciones presenciales sin soporte tecnológico.' },
    { id: 3, texto: 'Temo que depender demasiado de herramientas digitales reduzca mi capacidad autónoma de análisis.' }
  ];

  // US-06: Guardar y retomar progreso
  progresoRecuperado: boolean = false;
  fechaProgreso: string = '';

  constructor(
    private fb: FormBuilder,
    private evaluacionService: EvaluacionService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.formPaso1 = this.fb.group({
      p1: [3, Validators.required],
      p2: [3, Validators.required],
      p3: [3, Validators.required],
      p4: [3, Validators.required],
      p5: [3, Validators.required],
      p6: [3, Validators.required],
      p7: [3, Validators.required],
      p8: [3, Validators.required]
    });

    this.formPaso2 = this.fb.group({
      tiempoPantallaHoras: [8.5, [Validators.required, Validators.min(1), Validators.max(20)]],
      habitoSuenoHoras: [6.0, [Validators.required, Validators.min(2), Validators.max(14)]]
    });

    this.formPaso3 = this.fb.group({
      textoLibre: ['', [Validators.required, Validators.minLength(10)]]
    });

    // US-06: Verificar si hay progreso previo guardado para retomar
    this.verificarProgresoGuardado();
  }

  verificarProgresoGuardado(): void {
    const raw = localStorage.getItem('serana_evaluacion_progreso');
    if (!raw) return;
    try {
      const p = JSON.parse(raw);
      if (p && p.pasoActual && p.pasoActual >= 1 && p.pasoActual < 4) {
        if (p.formPaso1) this.formPaso1.patchValue(p.formPaso1);
        if (p.formPaso2) this.formPaso2.patchValue(p.formPaso2);
        if (p.textoLibre) this.formPaso3.patchValue({ textoLibre: p.textoLibre });
        this.evaluacionId = p.evaluacionId || null;
        this.pasoActual = p.pasoActual;
        this.progresoRecuperado = true;
        this.fechaProgreso = p.fechaGuardado;
        this.snackBar.open(`Progreso previo recuperado (Paso ${this.pasoActual}). Retomando evaluación (US-06).`, 'Continuar', { duration: 4000 });
      }
    } catch (e) {
      console.error('Error al recuperar progreso guardado:', e);
    }
  }

  guardarProgresoLocal(notificar: boolean = false): void {
    const data = {
      evaluacionId: this.evaluacionId,
      pasoActual: this.pasoActual,
      formPaso1: this.formPaso1?.value,
      formPaso2: this.formPaso2?.value,
      textoLibre: this.formPaso3?.get('textoLibre')?.value,
      fechaGuardado: new Date().toISOString()
    };
    localStorage.setItem('serana_evaluacion_progreso', JSON.stringify(data));
    if (notificar) {
      this.snackBar.open('Progreso guardado exitosamente (US-06). Puedes salir y retomar cuando desees.', 'Aceptar', { duration: 3500 });
    }
  }

  guardarYSalir(): void {
    this.guardarProgresoLocal(true);
    this.router.navigate(['/mis-evaluaciones']);
  }

  descartarProgreso(): void {
    localStorage.removeItem('serana_evaluacion_progreso');
    this.progresoRecuperado = false;
    this.reiniciarEvaluacion();
    this.snackBar.open('Progreso descartado. Comenzando nueva evaluación desde el inicio.', 'Aceptar', { duration: 2500 });
  }

  // PASO 1 -> PASO 2
  guardarPaso1(): void {
    if (this.formPaso1.invalid) return;

    this.cargando = true;
    const v = this.formPaso1.value;
    // Calcular puntajes simulados de escalas
    const puntajeGad = Number(v.p1) + Number(v.p2) + Number(v.p3) + Number(v.p4) + 4;
    const puntajePss = Number(v.p5) + Number(v.p6) + Number(v.p7) + Number(v.p8) + 8;

    const bodyPaso1 = {
      nivelEstres: Math.round(puntajePss / 2),
      nivelAnsiedad: Math.round(puntajeGad / 2),
      puntuacionGad7: puntajeGad,
      puntuacionPss10: puntajePss
    };

    this.evaluacionService.paso1Psicometrico(bodyPaso1).subscribe({
      next: (res) => {
        this.cargando = false;
        this.evaluacionId = res.evaluacionId;
        this.pasoActual = 2;
        this.guardarProgresoLocal(); // US-06
        this.snackBar.open('Cuestionario psicométrico guardado (US-04 y US-06). Paso 2 habilitado.', 'Aceptar', { duration: 2500 });
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error Paso 1:', err);
        const msg = err.error?.message || 'Error al guardar cuestionario psicométrico.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
      }
    });
  }

  // PASO 2 -> PASO 3
  guardarPaso2(): void {
    if (this.formPaso2.invalid || !this.evaluacionId) return;

    this.cargando = true;
    const bodyPaso2 = {
      tiempoPantallaHoras: Number(this.formPaso2.get('tiempoPantallaHoras')?.value),
      habitoSuenoHoras: Number(this.formPaso2.get('habitoSuenoHoras')?.value)
    };

    this.evaluacionService.paso2Habitos(this.evaluacionId, bodyPaso2).subscribe({
      next: () => {
        this.cargando = false;
        this.pasoActual = 3;
        this.guardarProgresoLocal(); // US-06
        this.snackBar.open('Hábitos de uso digital registrados (US-05 y US-06). Paso final habilitado.', 'Aceptar', { duration: 2500 });
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error Paso 2:', err);
        const msg = err.error?.message || 'Error al registrar hábitos de pantalla y sueño.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
      }
    });
  }

  // PASO 3 -> RESULTADOS (IA ML Híbrido)
  finalizarEvaluacion(): void {
    if (this.formPaso3.invalid || !this.evaluacionId) return;

    this.cargando = true;
    const bodyPaso3 = {
      textoLibre: this.formPaso3.get('textoLibre')?.value.trim()
    };

    this.evaluacionService.paso3TextoLibre(this.evaluacionId, bodyPaso3).subscribe({
      next: (res: EvaluacionResponseDTO) => {
        this.cargando = false;
        this.resultadoFinal = res;
        this.pasoActual = 4;
        localStorage.removeItem('serana_evaluacion_progreso'); // US-06 completado
        this.progresoRecuperado = false;
        this.snackBar.open('¡Evaluación finalizada y procesada por Inteligencia Artificial!', 'Aceptar', { duration: 3500 });
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error Paso 3:', err);
        const msg = err.error?.message || 'Error al ejecutar inferencia con Machine Learning.';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      }
    });
  }

  irAHistorial(): void {
    this.router.navigate(['/mis-evaluaciones']);
  }

  reiniciarEvaluacion(): void {
    this.pasoActual = 1;
    this.evaluacionId = null;
    this.resultadoFinal = null;
    this.progresoRecuperado = false;
    localStorage.removeItem('serana_evaluacion_progreso');
    this.formPaso1.reset({ p1: 3, p2: 3, p3: 3, p4: 3, p5: 3, p6: 3, p7: 3, p8: 3 });
    this.formPaso2.reset({ tiempoPantallaHoras: 8.5, habitoSuenoHoras: 6.0 });
    this.formPaso3.reset({ textoLibre: '' });
  }
}

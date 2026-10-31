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
  pasoActual: number = 1; // 1: Psicométrico, 2: Hábitos, 3: Texto Libre, 4: Resultado de Orientación
  cargando: boolean = false;
  evaluacionId: number | null = null;
  resultadoFinal: EvaluacionResponseDTO | null = null;

  formPaso1!: FormGroup;
  formPaso2!: FormGroup;
  formPaso3!: FormGroup;

  // 13 Reactivos completos de la Escala TS4US (Tecnoestrés Universitario)
  preguntasTS4US = [
    { id: 1, control: 'ts4us_1', texto: 'Siento sobrecarga de información digital y tareas virtuales en mis cursos.' },
    { id: 2, control: 'ts4us_2', texto: 'Las demandas digitales de la universidad invaden mi tiempo personal y de descanso.' },
    { id: 3, control: 'ts4us_3', texto: 'La complejidad de las plataformas virtuales me genera dificultad o desgaste.' },
    { id: 4, control: 'ts4us_4', texto: 'Siento inseguridad o estrés ante posibles fallas técnicas durante mis actividades académicas.' },
    { id: 5, control: 'ts4us_5', texto: 'Los cambios constantes en herramientas digitales me producen incertidumbre.' },
    { id: 6, control: 'ts4us_6', texto: 'Experimento fatiga visual o agotamiento físico tras estudiar frente a pantallas.' },
    { id: 7, control: 'ts4us_7', texto: 'Siento presión constante por responder mensajes y notificaciones académicas de inmediato.' },
    { id: 8, control: 'ts4us_8', texto: 'Me cuesta concentrarme debido a la multitarea continua entre varias pestañas y aplicaciones.' },
    { id: 9, control: 'ts4us_9', texto: 'Me preocupa quedar desactualizado frente a las nuevas herramientas tecnológicas exigidas.' },
    { id: 10, control: 'ts4us_10', texto: 'Siento una dependencia excesiva de las plataformas virtuales para poder avanzar en mis estudios.' },
    { id: 11, control: 'ts4us_11', texto: 'El volumen continuo de entregas virtuales me hace sentir saturado.' },
    { id: 12, control: 'ts4us_12', texto: 'Siento temor o intranquilidad de perder avisos importantes si me desconecto.' },
    { id: 13, control: 'ts4us_13', texto: 'Siento que pierdo el control sobre el tiempo que paso conectado a dispositivos digitales.' }
  ];

  // 8 Reactivos completos de la Escala ASAIDAS (Ansiedad y Dependencia a la IA / Entorno Digital)
  preguntasASAIDAS = [
    { id: 1, control: 'asaidas_1', texto: 'Siento la necesidad frecuente de consultar herramientas de Inteligencia Artificial para mis tareas.' },
    { id: 2, control: 'asaidas_2', texto: 'Experimento ansiedad o inseguridad cuando rindo evaluaciones presenciales sin apoyo tecnológico.' },
    { id: 3, control: 'asaidas_3', texto: 'Delego gran parte del análisis de mis trabajos en herramientas generativas digitales.' },
    { id: 4, control: 'asaidas_4', texto: 'Me genera temor que las respuestas o herramientas de IA contengan errores que afecten mis notas.' },
    { id: 5, control: 'asaidas_5', texto: 'Siento que el uso constante de asistentes digitales reduce mi autonomía de razonamiento crítico.' },
    { id: 6, control: 'asaidas_6', texto: 'Dudo de mi propia capacidad académica cuando no dispongo de herramientas digitales de consulta.' },
    { id: 7, control: 'asaidas_7', texto: 'Las notificaciones constantes de plataformas y grupos académicos me generan saturación emocional.' },
    { id: 8, control: 'asaidas_8', texto: 'Me afecta compararme en entornos digitales con el avance o desempeño de otros compañeros.' }
  ];

  progresoRecuperado: boolean = false;
  fechaProgreso: string = '';

  constructor(
    private fb: FormBuilder,
    private evaluacionService: EvaluacionService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Punto 1: Todas las opciones inician en null (sin preselección en 3), obligando al estudiante a responder cada ítem (1 a 5)
    const controlesPaso1: { [key: string]: any } = {};
    for (let i = 1; i <= 13; i++) {
      controlesPaso1[`ts4us_${i}`] = [null, [Validators.required, Validators.min(1), Validators.max(5)]];
    }
    for (let j = 1; j <= 8; j++) {
      controlesPaso1[`asaidas_${j}`] = [null, [Validators.required, Validators.min(1), Validators.max(5)]];
    }
    this.formPaso1 = this.fb.group(controlesPaso1);

    this.formPaso2 = this.fb.group({
      tiempoPantallaHoras: [8.0, [Validators.required, Validators.min(1), Validators.max(20)]],
      horasRedesSociales: [3.0, [Validators.required, Validators.min(0), Validators.max(16)]],
      horasEstudioVirtual: [5.0, [Validators.required, Validators.min(0), Validators.max(16)]],
      habitoSuenoHoras: [6.5, [Validators.required, Validators.min(2), Validators.max(14)]],
      dispositivoPrincipal: ['LAPTOP', [Validators.required]]
    });

    this.formPaso3 = this.fb.group({
      textoLibre: ['', [Validators.required, Validators.minLength(10)]]
    });

    this.verificarProgresoGuardado();
  }

  get cantidadRespondidasPaso1(): number {
    if (!this.formPaso1) return 0;
    const vals = this.formPaso1.value;
    return Object.keys(vals).filter(k => vals[k] !== null && vals[k] >= 1 && vals[k] <= 5).length;
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

  // PASO 1 -> PASO 2: Enviar las 13 respuestas reales de TS4US y 8 de ASAIDAS sin conversiones simuladas
  guardarPaso1(): void {
    if (this.formPaso1.invalid) {
      this.snackBar.open('Por favor, responde las 21 preguntas antes de continuar.', 'Aceptar', { duration: 3000 });
      return;
    }

    this.cargando = true;
    const v = this.formPaso1.value;
    const ts4usRespuestas: number[] = [];
    for (let i = 1; i <= 13; i++) {
      ts4usRespuestas.push(Number(v[`ts4us_${i}`]));
    }
    const asaidasRespuestas: number[] = [];
    for (let j = 1; j <= 8; j++) {
      asaidasRespuestas.push(Number(v[`asaidas_${j}`]));
    }

    const bodyPaso1 = {
      ts4usRespuestas,
      asaidasRespuestas
    };

    this.evaluacionService.paso1Psicometrico(bodyPaso1).subscribe({
      next: (res) => {
        this.cargando = false;
        this.evaluacionId = res.evaluacionId;
        this.pasoActual = 2;
        this.guardarProgresoLocal();
        this.snackBar.open('Respuestas psicométricas reales guardadas (TS4US y ASAIDAS). Paso 2 habilitado.', 'Aceptar', { duration: 2500 });
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error Paso 1:', err);
        const msg = err.error?.message || 'Error al guardar cuestionario psicométrico.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
      }
    });
  }

  // PASO 2 -> PASO 3: Enviar los 5 hábitos reales
  guardarPaso2(): void {
    if (this.formPaso2.invalid || !this.evaluacionId) return;

    this.cargando = true;
    const bodyPaso2 = {
      tiempoPantallaHoras: Number(this.formPaso2.get('tiempoPantallaHoras')?.value),
      horasRedesSociales: Number(this.formPaso2.get('horasRedesSociales')?.value),
      horasEstudioVirtual: Number(this.formPaso2.get('horasEstudioVirtual')?.value),
      habitoSuenoHoras: Number(this.formPaso2.get('habitoSuenoHoras')?.value),
      dispositivoPrincipal: String(this.formPaso2.get('dispositivoPrincipal')?.value || 'LAPTOP')
    };

    this.evaluacionService.paso2Habitos(this.evaluacionId, bodyPaso2).subscribe({
      next: () => {
        this.cargando = false;
        this.pasoActual = 3;
        this.guardarProgresoLocal();
        this.snackBar.open('Hábitos digitales y horas de sueño registrados. Paso final habilitado.', 'Aceptar', { duration: 2500 });
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error Paso 2:', err);
        const msg = err.error?.message || 'Error al registrar hábitos de pantalla y sueño.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
      }
    });
  }

  // PASO 3 -> RESULTADO EXPERIMENTAL DE ORIENTACIÓN
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
        localStorage.removeItem('serana_evaluacion_progreso');
        this.progresoRecuperado = false;
        this.snackBar.open('Evaluación finalizada. Se generó tu orientación preventiva.', 'Aceptar', { duration: 3500 });
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
    const resetPaso1: { [key: string]: any } = {};
    for (let i = 1; i <= 13; i++) resetPaso1[`ts4us_${i}`] = null;
    for (let j = 1; j <= 8; j++) resetPaso1[`asaidas_${j}`] = null;
    this.formPaso1.reset(resetPaso1);
    this.formPaso2.reset({
      tiempoPantallaHoras: 8.0,
      horasRedesSociales: 3.0,
      horasEstudioVirtual: 5.0,
      habitoSuenoHoras: 6.5,
      dispositivoPrincipal: 'LAPTOP'
    });
    this.formPaso3.reset({ textoLibre: '' });
  }
}

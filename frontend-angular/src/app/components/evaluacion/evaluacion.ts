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
  pasoActual: number = 1; // 1: Psicometría (TMD + STAI-Rasgo), 2: Hábitos SocioDemo, 3: Texto Libre, 4: Orientación IA
  cargando: boolean = false;
  evaluacionId: number | null = null;
  resultadoFinal: EvaluacionResponseDTO | null = null;

  formPaso1!: FormGroup;
  formPaso2!: FormGroup;
  formPaso3!: FormGroup;

  // Test de Dependencia al Móvil — TMD (Chóliz, 2012)
  // Bloque 1: Frecuencia (Q1 a Q10, escala 0 a 4: 0=Nunca, 1=Rara vez, 2=A veces, 3=Frecuentemente, 4=Muy frecuentemente)
  preguntasTMDFrecuencia = [
    { id: 1, control: 'tmd_1', texto: 'Me han llamado la atención o me han hecho alguna advertencia por utilizar mucho el teléfono móvil.' },
    { id: 2, control: 'tmd_2', texto: 'Me he puesto un límite de gasto o consumo en el móvil, pero lo agoto antes de terminar el mes.' },
    { id: 3, control: 'tmd_3', texto: 'He discutido con mis padres o familiares por el gasto o tiempo dedicado al teléfono móvil.' },
    { id: 4, control: 'tmd_4', texto: 'Dedico más tiempo del que quisiera a hablar por teléfono, enviar mensajes, usar WhatsApp o chatear.' },
    { id: 5, control: 'tmd_5', texto: 'He enviado más de cinco (5) mensajes o cadenas de chat en un día a través del teléfono móvil.' },
    { id: 6, control: 'tmd_6', texto: 'Me he acostado más tarde o he dormido menos por estar utilizando el teléfono móvil.' },
    { id: 7, control: 'tmd_7', texto: 'Gasto más dinero o tiempo en el teléfono móvil (llamadas, mensajes, WhatsApp, chat) del que me había previsto.' },
    { id: 8, control: 'tmd_8', texto: 'Cuando me aburro, utilizo el teléfono móvil.' },
    { id: 9, control: 'tmd_9', texto: 'Utilizo el teléfono móvil (llamadas, mensajes, WhatsApp, chat) en situaciones que no son apropiadas, como comiendo o conversando con otras personas.' },
    { id: 10, control: 'tmd_10', texto: 'Me han recriminado por el gasto o por el excesivo tiempo que dedico al teléfono móvil.' }
  ];

  // Bloque 2: Grado de Acuerdo (Q11 a Q22, escala 0 a 4: 0=Totalmente en desacuerdo, 1=Algo en desacuerdo, 2=Neutral, 3=Algo de acuerdo, 4=Totalmente de acuerdo)
  preguntasTMDAcuerdo = [
    { id: 11, control: 'tmd_11', texto: 'Cuando llevo un tiempo sin utilizar el móvil, siento la necesidad de llamar a alguien, enviar un mensaje o usar WhatsApp.' },
    { id: 12, control: 'tmd_12', texto: 'Desde que tengo teléfono móvil he aumentado el número de llamadas o comunicaciones que realizo.' },
    { id: 13, control: 'tmd_13', texto: 'Si se me estropeara el móvil durante un periodo largo y tardaran en arreglarlo, me sentiría muy mal.' },
    { id: 14, control: 'tmd_14', texto: 'Cada vez necesito utilizar con mayor frecuencia el teléfono móvil.' },
    { id: 15, control: 'tmd_15', texto: 'Si no tengo el teléfono móvil conmigo me encuentro mal.' },
    { id: 16, control: 'tmd_16', texto: 'Cuando tengo el teléfono móvil en la mano no puedo dejar de utilizarlo.' },
    { id: 17, control: 'tmd_17', texto: 'Desde que tengo teléfono móvil he aumentado el número de mensajes, WhatsApp o chats que envío.' },
    { id: 18, control: 'tmd_18', texto: 'Nada más levantarme por la mañana, lo primero que hago es ver si me han llamado o si me han enviado un mensaje o WhatsApp.' },
    { id: 19, control: 'tmd_19', texto: 'Gasto más recursos o tiempo en el móvil ahora que cuando lo adquirí al principio.' },
    { id: 20, control: 'tmd_20', texto: 'No creo que pudiera aguantar una semana sin teléfono móvil.' },
    { id: 21, control: 'tmd_21', texto: 'Cuando me siento solo(a), hago una llamada, envío un mensaje o escribo por WhatsApp a alguien.' },
    { id: 22, control: 'tmd_22', texto: 'Ahora mismo agarraría el móvil y enviaría un mensaje, haría una llamada o revisaría WhatsApp.' }
  ];

  // Escala de Ansiedad Rasgo — STAI-Rasgo (Spielberger, ítems 21 a 40 del cuestionario oficial STAI, escala 1 a 4)
  // 1=Casi nunca, 2=A veces, 3=A menudo, 4=Casi siempre
  preguntasSTAIRasgo = [
    { id: 21, control: 'stai_21', texto: 'Me siento bien / agradable.' },
    { id: 22, control: 'stai_22', texto: 'Me siento nervioso(a) e inquieto(a).' },
    { id: 23, control: 'stai_23', texto: 'Me siento satisfecho(a) conmigo mismo(a).' },
    { id: 24, control: 'stai_24', texto: 'Quisiera ser tan feliz como otros parecen serlo.' },
    { id: 25, control: 'stai_25', texto: 'Me siento como si fallara o fracasara.' },
    { id: 26, control: 'stai_26', texto: 'Me siento descansado(a).' },
    { id: 27, control: 'stai_27', texto: 'Soy una persona tranquila, serena y sosegada.' },
    { id: 28, control: 'stai_28', texto: 'Siento que las dificultades se acumulan tanto que no puedo superarlas.' },
    { id: 29, control: 'stai_29', texto: 'Me preocupo demasiado por cosas que en realidad no tienen importancia.' },
    { id: 30, control: 'stai_30', texto: 'Soy feliz.' },
    { id: 31, control: 'stai_31', texto: 'Tengo pensamientos que me perturban o incomodan.' },
    { id: 32, control: 'stai_32', texto: 'Me falta confianza en mí mismo(a).' },
    { id: 33, control: 'stai_33', texto: 'Me siento seguro(a).' },
    { id: 34, control: 'stai_34', texto: 'Tomo decisiones fácilmente.' },
    { id: 35, control: 'stai_35', texto: 'Me siento insuficiente o inadecuado(a).' },
    { id: 36, control: 'stai_36', texto: 'Estoy contento(a) y conforme.' },
    { id: 37, control: 'stai_37', texto: 'Algún pensamiento sin importancia pasa por mi mente y me molesta.' },
    { id: 38, control: 'stai_38', texto: 'Me afectan tanto los desengaños que no puedo olvidarlos.' },
    { id: 39, control: 'stai_39', texto: 'Soy una persona estable.' },
    { id: 40, control: 'stai_40', texto: 'Entro en un estado de tensión o agitación cuando pienso en mis preocupaciones recientes.' }
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
    // Punto 1: Todas las opciones inician en null (sin valores premarcados), obligando al estudiante a responder cada ítem
    const controlesPaso1: { [key: string]: any } = {};
    for (let i = 1; i <= 22; i++) {
      controlesPaso1[`tmd_${i}`] = [null, [Validators.required, Validators.min(0), Validators.max(4)]];
    }
    for (let j = 21; j <= 40; j++) {
      controlesPaso1[`stai_${j}`] = [null, [Validators.required, Validators.min(1), Validators.max(4)]];
    }
    this.formPaso1 = this.fb.group(controlesPaso1);

    // Paso 2: 15 variables de Hábitos Digitales y Contexto Sociofamiliar (SocioDemo, SIN molestias musculoesqueléticas)
    this.formPaso2 = this.fb.group({
      tipoDispositivo: [2, [Validators.required, Validators.min(1), Validators.max(4)]],
      tiempoUsoDiario: [3, [Validators.required, Validators.min(1), Validators.max(4)]],
      lugarUsoFrecuente: [2, [Validators.required, Validators.min(1), Validators.max(4)]],
      tipoAccesoRed: [3, [Validators.required, Validators.min(1), Validators.max(3)]],
      reglasHogarUso: [2, [Validators.required, Validators.min(1), Validators.max(4)]],
      percepcionDiscriminacion: [1, [Validators.required, Validators.min(1), Validators.max(4)]],
      discusionesUsoCelular: [2, [Validators.required, Validators.min(1), Validators.max(4)]],
      appMensajeria: [2, [Validators.required]],
      appVideojuegos: [1, [Validators.required]],
      appRedesSociales: [2, [Validators.required]],
      appMusica: [2, [Validators.required]],
      appVideos: [2, [Validators.required]],
      appGestionTiempo: [2, [Validators.required]],
      appCamara: [2, [Validators.required]],
      appCorreo: [2, [Validators.required]]
    });

    this.formPaso3 = this.fb.group({
      textoLibre: ['', [Validators.required, Validators.minLength(10)]]
    });

    this.verificarProgresoGuardado();
  }

  get cantidadRespondidasPaso1(): number {
    if (!this.formPaso1) return 0;
    const vals = this.formPaso1.value;
    return Object.keys(vals).filter(k => vals[k] !== null && vals[k] !== undefined).length;
  }

  verificarProgresoGuardado(): void {
    const raw = localStorage.getItem('serana_evaluacion_progreso_v2');
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
    localStorage.setItem('serana_evaluacion_progreso_v2', JSON.stringify(data));
    if (notificar) {
      this.snackBar.open('Progreso guardado exitosamente (US-06). Puedes salir y retomar cuando desees.', 'Aceptar', { duration: 3500 });
    }
  }

  guardarYSalir(): void {
    this.guardarProgresoLocal(true);
    this.router.navigate(['/mis-evaluaciones']);
  }

  descartarProgreso(): void {
    localStorage.removeItem('serana_evaluacion_progreso_v2');
    this.progresoRecuperado = false;
    this.reiniciarEvaluacion();
    this.snackBar.open('Progreso descartado. Comenzando nueva evaluación desde el inicio.', 'Aceptar', { duration: 2500 });
  }

  // PASO 1 -> PASO 2: Enviar las 22 respuestas reales de TMD (0..4) y 20 de STAI-Rasgo (1..4) sin conversiones simuladas
  guardarPaso1(): void {
    if (this.formPaso1.invalid) {
      this.snackBar.open('Por favor, responde las 42 preguntas antes de continuar.', 'Aceptar', { duration: 3000 });
      return;
    }

    this.cargando = true;
    const v = this.formPaso1.value;
    const tmdRespuestas: number[] = [];
    for (let i = 1; i <= 22; i++) {
      tmdRespuestas.push(Number(v[`tmd_${i}`]));
    }
    const staiRasgoRespuestas: number[] = [];
    for (let j = 21; j <= 40; j++) {
      staiRasgoRespuestas.push(Number(v[`stai_${j}`]));
    }

    const bodyPaso1 = {
      tmdRespuestas,
      staiRasgoRespuestas
    };

    this.evaluacionService.paso1Psicometrico(bodyPaso1).subscribe({
      next: (res) => {
        this.cargando = false;
        this.evaluacionId = res.evaluacionId;
        this.pasoActual = 2;
        this.guardarProgresoLocal();
        this.snackBar.open('Respuestas psicométricas reales guardadas (TMD y STAI-Rasgo). Paso 2 habilitado.', 'Aceptar', { duration: 2500 });
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error Paso 1:', err);
        const msg = err.error?.message || 'Error al guardar cuestionario psicométrico.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
      }
    });
  }

  // PASO 2 -> PASO 3: Enviar las 15 variables reales de Hábitos Digitales SocioDemo
  guardarPaso2(): void {
    if (this.formPaso2.invalid || !this.evaluacionId) return;

    this.cargando = true;
    const v = this.formPaso2.value;
    const bodyPaso2 = {
      tipoDispositivo: Number(v.tipoDispositivo),
      tiempoUsoDiario: Number(v.tiempoUsoDiario),
      lugarUsoFrecuente: Number(v.lugarUsoFrecuente),
      tipoAccesoRed: Number(v.tipoAccesoRed),
      reglasHogarUso: Number(v.reglasHogarUso),
      percepcionDiscriminacion: Number(v.percepcionDiscriminacion),
      discusionesUsoCelular: Number(v.discusionesUsoCelular),
      appMensajeria: Number(v.appMensajeria),
      appVideojuegos: Number(v.appVideojuegos),
      appRedesSociales: Number(v.appRedesSociales),
      appMusica: Number(v.appMusica),
      appVideos: Number(v.appVideos),
      appGestionTiempo: Number(v.appGestionTiempo),
      appCamara: Number(v.appCamara),
      appCorreo: Number(v.appCorreo)
    };

    this.evaluacionService.paso2Habitos(this.evaluacionId, bodyPaso2).subscribe({
      next: () => {
        this.cargando = false;
        this.pasoActual = 3;
        this.guardarProgresoLocal();
        this.snackBar.open('Hábitos de uso digital y contexto sociofamiliar registrados. Paso final habilitado.', 'Aceptar', { duration: 2500 });
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error Paso 2:', err);
        const msg = err.error?.message || 'Error al registrar hábitos digitales.';
        this.snackBar.open(msg, 'Cerrar', { duration: 3500 });
      }
    });
  }

  // PASO 3 -> RESULTADO EXPERIMENTAL DE ORIENTACIÓN (Desenlace STAI-Estado)
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
        localStorage.removeItem('serana_evaluacion_progreso_v2');
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
    localStorage.removeItem('serana_evaluacion_progreso_v2');
    const resetPaso1: { [key: string]: any } = {};
    for (let i = 1; i <= 22; i++) resetPaso1[`tmd_${i}`] = null;
    for (let j = 21; j <= 40; j++) resetPaso1[`stai_${j}`] = null;
    this.formPaso1.reset(resetPaso1);
    this.formPaso2.reset({
      tipoDispositivo: 2,
      tiempoUsoDiario: 3,
      lugarUsoFrecuente: 2,
      tipoAccesoRed: 3,
      reglasHogarUso: 2,
      percepcionDiscriminacion: 1,
      discusionesUsoCelular: 2,
      appMensajeria: 2,
      appVideojuegos: 1,
      appRedesSociales: 2,
      appMusica: 2,
      appVideos: 2,
      appGestionTiempo: 2,
      appCamara: 2,
      appCorreo: 2
    });
    this.formPaso3.reset({ textoLibre: '' });
  }
}

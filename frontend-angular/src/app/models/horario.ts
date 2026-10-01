// Refleja la entidad real Horario del backend
export interface Horario {
  id: number;
  link: string;
  fecha: string;       // 'YYYY-MM-DD'
  horaInicio: string;  // 'HH:mm:ss'
  horaFin: string;     // 'HH:mm:ss'
  disponible: boolean;
}

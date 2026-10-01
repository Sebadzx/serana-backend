// Refleja la entidad real Sesion del backend
export interface Sesion {
  id: number;
  fecha: string;      // 'YYYY-MM-DD'
  hora: string;        // 'HH:mm:ss'
  tipoSesion: string;
  estadoSesion: string;
  comentario: string;
}

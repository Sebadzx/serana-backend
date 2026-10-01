import { Component } from '@angular/core';
import { RecursoEducativo } from '../../../models/recurso-educativo';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RecursoEducativoService } from '../../../services/recurso-educativo-service';

@Component({
  selector: 'app-list-recursos-educativos',
  standalone: false,
  templateUrl: './list-recursos-educativos.html',
  styleUrl: './list-recursos-educativos.css',
})
export class ListRecursosEducativos {
    recursos: RecursoEducativo[] = [];
  searchQuery: string = '';
  selectedChip: string = 'Todos';
  chips: string[] = ['Todos', 'Ansiedad', 'Estrés', 'Autoestima', 'Sueño', 'Mindfulness'];

  constructor(private recursoEducativoService: RecursoEducativoService, private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.CargaLista();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.searchQuery = filterValue.trim().toLowerCase();
  }

  selectChip(chip: string) {
    this.selectedChip = chip;
  }

  get filteredRecursos(): RecursoEducativo[] {
    return this.recursos.filter(r => {
      const matchesSearch = !this.searchQuery || 
        r.titulo.toLowerCase().includes(this.searchQuery) ||
        r.tipoContenido.toLowerCase().includes(this.searchQuery);
      
      const matchesChip = this.selectedChip === 'Todos' || 
        r.titulo.toLowerCase().includes(this.selectedChip.toLowerCase());
        
      return matchesSearch && matchesChip;
    });
  }

  getVideos(): RecursoEducativo[] {
    return this.filteredRecursos.filter(r => r.tipoContenido.toLowerCase().includes('video'));
  }

  getArticulos(): RecursoEducativo[] {
    return this.filteredRecursos.filter(r => 
      r.tipoContenido.toLowerCase().includes('artículo') || 
      r.tipoContenido.toLowerCase().includes('articulo') ||
      (!r.tipoContenido.toLowerCase().includes('video') && !r.tipoContenido.toLowerCase().includes('audio'))
    );
  }

  Eliminar(id: number) {
    this.recursoEducativoService.delete(id).subscribe({
      next: () => {
        this.snackBar.open("Se eliminó el recurso educativo con Id:" + id.toString(), "", { duration: 2000 });
        this.CargaLista();
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  CargaLista() {
    this.recursoEducativoService.listAll().subscribe({
      next: (data: RecursoEducativo[]) => {
        this.recursos = data;
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  getImagen(recurso: RecursoEducativo): string {
    const titulo = recurso.titulo.toLowerCase();
    if (recurso.tipoContenido.toLowerCase().includes('video')) {
      if (titulo.includes('ansiedad') || titulo.includes('respiración') || titulo.includes('respiracion')) {
        return 'video_respiracion.png';
      } else if (titulo.includes('mindfulness') && recurso.id % 2 === 1) {
        return 'video_mindfulness_1.png';
      } else {
        return 'video_mindfulness_2.png';
      }
    } else {
      if (titulo.includes('estrés') || titulo.includes('estres') || titulo.includes('diario')) {
        return 'article_estres_1.png';
      } else if (titulo.includes('reparador') && recurso.id % 2 === 0) {
        return 'article_estres_2.png';
      } else if (titulo.includes('sueño') || titulo.includes('sueno')) {
        return 'article_sueno_1.png';
      } else {
        return 'article_sueno_2.png';
      }
    }
  }

  getDuration(recurso: RecursoEducativo): string {
    if (recurso.id === 1) return '02:43';
    if (recurso.id === 2) return '05:57';
    const mins = (recurso.id * 3) % 10;
    const secs = (recurso.id * 17) % 60;
    return `0${mins}:${secs < 10 ? '0' + secs : secs}`;
  }

  getReadingTime(recurso: RecursoEducativo): string {
    const mins = (recurso.id * 2) % 7 + 3;
    return `${mins} min de lectura`;
  }
}

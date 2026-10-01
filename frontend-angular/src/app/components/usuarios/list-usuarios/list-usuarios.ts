import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UsuarioDTO } from '../../../models/usuarioDTO';
import { UsuarioService } from '../../../services/usuario-service';

@Component({
  selector: 'app-list-usuarios',
  standalone: false,
  templateUrl: './list-usuarios.html',
  styleUrl: './list-usuarios.css',
})
export class ListUsuarios implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['id', 'city', 'gameDate', 'opciones'];
  dsListaUsuarios = new MatTableDataSource<UsuarioDTO>();

  @ViewChild(MatPaginator) sgPaginator!: MatPaginator;
  
  constructor (private usuarioService:UsuarioService, private snackBar: MatSnackBar){}

   ngOnInit(){
    this.CargaLista();
    this.dsListaUsuarios.filter='';
  }
 

  ngAfterViewInit() {
    this.dsListaUsuarios.paginator = this.sgPaginator;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dsListaUsuarios.filter = filterValue.trim().toLowerCase();
  }
  
   Eliminar(id: number){
                this.usuarioService.delete(id).subscribe({
                next:()=>{
                  this.snackBar.open("Se eliminó usuario con Id:"+id.toString(),"",{duration: 1000});
                  this.CargaLista();
                },
                error:(err: any)=>{
                  console.log(err);
                }
              })
        }

   CargaLista() {
     this.usuarioService.listAll().subscribe({
      next:(data:UsuarioDTO[])=>{        
        this.dsListaUsuarios.data = data;
        this.dsListaUsuarios.filter = '';
      },
      error:(err: any)=>{
        console.log(err);
      }
    });
    }   
}

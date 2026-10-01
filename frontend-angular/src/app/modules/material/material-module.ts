
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatRadioModule } from '@angular/material/radio';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSliderModule } from '@angular/material/slider';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatTableModule,
    MatIconModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatSnackBarModule,
    MatRadioModule,
    MatCardModule,
    MatCheckboxModule,
    MatSelectModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatDialogModule,
    MatPaginatorModule,
    MatNativeDateModule,
    MatSlideToggleModule, 
    MatSliderModule      
  ],
  exports:[
    MatTableModule,
    MatIconModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatSnackBarModule,
    MatRadioModule,
    MatCardModule,
    MatCheckboxModule,
    MatSelectModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatDialogModule,
    MatPaginatorModule,
    MatNativeDateModule,
    MatSlideToggleModule, // <-- Añadido
    MatSliderModule       // <-- Añadido
  ]
})
export class MaterialModule {}
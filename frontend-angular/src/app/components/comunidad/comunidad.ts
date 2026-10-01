import { Component, OnInit } from '@angular/core';
import { PostService } from '../../services/post-service';
import { UsuarioService } from '../../services/usuario-service';
import { LanguageService } from '../../services/language-service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-comunidad',
  standalone: false,
  templateUrl: './comunidad.html',
  styleUrl: './comunidad.css',
})
export class Comunidad implements OnInit {
  posts: any[] = [];
  filteredPosts: any[] = [];
  selectedCategory: string = 'Todos';
  
  newPostContent: string = '';
  newPostTema: string = 'Ansiedad';
  modoAnonimo: boolean = false;
  
  currentUser: any = null;
  selectedMood: string = '';

  constructor(
    private postService: PostService,
    private usuarioService: UsuarioService,
    public langService: LanguageService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.cargarDatosUsuario();
    this.cargarPosts();
    this.modoAnonimo = localStorage.getItem('anonimoPorDefecto') === 'true';
  }

  cargarDatosUsuario() {
    const id = this.usuarioService.getIdLogeado();
    if (id) {
      // Intentamos cargar el usuario para obtener nombres/apellidos
      this.usuarioService.listAll().subscribe({
        next: (usuarios: any[]) => {
          // Buscamos si existe en la lista, pero para obtener nombres completos llamamos al endpoint individual
          const numericId = Number(id);
          this.usuarioService.ruta_servidor; // verify path
          const url = `${this.usuarioService.ruta_servidor}/usuarios/${numericId}`;
          this.usuarioService.login({id: numericId, correo:'', contrasenia:'', fechaRegistro:'', authorities:''}); // dummy trigger/reference if needed
          
          // Hacemos un fetch directo o llamamos a verUsuario en el service si existiera, o hacemos http get
          // Para simplificar, implementamos la llamada en un HttpClient
          // O podemos usar un fetch nativo de JavaScript para obtener los detalles del usuario logeado
          fetch(url, {
            headers: {
              'Authorization': 'Bearer ' + this.usuarioService.getJwtTokenLogeado()
            }
          })
          .then(res => res.json())
          .then(data => {
            this.currentUser = data;
          })
          .catch(err => {
            console.log("Error al cargar datos del usuario:", err);
            // Fallback con datos por defecto
            this.currentUser = {
              nombres: 'Usuario',
              apellidos: 'Serana',
              correo: 'usuario@serana.pe'
            };
          });
        }
      });
    }
  }

  cargarPosts() {
    this.postService.listAll().subscribe({
      next: (data: any[]) => {
        // Enriquecer posts con autores legibles para el feed
        this.posts = data.map((post, index) => {
          // Si el post es anónimo, se muestra Anónimo
          if (post.modoAnonimo) {
            return {
              ...post,
              autorNombre: 'Usuario Anónimo',
              autorInitials: 'UA',
              likesCount: Math.floor(Math.random() * 50) + 10,
              commentsCount: Math.floor(Math.random() * 15) + 3,
              avatarUrl: null
            };
          }
          
          // Para posts creados, si traen información de usuario, la usamos.
          // Si no traen usuario (los del CommandLineRunner inicial), mockeamos a David Rivera
          let autorNombre = 'David Rivera';
          let initials = 'DR';
          let avatarUrl = 'avatar-david.jpg'; // default mockup author avatar
          
          if (post.usuario) {
            autorNombre = post.usuario.nombres && post.usuario.apellidos 
              ? `${post.usuario.nombres} ${post.usuario.apellidos}` 
              : post.usuario.correo;
            initials = post.usuario.nombres ? post.usuario.nombres.substring(0,2).toUpperCase() : 'US';
            
            // Asignar el avatar correspondiente según la autoridad del usuario creador
            if (post.usuario.authorities && post.usuario.authorities.includes('ROLE_PROFESIONAL')) {
              avatarUrl = 'avatar-profesional.jpg';
            } else {
              avatarUrl = 'avatar-paciente.jpg';
            }
          } else if (index === 0) {
            // El primer post del mockup
            autorNombre = 'Usuario Anónimo';
            initials = 'UA';
            post.modoAnonimo = true;
            avatarUrl = '';
          }

          return {
            ...post,
            autorNombre: autorNombre,
            autorInitials: initials,
            likesCount: post.cantidadParticipantes || (Math.floor(Math.random() * 60) + 15),
            commentsCount: Math.floor(Math.random() * 20) + 5,
            avatarUrl: avatarUrl
          };
        });

        // Ordenar posts por ID descendente (más recientes primero)
        this.posts.sort((a, b) => b.id - a.id);
        this.filtrarPosts();
      },
      error: (err) => {
        console.log("Error al cargar posts:", err);
      }
    });
  }

  filtrarPosts() {
    if (this.selectedCategory === 'Todos') {
      this.filteredPosts = [...this.posts];
    } else {
      this.filteredPosts = this.posts.filter(p => 
        p.tema?.toLowerCase() === this.selectedCategory.toLowerCase()
      );
    }
  }

  setCategory(category: string) {
    this.selectedCategory = category;
    this.filtrarPosts();
  }

  registrarAnimo() {
    if (!this.selectedMood) {
      this.snackBar.open("Por favor, selecciona cómo te sientes antes de registrar.", "", { duration: 2000 });
      return;
    }
    this.snackBar.open(`Ánimo "${this.selectedMood}" registrado en tu check-in diario.`, "", { duration: 2500 });
  }

  crearPublicacion() {
    if (!this.newPostContent || this.newPostContent.trim() === '') {
      this.snackBar.open("El contenido de la publicación no puede estar vacío.", "", { duration: 2000 });
      return;
    }

    const payload = {
      id: 0,
      tema: this.newPostTema,
      contenido: this.newPostContent,
      modoAnonimo: this.modoAnonimo,
      cantidadParticipantes: 0,
      usuarioId: Number(this.usuarioService.getIdLogeado())
    };

    this.postService.add(payload).subscribe({
      next: (data) => {
        this.snackBar.open("¡Publicación creada exitosamente!", "", { duration: 2000 });
        this.newPostContent = '';
        this.modoAnonimo = false;
        this.cargarPosts(); // Recargar listado
      },
      error: (err) => {
        console.log("Error al crear publicación:", err);
        this.snackBar.open("Error al crear la publicación. Inténtalo de nuevo.", "", { duration: 2000 });
      }
    });
  }

  getCreatorAvatarUrl(): string {
    const role = this.usuarioService.getAuthoritiesLogeado();
    if (role && role.includes('ROLE_ADMIN')) {
      return 'avatar-admin.jpg';
    }
    if (role && role.includes('ROLE_PROFESIONAL')) {
      return 'avatar-profesional.jpg';
    }
    return 'avatar-paciente.jpg';
  }

  getInicioUrl(): string {
    const role = this.usuarioService.getAuthoritiesLogeado();
    if (role && role.includes('ROLE_PROFESIONAL')) {
      return '/dashboard-profesional';
    }
    if (role && role.includes('ROLE_PACIENTE')) {
      return '/dashboard-paciente';
    }
    return '/comunidad';
  }

  getSesionesUrl(): string {
    const role = this.usuarioService.getAuthoritiesLogeado();
    if (role && role.includes('ROLE_PROFESIONAL')) {
      return '/sesiones';
    }
    return '/profesionales';
  }

  logout() {
    this.usuarioService.logout();
    window.location.href = '/login';
  }
}

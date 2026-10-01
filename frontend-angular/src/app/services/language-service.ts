import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  currentLanguage: string = 'Español (España)';

  constructor() {
    const saved = localStorage.getItem('idioma');
    if (saved) {
      this.currentLanguage = saved;
    }
  }

  setLanguage(lang: string) {
    this.currentLanguage = lang;
    localStorage.setItem('idioma', lang);
  }

  getLanguage(): string {
    return this.currentLanguage;
  }

  // Dictionary containing translations for all key components
  private dictionary: any = {
    'Español (España)': {
      // Sidebar
      'nav.inicio': 'Inicio',
      'nav.comunidad': 'Comunidad',
      'nav.sesiones': 'Sesiones',
      'nav.recursos': 'Recursos',
      'nav.perfil': 'Perfil',
      'nav.configuracion': 'Configuración',
      'brand.subtitle': 'Santuario Digital',

      // Header
      'header.search_community': 'Buscar en la comunidad...',
      'header.search_professionals': 'Buscar Profesionales',

      // Footer
      'footer.slogan': 'Tu santuario digital para la salud mental y el bienestar emocional.',
      'footer.explore': 'Explorar',
      'footer.privacy': 'Privacidad',
      'footer.terms': 'Términos',
      'footer.help': 'Ayuda',
      'footer.contact': 'Contacto',
      'footer.follow': 'Síguenos',
      'footer.copyright': '© 2024 Serana. Todos los derechos reservados.',
      'footer.made_with_love': 'Hecho con empatía para tu bienestar',

      // Comunidad Page
      'comunidad.title': 'Comunidad',
      'comunidad.textarea_placeholder': '¿Cómo te sientes hoy? Comparte tus pensamientos...',
      'comunidad.mood_label': 'Estado de ánimo:',
      'comunidad.category_label': 'Categoría:',
      'comunidad.post_btn': 'Publicar',
      'comunidad.rules_title': 'Reglas de la Comunidad',
      'comunidad.rules_intro': 'Para mantener un espacio seguro y de apoyo, por favor sigue estas normas:',
      'comunidad.rule_1': 'Sé empático y respetuoso con los demás.',
      'comunidad.rule_2': 'No compartas información personal de terceros.',
      'comunidad.rule_3': 'Reporta cualquier contenido inapropiado o dañino.',

      // Dashboard Profesional Page
      'dash.title': 'Dashboard de Inicio',
      'dash.patients_title': 'Pacientes Recientes',
      'dash.patient_role': 'Paciente de consulta',
      'dash.patient_seen': 'Última sesión:',
      'dash.specialists_title': 'Especialistas Destacados',
      'dash.quick_actions': 'Acciones Rápidas',
      'dash.shared_resources': 'Recursos Compartidos',
      'dash.action_new_session': 'Nueva Sesión',
      'dash.action_upload_resource': 'Subir Recurso',
      'dash.action_view_agenda': 'Ver Agenda',
      'dash.establecer_horario': 'Establecer Horario',
      'dash.horario_fecha': 'Fecha',
      'dash.horario_hora_inicio': 'Hora de inicio',
      'dash.horario_hora_fin': 'Hora de fin',
      'dash.horario_link': 'Enlace de videollamada (opcional)',
      'dash.horario_guardar': 'Guardar Horario',
      'dash.mis_horarios': 'Mis Horarios Registrados',
      'dash.sin_horarios_registrados': 'Aún no has registrado ningún horario.',
      'dash.horario_disponible': 'Disponible',
      'dash.horario_ocupado': 'Ocupado',
      'sesiones.mis_sesiones': 'Mis Sesiones',
      'sesiones.sin_perfil': 'Aún no tienes un registro en Personal Médico. Registra tu primer horario para activarlo.',
      'sesiones.sin_sesiones': 'Todavía no tienes sesiones programadas.',
      'sesiones.col_fecha': 'Fecha',
      'sesiones.col_hora': 'Hora',
      'sesiones.col_tipo': 'Tipo',
      'sesiones.col_estado': 'Estado',
      'sesiones.col_comentario': 'Comentario',
      'dash.mi_informacion': 'Mi Información Profesional',
      'dash.perfil_nombres': 'Nombres',
      'dash.perfil_apellidos': 'Apellidos',
      'dash.perfil_especialidad': 'Especialidad',
      'dash.perfil_lugartrabajo': 'Lugar de trabajo',
      'dash.perfil_guardar': 'Guardar Cambios',

      // Configuracion Page
      'config.title': 'Configuración',
      'config.appearance': 'Apariencia',
      'config.darkMode': 'Modo oscuro',
      'config.language': 'Idioma',
      'config.notifications': 'Notificaciones',
      'config.emailAlerts': 'Alertas por correo',
      'config.pushNotifications': 'Notificaciones push',
      'config.privacySecurity': 'Privacidad y Seguridad',
      'config.manageData': 'Gestionar datos',
      'config.changePassword': 'Cambiar contraseña',
      'config.anonymity': 'Anonimato',
      'config.anonymousDefault': 'Publicar como anónimo por defecto',

      // Buscar Profesionales
      'prof.page_title': 'Buscar Profesionales',
      'prof.search_placeholder': 'Buscar profesional, especialidad, o clínica...',
      'prof.filter_especialidad': 'Especialidad',
      'prof.filter_disponibilidad': 'Disponibilidad',
      'prof.filter_lugartrabajo': 'Lugar de trabajo',
      'prof.filter_modalidad': 'Modalidad Virtual',
      'prof.filter_precio': 'Precio',
      'prof.filtro_desarrollo': 'filtro en desarrollo',
      'prof.btn_ver_perfil': 'Ver perfil',
      'prof.btn_agendar': 'Agendar sesión',
      'prof.cargando': 'Cargando profesionales...',
      'prof.no_results': 'No se encontraron profesionales que coincidan con tu búsqueda.',
      'prof.no_encontrado': 'No se encontró información de este profesional.',
      'prof.volver': 'Volver a la búsqueda',
      'prof.calendario_title': 'Calendario de Disponibilidad',
      'prof.tiempo_disponible': 'Tiempo Disponible',
      'prof.sin_horarios': 'No hay horarios disponibles para este día. Elige otra fecha resaltada en el calendario.',
      'prof.biografia_title': 'Biografía',
      'prof.info_title': 'Información profesional',
      'prof.info_especialidad': 'Especialidad',
      'prof.info_lugartrabajo': 'Lugar de trabajo',
      'prof.selecciona_horario': 'Selecciona un horario disponible antes de reservar.',
      'prof.btn_reservar': 'Reservar sesión',

      // Reserva (placeholder)
      'reserva.title': 'Reserva de Sesión',
      'reserva.message': 'Estás a punto de reservar una sesión con',
      'reserva.coming_soon': 'Esta función está en desarrollo. ¡Pronto podrás confirmar tu cita aquí!',
      'reserva.confirmada': '¡Tu sesión ha sido reservada exitosamente! Recibirás el enlace de la videollamada antes de la cita.',
      'reserva.btn_volver': 'Volver al perfil'
    },
    'English': {
      // Sidebar
      'nav.inicio': 'Home',
      'nav.comunidad': 'Community',
      'nav.sesiones': 'Sessions',
      'nav.recursos': 'Resources',
      'nav.perfil': 'Profile',
      'nav.configuracion': 'Settings',
      'brand.subtitle': 'Digital Sanctuary',

      // Header
      'header.search_community': 'Search the community...',
      'header.search_professionals': 'Search Professionals',

      // Footer
      'footer.slogan': 'Your digital sanctuary for mental health and emotional well-being.',
      'footer.explore': 'Explore',
      'footer.privacy': 'Privacy',
      'footer.terms': 'Terms',
      'footer.help': 'Help',
      'footer.contact': 'Contact',
      'footer.follow': 'Follow Us',
      'footer.copyright': '© 2024 Serana. All rights reserved.',
      'footer.made_with_love': 'Made with empathy for your well-being',

      // Comunidad Page
      'comunidad.title': 'Community',
      'comunidad.textarea_placeholder': 'How are you feeling today? Share your thoughts...',
      'comunidad.mood_label': 'Mood:',
      'comunidad.category_label': 'Category:',
      'comunidad.post_btn': 'Publish',
      'comunidad.rules_title': 'Community Rules',
      'comunidad.rules_intro': 'To maintain a safe and supportive space, please follow these guidelines:',
      'comunidad.rule_1': 'Be empathetic and respectful to others.',
      'comunidad.rule_2': 'Do not share personal information of others.',
      'comunidad.rule_3': 'Report any inappropriate or harmful content.',

      // Dashboard Profesional Page
      'dash.title': 'Home Dashboard',
      'dash.patients_title': 'Recent Patients',
      'dash.patient_role': 'Consultation Patient',
      'dash.patient_seen': 'Last session:',
      'dash.specialists_title': 'Featured Specialists',
      'dash.quick_actions': 'Quick Actions',
      'dash.shared_resources': 'Shared Resources',
      'dash.action_new_session': 'New Session',
      'dash.action_upload_resource': 'Upload Resource',
      'dash.action_view_agenda': 'View Agenda',
      'dash.establecer_horario': 'Set Schedule',
      'dash.horario_fecha': 'Date',
      'dash.horario_hora_inicio': 'Start time',
      'dash.horario_hora_fin': 'End time',
      'dash.horario_link': 'Video call link (optional)',
      'dash.horario_guardar': 'Save Schedule',
      'dash.mis_horarios': 'My Registered Schedules',
      'dash.sin_horarios_registrados': 'You have not registered any schedule yet.',
      'dash.horario_disponible': 'Available',
      'dash.horario_ocupado': 'Booked',
      'sesiones.mis_sesiones': 'My Sessions',
      'sesiones.sin_perfil': 'You do not have a Medical Staff record yet. Register your first schedule to activate it.',
      'sesiones.sin_sesiones': 'You have no scheduled sessions yet.',
      'sesiones.col_fecha': 'Date',
      'sesiones.col_hora': 'Time',
      'sesiones.col_tipo': 'Type',
      'sesiones.col_estado': 'Status',
      'sesiones.col_comentario': 'Comment',
      'dash.mi_informacion': 'My Professional Information',
      'dash.perfil_nombres': 'First name',
      'dash.perfil_apellidos': 'Last name',
      'dash.perfil_especialidad': 'Specialty',
      'dash.perfil_lugartrabajo': 'Workplace',
      'dash.perfil_guardar': 'Save Changes',

      // Configuracion Page
      'config.title': 'Settings',
      'config.appearance': 'Appearance',
      'config.darkMode': 'Dark mode',
      'config.language': 'Language',
      'config.notifications': 'Notifications',
      'config.emailAlerts': 'Email alerts',
      'config.pushNotifications': 'Push notifications',
      'config.privacySecurity': 'Privacy & Security',
      'config.manageData': 'Manage data',
      'config.changePassword': 'Change password',
      'config.anonymity': 'Anonymity',
      'config.anonymousDefault': 'Post anonymously by default',

      // Find Professionals
      'prof.page_title': 'Find Professionals',
      'prof.search_placeholder': 'Search professional, specialty, or clinic...',
      'prof.filter_especialidad': 'Specialty',
      'prof.filter_disponibilidad': 'Availability',
      'prof.filter_lugartrabajo': 'Workplace',
      'prof.filter_modalidad': 'Virtual Modality',
      'prof.filter_precio': 'Price',
      'prof.filtro_desarrollo': 'filter under development',
      'prof.btn_ver_perfil': 'View profile',
      'prof.btn_agendar': 'Book session',
      'prof.cargando': 'Loading professionals...',
      'prof.no_results': 'No professionals matched your search.',
      'prof.no_encontrado': 'This professional could not be found.',
      'prof.volver': 'Back to search',
      'prof.calendario_title': 'Availability Calendar',
      'prof.tiempo_disponible': 'Available Time',
      'prof.sin_horarios': 'No time slots available for this day. Choose another highlighted date.',
      'prof.biografia_title': 'Biography',
      'prof.info_title': 'Professional information',
      'prof.info_especialidad': 'Specialty',
      'prof.info_lugartrabajo': 'Workplace',
      'prof.selecciona_horario': 'Select an available time slot before booking.',
      'prof.btn_reservar': 'Book session',

      // Booking (placeholder)
      'reserva.title': 'Session Booking',
      'reserva.message': 'You are about to book a session with',
      'reserva.coming_soon': 'This feature is under development. You will soon be able to confirm your appointment here!',
      'reserva.confirmada': 'Your session has been booked successfully! You will receive the video call link before the appointment.',
      'reserva.btn_volver': 'Back to profile'
    },
    'Français': {
      // Sidebar
      'nav.inicio': 'Accueil',
      'nav.comunidad': 'Communauté',
      'nav.sesiones': 'Sessions',
      'nav.recursos': 'Ressources',
      'nav.perfil': 'Profil',
      'nav.configuracion': 'Configuration',
      'brand.subtitle': 'Sanctuaire Numérique',

      // Header
      'header.search_community': 'Rechercher dans la communauté...',
      'header.search_professionals': 'Rechercher des professionnels',

      // Footer
      'footer.slogan': 'Votre sanctuaire numérique pour la santé mentale et le bien-être émotionnel.',
      'footer.explore': 'Explorer',
      'footer.privacy': 'Confidentialité',
      'footer.terms': 'Conditions',
      'footer.help': 'Aide',
      'footer.contact': 'Contact',
      'footer.follow': 'Suivez-nous',
      'footer.copyright': '© 2024 Serana. Tous droits réservés.',
      'footer.made_with_love': 'Fait avec empathie pour votre bien-être',

      // Comunidad Page
      'comunidad.title': 'Communauté',
      'comunidad.textarea_placeholder': 'Comment vous sentez-vous aujourd\'hui ? Partagez vos pensées...',
      'comunidad.mood_label': 'Humeur:',
      'comunidad.category_label': 'Catégorie:',
      'comunidad.post_btn': 'Publier',
      'comunidad.rules_title': 'Règles de la Communauté',
      'comunidad.rules_intro': 'Pour maintenir un espace sûr et bienveillant, veuillez suivre ces règles:',
      'comunidad.rule_1': 'Soyez empathique et respectueux envers les autres.',
      'comunidad.rule_2': 'Ne partagez pas d\'informations personnelles de tiers.',
      'comunidad.rule_3': 'Signalez tout contenu inapproprié ou nuisible.',

      // Dashboard Profesional Page
      'dash.title': 'Tableau de bord',
      'dash.patients_title': 'Patients Récents',
      'dash.patient_role': 'Patient de consultation',
      'dash.patient_seen': 'Dernière session:',
      'dash.specialists_title': 'Spécialistes Vedettes',
      'dash.quick_actions': 'Actions Rapides',
      'dash.shared_resources': 'Ressources Partagées',
      'dash.action_new_session': 'Nouvelle Session',
      'dash.action_upload_resource': 'Télécharger la ressource',
      'dash.action_view_agenda': 'Voir l\'agenda',
      'dash.establecer_horario': 'Définir un Horaire',
      'dash.horario_fecha': 'Date',
      'dash.horario_hora_inicio': 'Heure de début',
      'dash.horario_hora_fin': 'Heure de fin',
      'dash.horario_link': 'Lien de vidéoconférence (optionnel)',
      'dash.horario_guardar': 'Enregistrer l\'horaire',
      'dash.mis_horarios': 'Mes Horaires Enregistrés',
      'dash.sin_horarios_registrados': 'Vous n\'avez encore enregistré aucun horaire.',
      'dash.horario_disponible': 'Disponible',
      'dash.horario_ocupado': 'Réservé',
      'sesiones.mis_sesiones': 'Mes Sessions',
      'sesiones.sin_perfil': 'Vous n\'avez pas encore de fiche dans le Personnel Médical. Enregistrez votre premier horaire pour l\'activer.',
      'sesiones.sin_sesiones': 'Vous n\'avez encore aucune session programmée.',
      'sesiones.col_fecha': 'Date',
      'sesiones.col_hora': 'Heure',
      'sesiones.col_tipo': 'Type',
      'sesiones.col_estado': 'Statut',
      'sesiones.col_comentario': 'Commentaire',
      'dash.mi_informacion': 'Mes Informations Professionnelles',
      'dash.perfil_nombres': 'Prénoms',
      'dash.perfil_apellidos': 'Noms de famille',
      'dash.perfil_especialidad': 'Spécialité',
      'dash.perfil_lugartrabajo': 'Lieu de travail',
      'dash.perfil_guardar': 'Enregistrer les modifications',

      // Configuracion Page
      'config.title': 'Configuration',
      'config.appearance': 'Apparence',
      'config.darkMode': 'Mode sombre',
      'config.language': 'Langue',
      'config.notifications': 'Notifications',
      'config.emailAlerts': 'Alertes par e-mail',
      'config.pushNotifications': 'Notifications push',
      'config.privacySecurity': 'Confidentialité et Sécurité',
      'config.manageData': 'Gérer les données',
      'config.changePassword': 'Modifier le mot de passe',
      'config.anonymity': 'Anonymat',
      'config.anonymousDefault': 'Publier anonymement par défaut',

      // Rechercher des Professionnels
      'prof.page_title': 'Rechercher des Professionnels',
      'prof.search_placeholder': 'Rechercher un professionnel, une spécialité ou une clinique...',
      'prof.filter_especialidad': 'Spécialité',
      'prof.filter_disponibilidad': 'Disponibilité',
      'prof.filter_lugartrabajo': 'Lieu de travail',
      'prof.filter_modalidad': 'Modalité Virtuelle',
      'prof.filter_precio': 'Prix',
      'prof.filtro_desarrollo': 'filtre en développement',
      'prof.btn_ver_perfil': 'Voir le profil',
      'prof.btn_agendar': 'Planifier une session',
      'prof.cargando': 'Chargement des professionnels...',
      'prof.no_results': 'Aucun professionnel ne correspond à votre recherche.',
      'prof.no_encontrado': 'Ce professionnel est introuvable.',
      'prof.volver': 'Retour à la recherche',
      'prof.calendario_title': 'Calendrier de Disponibilité',
      'prof.tiempo_disponible': 'Temps Disponible',
      'prof.sin_horarios': 'Aucun créneau disponible ce jour. Choisissez une autre date en surbrillance.',
      'prof.biografia_title': 'Biographie',
      'prof.info_title': 'Informations professionnelles',
      'prof.info_especialidad': 'Spécialité',
      'prof.info_lugartrabajo': 'Lieu de travail',
      'prof.selecciona_horario': 'Sélectionnez un créneau disponible avant de réserver.',
      'prof.btn_reservar': 'Réserver une session',

      // Réservation (placeholder)
      'reserva.title': 'Réservation de Session',
      'reserva.message': 'Vous êtes sur le point de réserver une session avec',
      'reserva.coming_soon': 'Cette fonctionnalité est en cours de développement. Vous pourrez bientôt confirmer votre rendez-vous ici !',
      'reserva.confirmada': 'Votre session a été réservée avec succès ! Vous recevrez le lien de l\'appel vidéo avant le rendez-vous.',
      'reserva.btn_volver': 'Retour au profil'
    },
    'Português': {
      // Sidebar
      'nav.inicio': 'Início',
      'nav.comunidad': 'Comunidade',
      'nav.sesiones': 'Sessões',
      'nav.recursos': 'Recursos',
      'nav.perfil': 'Perfil',
      'nav.configuracion': 'Configurações',
      'brand.subtitle': 'Santuário Digital',

      // Header
      'header.search_community': 'Buscar na comunidade...',
      'header.search_professionals': 'Buscar profissionais',

      // Footer
      'footer.slogan': 'Seu santuário digital para saúde mental e bem-estar emocional.',
      'footer.explore': 'Explorar',
      'footer.privacy': 'Privacidade',
      'footer.terms': 'Termos',
      'footer.help': 'Ajuda',
      'footer.contact': 'Contato',
      'footer.follow': 'Siga-nos',
      'footer.copyright': '© 2024 Serana. Todos os direitos reservados.',
      'footer.made_with_love': 'Feito com empatia para o seu bem-estar',

      // Comunidad Page
      'comunidad.title': 'Comunidade',
      'comunidad.textarea_placeholder': 'Como você está se sentindo hoje? Compartilhe seus pensamentos...',
      'comunidad.mood_label': 'Humor:',
      'comunidad.category_label': 'Categoria:',
      'comunidad.post_btn': 'Publicar',
      'comunidad.rules_title': 'Regras da Comunidade',
      'comunidad.rules_intro': 'Para manter um espaço seguro e acolhedor, por favor siga estas normas:',
      'comunidad.rule_1': 'Seja empático e respeitoso com os outros.',
      'comunidad.rule_2': 'Não compartilhe informações pessoais de terceiros.',
      'comunidad.rule_3': 'Denuncie qualquer conteúdo inadequado ou prejudicial.',

      // Dashboard Profesional Page
      'dash.title': 'Painel de Controle',
      'dash.patients_title': 'Pacientes Recentes',
      'dash.patient_role': 'Paciente de consulta',
      'dash.patient_seen': 'Última sessão:',
      'dash.specialists_title': 'Especialistas em Destaque',
      'dash.quick_actions': 'Ações Rápidas',
      'dash.shared_resources': 'Recursos Compartilhados',
      'dash.action_new_session': 'Nova Sessão',
      'dash.action_upload_resource': 'Enviar Recurso',
      'dash.action_view_agenda': 'Ver Agenda',
      'dash.establecer_horario': 'Definir Horário',
      'dash.horario_fecha': 'Data',
      'dash.horario_hora_inicio': 'Hora de início',
      'dash.horario_hora_fin': 'Hora de término',
      'dash.horario_link': 'Link da videochamada (opcional)',
      'dash.horario_guardar': 'Salvar Horário',
      'dash.mis_horarios': 'Meus Horários Registrados',
      'dash.sin_horarios_registrados': 'Você ainda não registrou nenhum horário.',
      'dash.horario_disponible': 'Disponível',
      'dash.horario_ocupado': 'Ocupado',
      'sesiones.mis_sesiones': 'Minhas Sessões',
      'sesiones.sin_perfil': 'Você ainda não tem um registro em Pessoal Médico. Registre seu primeiro horário para ativá-lo.',
      'sesiones.sin_sesiones': 'Você ainda não tem sessões agendadas.',
      'sesiones.col_fecha': 'Data',
      'sesiones.col_hora': 'Hora',
      'sesiones.col_tipo': 'Tipo',
      'sesiones.col_estado': 'Status',
      'sesiones.col_comentario': 'Comentário',
      'dash.mi_informacion': 'Minhas Informações Profissionais',
      'dash.perfil_nombres': 'Nomes',
      'dash.perfil_apellidos': 'Sobrenomes',
      'dash.perfil_especialidad': 'Especialidade',
      'dash.perfil_lugartrabajo': 'Local de trabalho',
      'dash.perfil_guardar': 'Salvar Alterações',

      // Configuracion Page
      'config.title': 'Configurações',
      'config.appearance': 'Aparência',
      'config.darkMode': 'Modo escuro',
      'config.language': 'Idioma',
      'config.notifications': 'Notificações',
      'config.emailAlerts': 'Alertas por e-mail',
      'config.pushNotifications': 'Notificações push',
      'config.privacySecurity': 'Privacidade e Segurança',
      'config.manageData': 'Gerenciar dados',
      'config.changePassword': 'Alterar senha',
      'config.anonymity': 'Anonimato',
      'config.anonymousDefault': 'Publicar como anônimo por padrão',

      // Buscar Profissionais
      'prof.page_title': 'Buscar Profissionais',
      'prof.search_placeholder': 'Buscar profissional, especialidade ou clínica...',
      'prof.filter_especialidad': 'Especialidade',
      'prof.filter_disponibilidad': 'Disponibilidade',
      'prof.filter_lugartrabajo': 'Local de trabalho',
      'prof.filter_modalidad': 'Modalidade Virtual',
      'prof.filter_precio': 'Preço',
      'prof.filtro_desarrollo': 'filtro em desenvolvimento',
      'prof.btn_ver_perfil': 'Ver perfil',
      'prof.btn_agendar': 'Agendar sessão',
      'prof.cargando': 'Carregando profissionais...',
      'prof.no_results': 'Nenhum profissional encontrado para sua busca.',
      'prof.no_encontrado': 'Este profissional não foi encontrado.',
      'prof.volver': 'Voltar à busca',
      'prof.calendario_title': 'Calendário de Disponibilidade',
      'prof.tiempo_disponible': 'Tempo Disponível',
      'prof.sin_horarios': 'Não há horários disponíveis neste dia. Escolha outra data destacada.',
      'prof.biografia_title': 'Biografia',
      'prof.info_title': 'Informações profissionais',
      'prof.info_especialidad': 'Especialidade',
      'prof.info_lugartrabajo': 'Local de trabalho',
      'prof.selecciona_horario': 'Selecione um horário disponível antes de reservar.',
      'prof.btn_reservar': 'Reservar sessão',

      // Reserva (placeholder)
      'reserva.title': 'Reserva de Sessão',
      'reserva.message': 'Você está prestes a reservar uma sessão com',
      'reserva.coming_soon': 'Este recurso está em desenvolvimento. Em breve você poderá confirmar seu agendamento aqui!',
      'reserva.confirmada': 'Sua sessão foi reservada com sucesso! Você receberá o link da videochamada antes do atendimento.',
      'reserva.btn_volver': 'Voltar ao perfil'
    }
  };

  translate(key: string): string {
    const lang = this.currentLanguage;
    const langDict = this.dictionary[lang] || this.dictionary['Español (España)'];
    return langDict[key] || this.dictionary['Español (España)'][key] || key;
  }
}

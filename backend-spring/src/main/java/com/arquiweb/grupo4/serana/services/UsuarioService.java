package com.arquiweb.grupo4.serana.services;

import com.arquiweb.grupo4.serana.dtos.JwtResponseDTO;
import com.arquiweb.grupo4.serana.dtos.LoginRequestDTO;
import com.arquiweb.grupo4.serana.dtos.RegistroEstudianteDTO;
import com.arquiweb.grupo4.serana.entities.Usuario;

public interface UsuarioService {
    JwtResponseDTO autenticar(LoginRequestDTO loginDTO);
    Usuario registrarEstudiante(RegistroEstudianteDTO registroDTO);
    Usuario obtenerUsuarioAutenticado();
}

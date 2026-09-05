package com.upc.serana.service;

import com.upc.serana.dto.JwtResponseDTO;
import com.upc.serana.dto.LoginRequestDTO;
import com.upc.serana.dto.RegistroEstudianteDTO;
import com.upc.serana.entity.Usuario;

public interface UsuarioService {
    JwtResponseDTO autenticar(LoginRequestDTO loginDTO);
    Usuario registrarEstudiante(RegistroEstudianteDTO registroDTO);
    Usuario obtenerUsuarioAutenticado();
}

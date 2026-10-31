package com.arquiweb.grupo4.serana.services;

import com.arquiweb.grupo4.serana.entities.ConsentimientoInformado;
import com.arquiweb.grupo4.serana.entities.Usuario;

public interface ConsentimientoService {
    ConsentimientoInformado registrarConsentimiento(Usuario usuario, boolean aceptado, String ipAddress, String versionTerminos);
    boolean tieneConsentimientoVigente(Long usuarioId);
}

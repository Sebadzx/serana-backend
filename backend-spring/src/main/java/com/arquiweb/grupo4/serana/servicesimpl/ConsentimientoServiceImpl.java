package com.arquiweb.grupo4.serana.servicesimpl;

import com.arquiweb.grupo4.serana.entities.ConsentimientoInformado;
import com.arquiweb.grupo4.serana.entities.Usuario;
import com.arquiweb.grupo4.serana.repositories.ConsentimientoInformadoRepository;
import com.arquiweb.grupo4.serana.services.ConsentimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ConsentimientoServiceImpl implements ConsentimientoService {

    private final ConsentimientoInformadoRepository consentimientoRepository;

    public ConsentimientoServiceImpl(ConsentimientoInformadoRepository consentimientoRepository) {
        this.consentimientoRepository = consentimientoRepository;
    }

    @Override
    @Transactional
    public ConsentimientoInformado registrarConsentimiento(Usuario usuario, boolean aceptado, String ipAddress, String versionTerminos) {
        ConsentimientoInformado consentimiento = ConsentimientoInformado.builder()
                .usuario(usuario)
                .aceptado(aceptado)
                .fechaAceptacion(LocalDateTime.now())
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .versionTerminos(versionTerminos != null ? versionTerminos : "1.0")
                .build();

        return consentimientoRepository.save(consentimiento);
    }

    @Override
    public boolean tieneConsentimientoVigente(Long usuarioId) {
        return consentimientoRepository.findFirstByUsuarioIdOrderByFechaAceptacionDesc(usuarioId)
                .map(ConsentimientoInformado::isAceptado)
                .orElse(false);
    }
}

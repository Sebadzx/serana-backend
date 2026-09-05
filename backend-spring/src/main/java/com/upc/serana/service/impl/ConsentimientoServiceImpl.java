package com.upc.serana.service.impl;

import com.upc.serana.dto.ConsentimientoRequestDTO;
import com.upc.serana.dto.ConsentimientoResponseDTO;
import com.upc.serana.entity.ConsentimientoInformado;
import com.upc.serana.entity.Usuario;
import com.upc.serana.repository.ConsentimientoRepository;
import com.upc.serana.service.ConsentimientoService;
import com.upc.serana.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConsentimientoServiceImpl implements ConsentimientoService {

    private final ConsentimientoRepository consentimientoRepository;
    private final UsuarioService usuarioService;

    @Override
    @Transactional
    public ConsentimientoResponseDTO registrarConsentimiento(ConsentimientoRequestDTO requestDTO, String ipCliente) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();

        String version = (requestDTO.getVersionPolitica() != null && !requestDTO.getVersionPolitica().isBlank())
                ? requestDTO.getVersionPolitica()
                : "v1.0-Ley29733";

        // Generar hash digital de firma de consentimiento
        String hash = generarHashFirma(usuario.getEmail(), version, requestDTO.getAceptado());

        ConsentimientoInformado consentimiento = ConsentimientoInformado.builder()
                .usuario(usuario)
                .aceptado(requestDTO.getAceptado())
                .versionPolitica(version)
                .ipAnonimizada(anonimizarIp(ipCliente))
                .hashFirma(hash)
                .fechaAceptacion(LocalDateTime.now())
                .build();

        ConsentimientoInformado guardado = consentimientoRepository.save(consentimiento);

        return ConsentimientoResponseDTO.builder()
                .id(guardado.getId())
                .aceptado(guardado.getAceptado())
                .versionPolitica(guardado.getVersionPolitica())
                .fechaAceptacion(guardado.getFechaAceptacion())
                .mensaje(guardado.getAceptado()
                        ? "Consentimiento informado registrado exitosamente conforme a la Ley N° 29733."
                        : "El consentimiento no ha sido otorgado. No se podra acceder a las evaluaciones psicometricas.")
                .build();
    }

    @Override
    public boolean validarConsentimientoActivo(Long usuarioId) {
        return consentimientoRepository.existsByUsuarioIdAndAceptadoTrue(usuarioId);
    }

    @Override
    public ConsentimientoResponseDTO consultarEstadoActual() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado();
        return consentimientoRepository.findTopByUsuarioIdOrderByFechaAceptacionDesc(usuario.getId())
                .map(c -> ConsentimientoResponseDTO.builder()
                        .id(c.getId())
                        .aceptado(c.getAceptado())
                        .versionPolitica(c.getVersionPolitica())
                        .fechaAceptacion(c.getFechaAceptacion())
                        .mensaje(c.getAceptado() ? "Consentimiento vigente" : "Consentimiento revocado/no otorgado")
                        .build())
                .orElse(ConsentimientoResponseDTO.builder()
                        .id(null)
                        .aceptado(false)
                        .versionPolitica("N/A")
                        .fechaAceptacion(null)
                        .mensaje("No registra consentimiento previo")
                        .build());
    }

    private String anonimizarIp(String ip) {
        if (ip == null || ip.isBlank()) return "0.0.0.0";
        String[] partes = ip.split("\\.");
        if (partes.length == 4) {
            return partes[0] + "." + partes[1] + ".0.0";
        }
        return "ANONIMIZADA";
    }

    private String generarHashFirma(String email, String version, boolean aceptado) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = email + ":" + version + ":" + aceptado + ":" + System.currentTimeMillis();
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "HASH_SHA256_DEFAULT";
        }
    }
}

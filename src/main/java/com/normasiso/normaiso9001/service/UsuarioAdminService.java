package com.normasiso.normaiso9001.service;

import com.normasiso.normaiso9001.model.usersCrud.UsuarioMiembroDTO;
import com.normasiso.normaiso9001.repository.usersCrud.UsuarioAdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioAdminService {

    private final UsuarioAdminRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGeneratorService usernameGeneratorService;

    public UsuarioAdminService(UsuarioAdminRepository repo,
                               PasswordEncoder passwordEncoder,
                               UsernameGeneratorService usernameGeneratorService) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.usernameGeneratorService = usernameGeneratorService;
    }

    /**
     * Crea un nuevo USUARIO + MIEMBRO para la compañía indicada.
     * Genera username automático si viene vacío.
     */
    @Transactional
    public UsuarioMiembroDTO crearUsuarioYMiembroDesdeAdmin(UsuarioMiembroDTO dto, Long idCompania) {

        // 1) Username automático si no viene desde el front
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            String username = usernameGeneratorService.generateUniqueUsername(
                    dto.getNombre(),
                    dto.getApPaterno(),
                    dto.getApMaterno()
            );
            dto.setUsername(username);
        }

        // 2) Alinear rol / tipoUsuario (por si solo viene uno de los dos)
        if (dto.getTipoUsuario() == null && dto.getRol() != null) {
            dto.setTipoUsuario(dto.getRol());
        }
        if (dto.getRol() == null && dto.getTipoUsuario() != null) {
            dto.setRol(dto.getTipoUsuario());
        }

        // 3) Encriptar password (solo altas)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // 4) Insertar en USUARIO + MIEMBRO usando el método 'insertar' del repo
        UsuarioMiembroDTO creado = repo.insertar(dto, idCompania);

        // 5) Devolver DTO con ids generados
        return creado;
    }
}

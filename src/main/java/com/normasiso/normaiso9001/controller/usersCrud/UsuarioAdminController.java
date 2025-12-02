// src/main/java/com/normasiso/normaiso9001/controller/usersCrud/UsuarioAdminController.java
package com.normasiso.normaiso9001.controller.usersCrud;

import com.normasiso.normaiso9001.model.usersCrud.UsuarioMiembroDTO;
import com.normasiso.normaiso9001.repository.usersCrud.UsuarioAdminRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasAnyAuthority('ADMINISTRADOR','SGI')")
public class UsuarioAdminController {

    private final UsuarioAdminRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdminController(UsuarioAdminRepository usuarioRepo,
                                  PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Vista principal de administración de usuarios.
     */
    @GetMapping
    public String vistaAdminUsuarios(Model model, Authentication authentication) {

        String username = (authentication != null) ? authentication.getName() : "Usuario";

        String tipoUsuario = "DESCONOCIDO";
        if (authentication != null && authentication.getAuthorities() != null) {
            tipoUsuario = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)   // ej. "ROLE_SGI"
                    .findFirst()
                    .orElse("DESCONOCIDO");

            if (tipoUsuario.startsWith("ROLE_")) {
                tipoUsuario = tipoUsuario.substring(5);   // "SGI"
            }
        }

        model.addAttribute("active", "usuarios");
        model.addAttribute("username", username);
        model.addAttribute("tipoUsuario", tipoUsuario);

        return "usersCrud/usuariosAdmin";
    }

    /**
     * GET /admin/usuarios/api
     * Lista usuarios SOLO de la misma compañía del usuario logueado.
     * Soporta filtro ?q= para la barra de búsqueda.
     */
    @GetMapping("/api")
    @ResponseBody
    public List<UsuarioMiembroDTO> listar(
            @RequestParam(value = "q", required = false) String filtro,
            Authentication authentication) {

        String username = authentication.getName();

        // Obtenemos la compañía del usuario logueado
        Long idCompania = usuarioRepo.obtenerIdCompaniaPorUsername(username);
        if (idCompania == null) {
            // Si no tiene compañía asociada, devolvemos lista vacía
            return List.of();
        }

        return usuarioRepo.buscarPorCompania(idCompania, filtro);
    }

    /**
     * POST /admin/usuarios/api
     * Crea un nuevo USUARIO + MIEMBRO en la misma compañía del usuario logueado.
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<UsuarioMiembroDTO> crear(@RequestBody UsuarioMiembroDTO dto,
                                                   Authentication authentication) {

        // Validación mínima: password obligatorio
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Encriptar contraseña
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        // id_compania del usuario que está creando el registro
        String usernameCreador = authentication.getName();
        Long idCompania = usuarioRepo.obtenerIdCompaniaPorUsername(usernameCreador);
        if (idCompania == null) {
            // No debería pasar, pero por si acaso
            return ResponseEntity.badRequest().build();
        }

        // Guardar en BD (USUARIO + MIEMBRO) dentro de esa compañía
        UsuarioMiembroDTO creado = usuarioRepo.insertar(dto, idCompania);

        return ResponseEntity.ok(creado);
    }

    /**
     * PUT /admin/usuarios/api/{idUsuario}/{idMiembro}
     * Actualiza datos en USUARIO y MIEMBRO.
     */
    @PutMapping("/api/{idUsuario}/{idMiembro}")
    @ResponseBody
    public ResponseEntity<Void> actualizar(@PathVariable Long idUsuario,
                                           @PathVariable Long idMiembro,
                                           @RequestBody UsuarioMiembroDTO dto) {

        usuarioRepo.actualizar(idUsuario, idMiembro, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /admin/usuarios/api/{idUsuario}/{idMiembro}
     * Elimina primero MIEMBRO y luego USUARIO.
     */
    @DeleteMapping("/api/{idUsuario}/{idMiembro}")
    @ResponseBody
    public ResponseEntity<Void> eliminar(@PathVariable Long idUsuario,
                                         @PathVariable Long idMiembro) {

        usuarioRepo.eliminar(idUsuario, idMiembro);
        return ResponseEntity.ok().build();
    }
}

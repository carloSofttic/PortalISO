// src/main/java/com/normasiso/normaiso9001/controller/usuario/UsuarioAdminController.java
package com.normasiso.normaiso9001.controller.usersCrud;

import com.normasiso.normaiso9001.model.usersCrud.UsuarioMiembroDTO;
import com.normasiso.normaiso9001.repository.usersCrud.UsuarioAdminRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // Indica que esta clase manejará peticiones web (controlador MVC)
@RequestMapping("/admin/usuarios") // URL base para todas las rutas de este controlador
@PreAuthorize("hasAnyAuthority('ADMINISTRADOR','SGI')") 
// Restringe el acceso: solo ADMINISTRADOR o SGI pueden usar este módulo
public class UsuarioAdminController {

    // Inyección del repositorio que maneja las operaciones SQL sobre USUARIO y MIEMBRO
    private final UsuarioAdminRepository usuarioRepo;

    // PasswordEncoder para encriptar contraseñas antes de guardarlas en la BD
    private final PasswordEncoder passwordEncoder;

    // Constructor con inyección de dependencias
    public UsuarioAdminController(UsuarioAdminRepository usuarioRepo,
                                  PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Renderiza la vista Thymeleaf "usuarios-admin.html".
     * Esta vista contiene la tabla dinámica (AJAX) y los modales para CRUD.
     */
    @GetMapping
    public String vistaAdminUsuarios() {
        return "usersCrud/usuariosAdmin";
    }

    /**
     * ENDPOINT: GET /admin/usuarios/api
     * Retorna lista de usuarios combinada con datos de MIEMBRO.
     * Si se pasa ?q=texto aplica filtro de búsqueda.
     */
    @GetMapping("/api")
    @ResponseBody // Indica que la respuesta será JSON (no una vista)
    public List<UsuarioMiembroDTO> listar(@RequestParam(value = "q", required = false) String filtro) {
        return usuarioRepo.buscar(filtro);
    }

    /**
     * ENDPOINT: POST /admin/usuarios/api
     * Crea un nuevo registro en USUARIO y MIEMBRO dentro de una transacción.
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<UsuarioMiembroDTO> crear(@RequestBody UsuarioMiembroDTO dto) {

        // Validación mínima: la contraseña es obligatoria para crear un usuario
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Encriptar la contraseña antes de guardarla
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Guardar en BD (inserta en USUARIO y MIEMBRO)
        UsuarioMiembroDTO creado = usuarioRepo.insertar(dto);

        // Devolver el objeto creado como JSON
        return ResponseEntity.ok(creado);
    }

    /**
     * ENDPOINT: PUT /admin/usuarios/api/{idUsuario}/{idMiembro}
     * Actualiza datos tanto en USUARIO como en MIEMBRO.
     */
    @PutMapping("/api/{idUsuario}/{idMiembro}")
    @ResponseBody
    public ResponseEntity<Void> actualizar(@PathVariable Long idUsuario,
                                           @PathVariable Long idMiembro,
                                           @RequestBody UsuarioMiembroDTO dto) {

        // Ejecuta el UPDATE en las 2 tablas
        usuarioRepo.actualizar(idUsuario, idMiembro, dto);

        return ResponseEntity.ok().build();
    }

    /**
     * ENDPOINT: DELETE /admin/usuarios/api/{idUsuario}/{idMiembro}
     * Elimina primero el registro de MIEMBRO y luego el de USUARIO.
     */
    @DeleteMapping("/api/{idUsuario}/{idMiembro}")
    @ResponseBody
    public ResponseEntity<Void> eliminar(@PathVariable Long idUsuario,
                                         @PathVariable Long idMiembro) {

        usuarioRepo.eliminar(idUsuario, idMiembro);

        return ResponseEntity.ok().build();
    }
}

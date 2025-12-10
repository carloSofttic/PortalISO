// src/main/java/com/normasiso/normaiso9001/controller/catalogsCrud/CatalogoProcesosAdminController.java
package com.normasiso.normaiso9001.controller.catalogsCrud;

import com.normasiso.normaiso9001.dto.DashboardVM;
import com.normasiso.normaiso9001.dto.catalogsCrud.CatalogosProcesosAdminDTO;
import com.normasiso.normaiso9001.repository.catalogsCrud.CatalogoProcesosAdminRepository;
import com.normasiso.normaiso9001.repository.dashboard.DashboardRepository;
import com.normasiso.normaiso9001.security.login.AppUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/catalogoProcesos")
public class CatalogoProcesosAdminController {

    private final DashboardRepository dashboardRepository;
    private final CatalogoProcesosAdminRepository catalogosProcesosAdminRepository;

    public CatalogoProcesosAdminController(DashboardRepository dashboardRepository,
                                           CatalogoProcesosAdminRepository catalogosProcesosAdminRepository) {
        this.dashboardRepository = dashboardRepository;
        this.catalogosProcesosAdminRepository = catalogosProcesosAdminRepository;
    }

    // 🔹 Vista principal del catálogo
    @GetMapping
    public String vistaCatalogoProcesos(Model model,
                                        @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();

        DashboardVM vm = dashboardRepository.buildDashboard(username);
        model.addAttribute("vm", vm);

        String tipoUsuario = auth.getTipoUsuario();
        boolean primerInicio = auth.isPrimerInicioSesion();
        model.addAttribute("tipoUsuario", tipoUsuario);
        model.addAttribute("primerInicioSesion", primerInicio);

        if (primerInicio) {
            return "redirect:/onboarding";
        }

        List<CatalogosProcesosAdminDTO> procesos =
                catalogosProcesosAdminRepository.listarProcesosPorUsuario(username);

        model.addAttribute("procesos", procesos);

        return "catalogsCrud/catalogosProcesosAdmin";
    }

    // 🔹 AJAX: búsqueda
    @GetMapping("/buscar")
    @ResponseBody
    public List<CatalogosProcesosAdminDTO> buscarProcesos(
            @RequestParam("q") String q,
            @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();
        return catalogosProcesosAdminRepository.buscarProcesos(username, q);
    }

    // 🔹 Crear proceso
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> crearProceso(@RequestBody CatalogosProcesosAdminDTO dto,
                                          @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();

        boolean existe = catalogosProcesosAdminRepository
                .existeNombreOCodigo(username, dto.getNombreProceso(), dto.getCodigoProceso(), null);

        if (existe) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Ya existe un proceso con ese nombre o código.");
        }

        CatalogosProcesosAdminDTO creado =
                catalogosProcesosAdminRepository.insertarProceso(username, dto);

        return ResponseEntity.ok(creado);
    }

    // 🔹 Actualizar proceso
    @PutMapping("/api/{idProceso}")
    @ResponseBody
    public ResponseEntity<?> actualizarProceso(@PathVariable Long idProceso,
                                               @RequestBody CatalogosProcesosAdminDTO dto,
                                               @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();

        boolean existe = catalogosProcesosAdminRepository
                .existeNombreOCodigo(username, dto.getNombreProceso(), dto.getCodigoProceso(), idProceso);

        if (existe) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Ya existe un proceso con ese nombre o código.");
        }

        catalogosProcesosAdminRepository.actualizarProceso(username, idProceso, dto);
        return ResponseEntity.ok().build();
    }

    // 🔹 Eliminar proceso
    @DeleteMapping("/api/{idProceso}")
    @ResponseBody
    public ResponseEntity<Void> eliminarProceso(@PathVariable Long idProceso,
                                                @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();
        catalogosProcesosAdminRepository.eliminarProceso(username, idProceso);
        return ResponseEntity.ok().build();
    }
}

package com.normasiso.normaiso9001.controller.catalogsCrud;

import com.normasiso.normaiso9001.dto.catalogsCrud.CatalogosDocumentosAdminDTO;
import com.normasiso.normaiso9001.repository.catalogsCrud.CatalogoDocumentosAdminRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.normasiso.normaiso9001.security.login.AppUserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/catalogoDocumentos")
public class CatalogoDocumentosAdminController {

    private final CatalogoDocumentosAdminRepository repo;

    public CatalogoDocumentosAdminController(CatalogoDocumentosAdminRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String vista(Model model, @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();

        List<CatalogosDocumentosAdminDTO> lista = repo.buscar(username, "");

        model.addAttribute("tiposDoc", lista);

        return "catalogsCrud/catalogoDocumentosAdmin";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<CatalogosDocumentosAdminDTO> buscar(
            @RequestParam(defaultValue = "") String q,
            @AuthenticationPrincipal AppUserDetails auth) {

        return repo.buscar(auth.getUsername(), q);
    }

    @PostMapping("/api")
    public ResponseEntity<?> crear(
            @RequestBody CatalogosDocumentosAdminDTO dto,
            @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();

        if (repo.existeNombreOCodigo(username, dto.getNombreTipoDoc(), dto.getCodigoTipoDoc(), null)) {
            return ResponseEntity.status(409)
                    .body("Ya existe un documento con ese nombre o código.");
        }

        repo.crear(username, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody CatalogosDocumentosAdminDTO dto,
            @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();

        if (repo.existeNombreOCodigo(username, dto.getNombreTipoDoc(), dto.getCodigoTipoDoc(), id)) {
            return ResponseEntity.status(409)
                    .body("Ya existe un documento con ese nombre o código.");
        }

        repo.actualizar(username, id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUserDetails auth) {

        repo.eliminar(auth.getUsername(), id);
        return ResponseEntity.ok().build();
    }
}

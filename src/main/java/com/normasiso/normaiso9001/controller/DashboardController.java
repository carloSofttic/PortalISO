// controller/DashboardController.java
package com.normasiso.normaiso9001.controller;

import com.normasiso.normaiso9001.dto.DashboardVM;
import com.normasiso.normaiso9001.repository.dashboard.DashboardRepository;
import com.normasiso.normaiso9001.security.login.AppUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardRepository repo;

    public DashboardController(DashboardRepository repo) {
        this.repo = repo;
    }

    @GetMapping({"/", "/dashboard"})
    public String home(@AuthenticationPrincipal AppUserDetails auth, Model model) {

        // 1) Datos del dashboard (lo que ya tenías)
        DashboardVM vm = repo.buildDashboard(auth.getUsername());
        model.addAttribute("vm", vm);

        // 2) Marca por defecto "Dirección General" para el menú
        model.addAttribute("active", "dg");

        // 3) Info del usuario logueado
        String tipoUsuario = auth.getTipoUsuario();         // ADMINISTRADOR, SGI, LIDER_...
        boolean primerInicio = auth.isPrimerInicioSesion(); // true si "Si" en BD

        model.addAttribute("tipoUsuario", tipoUsuario);
        model.addAttribute("primerInicioSesion", primerInicio);

        // 4) Si es primer inicio → mandar a onboarding
        if (primerInicio) {
            return "redirect:/onboarding";
        }

        // 5) Siempre usa la misma plantilla dashboard.html
        return "dashboard";
    }
}

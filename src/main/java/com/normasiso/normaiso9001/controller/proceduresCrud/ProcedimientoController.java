package com.normasiso.normaiso9001.controller.proceduresCrud;

import com.normasiso.normaiso9001.dto.DashboardVM;
import com.normasiso.normaiso9001.dto.proceduresCrud.ProcedimientoListaDTO;
import com.normasiso.normaiso9001.dto.proceduresCrud.TipoDocDTO;   // 👈 IMPORTANTE
import com.normasiso.normaiso9001.repository.dashboard.DashboardRepository;
import com.normasiso.normaiso9001.repository.proceduresCrud.ProcedimientoRepository;
import com.normasiso.normaiso9001.security.login.AppUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/procedimientos")
public class ProcedimientoController {

    private final DashboardRepository dashboardRepository;
    private final ProcedimientoRepository procedimientoRepository;

    public ProcedimientoController(DashboardRepository dashboardRepository,
                                   ProcedimientoRepository procedimientoRepository) {
        this.dashboardRepository = dashboardRepository;
        this.procedimientoRepository = procedimientoRepository;
    }

    // 🔹 Vista principal (tabla)
    @GetMapping
    public String vistaProcedimientos(Model model,
                                      @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();  // ITM66554

        // ===== info topbar / dashboard =====
        DashboardVM vm = dashboardRepository.buildDashboard(username);
        model.addAttribute("vm", vm);

        String tipoUsuario = auth.getTipoUsuario();
        boolean primerInicio = auth.isPrimerInicioSesion();
        model.addAttribute("tipoUsuario", tipoUsuario);
        model.addAttribute("primerInicioSesion", primerInicio);

        if (primerInicio) {
            return "redirect:/onboarding";
        }

        // ===== tabla de procedimientos =====
        List<ProcedimientoListaDTO> lista =
                procedimientoRepository.listarProcedimientos(username);
        model.addAttribute("procedimientos", lista);

        // ===== opciones del select de tipo_doc ===== 👇
        List<TipoDocDTO> tiposDoc =
                procedimientoRepository.listarTiposDocumento(username);
        model.addAttribute("tiposDoc", tiposDoc);

        return "proceduresCrud/proceduresAdmin";
    }

    // 🔹 Endpoint AJAX búsqueda en tiempo real
    @GetMapping("/buscar")
    @ResponseBody
    public List<ProcedimientoListaDTO> buscarProcedimientos(
            @RequestParam("q") String q,
            @AuthenticationPrincipal AppUserDetails auth) {

        String username = auth.getUsername();
        return procedimientoRepository.buscarProcedimientos(username, q);
    }
}

// controller/onboarding/OnboardingController.java
package com.normasiso.normaiso9001.controller.onboarding;

import com.normasiso.normaiso9001.model.onboarding.OnboardingQuestions;
import com.normasiso.normaiso9001.repository.dashboard.DashboardRepository;
import com.normasiso.normaiso9001.repository.onboarding.OnboardingRepository;
import com.normasiso.normaiso9001.repository.onboarding.SectorDao;
import org.springframework.jdbc.core.JdbcTemplate;                    // 👈 NUEVO
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OnboardingController {

  private final SectorDao sectorDao;
  private final DashboardRepository dashboardRepo;
  private final OnboardingRepository onboardingRepo;
  private final JdbcTemplate jdbcTemplate;                        // 👈 NUEVO

  public OnboardingController(
      SectorDao sectorDao,
      DashboardRepository dashboardRepo,
      OnboardingRepository onboardingRepo,
      JdbcTemplate jdbcTemplate                                     // 👈 NUEVO
  ) {
    this.sectorDao = sectorDao;
    this.dashboardRepo = dashboardRepo;
    this.onboardingRepo = onboardingRepo;
    this.jdbcTemplate = jdbcTemplate;                              // 👈 NUEVO
  }

  @GetMapping("/onboarding")
  public String mostrarFormulario(@AuthenticationPrincipal User auth, Model model) {
    List<OnboardingQuestions> sectores = sectorDao.findAll();
    String nombreCompania = dashboardRepo.findNombreCompaniaByUsername(auth.getUsername());

    model.addAttribute("sectores", sectores);
    model.addAttribute("nombreCompania", nombreCompania);
    return "onboarding";
  }

  @PostMapping("/onboarding")
  public String guardarOnboarding(@AuthenticationPrincipal User auth,
      @RequestParam String nombreComercial,
      @RequestParam Long sectorCompania,
      @RequestParam Long isoSeleccionada,
      RedirectAttributes ra) {

    try {
      // 1️⃣ Crea o asegura la compañía y la membresía
      long idCompania = onboardingRepo.ensureCompaniaForUser(auth.getUsername(), nombreComercial);

      // 2️⃣ Guarda la norma ISO seleccionada
      onboardingRepo.upsertCompaniaIso(idCompania, isoSeleccionada);

      // 3️⃣ Registra el sector principal de la compañía
      onboardingRepo.setSectorPrincipal(idCompania, sectorCompania);

      // 4️⃣ Marcar que ya NO es primer inicio de sesión
      String sql = """
        UPDATE "USUARIO"
        SET "primerInicioSesion" = 'No'
        WHERE "username" = ?
        """;
      jdbcTemplate.update(sql, auth.getUsername());

      // 5️⃣ Mensaje temporal (flash) de éxito
      ra.addFlashAttribute("ok", "Onboarding completado correctamente.");

      // 6️⃣ Redirige a donde tú quieras (ahora tienes 'No' en la BD)
      return "redirect:/dashboard";   // o "redirect:/dashboard"

    } catch (Exception ex) {
      ex.printStackTrace();
      ra.addFlashAttribute("error", "Ocurrió un error al registrar tu información.");
      return "redirect:/onboarding";
    }
  }

}

// repository/DashboardRepository.java
package com.normasiso.normaiso9001.repository.dashboard;

import com.normasiso.normaiso9001.dto.DashboardVM;
import com.normasiso.normaiso9001.utils.RowMappers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepository {

  private final JdbcTemplate jdbc;

  public DashboardRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  // ========================================================
  // USER / COMPANY LOOKUPS
  // ========================================================

  public Long findUserIdByUsername(String username) {
    String sql = """
            SELECT "id_usuario"
            FROM public."USUARIO"
            WHERE UPPER("username") = UPPER(?)
        """;
    List<Long> list = jdbc.query(sql, RowMappers.LONG_MAPPER, username);
    return list.isEmpty() ? null : list.get(0);
  }

  public Long findCompaniaIdByUsuario(Long idUsuario) {
    if (idUsuario == null)
      return null;
    String sql = """
            SELECT m."id_compania"
            FROM public."MIEMBRO" m
            WHERE m."id_usuario" = ?
            ORDER BY m."id_miembro" ASC
            LIMIT 1
        """;
    List<Long> list = jdbc.query(sql, RowMappers.LONG_MAPPER, idUsuario);
    return list.isEmpty() ? null : list.get(0);
  }

  public String findNombreCompania(Long idCompania) {
    if (idCompania == null)
      return null;
    String sql = """
            SELECT c."nombre_compania"
            FROM public."COMPANIA" c
            WHERE c."id_compania" = ?
        """;
    List<String> list = jdbc.query(sql, RowMappers.STRING_MAPPER, idCompania);
    return list.isEmpty() ? null : list.get(0);
  }

  public String findIsoSeleccionada(Long idCompania) {
    if (idCompania == null)
      return null;
    String sql = """
            SELECT ni."nombre_iso"
            FROM public."COMPANIA_ISO" ci
            JOIN public."NORMA_ISO" ni ON ni."id_norma" = ci."id_norma"
            WHERE ci."id_compania" = ?
            ORDER BY ci."id_companiaIso" DESC
            LIMIT 1
        """;
    List<String> list = jdbc.query(sql, RowMappers.STRING_MAPPER, idCompania);
    return list.isEmpty() ? null : list.get(0);
  }

  public boolean hasSectorPrincipal(Long idCompania) {
    if (idCompania == null)
      return false;
    String sql = """
            SELECT COUNT(1)
            FROM public."SECTOR_PRINCIPAL"
            WHERE "id_compania" = ? AND "fecha_fin" IS NULL
        """;
    Long count = jdbc.queryForObject(sql, Long.class, idCompania);
    return count != null && count > 0;
  }

  public String findNombreCompaniaByUsername(String username) {
    Long idUsuario = findUserIdByUsername(username);
    Long idCompania = findCompaniaIdByUsuario(idUsuario);
    return findNombreCompania(idCompania);
  }

  // ========================================================
  // DASHBOARD PRINCIPAL
  // ========================================================
  public DashboardVM buildDashboard(String username) {

    DashboardVM vm = new DashboardVM();

    vm.setUsername(username);
    vm.getPendientes().clear();

    Long idUsuario = findUserIdByUsername(username);
    Long idCompania = findCompaniaIdByUsuario(idUsuario);

    // -------- Nombre de la empresa --------
    vm.setNombreCompania(findNombreCompania(idCompania));

    // -------- ISO seleccionada --------
    vm.setIsoSeleccionada(findIsoSeleccionada(idCompania));

    // -------- Checklist Onboarding --------
    int total = 3;
    int ok = 0;

    if (vm.getNombreCompania() != null && !vm.getNombreCompania().isBlank())
      ok++;
    else
      vm.getPendientes().add("Captura el nombre comercial de la compañía");

    if (vm.getIsoSeleccionada() != null && !vm.getIsoSeleccionada().isBlank())
      ok++;
    else
      vm.getPendientes().add("Selecciona la ISO de implementación");

    if (hasSectorPrincipal(idCompania))
      ok++;
    else
      vm.getPendientes().add("Selecciona el sector principal de la compañía");

    vm.setAvance((int) Math.round(ok * 100.0 / total));

    // ========================================================
    // 🔥 NUEVAS GRAFICAS (IT - PP - F - GLOBAL)
    // ========================================================
    // Datos con BASE DE DATOS
    // fillGraficas(vm, idCompania);
    // Datos ficticios
    fillGraficasFake(vm);

    return vm;
  }

  // ========================================================
  // GRAFICAS: IT / PP / F / GLOBAL
  // ========================================================
  private void fillGraficas(DashboardVM vm, Long idCompania) {
    if (idCompania == null) {
      // Dejar gráficas en cero si no existe compañía
      return;
    }

    // ============================================
    // 1) IT — Instructivos de trabajo
    // ============================================

    // ⚠ Aquí debes reemplazar las tablas según tu BD multitenant
    String sqlIT = """
            SELECT
                SUM(CASE WHEN estado = 'REVISAR'  THEN 1 ELSE 0 END) AS revisar,
                SUM(CASE WHEN estado = 'APROBAR' THEN 1 ELSE 0 END) AS aprobar,
                SUM(CASE WHEN estado = 'RECHAZAR' THEN 1 ELSE 0 END) AS rechazar,
                COUNT(*) AS total
            FROM <SCHEMA>.DOCUMENTO_IT
            WHERE id_compania = ?
        """;

    try {
      var it = jdbc.queryForMap(sqlIT, idCompania);

      int revisar = ((Number) it.get("revisar")).intValue();
      int aprobar = ((Number) it.get("aprobar")).intValue();
      int rechazar = ((Number) it.get("rechazar")).intValue();
      int total = ((Number) it.get("total")).intValue();

      vm.setItRevisar(revisar);
      vm.setItAprobar(aprobar);
      vm.setItRechazar(rechazar);

      vm.setItPorcentaje(
          total == 0 ? 0 : (int) Math.round(((double) (total - (revisar + aprobar + rechazar)) / total) * 100));

    } catch (Exception e) {
      // Si aún no existen tablas → inicializa en 0
      vm.setItPorcentaje(0);
    }

    // ============================================
    // 2) PP — Procedimientos
    // ============================================

    String sqlPP = """
            SELECT
                SUM(CASE WHEN estado = 'REVISAR'  THEN 1 ELSE 0 END) AS revisar,
                SUM(CASE WHEN estado = 'APROBAR' THEN 1 ELSE 0 END) AS aprobar,
                SUM(CASE WHEN estado = 'RECHAZAR' THEN 1 ELSE 0 END) AS rechazar,
                COUNT(*) AS total
            FROM <SCHEMA>.DOCUMENTO_PP
            WHERE id_compania = ?
        """;

    try {
      var pp = jdbc.queryForMap(sqlPP, idCompania);

      int revisar = ((Number) pp.get("revisar")).intValue();
      int aprobar = ((Number) pp.get("aprobar")).intValue();
      int rechazar = ((Number) pp.get("rechazar")).intValue();
      int total = ((Number) pp.get("total")).intValue();

      vm.setPpRevisar(revisar);
      vm.setPpAprobar(aprobar);
      vm.setPpRechazar(rechazar);

      vm.setPpPorcentaje(
          total == 0 ? 0 : (int) Math.round(((double) (total - (revisar + aprobar + rechazar)) / total) * 100));

    } catch (Exception e) {
      vm.setPpPorcentaje(0);
    }

    // ============================================
    // 3) F — Formatos
    // ============================================

    String sqlF = """
            SELECT
                SUM(CASE WHEN estado = 'REVISAR'  THEN 1 ELSE 0 END) AS revisar,
                SUM(CASE WHEN estado = 'APROBAR' THEN 1 ELSE 0 END) AS aprobar,
                SUM(CASE WHEN estado = 'RECHAZAR' THEN 1 ELSE 0 END) AS rechazar,
                COUNT(*) AS total
            FROM <SCHEMA>.DOCUMENTO_F
            WHERE id_compania = ?
        """;

    try {
      var f = jdbc.queryForMap(sqlF, idCompania);

      int revisar = ((Number) f.get("revisar")).intValue();
      int aprobar = ((Number) f.get("aprobar")).intValue();
      int rechazar = ((Number) f.get("rechazar")).intValue();
      int total = ((Number) f.get("total")).intValue();

      vm.setfRevisar(revisar);
      vm.setfAprobar(aprobar);
      vm.setfRechazar(rechazar);

      vm.setfPorcentaje(
          total == 0 ? 0 : (int) Math.round(((double) (total - (revisar + aprobar + rechazar)) / total) * 100));

    } catch (Exception e) {
      vm.setfPorcentaje(0);
    }

    // ============================================
    // 4) AVANCE GLOBAL (promedio IT + PP + F)
    // ============================================

    int global = (vm.getItPorcentaje() + vm.getPpPorcentaje() + vm.getfPorcentaje()) / 3;
    vm.setGlobalPorcentaje(global);
  }

  /**
   * Datos ficticios para las gráficas
   * Se eliminan cuando tengas tus tablas reales
   */
  private void fillGraficasFake(DashboardVM vm) {

    // ============================
    // 1) IT – Instructivos de trabajo
    // ============================
    vm.setItRevisar(4);
    vm.setItAprobar(2);
    vm.setItRechazar(1);
    vm.setItPorcentaje(70); // 70% completado

    // ============================
    // 2) PP – Procedimientos
    // ============================
    vm.setPpRevisar(3);
    vm.setPpAprobar(1);
    vm.setPpRechazar(0);
    vm.setPpPorcentaje(55); // 55% completado

    // ============================
    // 3) F – Formatos
    // ============================
    vm.setfRevisar(10);
    vm.setfAprobar(3);
    vm.setfRechazar(2);
    vm.setfPorcentaje(40); // 40% completado

    // ============================
    // 4) GLOBAL – promedio simple
    // ============================
    int global = (vm.getItPorcentaje() + vm.getPpPorcentaje() + vm.getfPorcentaje()) / 3;
    vm.setGlobalPorcentaje(global);
  }

}

// src/main/java/com/normasiso/normaiso9001/controller/api/DebugApiController.java
package com.normasiso.normaiso9001.controller.onboarding;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugApiController {
  private final JdbcTemplate jdbc;
  public DebugApiController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  @GetMapping("/conexion")
  public Map<String,Object> conexion() {
    return jdbc.queryForMap("""
      SELECT current_database() AS db,
             current_schema()   AS schema,
             inet_server_addr() AS host,
             inet_server_port() AS port,
             current_user       AS user,
             current_setting('search_path') AS search_path
    """);
  }

  @GetMapping("/ping") public Map<String,Object> ping() {
    return jdbc.queryForMap("SELECT 1 AS ok");
  }

  @GetMapping("/sni/conteo")
  public Map<String,Object> conteoSni() {
    Long c = jdbc.queryForObject("SELECT COUNT(*) FROM public.\"SECTOR_NORMA_ISO\"", Long.class);
    return Map.of("count", c);
  }

  @GetMapping("/sectores/{id}/conteos")
  public Map<String,Object> conteos(@PathVariable Long id) {
    Long totalSni = jdbc.queryForObject(
      "SELECT COUNT(*) FROM public.\"SECTOR_NORMA_ISO\" WHERE id_sector = ?",
      Long.class, id
    );
    Long totalJoin = jdbc.queryForObject("""
      SELECT COUNT(*)
      FROM public."NORMA_ISO" n
      JOIN public."SECTOR_NORMA_ISO" sni ON sni.id_norma = n.id_norma
      WHERE sni.id_sector = ?
      """, Long.class, id
    );
    return Map.of("id_sector", id, "conteo_sni", totalSni, "conteo_join", totalJoin);
  }

  @GetMapping("/sectores/{id}/raw")
  public List<Map<String,Object>> raw(@PathVariable Long id) {
    return jdbc.queryForList("""
      SELECT n.id_norma, n.codigo_iso, n.nombre_iso
      FROM public."NORMA_ISO" n
      JOIN public."SECTOR_NORMA_ISO" sni ON sni.id_norma = n.id_norma
      WHERE sni.id_sector = ?
      ORDER BY n.codigo_iso
      """, id
    );
  }
}

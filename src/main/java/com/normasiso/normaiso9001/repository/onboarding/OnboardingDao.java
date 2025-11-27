package com.normasiso.normaiso9001.repository.onboarding;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OnboardingDao {

  private final JdbcTemplate jdbc;

  public OnboardingDao(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  

  // Inserta la relación compañía - norma ISO
  public int vincularCompaniaNorma(Long idCompania, Long idNorma) {
    final String sql = """
      INSERT INTO public."COMPANIA_ISO"(id_compania, id_norma, status, fecha_implementacion)
      VALUES (?, ?, 'En proceso', now())
      """;
    return jdbc.update(sql, idCompania, idNorma);
  }
}

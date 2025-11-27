package com.normasiso.normaiso9001.repository.onboarding;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserCompanyDao {

  private final JdbcTemplate jdbc;

  public UserCompanyDao(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public static class CompanyVm {
    public Long id;
    public String nombreComercial;
  }

  private static final RowMapper<CompanyVm> MAPPER = (rs, n) -> {
    CompanyVm vm = new CompanyVm();
    vm.id = rs.getLong("id_compania");
    vm.nombreComercial = rs.getString("nombre_comercial");
    return vm;
  };

  /** Trae la compañía por username (o email) actual. */
  public CompanyVm getCompanyByUsername(String username) {
    // Cambia u.username por tu campo real de login (p. ej. u.correo)
    final String sql = """
      SELECT m.id_compania, c.nombre_compania
      FROM public."MIEMBRO" m
      JOIN public."USUARIO" u ON m.id_usuario = u.id_usuario
      JOIN public."COMPANIA" c ON m.id_compania = c.id_compania
      WHERE u.username = ?
      """;
    return jdbc.query(sql, ps -> ps.setString(1, username), MAPPER)
              .stream().findFirst().orElse(null);
  }
}

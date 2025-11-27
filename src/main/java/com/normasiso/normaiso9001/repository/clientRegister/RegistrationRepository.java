package com.normasiso.normaiso9001.repository.clientRegister;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;

@Repository
public class RegistrationRepository {

  private final JdbcTemplate jdbc;

  public RegistrationRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  // ==========================================================================
  //                                COMPAÑÍA
  // ==========================================================================

  /**
   * Verifica si existe una compañía con ese nombre (case insensitive)
   */
  public boolean existsCompaniaByNombre(String nombre) {
    String sql = """
      SELECT COUNT(1)
      FROM public."COMPANIA"
      WHERE LOWER("nombre_compania") = LOWER(?)
      """;

    Integer c = jdbc.queryForObject(sql, Integer.class, nombre);
    return c != null && c > 0;
  }

  /**
   * Inserta compañía y retorna su ID
   */
  public Long insertCompania(String nombre, String rfc, LocalDate fechaCreacion) {

    String sql = """
      INSERT INTO public."COMPANIA"
        ("fecha_creacion","fecha_registro","nombre_compania","rfc")
      VALUES
        (?, CURRENT_DATE, ?, ?)
      RETURNING "id_compania"
      """;

    return jdbc.queryForObject(
        sql,
        Long.class,
        Date.valueOf(fechaCreacion),     // fecha_creacion
        nombre,                          // nombre_compania
        (rfc == null || rfc.isBlank()) ? null : rfc.toUpperCase()
    );
  }

  /**
   * ➕ NUEVO: Actualiza el schema_name de la compañía
   */
  public void updateCompaniaSchemaName(Long idCompania, String schemaName) {

    String sql = """
      UPDATE public."COMPANIA"
      SET "schema_name" = ?
      WHERE "id_compania" = ?
      """;

    jdbc.update(sql, schemaName, idCompania);
  }

  // ==========================================================================
  //                                 USUARIO
  // ==========================================================================

  public boolean existsUsuarioByUsername(String username) {
    String sql = """
      SELECT COUNT(1)
      FROM public."USUARIO"
      WHERE UPPER("username") = UPPER(?)
      """;
    Integer c = jdbc.queryForObject(sql, Integer.class, username);
    return c != null && c > 0;
  }

  public boolean existsUsuarioByCorreo(String correo) {
    String sql = """
      SELECT COUNT(1)
      FROM public."USUARIO"
      WHERE UPPER("email") = UPPER(?)
      """;
    Integer c = jdbc.queryForObject(sql, Integer.class, correo);
    return c != null && c > 0;
  }

  /**
   * Inserta un usuario ADMIN y retorna su ID
   */
  public Long insertUsuario(String username, String correo, String passwordHash, boolean enabled) {
    String sql = """
      INSERT INTO public."USUARIO"
        ("fecha_creacion","username","tipo_usuario","password","email")
      VALUES
        (CURRENT_DATE, ?, 'ADMINISTRADOR', ?, ?)
      RETURNING "id_usuario"
      """;

    return jdbc.queryForObject(sql, Long.class, username, passwordHash, correo);
  }

  // ==========================================================================
  //                                 MIEMBRO
  // ==========================================================================

  /**
   * Inserta un miembro de empresa (representante) y retorna su ID
   */
  public Long insertMiembro(Long idCompania,
                            Long idUsuario,
                            String nombre,
                            String apPaterno,
                            String apMaterno,
                            String telefono,
                            String rol,
                            String correo) {

    String sql = """
      INSERT INTO public."MIEMBRO"
        ("nombre","ap_paterno","ap_materno","telefono","email_miembro","rol","id_compania","id_usuario")
      VALUES
        (?,?,?,?,?,?,?,?)
      RETURNING "id_miembro"
      """;

    return jdbc.queryForObject(
        sql,
        Long.class,
        nombre,
        apPaterno,
        apMaterno,
        telefono,
        correo,
        rol,
        idCompania,
        idUsuario
    );
  }
}

// repository/onboarding/OnboardingRepository.java
package com.normasiso.normaiso9001.repository.onboarding;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OnboardingRepository {

  private final JdbcTemplate jdbc;

  public OnboardingRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /** Busca id_usuario por username (en mayúsculas para evitar issues de casing) */
  public long findUserIdByUsername(String username) {
    String sql = """
      SELECT "id_usuario"
      FROM "USUARIO"
      WHERE UPPER("username") = UPPER(?)
      """;
    return jdbc.queryForObject(sql, Long.class, username);
  }

  /** Regresa la compañía del usuario (primera membresía) o null si no tiene */
  public Long findCompaniaIdByUsuario(long idUsuario) {
    String sql = """
      SELECT m."id_compania"
      FROM "MIEMBRO" m
      WHERE m."id_usuario"=?
      ORDER BY m."id_miembro" ASC
      LIMIT 1
      """;
    var list = jdbc.query(sql, (rs, n) -> rs.getLong(1), idUsuario);
    return list.isEmpty() ? null : list.get(0);
  }

  /** Crea la compañía (si no existe) y asegura la membresía del usuario. Devuelve id_compania. */
  @Transactional
  public long ensureCompaniaForUser(String username, String nombreComercial) {
    long idUsuario = findUserIdByUsername(username);
    Long idCompania = findCompaniaIdByUsuario(idUsuario);
    if (idCompania != null) {
      // Si ya tiene compañía, opcionalmente puedes actualizar el nombre si viene distinto
      if (nombreComercial != null && !nombreComercial.isBlank()) {
        jdbc.update("""
          UPDATE "COMPANIA"
             SET "nombre_compania" = ?
           WHERE "id_compania" = ?
        """, nombreComercial, idCompania);
      }
      return idCompania;
    }

    // Crear compañía y devolver id (PostgreSQL: RETURNING)
    Long nuevoId = jdbc.queryForObject("""
      INSERT INTO "COMPANIA"("nombre_compania")
      VALUES (?)
      RETURNING "id_compania"
    """, Long.class, nombreComercial);

    // Crear membresía USUARIO->COMPANIA
    jdbc.update("""
      INSERT INTO "MIEMBRO"("id_usuario","id_compania")
      VALUES (?,?)
    """, idUsuario, nuevoId);

    return nuevoId;
  }

  /** Inserta (si no existe) la ISO seleccionada para la compañía */
  @Transactional
  public void upsertCompaniaIso(long idCompania, long idNorma) {
    // Si tienes UNIQUE("id_compania","id_norma") puedes hacer ON CONFLICT; si no, usa check+insert
    Integer count = jdbc.queryForObject("""
      SELECT COUNT(1)
      FROM "COMPANIA_ISO"
      WHERE "id_compania"=? AND "id_norma"=?
    """, Integer.class, idCompania, idNorma);

    if (count == null || count == 0) {
        jdbc.update("""
            INSERT INTO "COMPANIA_ISO" ("id_compania", "id_norma", "status", "fecha_implementacion")
            VALUES (?, ?, 'En Proceso', CURRENT_DATE)
        """, idCompania, idNorma);
    }
  }

  /** Deja activo un único SECTOR_PRINCIPAL (cierra el anterior y abre el nuevo) */
  @Transactional
  public void setSectorPrincipal(long idCompania, long idSector) {
    // Cerrar cualquiera vigente que sea distinto al sector nuevo
    jdbc.update("""
      UPDATE "SECTOR_PRINCIPAL"
         SET "fecha_fin" = NOW()
       WHERE "id_compania"=? AND "fecha_fin" IS NULL AND "id_sector"<>?
    """, idCompania, idSector);

    // ¿Ya existe vigente el mismo sector?
    Integer activos = jdbc.queryForObject("""
      SELECT COUNT(1)
      FROM "SECTOR_PRINCIPAL"
      WHERE "id_compania"=? AND "id_sector"=? AND "fecha_fin" IS NULL
    """, Integer.class, idCompania, idSector);

    if (activos == null || activos == 0) {
      jdbc.update("""
        INSERT INTO "SECTOR_PRINCIPAL"("id_compania","id_sector","fecha_inicio","fecha_fin")
        VALUES (?, ?, CURRENT_DATE, NULL)
      """, idCompania, idSector);
    }
  }
}

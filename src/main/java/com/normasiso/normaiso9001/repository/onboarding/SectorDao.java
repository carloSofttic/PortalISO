package com.normasiso.normaiso9001.repository.onboarding;

import com.normasiso.normaiso9001.model.onboarding.OnboardingQuestions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class SectorDao {

  private static final Logger log = LoggerFactory.getLogger(SectorDao.class);

  private final JdbcTemplate jdbc;

  public SectorDao(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  // Mapper: alinea columnas reales de BD con los nombres del modelo
  private static final RowMapper<OnboardingQuestions> SECTOR_MAPPER = new RowMapper<>() {
    @Override
    public OnboardingQuestions mapRow(ResultSet rs, int rowNum) throws SQLException {
      OnboardingQuestions s = new OnboardingQuestions();
      // columnas REALES en tu BD: id_sector, nombre_sector
      s.setId(rs.getLong("id_sector"));
      s.setNombreSector(rs.getString("nombre_sector"));
      return s;
    }
  };

  /**
   * Obtiene todos los sectores.
   */
  public List<OnboardingQuestions> findAll() {
    final String sql = """
        SELECT id_sector, nombre_sector
        FROM public."SECTOR"
        ORDER BY nombre_sector
        """;

    log.debug("SQL findAll => {}", sql);

    try {
      List<OnboardingQuestions> list = jdbc.query(sql, SECTOR_MAPPER);
      log.info("findAll: {} sectores cargados", list.size());
      return list;
    } catch (DataAccessException ex) {
      log.error("Error en findAll() ejecutando SQL: {}\nMensaje: {}\nCausa: {}",
          sql, ex.getMessage(), (ex.getCause() != null ? ex.getCause().getMessage() : "N/A"));
      // Puedes relanzar como RuntimeException si quieres que suba el 500:
      // throw ex;
      // o devolver lista vacía para que la vista no truene:
      return Collections.emptyList();
    }
  }

  /**
   * Busca un sector por su ID.
   
  public Optional<OnboardingQuestions> findById(Long idSector) {
    final String sql = """
        SELECT id_sector, nombre_sector
        FROM public."SECTOR"
        WHERE id_sector = ?
        """;

    log.debug("SQL findById => {} ; params=[{}]", sql, idSector);

    try {
      OnboardingQuestions s = jdbc.queryForObject(sql, SECTOR_MAPPER, idSector);
      log.info("findById: encontrado id_sector={}", idSector);
      return Optional.ofNullable(s);
    } catch (DataAccessException ex) {
      log.warn("findById: no encontrado id_sector={} o error SQL. Mensaje: {}", idSector, ex.getMessage());
      return Optional.empty();
    }
  }

  
   * Inserta un sector (si lo llegaras a necesitar).
   
  public int insert(String nombreSector) {
    final String sql = """
        INSERT INTO public."SECTOR"(nombre_sector)
        VALUES (?)
        """;

    log.debug("SQL insert => {} ; params=[{}]", sql, nombreSector);

    try {
      int rows = jdbc.update(sql, nombreSector);
      log.info("insert: {} fila(s) afectadas (nombre_sector={})", rows, nombreSector);
      return rows;
    } catch (DataAccessException ex) {
      log.error("Error en insert(nombre_sector={}). Mensaje: {}", nombreSector, ex.getMessage());
      return 0;
    }
  }*/
}

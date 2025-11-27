// src/main/java/com/normasiso/normaiso9001/repository/onboarding/NormaIsoDao.java
package com.normasiso.normaiso9001.repository.onboarding;

import com.normasiso.normaiso9001.model.onboarding.NormaIso;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class NormaIsoDao {
  private final JdbcTemplate jdbc;
  public NormaIsoDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<NormaIso> findBySector(Long idSector) {
    final String sql = """
      SELECT n.id_norma, n.codigo_iso, n.nombre_iso
      FROM public."NORMA_ISO" n
      JOIN public."SECTOR_NORMA_ISO" sni ON sni.id_norma = n.id_norma
      WHERE sni.id_sector = ?
      ORDER BY n.codigo_iso
      """;
    return jdbc.query(sql, (rs, i) -> {
      NormaIso n = new NormaIso();
      n.setId(rs.getLong("id_norma"));
      n.setCodigo(String.valueOf(rs.getLong("codigo_iso"))); // o rs.getString("codigo_iso")
      n.setNombre(rs.getString("nombre_iso"));
      return n;
    }, idSector);
  }
}

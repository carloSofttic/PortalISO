// src/main/java/com/normasiso/normaiso9001/repository/catalogsCrud/CatalogoProcesosAdminRepository.java
package com.normasiso.normaiso9001.repository.catalogsCrud;

import com.normasiso.normaiso9001.dto.catalogsCrud.CatalogosProcesosAdminDTO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CatalogoProcesosAdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public CatalogoProcesosAdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ==========================
    // 🔹 1) OBTENER SCHEMA DINÁMICO SEGÚN USUARIO LOGUEADO
    // ============================================================
    public String obtenerSchemaPorUsername(String username) {

        String sql = "SELECT c.schema_name " +
                "FROM public.\"MIEMBRO\" m " +
                "INNER JOIN public.\"USUARIO\" u ON u.id_usuario = m.id_usuario " +
                "INNER JOIN public.\"COMPANIA\" c ON c.id_compania = m.id_compania " +
                "WHERE u.username = ?";

        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (EmptyResultDataAccessException ex) {
            throw new IllegalStateException(
                    "❌ No se encontró un schema para el usuario: " + username);
        }
    }

    // ==========================
    //   ROWMAPPER
    // ==========================
    private static class ProcesoRowMapper implements RowMapper<CatalogosProcesosAdminDTO> {
        @Override
        public CatalogosProcesosAdminDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            CatalogosProcesosAdminDTO dto = new CatalogosProcesosAdminDTO();
            dto.setIdProceso(rs.getLong("id_proceso"));
            dto.setCodigoProceso(rs.getString("codigo_proceso"));
            dto.setNombreProceso(rs.getString("nombre_proceso"));
            return dto;
        }
    }

    // ==========================
    //   LISTAR PROCESOS
    // ==========================
    public List<CatalogosProcesosAdminDTO> listarProcesosPorUsuario(String username) {
        String schema = obtenerSchemaPorUsername(username);

        String sql = "SELECT id_proceso, codigo_proceso, nombre_proceso " +
                     "FROM " + schema + ".proceso " +
                     "ORDER BY id_proceso DESC";

        return jdbcTemplate.query(sql, new ProcesoRowMapper());
    }

    // ==========================
    //   BUSCAR PROCESOS (LIKE)
    // ==========================
    public List<CatalogosProcesosAdminDTO> buscarProcesos(String username, String q) {
        String schema = obtenerSchemaPorUsername(username);
        String filtro = "%" + (q == null ? "" : q.trim().toLowerCase()) + "%";

        String sql = "SELECT id_proceso, codigo_proceso, nombre_proceso " +
                     "FROM " + schema + ".proceso " +
                     "WHERE LOWER(nombre_proceso) LIKE ? " +
                     "   OR LOWER(codigo_proceso) LIKE ? " +
                     "ORDER BY id_proceso DESC";

        return jdbcTemplate.query(sql, new ProcesoRowMapper(), filtro, filtro);
    }

    // ==========================
    //   VALIDAR DUPLICADOS
    // ==========================
    public boolean existeNombreOCodigo(String username,
                                       String nombre,
                                       String codigo,
                                       Long excluirId) {
        String schema = obtenerSchemaPorUsername(username);

        StringBuilder sb = new StringBuilder(
                "SELECT COUNT(*) FROM " + schema + ".proceso " +
                "WHERE (LOWER(nombre_proceso) = LOWER(?) " +
                "   OR UPPER(codigo_proceso) = UPPER(?))");

        List<Object> params = new ArrayList<>();
        params.add(nombre);
        params.add(codigo);

        if (excluirId != null) {
            sb.append(" AND id_proceso <> ?");
            params.add(excluirId);
        }

        Integer count = jdbcTemplate.queryForObject(sb.toString(), Integer.class, params.toArray());
        return count != null && count > 0;
    }

    // ==========================
    //   INSERTAR PROCESO
    // ==========================
    public CatalogosProcesosAdminDTO insertarProceso(String username,
                                                     CatalogosProcesosAdminDTO dto) {
        String schema = obtenerSchemaPorUsername(username);

        String sql = "INSERT INTO " + schema + ".proceso " +
                     "(nombre_proceso, codigo_proceso) " +
                     "VALUES (?, ?) RETURNING id_proceso";

        Long nuevoId = jdbcTemplate.queryForObject(sql, Long.class,
                dto.getNombreProceso(),
                dto.getCodigoProceso());

        dto.setIdProceso(nuevoId);
        return dto;
    }

    // ==========================
    //   ACTUALIZAR PROCESO
    // ==========================
    public void actualizarProceso(String username,
                                  Long idProceso,
                                  CatalogosProcesosAdminDTO dto) {

        String schema = obtenerSchemaPorUsername(username);

        String sql = "UPDATE " + schema + ".proceso " +
                     "SET nombre_proceso = ?, codigo_proceso = ? " +
                     "WHERE id_proceso = ?";

        jdbcTemplate.update(sql,
                dto.getNombreProceso(),
                dto.getCodigoProceso(),
                idProceso);
    }

    // ==========================
    //   ELIMINAR PROCESO
    // ==========================
    public void eliminarProceso(String username, Long idProceso) {
        String schema = obtenerSchemaPorUsername(username);

        String sql = "DELETE FROM " + schema + ".proceso WHERE id_proceso = ?";
        jdbcTemplate.update(sql, idProceso);
    }
}

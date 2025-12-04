package com.normasiso.normaiso9001.repository.proceduresCrud;

import com.normasiso.normaiso9001.dto.proceduresCrud.ProcedimientoListaDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProcedimientoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProcedimientoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ============================================================
    // 🔹 1) OBTENER SCHEMA DINÁMICO SEGÚN USUARIO LOGUEADO
    // ============================================================
    public String obtenerSchemaPorUsuario(String username) {

        String sql =
                "SELECT c.schema_name " +
                "FROM public.\"MIEMBRO\" m " +
                "INNER JOIN public.\"USUARIO\" u ON u.id_usuario = m.id_usuario " +
                "INNER JOIN public.\"COMPANIA\" c ON c.id_compania = m.id_compania " +
                "WHERE u.username = ?";

        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (EmptyResultDataAccessException ex) {
            throw new IllegalStateException(
                    "❌ No se encontró un schema para el usuario: " + username
            );
        }
    }

    // ============================================================
    // 🔹 2) ROWMAPPER
    // ============================================================
    private static class ProcedimientoRowMapper implements RowMapper<ProcedimientoListaDTO> {
        @Override
        public ProcedimientoListaDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProcedimientoListaDTO dto = new ProcedimientoListaDTO();
            dto.setNombreProceso(rs.getString("nombre_proceso"));
            dto.setIdDocumento(rs.getLong("id_documento"));
            dto.setNombreDocumento(rs.getString("nombre_documento"));
            dto.setCodigoDocumento(rs.getString("codigo_documento"));
            dto.setTipoDocFormat(rs.getString("tipo_doc_format"));
            dto.setFechaVencimiento(rs.getObject("fecha_vencimiento_format", java.time.LocalDate.class));
            dto.setFechaCambio(rs.getObject("fecha_cambio_doc", java.time.LocalDate.class));
            dto.setFechaEmision(rs.getObject("fecha_emision_doc", java.time.LocalDate.class));
            dto.setStatusFormat(rs.getString("status_format"));
            dto.setMetodoResguardo(rs.getString("metodo_resguardo"));
            return dto;
        }
    }

    // ============================================================
    // 🔹 3) LISTAR PROCEDIMIENTOS DEL USUARIO LOGUEADO
    // ============================================================
    public List<ProcedimientoListaDTO> listarProcedimientos(String username) {

        // 🔥 Obtenemos el schema REAL
        String schema = obtenerSchemaPorUsuario(username);

        String sql =
                "SELECT p.nombre_proceso, " +
                "       d.id_documento, " +
                "       d.nombre_documento, " +
                "       d.codigo_documento, " +
                "       d.tipo_doc_format, " +
                "       d.fecha_vencimiento_format, " +
                "       d.fecha_cambio_doc, " +
                "       d.fecha_emision_doc, " +
                "       d.status_format, " +
                "       d.metodo_resguardo " +
                "FROM " + schema + ".documento d " +
                "INNER JOIN " + schema + ".proceso_doc pd ON d.id_documento = pd.id_documento " +
                "INNER JOIN " + schema + ".proceso p ON pd.id_proceso = p.id_proceso " +
                "INNER JOIN " + schema + ".usuario_doc ud ON d.id_documento = ud.id_documento " +
                "INNER JOIN public.\"USUARIO\" u ON ud.id_usuario = u.id_usuario " +
                "WHERE u.username = ? " +
                "ORDER BY d.fecha_emision_doc DESC";

        return jdbcTemplate.query(sql, new ProcedimientoRowMapper(), username);
    }

    // ============================================================
    // 🔹 4) BÚSQUEDA AJAX CON SCHEMA DINÁMICO
    // ============================================================
    public List<ProcedimientoListaDTO> buscarProcedimientos(String username, String filtro) {

        // 🔥 Obtenemos el schema REAL otra vez
        String schema = obtenerSchemaPorUsuario(username);

        String like = "%" + filtro.trim().toLowerCase() + "%";

        String sql =
                "SELECT p.nombre_proceso, " +
                "       d.id_documento, " +
                "       d.nombre_documento, " +
                "       d.codigo_documento, " +
                "       d.tipo_doc_format, " +
                "       d.fecha_vencimiento_format, " +
                "       d.fecha_cambio_doc, " +
                "       d.fecha_emision_doc, " +
                "       d.status_format, " +
                "       d.metodo_resguardo " +
                "FROM " + schema + ".documento d " +
                "INNER JOIN " + schema + ".proceso_doc pd ON d.id_documento = pd.id_documento " +
                "INNER JOIN " + schema + ".proceso p ON pd.id_proceso = p.id_proceso " +
                "INNER JOIN " + schema + ".usuario_doc ud ON d.id_documento = ud.id_documento " +
                "INNER JOIN public.\"USUARIO\" u ON ud.id_usuario = u.id_usuario " +
                "WHERE u.username = ? " +
                "  AND (LOWER(d.nombre_documento) LIKE ? " +
                "       OR LOWER(d.codigo_documento) LIKE ? " +
                "       OR LOWER(p.nombre_proceso) LIKE ?) " +
                "ORDER BY d.fecha_emision_doc DESC";

        return jdbcTemplate.query(
                sql,
                new ProcedimientoRowMapper(),
                username, like, like, like
        );
    }
}

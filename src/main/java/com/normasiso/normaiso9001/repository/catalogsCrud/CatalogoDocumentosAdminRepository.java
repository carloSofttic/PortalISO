package com.normasiso.normaiso9001.repository.catalogsCrud;

import com.normasiso.normaiso9001.dto.catalogsCrud.CatalogosDocumentosAdminDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CatalogoDocumentosAdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public CatalogoDocumentosAdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ============================================================
    //  OBTENER SCHEMA DEL TENANT DEL USUARIO LOGUEADO
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

    // ============================================================
    // ROWMAPPER
    // ============================================================
    private static class TipoDocMapper implements RowMapper<CatalogosDocumentosAdminDTO> {
        @Override
        public CatalogosDocumentosAdminDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            CatalogosDocumentosAdminDTO dto = new CatalogosDocumentosAdminDTO();
            dto.setIdTipoDoc(rs.getLong("id_tipodoc"));
            dto.setNombreTipoDoc(rs.getString("tipo_documento"));
            dto.setCodigoTipoDoc(rs.getString("codigo_tipodoc"));
            return dto;
        }
    }

    // ============================================================
    // BUSCAR DOCUMENTOS
    // ============================================================
    public List<CatalogosDocumentosAdminDTO> buscar(String username, String q) {

        String tenant = obtenerSchemaPorUsername(username);

        String sql =
                "SELECT id_tipodoc, tipo_documento, codigo_tipodoc " +
                "FROM " + tenant + ".tipo_doc " +
                "WHERE LOWER(tipo_documento) LIKE LOWER(?) " +
                "   OR LOWER(codigo_tipodoc) LIKE LOWER(?) " +
                "ORDER BY id_tipodoc ASC";

        String like = "%" + q + "%";

        return jdbcTemplate.query(sql, new TipoDocMapper(), like, like);
    }

    // ============================================================
    // VALIDAR DUPLICADOS
    // ============================================================
    public boolean existeNombreOCodigo(String username, String nombre, String codigo, Long excluirId) {

        String tenant = obtenerSchemaPorUsername(username);

        String sql =
            "SELECT COUNT(*) FROM " + tenant + ".tipo_doc " +
            "WHERE (LOWER(tipo_documento) = LOWER(?) OR LOWER(codigo_tipodoc) = LOWER(?)) ";

        if (excluirId != null) {
            sql += " AND id_tipodoc <> " + excluirId;
        }

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nombre, codigo);
        return count != null && count > 0;
    }

    // ============================================================
    // CREAR
    // ============================================================
    public int crear(String username, CatalogosDocumentosAdminDTO dto) {

        String tenant = obtenerSchemaPorUsername(username);

        String sql =
            "INSERT INTO " + tenant + ".tipo_doc (tipo_documento, codigo_tipodoc) VALUES (?, ?)";

        return jdbcTemplate.update(sql,
                dto.getNombreTipoDoc(),
                dto.getCodigoTipoDoc()
        );
    }

    // ============================================================
    // ACTUALIZAR
    // ============================================================
    public int actualizar(String username, Long id, CatalogosDocumentosAdminDTO dto) {

        String tenant = obtenerSchemaPorUsername(username);

        String sql =
            "UPDATE " + tenant + ".tipo_doc " +
            "SET tipo_documento = ?, codigo_tipodoc = ? " +
            "WHERE id_tipodoc = ?";

        return jdbcTemplate.update(sql,
                dto.getNombreTipoDoc(),
                dto.getCodigoTipoDoc(),
                id);
    }

    // ============================================================
    // ELIMINAR
    // ============================================================
    public int eliminar(String username, Long id) {

        String tenant = obtenerSchemaPorUsername(username);

        String sql = "DELETE FROM " + tenant + ".tipo_doc WHERE id_tipodoc = ?";

        return jdbcTemplate.update(sql, id);
    }
}

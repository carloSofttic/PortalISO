package com.normasiso.normaiso9001.repository.usersCrud;

import com.normasiso.normaiso9001.model.usersCrud.UsuarioMiembroDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioAdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioAdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===================== ROWMAPPER =====================
    private static class UsuarioMiembroRowMapper implements RowMapper<UsuarioMiembroDTO> {
        @Override
        public UsuarioMiembroDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            UsuarioMiembroDTO dto = new UsuarioMiembroDTO();

            // --- USUARIO ---
            dto.setIdUsuario(rs.getLong("id_usuario"));
            dto.setFechaCreacion(rs.getObject("fecha_creacion", LocalDate.class));
            dto.setUsername(rs.getString("username"));
            dto.setTipoUsuario(rs.getString("tipo_usuario"));
            dto.setEmail(rs.getString("email"));

            // --- MIEMBRO ---
            dto.setIdMiembro(rs.getLong("id_miembro"));
            dto.setNombre(rs.getString("nombre"));
            dto.setApPaterno(rs.getString("ap_paterno"));
            dto.setApMaterno(rs.getString("ap_materno"));
           
            dto.setTelefono(rs.getString("telefono"));
            dto.setEmailMiembro(rs.getString("email_miembro"));
            dto.setRol(rs.getString("rol"));

            // Si tu DTO tiene campo idCompania podrías mapearlo aquí:
            // dto.setIdCompania(rs.getLong("id_compania"));

            return dto;
        }
    }

    // SELECT base (tablas entre comillas porque se llaman "USUARIO" y "MIEMBRO")
    private String baseSelect() {
        return "SELECT " +
                "u.id_usuario, u.fecha_creacion, u.username, u.tipo_usuario, " +
                "u.email, " +
                "m.id_miembro, m.nombre, m.ap_paterno, m.ap_materno, " +
                "m.telefono, m.email_miembro, m.rol " +
                // si quieres también m.id_compania, añádelo aquí
                "FROM \"USUARIO\" u " +
                "LEFT JOIN \"MIEMBRO\" m ON m.email_miembro = u.email ";
    }

    // ======================================================
    //  OBTENER id_compania DEL USUARIO LOGUEADO
    // ======================================================
    public Long obtenerIdCompaniaPorUsername(String username) {
        try {
            // Buscamos la compañía del miembro asociado a ese usuario
            String sql =
                    "SELECT m.id_compania " +
                    "FROM \"USUARIO\" u " +
                    "JOIN \"MIEMBRO\" m ON m.id_usuario = u.id_usuario " +
                    "WHERE u.username = ? " +
                    "LIMIT 1";

            return jdbcTemplate.queryForObject(sql, Long.class, username);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    // ======================================================
    //  LISTAR / BUSCAR SOLO POR COMPAÑÍA
    // ======================================================
    public List<UsuarioMiembroDTO> buscarPorCompania(Long idCompania, String filtro) {

        StringBuilder sql = new StringBuilder(baseSelect());
        List<Object> params = new ArrayList<>();

        // Filtro por compañía
        sql.append("WHERE m.id_compania = ? ");
        params.add(idCompania);

        if (filtro != null && !filtro.trim().isEmpty()) {
            String like = "%" + filtro.trim().toLowerCase() + "%";

            sql.append("AND (")
               .append("LOWER(u.username)      LIKE ? OR ")
               .append("LOWER(u.tipo_usuario)  LIKE ? OR ")
               .append("LOWER(u.email)         LIKE ? OR ")
               .append("LOWER(m.nombre)        LIKE ? OR ")
               .append("LOWER(m.ap_paterno)    LIKE ? OR ")
               .append("LOWER(m.ap_materno)    LIKE ? OR ")
               .append("LOWER(m.email_miembro) LIKE ? OR ")
               .append("LOWER(m.rol)           LIKE ? ")
               .append(") ");

            // mismos parámetros para todos los LIKE de arriba
            for (int i = 0; i < 8; i++) {
                params.add(like);
            }
        }

        sql.append("ORDER BY u.id_usuario DESC");

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                new UsuarioMiembroRowMapper()
        );
    }

    // ======================================================
    //  (Opcional) LISTAR / BUSCAR SIN COMPAÑÍA
    //  Si ya no lo usas en ningún lado, puedes borrarlo.
    // ======================================================
    public List<UsuarioMiembroDTO> buscar(String filtro) {
        String sql = baseSelect();

        if (filtro == null || filtro.trim().isEmpty()) {
            sql += "ORDER BY u.id_usuario DESC";
            return jdbcTemplate.query(sql, new UsuarioMiembroRowMapper());
        }

        String like = "%" + filtro.trim().toLowerCase() + "%";

        sql += "WHERE " +
                "LOWER(u.username)      LIKE ? OR " +
                "LOWER(u.tipo_usuario)  LIKE ? OR " +
                "LOWER(u.email)         LIKE ? OR " +
                "LOWER(m.nombre)        LIKE ? OR " +
                "LOWER(m.ap_paterno)    LIKE ? OR " +
                "LOWER(m.ap_materno)    LIKE ? OR " +
                "LOWER(m.email_miembro) LIKE ? OR " +
                "LOWER(m.rol)           LIKE ? " +
                "ORDER BY u.id_usuario DESC";

        return jdbcTemplate.query(
                sql,
                new UsuarioMiembroRowMapper(),
                like, like, like, like, like, like, like, like
        );
    }

    // ================== INSERTAR USUARIO + MIEMBRO ==================
    /**
     * Inserta un nuevo USUARIO + MIEMBRO en **la misma compañía**.
     *
     * @param dto        datos del usuario/miembro a crear
     * @param idCompania id_compania del usuario que está creando el registro
     */
    @Transactional
    public UsuarioMiembroDTO insertar(UsuarioMiembroDTO dto, Long idCompania) {

        // 1) Insertamos en USUARIO
        String sqlUsuario = "INSERT INTO \"USUARIO\" " +
                "(fecha_creacion, username, tipo_usuario, password, email) " +
                "VALUES (CURRENT_DATE, ?, ?, ?, ?) " +
                "RETURNING id_usuario, fecha_creacion";

        UsuarioMiembroDTO res = jdbcTemplate.queryForObject(
                sqlUsuario,
                (rs, rowNum) -> {
                    UsuarioMiembroDTO u = new UsuarioMiembroDTO();
                    u.setIdUsuario(rs.getLong("id_usuario"));
                    u.setFechaCreacion(rs.getObject("fecha_creacion", LocalDate.class));
                    return u;
                },
                dto.getUsername(),
                dto.getTipoUsuario(),
                dto.getPassword(),   // ya encriptado desde el controller
                dto.getEmail()
        );

        Long idUsuario = res.getIdUsuario();

        // 2) Insertamos en MIEMBRO con MISMA COMPAÑÍA
        String sqlMiembro = "INSERT INTO \"MIEMBRO\" " +
                "(id_compania, nombre, ap_paterno, ap_materno, telefono, email_miembro, rol, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING id_miembro";

        Long idMiembro = jdbcTemplate.queryForObject(
                sqlMiembro,
                Long.class,
                idCompania,               // <-- misma empresa
                dto.getNombre(),
                dto.getApPaterno(),
                dto.getApMaterno(),
                dto.getTelefono(),
                dto.getEmail(),           // mismo correo que en USUARIO
                dto.getTipoUsuario(),      // rol = tipo_usuario
                idUsuario
        );

        dto.setIdUsuario(idUsuario);
        dto.setIdMiembro(idMiembro);
        dto.setFechaCreacion(res.getFechaCreacion());
        dto.setEmailMiembro(dto.getEmail());
        dto.setRol(dto.getTipoUsuario());
        // dto.setIdCompania(idCompania); // si tu DTO tiene ese campo

        return dto;
    }

    // ============================ ACTUALIZAR ============================
    @Transactional
    public void actualizar(Long idUsuario, Long idMiembro, UsuarioMiembroDTO dto) {

        String sqlU = "UPDATE \"USUARIO\" SET " +
                "username = ?, tipo_usuario = ?, email = ? " +
                "WHERE id_usuario = ?";

        jdbcTemplate.update(
                sqlU,
                dto.getUsername(),
                dto.getTipoUsuario(),
                dto.getEmail(),
                idUsuario
        );

        String sqlM = "UPDATE \"MIEMBRO\" SET " +
                "nombre = ?, ap_paterno = ?, ap_materno = ?, " +
                "telefono = ?, email_miembro = ?, rol = ? " +
                "WHERE id_miembro = ?";

        jdbcTemplate.update(
                sqlM,
                dto.getNombre(),
                dto.getApPaterno(),
                dto.getApMaterno(),
            
                dto.getTelefono(),
                dto.getEmail(),
                dto.getTipoUsuario(),
                idMiembro
        );
    }

    // ============================ ELIMINAR ============================
    @Transactional
    public void eliminar(Long idUsuario, Long idMiembro) {

        String sqlM = "DELETE FROM \"MIEMBRO\" WHERE id_miembro = ?";
        jdbcTemplate.update(sqlM, idMiembro);

        String sqlU = "DELETE FROM \"USUARIO\" WHERE id_usuario = ?";
        jdbcTemplate.update(sqlU, idUsuario);
    }
}

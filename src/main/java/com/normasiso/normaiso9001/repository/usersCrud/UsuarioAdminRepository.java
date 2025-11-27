package com.normasiso.normaiso9001.repository.usersCrud;

import com.normasiso.normaiso9001.model.usersCrud.UsuarioMiembroDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class UsuarioAdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioAdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Mapea fila del JOIN "USUARIO" + "MIEMBRO" al DTO
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
            // OJO: ya NO leemos primerInicioSesi para evitar error de columna

            // --- MIEMBRO ---
            dto.setIdMiembro(rs.getLong("id_miembro"));
            dto.setNombre(rs.getString("nombre"));
            dto.setApPaterno(rs.getString("ap_paterno"));
            dto.setApMaterno(rs.getString("ap_materno"));
            dto.setRfcMiembro(rs.getString("rfc_miembro"));
            dto.setTelefono(rs.getString("telefono"));
            dto.setEmailMiembro(rs.getString("email_miembro"));
            dto.setRol(rs.getString("rol"));

            return dto;
        }
    }

    // SELECT base (tablas entre comillas porque se llaman "USUARIO" y "MIEMBRO")
    private String baseSelect() {
        return "SELECT " +
                "u.id_usuario, u.fecha_creacion, u.username, u.tipo_usuario, " +
                "u.email, " + // quitamos u.primerInicioSesi del SELECT
                "m.id_miembro, m.nombre, m.ap_paterno, m.ap_materno, " +
                "m.rfc_miembro, m.telefono, m.email_miembro, m.rol " +
                "FROM \"USUARIO\" u " +
                "LEFT JOIN \"MIEMBRO\" m ON m.email_miembro = u.email ";
    }

    // LISTAR / BUSCAR
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

    // INSERTAR USUARIO + MIEMBRO
    @Transactional
    public UsuarioMiembroDTO insertar(UsuarioMiembroDTO dto) {

        // No incluimos primerInicioSesi, dejamos que use DEFAULT si existe
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

        String sqlMiembro = "INSERT INTO \"MIEMBRO\" " +
                "(nombre, ap_paterno, ap_materno, rfc_miembro, telefono, email_miembro, rol) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING id_miembro";

        Long idMiembro = jdbcTemplate.queryForObject(
                sqlMiembro,
                Long.class,
                dto.getNombre(),
                dto.getApPaterno(),
                dto.getApMaterno(),
                dto.getRfcMiembro(),
                dto.getTelefono(),
                dto.getEmail(),        // mismo correo que en USUARIO
                dto.getTipoUsuario()   // rol = tipo_usuario
        );

        dto.setIdUsuario(idUsuario);
        dto.setIdMiembro(idMiembro);
        dto.setFechaCreacion(res.getFechaCreacion());
        dto.setEmailMiembro(dto.getEmail());
        dto.setRol(dto.getTipoUsuario());
        // dto.setPrimerInicioSesi("Si"); // opcional, solo en memoria

        return dto;
    }

    // ACTUALIZAR
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
                "rfc_miembro = ?, telefono = ?, email_miembro = ?, rol = ? " +
                "WHERE id_miembro = ?";

        jdbcTemplate.update(
                sqlM,
                dto.getNombre(),
                dto.getApPaterno(),
                dto.getApMaterno(),
                dto.getRfcMiembro(),
                dto.getTelefono(),
                dto.getEmail(),
                dto.getTipoUsuario(),
                idMiembro
        );
    }

    // ELIMINAR
    @Transactional
    public void eliminar(Long idUsuario, Long idMiembro) {

        String sqlM = "DELETE FROM \"MIEMBRO\" WHERE id_miembro = ?";
        jdbcTemplate.update(sqlM, idMiembro);

        String sqlU = "DELETE FROM \"USUARIO\" WHERE id_usuario = ?";
        jdbcTemplate.update(sqlU, idUsuario);
    }
}

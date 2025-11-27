// utils/UserRowMapper.java
package com.normasiso.normaiso9001.utils;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<Usuario> {

    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getLong("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setTipoUsuario(rs.getString("tipo_usuario"));
        u.setEmail(rs.getString("email"));
        u.setPrimerInicioSesion(rs.getString("primerInicioSesion")); // VARCHAR "Si"/"No"
        return u;
    }
}

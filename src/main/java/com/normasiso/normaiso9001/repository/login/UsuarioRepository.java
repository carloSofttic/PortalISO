package com.normasiso.normaiso9001.repository.login;

import com.normasiso.normaiso9001.utils.Usuario;
import com.normasiso.normaiso9001.utils.UserRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Usuario> findByUsername(String username) {
        String sql = "SELECT * FROM usuario WHERE username = ?";

        List<Usuario> lista = jdbcTemplate.query(
                sql,
                new UserRowMapper(),   // ⬅️ Usamos tu RowMapper
                username
        );

        return lista.stream().findFirst();
    }
}

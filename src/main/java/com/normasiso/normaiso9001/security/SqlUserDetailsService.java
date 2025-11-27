// SqlUserDetailsService.java
package com.normasiso.normaiso9001.security;

import com.normasiso.normaiso9001.utils.UserRowMapper;
import com.normasiso.normaiso9001.security.login.AppUserDetails;
import com.normasiso.normaiso9001.utils.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlUserDetailsService implements UserDetailsService {

  private final JdbcTemplate jdbc;

  public SqlUserDetailsService(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  // SqlUserDetailsService.java
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var sql = """
        SELECT "id_usuario","username","password","tipo_usuario","email","primerInicioSesion"
        FROM "USUARIO"
        WHERE UPPER("username") = UPPER(?)
        """;

    List<Usuario> usuarios = jdbc.query(sql, new UserRowMapper(), username);
    if (usuarios.isEmpty()) {
      throw new UsernameNotFoundException("No existe: " + username);
    }

    Usuario u = usuarios.get(0);

    var auth = List.of(new SimpleGrantedAuthority("ROLE_" + u.getTipoUsuario().toUpperCase()));

    return new AppUserDetails(
        u.getUsername(),
        u.getPassword(), // hash BCrypt
        auth,
        u.getTipoUsuario(), // 👈 NUEVO: ADMINISTRADOR, SGI, etc.
        u.getPrimerInicioSesion() // 👈 ya lo tenías
    );
  }

}

package com.normasiso.normaiso9001.security.login;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AppUserDetails extends User {

    private final String tipoUsuario;        // ADMINISTRADOR, SGI, LIDER_DESARROLLO...
    private final String primerInicioSesion; // "Si" / "No"

    public AppUserDetails(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String tipoUsuario,
            String primerInicioSesion
    ) {
        super(username, password, authorities);
        this.tipoUsuario = tipoUsuario;
        this.primerInicioSesion = primerInicioSesion;
    }

    // ==== helpers / getters ====

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    // true si en BD está "Si"
    public boolean isPrimerInicioSesion() {
        return primerInicioSesion != null && primerInicioSesion.equalsIgnoreCase("si");
    }

    public String getPrimerInicioSesionRaw() {
        return primerInicioSesion;
    }
}

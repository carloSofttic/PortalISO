package com.normasiso.normaiso9001.utils;

public class Usuario {

    private Long idUsuario;
    private String username;
    private String password;     // hash BCrypt
    private String tipoUsuario;  // ADMIN / USER
    private String email;
    private String primerInicioSesion; // "Si" / "No"

    // getters y setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrimerInicioSesion() { return primerInicioSesion; }
    public void setPrimerInicioSesion(String primerInicioSesion) { this.primerInicioSesion = primerInicioSesion; }
}

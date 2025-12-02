// src/main/java/com/normasiso/normaiso9001/model/usuario/UsuarioMiembroDTO.java
package com.normasiso.normaiso9001.model.usersCrud;

import java.time.LocalDate;

public class UsuarioMiembroDTO {

    // Campos USUARIO
    private Long idUsuario;
    private LocalDate fechaCreacion;
    private String username;
    private String tipoUsuario;
    private String password;          // solo para altas
    private String email;
    private String primerInicioSesi;
    

    // Campos MIEMBRO
    private Long idMiembro;
    private String nombre;
    private String apPaterno;
    private String apMaterno;
    private String rfcMiembro;
    private String telefono;
    private String emailMiembro;
    private String rol;

    // ===== Getters y Setters =====
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrimerInicioSesi() { return primerInicioSesi; }
    public void setPrimerInicioSesi(String primerInicioSesi) { this.primerInicioSesi = primerInicioSesi; }

    public Long getIdMiembro() { return idMiembro; }
    public void setIdMiembro(Long idMiembro) { this.idMiembro = idMiembro; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApPaterno() { return apPaterno; }
    public void setApPaterno(String apPaterno) { this.apPaterno = apPaterno; }

    public String getApMaterno() { return apMaterno; }
    public void setApMaterno(String apMaterno) { this.apMaterno = apMaterno; }

    public String getRfcMiembro() { return rfcMiembro; }
    public void setRfcMiembro(String rfcMiembro) { this.rfcMiembro = rfcMiembro; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmailMiembro() { return emailMiembro; }
    public void setEmailMiembro(String emailMiembro) { this.emailMiembro = emailMiembro; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}

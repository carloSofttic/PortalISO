package com.normasiso.normaiso9001.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class RegisterForm {

    // ---------- DATOS DE LA EMPRESA ----------
    @NotBlank(message = "El nombre de la empresa es obligatorio.")
    private String nombreEmpresa;

    @Pattern(
        regexp = "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$",
        message = "El RFC no tiene un formato válido."
    )
    @Size(max = 13, message = "El RFC no puede tener más de 13 caracteres.")
    private String rfc;

    @NotNull(message = "La fecha de creación es obligatoria.")
    private LocalDate fechaCreacion;


    // ---------- DATOS DEL REPRESENTANTE ----------
    @NotBlank(message = "El nombre del representante es obligatorio.")
    private String nombreMiembro;

    @NotBlank(message = "El apellido paterno es obligatorio.")
    private String appaternoMiembro;

    @NotBlank(message = "El apellido materno es obligatorio.")
    private String apmaternoMiembro;

    @Pattern(
        regexp = "^(\\+?52)?\\d{10}$",
        message = "El teléfono debe contener 10 dígitos, opcionalmente con +52."
    )
    private String telefono;

    @Email(message = "Correo electrónico inválido.")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    private String correo;

    @NotBlank(message = "El rol dentro de la empresa es obligatorio.")
    private String rolMiembro;


    // ---------- DATOS DEL USUARIO ----------
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'`~<>,.?/\\\\]).{8,}$",
        message = "La contraseña debe incluir al menos una mayúscula, un número y un símbolo."
    )
    private String password;


    // ---------- GETTERS Y SETTERS ----------

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreMiembro() {
        return nombreMiembro;
    }

    public void setNombreMiembro(String nombreMiembro) {
        this.nombreMiembro = nombreMiembro;
    }

    public String getAppaternoMiembro() {
        return appaternoMiembro;
    }

    public void setAppaternoMiembro(String appaternoMiembro) {
        this.appaternoMiembro = appaternoMiembro;
    }

    public String getApmaternoMiembro() {
        return apmaternoMiembro;
    }

    public void setApmaternoMiembro(String apmaternoMiembro) {
        this.apmaternoMiembro = apmaternoMiembro;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRolMiembro() {
        return rolMiembro;
    }

    public void setRolMiembro(String rolMiembro) {
        this.rolMiembro = rolMiembro;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

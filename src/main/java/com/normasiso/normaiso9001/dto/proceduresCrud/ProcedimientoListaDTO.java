package com.normasiso.normaiso9001.dto.proceduresCrud;

import java.time.LocalDate;

public class ProcedimientoListaDTO {

    private Long idDocumento;
    private String nombreProceso;
    private String nombreDocumento;
    private String codigoDocumento;
    private String codigo_tipodoc;           // IT / PP / F...
    private LocalDate fechaVencimiento;
    private LocalDate fechaCambio;
    private LocalDate fechaEmision;
    private String statusFormat;
    private String statusValidity;            // revision / vigente / etc.
    private String metodoResguardo;

    // ===== Getters & Setters =====
    public Long getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Long idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getNombreProceso() {
        return nombreProceso;
    }

    public void setNombreProceso(String nombreProceso) {
        this.nombreProceso = nombreProceso;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getCodigoDocumento() {
        return codigoDocumento;
    }

    public void setCodigoDocumento(String codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }

    public String getTipoDocFormat() {
        return codigo_tipodoc;
    }

    public void setTipoDocFormat(String codigo_tipodoc) {
        this.codigo_tipodoc = codigo_tipodoc;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDate getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDate fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getStatusFormat() {
        return statusFormat;
    }

    public void setStatusFormat(String statusFormat) {
        this.statusFormat = statusFormat;
    }

    public String getStatusValidity(){
        return statusValidity;
    }

    public void setStatusValidity(String statusValidity){
        this.statusValidity = statusValidity;
    }

    public String getMetodoResguardo() {
        return metodoResguardo;
    }

    public void setMetodoResguardo(String metodoResguardo) {
        this.metodoResguardo = metodoResguardo;
    }
}

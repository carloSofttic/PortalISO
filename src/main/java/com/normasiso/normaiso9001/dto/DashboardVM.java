// src/main/java/com/normasiso/normaiso9001/dto/DashboardVM.java
package com.normasiso.normaiso9001.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel para el Dashboard principal de la plataforma.
 * Incluye:
 * - Datos del usuario
 * - Información de la compañía
 * - Avance del onboarding
 * - Pendientes
 * - Datos para las gráficas: IT, PP, F y avance global
 */
public class DashboardVM {

    // =========================================================
    // DATOS YA EXISTENTES
    // =========================================================
    private String username;
    private String nombreCompania;
    private String isoSeleccionada;
    private int avance;
    private List<String> pendientes = new ArrayList<>();

    // =========================================================
    // ► GRAFICA 1: Instructivos de Trabajo (IT)
    // =========================================================
    private int itPorcentaje;     // porcentaje IT completado/pending
    private int itRevisar;        // pendientes por revisar
    private int itAprobar;        // pendientes por aprobar
    private int itRechazar;       // rechazados o por corregir

    // =========================================================
    // ► GRAFICA 2: Procedimientos (PP)
    // =========================================================
    private int ppPorcentaje;
    private int ppRevisar;
    private int ppAprobar;
    private int ppRechazar;

    // =========================================================
    // ► GRAFICA 3: Formatos (F)
    // =========================================================
    private int fPorcentaje;
    private int fRevisar;
    private int fAprobar;
    private int fRechazar;

    // =========================================================
    // ► GRAFICA 4: Avance global (IT + PP + F)
    // =========================================================
    private int globalPorcentaje;


    // =========================================================
    // GETTERS & SETTERS
    // =========================================================
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombreCompania() { return nombreCompania; }
    public void setNombreCompania(String nombreCompania) { this.nombreCompania = nombreCompania; }

    public String getIsoSeleccionada() { return isoSeleccionada; }
    public void setIsoSeleccionada(String isoSeleccionada) { this.isoSeleccionada = isoSeleccionada; }

    public int getAvance() { return avance; }
    public void setAvance(int avance) { this.avance = avance; }

    public List<String> getPendientes() { return pendientes; }
    public void setPendientes(List<String> pendientes) { this.pendientes = pendientes; }

    // ---------------- IT ----------------
    public int getItPorcentaje() { return itPorcentaje; }
    public void setItPorcentaje(int itPorcentaje) { this.itPorcentaje = itPorcentaje; }

    public int getItRevisar() { return itRevisar; }
    public void setItRevisar(int itRevisar) { this.itRevisar = itRevisar; }

    public int getItAprobar() { return itAprobar; }
    public void setItAprobar(int itAprobar) { this.itAprobar = itAprobar; }

    public int getItRechazar() { return itRechazar; }
    public void setItRechazar(int itRechazar) { this.itRechazar = itRechazar; }

    // ---------------- PP ----------------
    public int getPpPorcentaje() { return ppPorcentaje; }
    public void setPpPorcentaje(int ppPorcentaje) { this.ppPorcentaje = ppPorcentaje; }

    public int getPpRevisar() { return ppRevisar; }
    public void setPpRevisar(int ppRevisar) { this.ppRevisar = ppRevisar; }

    public int getPpAprobar() { return ppAprobar; }
    public void setPpAprobar(int ppAprobar) { this.ppAprobar = ppAprobar; }

    public int getPpRechazar() { return ppRechazar; }
    public void setPpRechazar(int ppRechazar) { this.ppRechazar = ppRechazar; }

    // ---------------- FORMATOS ----------------
    public int getfPorcentaje() { return fPorcentaje; }
    public void setfPorcentaje(int fPorcentaje) { this.fPorcentaje = fPorcentaje; }

    public int getfRevisar() { return fRevisar; }
    public void setfRevisar(int fRevisar) { this.fRevisar = fRevisar; }

    public int getfAprobar() { return fAprobar; }
    public void setfAprobar(int fAprobar) { this.fAprobar = fAprobar; }

    public int getfRechazar() { return fRechazar; }
    public void setfRechazar(int fRechazar) { this.fRechazar = fRechazar; }

    // ---------------- GLOBAL ----------------
    public int getGlobalPorcentaje() { return globalPorcentaje; }
    public void setGlobalPorcentaje(int globalPorcentaje) { this.globalPorcentaje = globalPorcentaje; }
}

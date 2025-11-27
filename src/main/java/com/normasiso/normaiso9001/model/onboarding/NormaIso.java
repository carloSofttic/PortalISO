package com.normasiso.normaiso9001.model.onboarding;

public class NormaIso {
  private Long id;
  private String codigo;   // ej. "ISO 9001"
  private String nombre;   // ej. "Gestión de la calidad"

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
}

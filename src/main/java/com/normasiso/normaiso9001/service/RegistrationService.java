package com.normasiso.normaiso9001.service;

import com.normasiso.normaiso9001.dto.RegisterForm;
import com.normasiso.normaiso9001.repository.clientRegister.RegistrationRepository;
import com.normasiso.normaiso9001.multitenant.TenantSchemaService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@Service
public class RegistrationService {

  private final RegistrationRepository repo;
  private final PasswordEncoder passwordEncoder;

  // Servicio que se encarga de generar el nombre del schema
  // y de ejecutar el CREATE SCHEMA + CREATE TABLE formato
  private final TenantSchemaService tenantSchemaService;

  // Constructor con inyección de dependencias
  public RegistrationService(RegistrationRepository repo,
                             PasswordEncoder passwordEncoder,
                             TenantSchemaService tenantSchemaService) {
    this.repo = repo;
    this.passwordEncoder = passwordEncoder;
    this.tenantSchemaService = tenantSchemaService;
  }

  /**
   * Registra:
   * 1) Compañía
   * 2) Usuario (representante)
   * 3) Miembro
   * 4) Crea el schema del tenant + tabla formato
   * 5) Guarda el schema_name en la tabla COMPANIA
   */
  @Transactional
  public void register(RegisterForm f) {

    // ========= Validaciones básicas de campos obligatorios =========
    if (f.getCorreo() == null || f.getCorreo().isBlank()) {
      throw new IllegalStateException("El correo electrónico es obligatorio.");
    }
    if (f.getNombreEmpresa() == null || f.getNombreEmpresa().isBlank()) {
      throw new IllegalStateException("El nombre de la empresa es obligatorio.");
    }

    // Normaliza el nombre de la empresa (quita espacios al inicio/fin)
    String nombreEmpresa = f.getNombreEmpresa().trim();

    // ========= Validar que no exista ya una compañía con ese nombre =========
    if (repo.existsCompaniaByNombre(nombreEmpresa)) {
      throw new IllegalStateException("Ya existe una empresa registrada con ese nombre.");
    }

    // ========= Generar username del representante =========
    // Se generan iniciales + 5 dígitos aleatorios
    String username = generarUsername(
        f.getNombreMiembro(),
        f.getAppaternoMiembro(),
        f.getApmaternoMiembro()
    );

    // Si el username ya existe, se regenera hasta encontrar uno libre
    while (repo.existsUsuarioByUsername(username)) {
      username = generarUsername(
          f.getNombreMiembro(),
          f.getAppaternoMiembro(),
          f.getApmaternoMiembro()
      );
    }

    // ========= Validaciones de correo =========
    String correo = f.getCorreo().trim().toLowerCase();
    if (repo.existsUsuarioByCorreo(correo)) {
      throw new IllegalStateException("Ese correo ya está registrado.");
    }

    // ========= Validar y obtener fecha de creación de la empresa =========
    LocalDate fechaCreacion = f.getFechaCreacion();
    if (fechaCreacion == null) {
      throw new IllegalStateException("La fecha de creación es obligatoria (yyyy-MM-dd).");
    }

    // ========= Normalizar RFC (opcional) =========
    String rfc = (f.getRfc() == null || f.getRfc().isBlank())
        ? null
        : f.getRfc().trim().toUpperCase();

    // ========= 1) Crear compañía en tabla COMPANIA =========
    // Devuelve el id_compania generado
    Long idCompania = repo.insertCompania(
        nombreEmpresa,
        rfc,
        fechaCreacion
    );

    // ========= 2) Crear usuario (representante) en tabla USUARIO =========
    // Se guarda la contraseña ya encriptada con PasswordEncoder
    Long idUsuario = repo.insertUsuario(
        username,
        correo,
        passwordEncoder.encode(f.getPassword()),
        true  // enabled = true
    );

    // ========= 3) Crear miembro (representante) en tabla MIEMBRO =========
    repo.insertMiembro(
        idCompania,
        idUsuario,
        f.getNombreMiembro()     == null ? null : f.getNombreMiembro().trim(),
        f.getAppaternoMiembro()  == null ? null : f.getAppaternoMiembro().trim(),
        f.getApmaternoMiembro()  == null ? null : f.getApmaternoMiembro().trim(),
        f.getTelefono()          == null ? null : f.getTelefono().trim(),
        f.getRolMiembro()        == null ? null : f.getRolMiembro().trim(),
        correo
    );

    // ========= 4) Crear schema del tenant + tabla formato =========
    // 4.1 Generar nombre de schema a partir del nombre de la empresa y su id
    //     Ejemplo: "Farmacia El Águila" + id=5 -> tenant_farmacia_el_aguila_5
    String schemaName = tenantSchemaService.generarNombreSchema(nombreEmpresa, idCompania);

    // 4.2 Crear en la base de datos:
    //     - CREATE SCHEMA schemaName;
    //     - CREATE TABLE schemaName.formato (...)
    tenantSchemaService.crearSchemaYTablaFormato(schemaName);

    // 4.3 Guardar el nombre del schema en la tabla COMPANIA (columna schema_name)
    repo.updateCompaniaSchemaName(idCompania, schemaName);

    System.out.println("✅ Usuario creado: " + username + " | schema creado: " + schemaName);
  }

  // ---------------------------------------------------------------------------
  // Método auxiliar para generar un username: iniciales + 5 dígitos aleatorios
  // ---------------------------------------------------------------------------
  private String generarUsername(String nombre, String apPaterno, String apMaterno) {
    // Inicial del nombre
    String inicialNombre = (nombre != null && !nombre.isBlank())
        ? nombre.substring(0, 1).toUpperCase()
        : "X";

    // Inicial del apellido paterno
    String inicialApPat = (apPaterno != null && !apPaterno.isBlank())
        ? apPaterno.substring(0, 1).toUpperCase()
        : "X";

    // Inicial del apellido materno (puede no existir)
    String inicialApMat = (apMaterno != null && !apMaterno.isBlank())
        ? apMaterno.substring(0, 1).toUpperCase()
        : "";

    // Generar 5 dígitos aleatorios (entre 10000 y 99999)
    int numeros = new Random().nextInt(90000) + 10000;

    // Ejemplo final: AXB12345
    return inicialNombre + inicialApPat + inicialApMat + numeros;
  }
}

package com.normasiso.normaiso9001.multitenant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TenantSchemaService {

    private final JdbcTemplate jdbcTemplate;

    public TenantSchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Crea un schema para la organización y dentro la tabla formato.
     */
    public void crearSchemaYTablaFormato(String schemaName) {
        // MUY IMPORTANTE: schemaName debe venir saneado (solo letras, números, guiones bajos)
        String createSchemaSql = "CREATE SCHEMA " + schemaName + ";";

        String createTableSql = "CREATE TABLE " + schemaName + ".formato ("
                + "id_formato BIGSERIAL PRIMARY KEY, "
                + "nombre_format VARCHAR(255), "
                + "codigo_format VARCHAR(100), "
                + "tipo_doc_format VARCHAR(100), "
                + "fecha_vencimiento_format DATE, "
                + "status_format VARCHAR(50) DEFAULT 'revision'"
                + ");";

        jdbcTemplate.execute(createSchemaSql);
        jdbcTemplate.execute(createTableSql);
    }

    /**
     * Convierte el nombre de la empresa a un nombre de schema válido.
     * Ej: "Farmacia El Águila S.A." -> "tenant_farmacia_el_aguila"
     */
    public String generarNombreSchema(String nombreEmpresa, Long idOrganizacion) {
        String slug = nombreEmpresa
                .toLowerCase()
                .replaceAll("[áàä]", "a")
                .replaceAll("[éèë]", "e")
                .replaceAll("[íìï]", "i")
                .replaceAll("[óòö]", "o")
                .replaceAll("[úùü]", "u")
                .replaceAll("[ñ]", "n")
                .replaceAll("[^a-z0-9]+", "_") // todo lo raro -> _
                .replaceAll("^_+", "")         // quitar _ inicial
                .replaceAll("_+$", "");        // quitar _ final

        if (slug.isEmpty()) {
            slug = "empresa";
        } else if (!Character.isLetter(slug.charAt(0))) {
            slug = "e_" + slug; // asegurar que empieza por letra
        }

        // incluir el id para garantizar que no se repita
        return "tenant_" + slug + "_" + idOrganizacion;
    }
}

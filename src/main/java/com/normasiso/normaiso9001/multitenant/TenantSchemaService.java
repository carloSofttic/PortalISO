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
        // MUY IMPORTANTE: schemaName debe venir saneado (solo letras, números, guiones
        // bajos)
        String createSchemaSql = "CREATE SCHEMA " + schemaName + ";";

        String createTableSql = "CREATE TABLE " + schemaName + ".DOCUMENTO ("
                + "id_documento BIGSERIAL PRIMARY KEY, "
                + "nombre_documento VARCHAR(255), "
                + "codigo_documento VARCHAR(14), "
                + "tipo_doc_format VARCHAR(4), "
                + "fecha_vencimiento_format DATE, "
                + "fecha_cambio_doc DATE, "
                + "fecha_emision_doc DATE, "
                + "status_format VARCHAR(50) DEFAULT 'Revision', "
                + "metodo_resguardo VARCHAR(20) DEFAULT 'Digital' "
                + ");";

        String createTable2Sql = "CREATE TABLE " + schemaName + ".PROCESO ("
                + "id_proceso BIGSERIAL PRIMARY KEY, "
                + "nombre_proceso VARCHAR(255) "
                + ");";

        String createTable3Sql = "CREATE TABLE " + schemaName + ".PROCESO_DOC ("
                + "  id_procesoDoc BIGSERIAL PRIMARY KEY, "
                + "  id_proceso BIGINT NOT NULL, "
                + "  id_documento BIGINT NOT NULL, "
                + "  FOREIGN KEY (id_proceso) "
                + "      REFERENCES PROCESO(id_proceso) "
                + "      ON UPDATE CASCADE "
                + "      ON DELETE CASCADE, "
                + "  FOREIGN KEY (id_documento) "
                + "      REFERENCES DOCUMENTO(id_documento) "
                + "      ON UPDATE CASCADE "
                + "      ON DELETE CASCADE "
                + ");";

        jdbcTemplate.execute(createSchemaSql);
        jdbcTemplate.execute(createTableSql);
        jdbcTemplate.execute(createTable2Sql);
        jdbcTemplate.execute(createTable3Sql);
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
                .replaceAll("^_+", "") // quitar _ inicial
                .replaceAll("_+$", ""); // quitar _ final

        if (slug.isEmpty()) {
            slug = "empresa";
        } else if (!Character.isLetter(slug.charAt(0))) {
            slug = "e_" + slug; // asegurar que empieza por letra
        }

        // incluir el id para garantizar que no se repita
        return "tenant_" + slug + "_" + idOrganizacion;
    }
}

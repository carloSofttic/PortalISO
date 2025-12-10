package com.normasiso.normaiso9001.dto.catalogsCrud;

public class CatalogosDocumentosAdminDTO {

    private Long idTipoDoc;
    private String nombreTipoDoc;   // tipo_documento
    private String codigoTipoDoc;   // codigo_tipodoc

    // ===== GETTERS & SETTERS =====

    public Long getIdTipoDoc() {
        return idTipoDoc;
    }

    public void setIdTipoDoc(Long idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    public String getNombreTipoDoc() {
        return nombreTipoDoc;
    }

    public void setNombreTipoDoc(String nombreTipoDoc) {
        this.nombreTipoDoc = nombreTipoDoc;
    }

    public String getCodigoTipoDoc() {
        return codigoTipoDoc;
    }

    public void setCodigoTipoDoc(String codigoTipoDoc) {
        this.codigoTipoDoc = codigoTipoDoc;
    }
}

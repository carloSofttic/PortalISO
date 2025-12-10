// DTO simple
package com.normasiso.normaiso9001.dto.proceduresCrud;

public class TipoDocDTO {
    private Long idTipoDoc;
    private String tipoDocumento;
    private String codigoTipoDoc;

    public Long getIdTipoDoc() { return idTipoDoc; }
    public void setIdTipoDoc(Long idTipoDoc) { this.idTipoDoc = idTipoDoc; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getCodigoTipoDoc() { return codigoTipoDoc; }
    public void setCodigoTipoDoc(String codigoTipoDoc) { this.codigoTipoDoc = codigoTipoDoc; }
}

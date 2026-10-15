/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Timestamp;

/**
 * Entidad que representa un comprobante de pago.
 * 
 * Almacena la información del documento emitido para respaldar
 * una transacción económica realizada en la clínica.
 * 
 * @author Grupo04
 */
public class Comprobante {
    
    private int idComprobante;
    private int idPago;
    private String numeroComprobante;
    private String tipoComprobante;
    private Timestamp fechaEmision;

    /**
     * Constructor por defecto.
     */
    public Comprobante() {}

    /**
     * Constructor con los datos principales del comprobante.
     * 
     * @param idPago              Identificador del pago asociado
     * @param numeroComprobante   Número único del comprobante
     * @param tipoComprobante     Tipo de comprobante (BOLETA o FACTURA)
     */
    public Comprobante(int idPago, String numeroComprobante, String tipoComprobante) {
        this.idPago = idPago;
        this.numeroComprobante = numeroComprobante;
        this.tipoComprobante = tipoComprobante;
    }

    // MÉTODOS GETTER Y SETTER

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Timestamp getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Timestamp fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    /**
     * Devuelve una representación textual del comprobante.
     * 
     * @return Cadena con el tipo y número del comprobante
     */
    @Override
    public String toString() {
        return tipoComprobante + " N° " + numeroComprobante;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entidad que representa un pago realizado.
 *
 * Registra las transacciones económicas asociadas a atenciones médicas,
 * incluyendo el monto, método de pago utilizado y el cajero que procesó la
 * operación.
 *
 * @author Grupo04
 */
public class Pago {

    private int idPago;
    private int idAtencion;
    private int idCajero;
    private BigDecimal monto;
    private String metodoPago;
    private Timestamp fechaPago;

    /**
     * Constructor por defecto.
     */
    public Pago() {
    }

    /**
     * Constructor con los datos principales del pago.
     *
     * @param idAtencion Identificador de la atención médica asociada
     * @param idCajero Identificador del cajero que registra el pago
     * @param monto Monto total de la transacción
     * @param metodoPago Método utilizado (EFECTIVO, TARJETA, TRANSFERENCIA,
     * BILLETERA_ELECTRONICA)
     */
    public Pago(int idAtencion, int idCajero, BigDecimal monto, String metodoPago) {
        this.idAtencion = idAtencion;
        this.idCajero = idCajero;
        this.monto = monto;
        this.metodoPago = metodoPago;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public int getIdCajero() {
        return idCajero;
    }

    public void setIdCajero(int idCajero) {
        this.idCajero = idCajero;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Timestamp getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Timestamp fechaPago) {
        this.fechaPago = fechaPago;
    }

    /**
     * Devuelve una representación textual del pago.
     *
     * @return Cadena con el identificador, monto y método de pago
     */
    @Override
    public String toString() {
        return "Pago ID: " + idPago + " - Monto: S/" + monto + " - Método: " + metodoPago;
    }
}

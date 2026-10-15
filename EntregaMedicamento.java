/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Timestamp;

/**
 * Entidad que representa la entrega de medicamentos.
 *
 * Registra la dispensación de medicamentos recetados a pacientes, incluyendo la
 * cantidad entregada y el responsable de la operación.
 *
 * @author Grupo04
 */
public class EntregaMedicamento {

    private int idEntrega;
    private int idRecetaDetalle;
    private int idFarmaceutico;
    private int cantidadEntregada;
    private Timestamp fechaEntrega;

    /**
     * Constructor por defecto.
     */
    public EntregaMedicamento() {
    }

    /**
     * Constructor con los datos principales de la entrega.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @param idFarmaceutico Identificador del farmacéutico que realiza la
     * entrega
     * @param cantidadEntregada Cantidad de medicamento dispensada
     */
    public EntregaMedicamento(int idRecetaDetalle, int idFarmaceutico, int cantidadEntregada) {
        this.idRecetaDetalle = idRecetaDetalle;
        this.idFarmaceutico = idFarmaceutico;
        this.cantidadEntregada = cantidadEntregada;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(int idEntrega) {
        this.idEntrega = idEntrega;
    }

    public int getIdRecetaDetalle() {
        return idRecetaDetalle;
    }

    public void setIdRecetaDetalle(int idRecetaDetalle) {
        this.idRecetaDetalle = idRecetaDetalle;
    }

    public int getIdFarmaceutico() {
        return idFarmaceutico;
    }

    public void setIdFarmaceutico(int idFarmaceutico) {
        this.idFarmaceutico = idFarmaceutico;
    }

    public int getCantidadEntregada() {
        return cantidadEntregada;
    }

    public void setCantidadEntregada(int cantidadEntregada) {
        this.cantidadEntregada = cantidadEntregada;
    }

    public Timestamp getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Timestamp fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    /**
     * Devuelve una representación textual de la entrega.
     *
     * @return Cadena con el identificador y la cantidad entregada
     */
    @Override
    public String toString() {
        return "Entrega ID: " + idEntrega + " - Cantidad: " + cantidadEntregada;
    }
}

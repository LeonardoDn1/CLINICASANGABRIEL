/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 * Entidad que representa el detalle de una receta médica.
 *
 * Especifica los medicamentos prescritos en una receta, incluyendo la cantidad
 * y las indicaciones de uso para cada uno.
 *
 * @author Grupo04
 */
public class RecetaDetalle {

    private int idRecetaDetalle;
    private int idReceta;
    private int idMedicamento;
    private int cantidad;
    private String indicaciones;

    /**
     * Constructor por defecto.
     */
    public RecetaDetalle() {
    }

    /**
     * Constructor con los datos principales del detalle.
     *
     * @param idReceta Identificador de la receta
     * @param idMedicamento Identificador del medicamento prescrito
     * @param cantidad Cantidad prescrita
     * @param indicaciones Instrucciones de uso o dosificación
     */
    public RecetaDetalle(int idReceta, int idMedicamento, int cantidad, String indicaciones) {
        this.idReceta = idReceta;
        this.idMedicamento = idMedicamento;
        this.cantidad = cantidad;
        this.indicaciones = indicaciones;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdRecetaDetalle() {
        return idRecetaDetalle;
    }

    public void setIdRecetaDetalle(int idRecetaDetalle) {
        this.idRecetaDetalle = idRecetaDetalle;
    }

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public int getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    /**
     * Devuelve una representación textual del detalle.
     *
     * @return Cadena con el identificador del medicamento y la cantidad
     */
    @Override
    public String toString() {
        return "Medicamento ID: " + idMedicamento + " - Cantidad: " + cantidad;
    }
}

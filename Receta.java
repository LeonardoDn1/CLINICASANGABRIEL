/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Timestamp;

/**
 * Entidad que representa una receta médica electrónica.
 *
 * Agrupa los medicamentos prescritos durante una atención médica, permitiendo
 * su posterior dispensación en farmacia.
 *
 * @author Grupo04
 */
public class Receta {

    private int idReceta;
    private int idAtencion;
    private Timestamp fechaEmision;

    /**
     * Constructor por defecto.
     */
    public Receta() {
    }

    /**
     * Constructor con la atención médica asociada.
     *
     * @param idAtencion Identificador de la atención médica
     */
    public Receta(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    /**
     * Constructor con todos los parámetros.
     *
     * @param idReceta Identificador único de la receta
     * @param idAtencion Identificador de la atención médica asociada
     * @param fechaEmision Fecha y hora de emisión de la receta
     */
    public Receta(int idReceta, int idAtencion, Timestamp fechaEmision) {
        this.idReceta = idReceta;
        this.idAtencion = idAtencion;
        this.fechaEmision = fechaEmision;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public Timestamp getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Timestamp fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    /**
     * Devuelve una representación textual de la receta.
     *
     * @return Cadena con los identificadores de receta y atención
     */
    @Override
    public String toString() {
        return "Receta ID: " + idReceta + " - Atención ID: " + idAtencion;
    }
}

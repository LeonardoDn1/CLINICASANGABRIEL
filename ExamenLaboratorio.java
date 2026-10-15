/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Timestamp;

/**
 * Entidad que representa la solicitud de un examen de laboratorio.
 *
 * Gestiona las órdenes médicas para exámenes clínicos y de diagnóstico,
 * permitiendo el seguimiento de su estado hasta la entrega de resultados.
 *
 * @author Grupo04
 */
public class ExamenLaboratorio {

    private int idExamen;
    private int idAtencion;
    private String tipoExamen;
    private String estado;
    private Timestamp fechaSolicitud;

    /**
     * Constructor por defecto.
     */
    public ExamenLaboratorio() {
    }

    /**
     * Constructor con los datos principales del examen.
     *
     * @param idAtencion Identificador de la atención médica asociada
     * @param tipoExamen Tipo de examen solicitado
     * @param estado Estado actual de la solicitud
     */
    public ExamenLaboratorio(int idAtencion, String tipoExamen, String estado) {
        this.idAtencion = idAtencion;
        this.tipoExamen = tipoExamen;
        this.estado = estado;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public String getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Timestamp fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Devuelve una representación textual del examen.
     *
     * @return Cadena con el tipo de examen y su estado actual
     */
    @Override
    public String toString() {
        return tipoExamen + " - Estado: " + estado;
    }
}

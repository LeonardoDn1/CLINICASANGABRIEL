/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Timestamp;

/**
 * Entidad que representa el resultado de un examen de laboratorio.
 *
 * Almacena los hallazgos y conclusiones de los análisis clínicos realizados,
 * permitiendo al médico consultar la información para respaldar sus
 * diagnósticos.
 *
 * @author Grupo04
 */
public class ResultadoLaboratorio {

    private int idResultado;
    private int idExamen;
    private int idLaboratorista;
    private String resultadoTexto;
    private String rutaArchivo;
    private Timestamp fechaResultado;

    /**
     * Constructor por defecto.
     */
    public ResultadoLaboratorio() {
    }

    /**
     * Constructor con los datos principales del resultado.
     *
     * @param idExamen Identificador del examen asociado
     * @param idLaboratorista Identificador del laboratorista que registra el
     * resultado
     * @param resultadoTexto Descripción textual del resultado
     * @param rutaArchivo Ruta del archivo adjunto (si existe)
     */
    public ResultadoLaboratorio(int idExamen, int idLaboratorista,
            String resultadoTexto, String rutaArchivo) {
        this.idExamen = idExamen;
        this.idLaboratorista = idLaboratorista;
        this.resultadoTexto = resultadoTexto;
        this.rutaArchivo = rutaArchivo;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdResultado() {
        return idResultado;
    }

    public void setIdResultado(int idResultado) {
        this.idResultado = idResultado;
    }

    public int getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
    }

    public int getIdLaboratorista() {
        return idLaboratorista;
    }

    public void setIdLaboratorista(int idLaboratorista) {
        this.idLaboratorista = idLaboratorista;
    }

    public String getResultadoTexto() {
        return resultadoTexto;
    }

    public void setResultadoTexto(String resultadoTexto) {
        this.resultadoTexto = resultadoTexto;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public Timestamp getFechaResultado() {
        return fechaResultado;
    }

    public void setFechaResultado(Timestamp fechaResultado) {
        this.fechaResultado = fechaResultado;
    }

    /**
     * Devuelve una representación textual del resultado.
     *
     * @return Cadena con el identificador del examen asociado
     */
    @Override
    public String toString() {
        return "Resultado - Examen ID: " + idExamen;
    }
}

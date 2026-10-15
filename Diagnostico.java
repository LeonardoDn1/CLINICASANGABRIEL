/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 * Entidad que representa un diagnóstico médico.
 *
 * Almacena la identificación y descripción de las condiciones clínicas
 * determinadas durante una atención médica, utilizando el estándar CIE-10.
 *
 * @author Grupo04
 */
public class Diagnostico {

    private int idDiagnostico;
    private int idAtencion;
    private String codigoCie10;
    private String descripcion;

    /**
     * Constructor por defecto.
     */
    public Diagnostico() {
    }

    /**
     * Constructor con los datos principales del diagnóstico.
     *
     * @param idAtencion Identificador de la atención médica asociada
     * @param codigoCie10 Código estándar CIE-10 del diagnóstico
     * @param descripcion Descripción textual del diagnóstico
     */
    public Diagnostico(int idAtencion, String codigoCie10, String descripcion) {
        this.idAtencion = idAtencion;
        this.codigoCie10 = codigoCie10;
        this.descripcion = descripcion;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdDiagnostico() {
        return idDiagnostico;
    }

    public void setIdDiagnostico(int idDiagnostico) {
        this.idDiagnostico = idDiagnostico;
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public String getCodigoCie10() {
        return codigoCie10;
    }

    public void setCodigoCie10(String codigoCie10) {
        this.codigoCie10 = codigoCie10;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve una representación textual del diagnóstico.
     *
     * @return Cadena con el código CIE-10 y su descripción
     */
    @Override
    public String toString() {
        return codigoCie10 + " - " + descripcion;
    }
}

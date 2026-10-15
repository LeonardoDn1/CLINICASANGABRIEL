/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Date;

/**
 * Entidad que representa el seguro médico de un paciente.
 *
 * Almacena la información de la póliza de salud del paciente, incluyendo la
 * aseguradora, número de póliza y período de vigencia.
 *
 * @author Grupo04
 */
public class SeguroMedico {

    private int idSeguro;
    private int idPaciente;
    private String aseguradora;
    private String numeroPoliza;
    private Date vigenciaInicio;
    private Date vigenciaFin;

    /**
     * Constructor por defecto.
     */
    public SeguroMedico() {
    }

    /**
     * Constructor con los datos principales del seguro.
     *
     * @param idPaciente Identificador del paciente asegurado
     * @param aseguradora Nombre de la empresa aseguradora
     * @param numeroPoliza Número de la póliza de salud
     * @param vigenciaInicio Fecha de inicio de la vigencia
     * @param vigenciaFin Fecha de fin de la vigencia
     */
    public SeguroMedico(int idPaciente, String aseguradora, String numeroPoliza,
            Date vigenciaInicio, Date vigenciaFin) {
        this.idPaciente = idPaciente;
        this.aseguradora = aseguradora;
        this.numeroPoliza = numeroPoliza;
        this.vigenciaInicio = vigenciaInicio;
        this.vigenciaFin = vigenciaFin;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdSeguro() {
        return idSeguro;
    }

    public void setIdSeguro(int idSeguro) {
        this.idSeguro = idSeguro;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getAseguradora() {
        return aseguradora;
    }

    public void setAseguradora(String aseguradora) {
        this.aseguradora = aseguradora;
    }

    public String getNumeroPoliza() {
        return numeroPoliza;
    }

    public void setNumeroPoliza(String numeroPoliza) {
        this.numeroPoliza = numeroPoliza;
    }

    public Date getVigenciaInicio() {
        return vigenciaInicio;
    }

    public void setVigenciaInicio(Date vigenciaInicio) {
        this.vigenciaInicio = vigenciaInicio;
    }

    public Date getVigenciaFin() {
        return vigenciaFin;
    }

    public void setVigenciaFin(Date vigenciaFin) {
        this.vigenciaFin = vigenciaFin;
    }

    /**
     * Devuelve una representación textual del seguro.
     *
     * @return Cadena con el nombre de la aseguradora y número de póliza
     */
    @Override
    public String toString() {
        return aseguradora + " - Póliza: " + numeroPoliza;
    }
}

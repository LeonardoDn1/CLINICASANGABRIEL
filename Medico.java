/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 * Entidad que representa a un médico.
 *
 * Vincula a un usuario del sistema con su información profesional, incluyendo
 * el número de colegiatura que lo identifica como profesional de la salud
 * habilitado.
 *
 * @author Grupo04
 */
public class Medico {

    private int idMedico;
    private int idUsuario;
    private String numeroColegiatura;
    private String nombreCompleto;

    /**
     * Constructor por defecto.
     */
    public Medico() {
    }

    /**
     * Constructor con los datos principales del médico.
     *
     * @param idMedico Identificador único del médico
     * @param idUsuario Identificador del usuario asociado
     * @param numeroColegiatura Número de colegiatura profesional
     */
    public Medico(int idMedico, int idUsuario, String numeroColegiatura) {
        this.idMedico = idMedico;
        this.idUsuario = idUsuario;
        this.numeroColegiatura = numeroColegiatura;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNumeroColegiatura() {
        return numeroColegiatura;
    }

    public void setNumeroColegiatura(String numeroColegiatura) {
        this.numeroColegiatura = numeroColegiatura;
    }
    
    public String getNombreCompleto() {
    return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    /**
     * Devuelve una representación textual del médico.
     *
     * @return Cadena con el identificador y número de colegiatura
     */
    @Override
    public String toString() {

    if(nombreCompleto != null && !nombreCompleto.isEmpty()) {
        return nombreCompleto + " - " + numeroColegiatura;
    }

    return "Médico ID: " + idMedico + " - Colegiatura: " + numeroColegiatura;
    }
}

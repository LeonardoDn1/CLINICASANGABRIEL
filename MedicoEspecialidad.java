/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 * Entidad que representa la relación entre médicos y especialidades.
 *
 * Establece la asociación muchos-a-muchos entre médicos y las áreas de
 * especialización en las que están habilitados para atender.
 *
 * @author Grupo04
 */
public class MedicoEspecialidad {

    private int idMedico;
    private int idEspecialidad;

    /**
     * Constructor por defecto.
     */
    public MedicoEspecialidad() {
    }

    /**
     * Constructor con los identificadores de la relación.
     *
     * @param idMedico Identificador del médico
     * @param idEspecialidad Identificador de la especialidad
     */
    public MedicoEspecialidad(int idMedico, int idEspecialidad) {
        this.idMedico = idMedico;
        this.idEspecialidad = idEspecialidad;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    /**
     * Devuelve una representación textual de la relación.
     *
     * @return Cadena con los identificadores del médico y especialidad
     */
    @Override
    public String toString() {
        return "Médico ID: " + idMedico + " - Especialidad ID: " + idEspecialidad;
    }
}

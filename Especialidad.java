/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 * Entidad que representa una especialidad médica.
 *
 * Define las diferentes áreas de especialización médica disponibles en la
 * clínica, las cuales son asignadas a los médicos.
 *
 * @author Fernandog
 */
public class Especialidad {

    private int idEspecialidad;
    private String nombre;

    /**
     * Constructor por defecto.
     */
    public Especialidad() {
    }

    /**
     * Constructor con los datos principales de la especialidad.
     *
     * @param idEspecialidad Identificador único de la especialidad
     * @param nombre Nombre de la especialidad médica
     */
    public Especialidad(int idEspecialidad, String nombre) {
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el nombre de la especialidad.
     *
     * @return Nombre de la especialidad
     */
    @Override
    public String toString() {
        return nombre;
    }
}

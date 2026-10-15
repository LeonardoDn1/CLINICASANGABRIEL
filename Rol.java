/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 * Entidad que representa un rol de usuario en el sistema.
 *
 * Define los perfiles institucionales disponibles para los usuarios,
 * determinando las funciones y accesos permitidos dentro de la aplicación.
 *
 * @author Grupo04
 */
public class Rol {

    private int idRol;
    private String nombreRol;
    private String descripcion;

    /**
     * Constructor por defecto.
     */
    public Rol() {
    }

    /**
     * Constructor con los datos principales del rol.
     *
     * @param idRol Identificador único del rol
     * @param nombreRol Nombre del rol institucional
     * @param descripcion Descripción de las funciones del rol
     */
    public Rol(int idRol, String nombreRol, String descripcion) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve el nombre del rol.
     *
     * @return Nombre del rol
     */
    @Override
    public String toString() {
        return nombreRol;
    }
}

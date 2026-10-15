/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Timestamp;

/**
 * Entidad que representa un usuario del sistema.
 *
 * Gestiona las credenciales de acceso y la información personal de las personas
 * autorizadas para utilizar la aplicación, incluyendo su rol y estado de
 * actividad.
 *
 * @author Grupo04
 */
public class Usuario {

    private int idUsuario;
    private String username;
    private String passwordHash;
    private int idRol;
    private String nombres;
    private String apellidos;
    private String email;
    private String estado;
    private Timestamp fechaCreacion;
    private Timestamp ultimoAcceso;

    /**
     * Constructor por defecto.
     */
    public Usuario() {
    }

    /**
     * Constructor con los datos principales del usuario.
     *
     * @param idUsuario Identificador único del usuario
     * @param username Nombre de usuario para el inicio de sesión
     * @param passwordHash Hash de la contraseña (almacenado de forma segura)
     * @param idRol Identificador del rol asignado al usuario
     * @param nombres Nombres del usuario
     * @param apellidos Apellidos del usuario
     * @param email Correo electrónico de contacto
     * @param estado Estado actual del usuario (ACTIVO o BLOQUEADO)
     */
    public Usuario(int idUsuario, String username, String passwordHash, int idRol,
            String nombres, String apellidos, String email, String estado) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.passwordHash = passwordHash;
        this.idRol = idRol;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.estado = estado;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Timestamp getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(Timestamp ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    /**
     * Devuelve una representación textual del usuario.
     *
     * @return Cadena con el nombre completo y nombre de usuario
     */
    @Override
    public String toString() {
        return nombres + " " + apellidos + " (" + username + ")";
    }

    // PATRÓN BUILDER
    /**
     * Clase interna para construcción fluida de objetos Usuario.
     */
    public static class Builder {

        private String username;
        private String passwordHash;
        private int idRol;
        private String nombres;
        private String apellidos;
        private int idUsuario;
        private String email;
        private String estado = "ACTIVO";
        private Timestamp fechaCreacion;
        private Timestamp ultimoAcceso;

        /**
         * Constructor del Builder con parámetros obligatorios.
         *
         * @param username Nombre de usuario para el inicio de sesión
         * @param passwordHash Hash de la contraseña
         * @param idRol Identificador del rol asignado
         * @param nombres Nombres del usuario
         * @param apellidos Apellidos del usuario
         */
        public Builder(String username, String passwordHash, int idRol,
                String nombres, String apellidos) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.idRol = idRol;
            this.nombres = nombres;
            this.apellidos = apellidos;
        }

        public Builder idUsuario(int idUsuario) {
            this.idUsuario = idUsuario;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public Builder fechaCreacion(Timestamp fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public Builder ultimoAcceso(Timestamp ultimoAcceso) {
            this.ultimoAcceso = ultimoAcceso;
            return this;
        }

        /**
         * Construye la instancia final de Usuario.
         *
         * @return Instancia de Usuario con los atributos configurados
         */
        public Usuario build() {
            Usuario usuario = new Usuario();
            usuario.idUsuario = this.idUsuario;
            usuario.username = this.username;
            usuario.passwordHash = this.passwordHash;
            usuario.idRol = this.idRol;
            usuario.nombres = this.nombres;
            usuario.apellidos = this.apellidos;
            usuario.email = this.email;
            usuario.estado = this.estado;
            usuario.fechaCreacion = this.fechaCreacion;
            usuario.ultimoAcceso = this.ultimoAcceso;
            return usuario;
        }
    }
}

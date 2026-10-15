/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entidad que representa a un paciente.
 *
 * Almacena la información personal y clínica de los pacientes atendidos en la
 * clínica, incluyendo datos de identificación, contacto y el número único de
 * historia clínica para su seguimiento.
 *
 * @author Grupo04
 */
public class Paciente {

    private int idPaciente;
    private String numeroHistoriaClinica;
    private String dni;
    private String nombres;
    private String apellidos;
    private Date fechaNacimiento;
    private String sexo;
    private String telefono;
    private String direccion;
    private String estado;
    private Timestamp fechaRegistro;

    /**
     * Constructor por defecto.
     */
    public Paciente() {
    }

    /**
     * Constructor con todos los parámetros del paciente.
     *
     * @param idPaciente Identificador único del paciente
     * @param numeroHistoriaClinica Número único de historia clínica
     * @param dni Documento nacional de identidad
     * @param nombres Nombres del paciente
     * @param apellidos Apellidos del paciente
     * @param fechaNacimiento Fecha de nacimiento
     * @param sexo Sexo (M o F)
     * @param telefono Número de teléfono de contacto
     * @param direccion Dirección de residencia
     * @param estado Estado del registro (ACTIVO o INACTIVO)
     */
    public Paciente(int idPaciente, String numeroHistoriaClinica, String dni,
            String nombres, String apellidos, Date fechaNacimiento,
            String sexo, String telefono, String direccion, String estado) {
        this.idPaciente = idPaciente;
        this.numeroHistoriaClinica = numeroHistoriaClinica;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = estado;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNumeroHistoriaClinica() {
        return numeroHistoriaClinica;
    }

    public void setNumeroHistoriaClinica(String numeroHistoriaClinica) {
        this.numeroHistoriaClinica = numeroHistoriaClinica;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Devuelve una representación textual del paciente.
     *
     * @return Cadena con el nombre completo y DNI
     */
    @Override
    public String toString() {
        return nombres + " " + apellidos + " (DNI: " + dni + ")";
    }

    // PATRÓN BUILDER
    /**
     * Clase interna para construcción fluida de objetos Paciente.
     */
    public static class Builder {

        private String dni;
        private String nombres;
        private String apellidos;
        private Date fechaNacimiento;
        private String sexo;
        private String telefono;
        private String direccion;
        private int idPaciente;
        private String numeroHistoriaClinica;
        private String estado = "ACTIVO";
        private Timestamp fechaRegistro;

        /**
         * Constructor del Builder con parámetros obligatorios.
         *
         * @param dni Documento nacional de identidad
         * @param nombres Nombres del paciente
         * @param apellidos Apellidos del paciente
         * @param fechaNacimiento Fecha de nacimiento
         * @param sexo Sexo (M o F)
         * @param telefono Número de teléfono de contacto
         * @param direccion Dirección de residencia
         */
        public Builder(String dni, String nombres, String apellidos,
                Date fechaNacimiento, String sexo, String telefono, String direccion) {
            this.dni = dni;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.fechaNacimiento = fechaNacimiento;
            this.sexo = sexo;
            this.telefono = telefono;
            this.direccion = direccion;
        }

        public Builder idPaciente(int idPaciente) {
            this.idPaciente = idPaciente;
            return this;
        }

        public Builder numeroHistoriaClinica(String numeroHistoriaClinica) {
            this.numeroHistoriaClinica = numeroHistoriaClinica;
            return this;
        }

        public Builder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public Builder fechaRegistro(Timestamp fechaRegistro) {
            this.fechaRegistro = fechaRegistro;
            return this;
        }

        /**
         * Construye la instancia final de Paciente.
         *
         * @return Instancia de Paciente con los atributos configurados
         */
        public Paciente build() {
            Paciente paciente = new Paciente();
            paciente.idPaciente = this.idPaciente;
            paciente.numeroHistoriaClinica = this.numeroHistoriaClinica;
            paciente.dni = this.dni;
            paciente.nombres = this.nombres;
            paciente.apellidos = this.apellidos;
            paciente.fechaNacimiento = this.fechaNacimiento;
            paciente.sexo = this.sexo;
            paciente.telefono = this.telefono;
            paciente.direccion = this.direccion;
            paciente.estado = this.estado;
            paciente.fechaRegistro = this.fechaRegistro;
            return paciente;
        }
    }
}

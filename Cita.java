/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Entidad que representa una cita médica.
 * 
 * Gestiona la programación de consultas entre pacientes y médicos,
 * incluyendo fecha, hora, estado y validaciones de disponibilidad.
 * 
 * @author Grupo04
 */
public class Cita {
    
    private int idCita;
    private int idPaciente;
    private int idMedico;
    private Date fecha;
    private Time horaInicio;
    private Time horaFin;
    private String estado;
    private Timestamp fechaCreacion;

    /**
     * Constructor por defecto.
     */
    public Cita() {}

    /**
     * Constructor con los datos principales de la cita.
     * 
     * @param idPaciente  Identificador del paciente
     * @param idMedico    Identificador del médico
     * @param fecha       Fecha de la cita
     * @param horaInicio  Hora de inicio
     * @param horaFin     Hora de fin
     * @param estado      Estado actual de la cita
     */
    public Cita(int idPaciente, int idMedico, Date fecha,
                Time horaInicio, Time horaFin, String estado) {
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Time getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Time horaFin) {
        this.horaFin = horaFin;
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

    /**
     * Devuelve una representación textual de la cita.
     * 
     * @return Cadena con el identificador, fecha y hora de inicio
     */
    @Override
    public String toString() {
        return "Cita ID: " + idCita + " - Fecha: " + fecha + " " + horaInicio;
    }

    // PATRÓN BUILDER

    /**
     * Clase interna para construcción fluida de objetos Cita.
     */
    public static class Builder {
        
        private int idPaciente;
        private int idMedico;
        private Date fecha;
        private Time horaInicio;
        private int idCita;
        private Time horaFin;
        private String estado = "PROGRAMADA";
        private Timestamp fechaCreacion;

        /**
         * Constructor del Builder con parámetros obligatorios.
         * 
         * @param idPaciente  Identificador del paciente
         * @param idMedico    Identificador del médico
         * @param fecha       Fecha de la cita
         * @param horaInicio  Hora de inicio
         */
        public Builder(int idPaciente, int idMedico, Date fecha, Time horaInicio) {
            this.idPaciente = idPaciente;
            this.idMedico = idMedico;
            this.fecha = fecha;
            this.horaInicio = horaInicio;
        }

        public Builder idCita(int idCita) {
            this.idCita = idCita;
            return this;
        }

        public Builder horaFin(Time horaFin) {
            this.horaFin = horaFin;
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

        /**
         * Construye la instancia final de Cita.
         * 
         * @return Instancia de Cita con los atributos configurados
         */
        public Cita build() {
            Cita cita = new Cita();
            cita.idCita = this.idCita;
            cita.idPaciente = this.idPaciente;
            cita.idMedico = this.idMedico;
            cita.fecha = this.fecha;
            cita.horaInicio = this.horaInicio;
            cita.horaFin = this.horaFin;
            cita.estado = this.estado;
            cita.fechaCreacion = this.fechaCreacion;
            return cita;
        }
    }
}
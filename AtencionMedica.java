/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Timestamp;

/**
 * Entidad que representa una atención médica.
 *
 * Esta clase encapsula toda la información relacionada con una consulta o
 * procedimiento médico realizado a un paciente. Contiene los datos clínicos
 * documentados durante la atención, incluyendo el motivo de la consulta,
 * antecedentes, tratamiento aplicado y observaciones relevantes para el
 * historial clínico. La entidad mantiene una relación directa con una cita
 * médica a través del campo idCita, lo que garantiza que toda atención esté
 * respaldada por una cita programada previamente.
 *
 * @author Grupo04
 */
public class AtencionMedica {

    private int idAtencion;
    private int idCita;
    private String motivoConsulta;
    private String antecedentes;
    private String tratamiento;
    private String observaciones;
    private boolean cerrada;
    private Timestamp fechaAtencion;

    /**
     * Constructor por defecto. Inicializa una instancia vacía de la entidad
     * AtencionMedica.
     */
    public AtencionMedica() {
    }

    /**
     * Constructor con todos los parámetros.
     *
     * @param idAtencion Identificador único de la atención
     * @param idCita Identificador de la cita asociada
     * @param motivoConsulta Razón principal de la consulta médica
     * @param antecedentes Historial clínico previo del paciente
     * @param tratamiento Plan de tratamiento establecido
     * @param observaciones Notas adicionales del médico
     * @param cerrada Estado de cierre de la atención
     * @param fechaAtencion Fecha y hora en que se realizó la atención
     */
    public AtencionMedica(int idAtencion, int idCita, String motivoConsulta,
            String antecedentes, String tratamiento,
            String observaciones, boolean cerrada, Timestamp fechaAtencion) {
        this.idAtencion = idAtencion;
        this.idCita = idCita;
        this.motivoConsulta = motivoConsulta;
        this.antecedentes = antecedentes;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.cerrada = cerrada;
        this.fechaAtencion = fechaAtencion;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(String antecedentes) {
        this.antecedentes = antecedentes;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isCerrada() {
        return cerrada;
    }

    public void setCerrada(boolean cerrada) {
        this.cerrada = cerrada;
    }

    public Timestamp getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(Timestamp fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    /**
     * Devuelve una representación textual de la atención médica.
     *
     * @return Cadena que contiene el identificador de la atención y el
     * identificador de la cita asociada
     */
    @Override
    public String toString() {
        return "Atención ID: " + idAtencion + " - Cita ID: " + idCita;
    }

    // PATRÓN BUILDER
    /**
     * Clase interna que implementa el patrón Builder para la construcción
     * fluida de objetos AtencionMedica.
     *
     * Este patrón permite crear instancias complejas especificando únicamente
     * los atributos necesarios, mejorando la legibilidad del código y evitando
     * la sobrecarga de constructores.
     */
    public static class Builder {

        private int idCita;
        private String motivoConsulta;
        private int idAtencion;
        private String antecedentes;
        private String tratamiento;
        private String observaciones;
        private boolean cerrada = false;
        private Timestamp fechaAtencion;

        /**
         * Constructor del Builder con los parámetros obligatorios.
         *
         * @param idCita Identificador de la cita asociada
         * @param motivoConsulta Razón principal de la consulta médica
         */
        public Builder(int idCita, String motivoConsulta) {
            this.idCita = idCita;
            this.motivoConsulta = motivoConsulta;
        }

        /**
         * Establece el identificador de la atención.
         *
         * @param idAtencion Identificador único de la atención
         * @return La instancia actual del Builder para encadenamiento
         */
        public Builder idAtencion(int idAtencion) {
            this.idAtencion = idAtencion;
            return this;
        }

        /**
         * Establece los antecedentes clínicos del paciente.
         *
         * @param antecedentes Historial clínico previo
         * @return La instancia actual del Builder para encadenamiento
         */
        public Builder antecedentes(String antecedentes) {
            this.antecedentes = antecedentes;
            return this;
        }

        /**
         * Establece el plan de tratamiento.
         *
         * @param tratamiento Plan de tratamiento establecido
         * @return La instancia actual del Builder para encadenamiento
         */
        public Builder tratamiento(String tratamiento) {
            this.tratamiento = tratamiento;
            return this;
        }

        /**
         * Establece las observaciones adicionales del médico.
         *
         * @param observaciones Notas adicionales de la consulta
         * @return La instancia actual del Builder para encadenamiento
         */
        public Builder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        /**
         * Establece el estado de cierre de la atención.
         *
         * @param cerrada Estado que indica si la atención está finalizada
         * @return La instancia actual del Builder para encadenamiento
         */
        public Builder cerrada(boolean cerrada) {
            this.cerrada = cerrada;
            return this;
        }

        /**
         * Establece la fecha y hora de la atención.
         *
         * @param fechaAtencion Fecha y hora en que se realizó la atención
         * @return La instancia actual del Builder para encadenamiento
         */
        public Builder fechaAtencion(Timestamp fechaAtencion) {
            this.fechaAtencion = fechaAtencion;
            return this;
        }

        /**
         * Construye la instancia final de AtencionMedica.
         *
         * @return La instancia de AtencionMedica con los atributos configurados
         */
        public AtencionMedica build() {
            AtencionMedica atencion = new AtencionMedica();
            atencion.idAtencion = this.idAtencion;
            atencion.idCita = this.idCita;
            atencion.motivoConsulta = this.motivoConsulta;
            atencion.antecedentes = this.antecedentes;
            atencion.tratamiento = this.tratamiento;
            atencion.observaciones = this.observaciones;
            atencion.cerrada = this.cerrada;
            atencion.fechaAtencion = this.fechaAtencion;
            return atencion;
        }
    }
}

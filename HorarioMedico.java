/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Time;

/**
 * Entidad que representa el horario de atención de un médico.
 *
 * Define los días y rangos horarios en los que un médico se encuentra
 * disponible para recibir citas, permitiendo la gestión de su agenda.
 *
 * @author Grupo04
 */
public class HorarioMedico {

    private int idHorario;
    private int idMedico;
    private String diaSemana;
    private Time horaInicio;
    private Time horaFin;

    /**
     * Constructor por defecto.
     */
    public HorarioMedico() {
    }

    /**
     * Constructor con los datos principales del horario.
     *
     * @param idMedico Identificador del médico
     * @param diaSemana Día de la semana (LUNES, MARTES, ...)
     * @param horaInicio Hora de inicio de la jornada
     * @param horaFin Hora de finalización de la jornada
     */
    public HorarioMedico(int idMedico, String diaSemana, Time horaInicio, Time horaFin) {
        this.idMedico = idMedico;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
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

    /**
     * Devuelve una representación textual del horario.
     *
     * @return Cadena con el día, hora de inicio y hora de fin
     */
    @Override
    public String toString() {
        return diaSemana + " " + horaInicio + " - " + horaFin;
    }
}

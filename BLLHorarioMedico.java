/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import entidades.*;
import datos.*;
import java.util.*;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Capa de lógica de negocio para la entidad HorarioMedico.
 *
 * Gestiona el registro, consulta y actualización de los horarios de atención de
 * los médicos.
 *
 * @author Grupo04
 */
public class BLLHorarioMedico {

    /**
     * Registra el horario de atención de un médico.
     *
     * @param idMedico Identificador del médico
     * @param diaSemana Día de la semana (LUNES, MARTES, ...)
     * @param horaInicio Hora de inicio de la jornada
     * @param horaFin Hora de finalización de la jornada
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarHorario(int idMedico, String diaSemana, java.sql.Time horaInicio, java.sql.Time horaFin) {
        if (idMedico > 0 && diaSemana != null && !diaSemana.trim().isEmpty()
                && horaInicio != null && horaFin != null && horaInicio.before(horaFin)) {

            HorarioMedico horario = new HorarioMedico(idMedico, diaSemana, horaInicio, horaFin);
            DAL_HorarioMedico dal = new DAL_HorarioMedico();

            if (dal.insertar(horario)) {
                showMessageDialog(null, "Registro insertado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al insertar registro", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Busca un horario por su identificador.
     *
     * @param idHorario Identificador del horario
     * @return Objeto HorarioMedico encontrado, o null si no existe
     */
    public HorarioMedico buscarHorario(int idHorario) {
        if (idHorario > 0) {
            DAL_HorarioMedico dal = new DAL_HorarioMedico();
            return dal.buscarPorId(idHorario);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista los horarios registrados de un médico específico.
     *
     * @param idMedico Identificador del médico
     * @return Lista de horarios del médico
     */
    public ArrayList<HorarioMedico> listarPorMedico(int idMedico) {
        DAL_HorarioMedico dal = new DAL_HorarioMedico();
        return new ArrayList<>(dal.listarPorMedico(idMedico));
    }

    /**
     * Busca horarios de un médico según el día de la semana.
     *
     * @param idMedico Identificador del médico
     * @param diaSemana Día de la semana a consultar
     * @return Lista de horarios del médico en el día especificado
     */
    public ArrayList<HorarioMedico> buscarPorMedicoYDia(int idMedico, String diaSemana) {
        DAL_HorarioMedico dal = new DAL_HorarioMedico();
        return new ArrayList<>(dal.buscarPorMedicoYDia(idMedico, diaSemana));
    }

    /**
     * Lista todos los horarios registrados.
     *
     * @return Lista de objetos HorarioMedico
     */
    public ArrayList<HorarioMedico> listarHorarios() {
        DAL_HorarioMedico dal = new DAL_HorarioMedico();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza el horario de atención de un médico.
     *
     * @param idHorario Identificador del horario
     * @param diaSemana Nuevo día de la semana
     * @param horaInicio Nueva hora de inicio
     * @param horaFin Nueva hora de fin
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarHorario(int idHorario, String diaSemana, java.sql.Time horaInicio, java.sql.Time horaFin) {
        if (idHorario > 0 && diaSemana != null && !diaSemana.trim().isEmpty()
                && horaInicio != null && horaFin != null && horaInicio.before(horaFin)) {

            HorarioMedico horario = new HorarioMedico(0, diaSemana, horaInicio, horaFin);
            horario.setIdHorario(idHorario);

            DAL_HorarioMedico dal = new DAL_HorarioMedico();

            if (dal.actualizar(horario)) {
                showMessageDialog(null, "Registro actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar registro", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Elimina un horario por su identificador.
     *
     * @param idHorario Identificador del horario
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarHorario(int idHorario) {
        if (idHorario > 0) {
            DAL_HorarioMedico dal = new DAL_HorarioMedico();
            if (dal.eliminarLogico(idHorario)) {
                showMessageDialog(null, "Registro eliminado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo eliminar el registro", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return 2;
        }
    }
}

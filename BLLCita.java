/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import entidades.*;
import datos.*;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Time;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Capa de lógica de negocio para la entidad Cita.
 *
 * Gestiona la programación, consulta, actualización y cancelación de citas
 * médicas, incluyendo validaciones de disponibilidad y control de estados.
 *
 * @author Grupo04
 */
public class BLLCita {

    /**
     * Registra una nueva cita médica.
     *
     * @param idPaciente Identificador del paciente
     * @param idMedico Identificador del médico
     * @param fecha Fecha de la cita
     * @param horaInicio Hora de inicio de la cita
     * @param horaFin Hora de fin de la cita
     * @param estado Estado inicial de la cita
     * @return 0 si fue exitoso, 1-4 si ocurrió un error
     */
    public int insertarCita(int idPaciente, int idMedico, Date fecha, Time horaInicio, Time horaFin, String estado) {
        if (idPaciente > 0 && idMedico > 0 && fecha != null && horaInicio != null && horaFin != null) {
            DAL_Cita dal = new DAL_Cita();
            ArrayList<Cita> citas = new ArrayList<>(dal.listarPorMedicoYFecha(idMedico, fecha));

            for (Cita c : citas) {
                if (c.getHoraInicio().equals(horaInicio)) {
                    showMessageDialog(null, "El médico ya tiene una cita registrada en ese horario", "Error", 0);
                    return 1;
                }
            }

            long diferencia = horaFin.getTime() - horaInicio.getTime();
            if (diferencia != 1800000) {
                showMessageDialog(null, "La duración de la cita debe ser de 30 minutos", "Error", 0);
                return 2;
            }

            if (estado == null || estado.trim().isEmpty()) {
                estado = "PROGRAMADA";
            }

            Cita cita = new Cita(idPaciente, idMedico, fecha, horaInicio, horaFin, estado);

            if (dal.insertar(cita)) {
                showMessageDialog(null, "Registro insertado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al insertar registro", "Error", 0);
                return 3;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 4;
        }
    }

    /**
     * Busca una cita por su identificador.
     *
     * @param idCita Identificador de la cita
     * @return Objeto Cita encontrado, o null si no existe
     */
    public Cita buscarCita(int idCita) {
        if (idCita > 0) {
            DAL_Cita dal = new DAL_Cita();
            return dal.buscarPorId(idCita);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todas las citas registradas.
     *
     * @return Lista de objetos Cita
     */
    public ArrayList<Cita> listarCita() {
        DAL_Cita dal = new DAL_Cita();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Consulta las citas asignadas a un médico en una fecha determinada.
     *
     * @param idMedico Identificador del médico
     * @param fecha Fecha a consultar
     * @return Lista de citas del médico en la fecha indicada
     */
    public ArrayList<Cita> listarCitasPorMedicoFecha(int idMedico, Date fecha) {
        if (idMedico > 0 && fecha != null) {
            DAL_Cita dal = new DAL_Cita();
            return new ArrayList<>(dal.listarPorMedicoYFecha(idMedico, fecha));
        }
        return new ArrayList<>();
    }

    /**
     * Consulta el historial de citas de un paciente.
     *
     * @param idPaciente Identificador del paciente
     * @return Lista de citas del paciente
     */
    public ArrayList<Cita> listarCitasPorPaciente(int idPaciente) {
        if (idPaciente > 0) {
            DAL_Cita dal = new DAL_Cita();
            return new ArrayList<>(dal.listarPorPaciente(idPaciente));
        }
        return new ArrayList<>();
    }

    /**
     * Actualiza la información de una cita existente. No permite modificar
     * citas que ya han sido atendidas.
     *
     * @param idCita Identificador de la cita
     * @param fecha Nueva fecha de la cita
     * @param horaInicio Nueva hora de inicio
     * @param horaFin Nueva hora de fin
     * @param estado Nuevo estado de la cita
     * @return 0 si fue exitoso, 1-3 si ocurrió un error
     */
    public int actualizarCita(int idCita, Date fecha, Time horaInicio, Time horaFin, String estado) {
        if (idCita > 0 && fecha != null && horaInicio != null && horaFin != null) {
            DAL_Cita dal = new DAL_Cita();
            Cita citaActual = dal.buscarPorId(idCita);

            if (citaActual != null && citaActual.getEstado().equals("ATENDIDA")) {
                showMessageDialog(null, "No se puede modificar una cita atendida", "Error", 0);
                return 1;
            }

            Cita cita = new Cita();
            cita.setIdCita(idCita);
            cita.setFecha(fecha);
            cita.setHoraInicio(horaInicio);
            cita.setHoraFin(horaFin);
            cita.setEstado(estado);

            if (dal.actualizar(cita)) {
                showMessageDialog(null, "Registro actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar registro", "Error", 0);
                return 2;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 3;
        }
    }

    /**
     * Cancela una cita registrada (cambia su estado a CANCELADA).
     *
     * @param idCita Identificador de la cita a cancelar
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int cancelarCita(int idCita) {
        if (idCita > 0) {
            DAL_Cita dal = new DAL_Cita();
            if (dal.eliminarLogico(idCita)) {
                showMessageDialog(null, "Cita cancelada", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo cancelar la cita", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return 2;
        }
    }

    /**
     * Cambia el estado de una cita a uno de los estados permitidos.
     *
     * @param idCita Identificador de la cita
     * @param estado Nuevo estado (PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA,
     * NO_ASISTIO)
     * @return 0 si fue exitoso, 1-3 si ocurrió un error
     */
    public int cambiarEstadoCita(int idCita, String estado) {
        if (idCita > 0 && estado != null) {
            if (!estado.equals("PROGRAMADA") && !estado.equals("CONFIRMADA")
                    && !estado.equals("ATENDIDA") && !estado.equals("CANCELADA")
                    && !estado.equals("NO_ASISTIO")) {

                showMessageDialog(null, "Estado no valido", "Error", 0);
                return 1;
            }

            DAL_Cita dal = new DAL_Cita();

            if (dal.cambiarEstado(idCita, estado)) {
                showMessageDialog(null, "Estado actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al cambiar estado", "Error", 0);
                return 2;
            }
        }
        showMessageDialog(null, "Datos no validos", "Error", 0);
        return 3;
    }
}

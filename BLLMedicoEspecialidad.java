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
 * Capa de lógica de negocio para la relación Medico-Especialidad.
 *
 * Gestiona la asignación y eliminación de especialidades a los médicos,
 * manteniendo la integridad referencial entre ambas entidades.
 *
 * @author Grupo04
 */
public class BLLMedicoEspecialidad {

    /**
     * Asigna una especialidad a un médico.
     *
     * @param idMedico Identificador del médico
     * @param idEspecialidad Identificador de la especialidad
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarMedicoEspecialidad(int idMedico, int idEspecialidad) {
        if (idMedico > 0 && idEspecialidad > 0) {
            MedicoEspecialidad me = new MedicoEspecialidad(idMedico, idEspecialidad);
            DAL_MedicoEspecialidad dal = new DAL_MedicoEspecialidad();

            if (dal.insertar(me)) {
                showMessageDialog(null, "Especialidad asignada correctamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al asignar especialidad", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Elimina una especialidad específica de un médico.
     *
     * @param idMedico Identificador del médico
     * @param idEspecialidad Identificador de la especialidad a eliminar
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int eliminar(int idMedico, int idEspecialidad) {
        if (idMedico > 0 && idEspecialidad > 0) {
            DAL_MedicoEspecialidad dal = new DAL_MedicoEspecialidad();

            if (dal.eliminar(idMedico, idEspecialidad)) {
                showMessageDialog(null, "Especialidad eliminada correctamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo eliminar la especialidad", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Elimina todas las especialidades asignadas a un médico.
     *
     * @param idMedico Identificador del médico
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarEspecialidadesMedico(int idMedico) {
        if (idMedico > 0) {
            DAL_MedicoEspecialidad dal = new DAL_MedicoEspecialidad();

            if (dal.eliminarPorMedico(idMedico)) {
                showMessageDialog(null, "Especialidades eliminadas correctamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudieron eliminar las especialidades", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return 2;
        }
    }

    /**
     * Consulta las especialidades asignadas a un médico específico.
     *
     * @param idMedico Identificador del médico
     * @return Lista de relaciones Medico-Especialidad del médico
     */
    public ArrayList<MedicoEspecialidad> listarPorMedico(int idMedico) {
        DAL_MedicoEspecialidad dal = new DAL_MedicoEspecialidad();
        return new ArrayList<>(dal.listarPorMedico(idMedico));
    }

    /**
     * Lista todas las relaciones médico-especialidad registradas.
     *
     * @return Lista de objetos MedicoEspecialidad
     */
    public ArrayList<MedicoEspecialidad> listarMedicoEspecialidades() {
        DAL_MedicoEspecialidad dal = new DAL_MedicoEspecialidad();
        return new ArrayList<>(dal.listarTodos());
    }
}

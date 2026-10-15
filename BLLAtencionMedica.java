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
 * Capa de lógica de negocio para la entidad AtencionMedica.
 *
 * Gestiona el registro, consulta, actualización y cierre de las atenciones
 * médicas realizadas a los pacientes.
 *
 * @author Grupo04
 */
public class BLLAtencionMedica {

    /**
     * Registra una nueva atención médica.
     *
     * @param atencion Objeto AtencionMedica con los datos a registrar
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarAtencion(AtencionMedica atencion) {
        if (atencion != null && atencion.getIdCita() > 0
                && atencion.getMotivoConsulta() != null && !atencion.getMotivoConsulta().trim().isEmpty()
                && atencion.getTratamiento() != null && !atencion.getTratamiento().trim().isEmpty()) {

            DAL_AtencionMedica dal = new DAL_AtencionMedica();
            if (dal.insertar(atencion)) {
                return 0;
            } else {
                showMessageDialog(null, "Error al registrar atención médica", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos de atención no válidos", "Error", 0);
            return 2;
        }
    }

    /**
     * Busca una atención médica por su identificador.
     *
     * @param idAtencion Identificador de la atención
     * @return Objeto AtencionMedica encontrado, o null si no existe
     */
    public AtencionMedica buscarAtencion(int idAtencion) {
        if (idAtencion > 0) {
            DAL_AtencionMedica dal = new DAL_AtencionMedica();
            return dal.buscarPorId(idAtencion);
        } else {
            showMessageDialog(null, "Código no válido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca la atención asociada a una cita médica específica.
     *
     * @param idCita Identificador de la cita
     * @return Objeto AtencionMedica encontrado, o null si no existe
     */
    public AtencionMedica buscarPorCita(int idCita) {
        if (idCita > 0) {
            DAL_AtencionMedica dal = new DAL_AtencionMedica();
            return dal.buscarPorCita(idCita);
        } else {
            showMessageDialog(null, "Código de cita no válido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todas las atenciones médicas registradas.
     *
     * @return Lista de objetos AtencionMedica
     */
    public ArrayList<AtencionMedica> listarAtenciones() {
        DAL_AtencionMedica dal = new DAL_AtencionMedica();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza una atención médica existente. Las atenciones cerradas no
     * pueden ser modificadas.
     *
     * @param atencion Objeto AtencionMedica con los datos actualizados
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos o la atención está cerrada
     */
    public int actualizarAtencion(AtencionMedica atencion) {
        if (atencion != null && atencion.getIdAtencion() > 0) {
            DAL_AtencionMedica dal = new DAL_AtencionMedica();
            AtencionMedica aux = dal.buscarPorId(atencion.getIdAtencion());

            if (aux != null && aux.isCerrada()) {
                showMessageDialog(null, "La atención ya está cerrada", "Advertencia", 2);
                return 2;
            }

            if (dal.actualizar(atencion)) {
                showMessageDialog(null, "Registro actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar atención", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no válidos", "Error", 0);
            return 2;
        }
    }

    /**
     * Cierra una atención médica, impidiendo modificaciones posteriores.
     *
     * @param idAtencion Identificador de la atención a cerrar
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int cerrarAtencion(int idAtencion) {
        if (idAtencion > 0) {
            DAL_AtencionMedica dal = new DAL_AtencionMedica();
            if (dal.cerrarAtencion(idAtencion)) {
                showMessageDialog(null, "Atención cerrada correctamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo cerrar la atención", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no válido", "Error", 0);
            return 2;
        }
    }

    /**
     * Elimina lógicamente una atención (equivale a cerrarla).
     *
     * @param idAtencion Identificador de la atención
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarAtencion(int idAtencion) {
        if (idAtencion > 0) {
            DAL_AtencionMedica dal = new DAL_AtencionMedica();
            if (dal.eliminarLogico(idAtencion)) {
                showMessageDialog(null, "Atención cerrada correctamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo procesar la operación", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no válido", "Error", 0);
            return 2;
        }
    }
}

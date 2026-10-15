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
 * Capa de lógica de negocio para la entidad ExamenLaboratorio.
 *
 * Gestiona las solicitudes de exámenes de laboratorio, incluyendo el registro,
 * cambio de estado y consulta de solicitudes.
 *
 * @author Grupo04
 */
public class BLLExamenLaboratorio {

    /**
     * Registra una nueva solicitud de examen de laboratorio.
     *
     * @param idAtencion Identificador de la atención médica asociada
     * @param tipoExamen Tipo de examen solicitado
     * @param estado Estado inicial de la solicitud
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarExamen(int idAtencion, String tipoExamen, String estado) {
        if (idAtencion > 0 && tipoExamen != null && !tipoExamen.trim().isEmpty()) {
            if (estado == null || (!estado.equals("PENDIENTE") && !estado.equals("EN_PROCESO")
                    && !estado.equals("FINALIZADO") && !estado.equals("ENTREGADO"))) {
                estado = "PENDIENTE";
            }

            ExamenLaboratorio examen = new ExamenLaboratorio(idAtencion, tipoExamen, estado);
            DAL_ExamenLaboratorio dal = new DAL_ExamenLaboratorio();

            if (dal.insertar(examen)) {
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
     * Busca un examen de laboratorio por su identificador.
     *
     * @param idExamen Identificador del examen
     * @return Objeto ExamenLaboratorio encontrado, o null si no existe
     */
    public ExamenLaboratorio buscarExamen(int idExamen) {
        if (idExamen > 0) {
            DAL_ExamenLaboratorio dal = new DAL_ExamenLaboratorio();
            return dal.buscarPorId(idExamen);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Cambia el estado de un examen validando la transición permitida.
     *
     * @param idExamen Identificador del examen
     * @param nuevoEstado Nuevo estado a asignar
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos o la transición no es permitida
     */
    public int cambiarEstadoExamen(int idExamen, String nuevoEstado) {
        if (idExamen <= 0 || nuevoEstado == null) {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }

        ExamenLaboratorio examen = buscarExamen(idExamen);
        if (examen == null) {
            showMessageDialog(null, "No se encontró el examen", "Error", 0);
            return 1;
        }

        String estadoActual = examen.getEstado();
        boolean transicionValida
                = ("PENDIENTE".equals(estadoActual) && "EN_PROCESO".equals(nuevoEstado))
                || ("EN_PROCESO".equals(estadoActual) && "FINALIZADO".equals(nuevoEstado))
                || ("FINALIZADO".equals(estadoActual) && "ENTREGADO".equals(nuevoEstado));

        if (!transicionValida) {
            showMessageDialog(null, "No se permite cambiar el examen de " + estadoActual + " a " + nuevoEstado,
                    "Estado no permitido", 0);
            return 2;
        }

        DAL_ExamenLaboratorio dal = new DAL_ExamenLaboratorio();
        if (dal.cambiarEstado(idExamen, nuevoEstado)) {
            return 0;
        } else {
            showMessageDialog(null, "No se pudo actualizar el estado", "Error", 0);
            return 1;
        }
    }

    /**
     * Lista los exámenes asociados a una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Lista de exámenes de la atención
     */
    public ArrayList<ExamenLaboratorio> listarPorAtencion(int idAtencion) {
        DAL_ExamenLaboratorio dal = new DAL_ExamenLaboratorio();
        return new ArrayList<>(dal.listarPorAtencion(idAtencion));
    }

    /**
     * Lista las solicitudes de examen filtradas por estado.
     *
     * @param estado Estado de las solicitudes (PENDIENTE, EN_PROCESO,
     * FINALIZADO, ENTREGADO)
     * @return Lista de exámenes con el estado especificado
     */
    public ArrayList<ExamenLaboratorio> listarPorEstado(String estado) {
        DAL_ExamenLaboratorio dal = new DAL_ExamenLaboratorio();
        return new ArrayList<>(dal.listarPorEstado(estado));
    }

    /**
     * Lista todas las solicitudes de examen registradas.
     *
     * @return Lista de objetos ExamenLaboratorio
     */
    public ArrayList<ExamenLaboratorio> listarExamenes() {
        DAL_ExamenLaboratorio dal = new DAL_ExamenLaboratorio();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza la información de una solicitud de examen.
     *
     * @param idExamen Identificador del examen
     * @param tipoExamen Nuevo tipo de examen
     * @param estado Nuevo estado del examen
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarExamen(int idExamen, String tipoExamen, String estado) {
        if (idExamen > 0 && tipoExamen != null && !tipoExamen.trim().isEmpty()) {
            if (!"PENDIENTE".equals(estado) && !"EN_PROCESO".equals(estado)
                    && !"FINALIZADO".equals(estado) && !"ENTREGADO".equals(estado)) {
                showMessageDialog(null, "Estado no valido", "Error", 0);
                return 2;
            }

            ExamenLaboratorio examen = new ExamenLaboratorio(0, tipoExamen, estado);
            examen.setIdExamen(idExamen);
            DAL_ExamenLaboratorio dal = new DAL_ExamenLaboratorio();

            if (dal.actualizar(examen)) {
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
     * Elimina una solicitud de examen por su identificador.
     *
     * @param idExamen Identificador del examen
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarExamen(int idExamen) {
        if (idExamen > 0) {
            DAL_ExamenLaboratorio dal = new DAL_ExamenLaboratorio();
            if (dal.eliminarLogico(idExamen)) {
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

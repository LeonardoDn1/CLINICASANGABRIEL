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
 * Capa de lógica de negocio para la entidad Auditoria.
 *
 * Gestiona el registro y consulta de eventos del sistema para mantener un
 * historial de operaciones realizadas por los usuarios.
 *
 * @author Grupo04
 */
public class BLLAuditoria {

    /**
     * Registra una operación realizada dentro del sistema.
     *
     * @param idUsuario Identificador del usuario que ejecutó la operación
     * @param modulo Módulo del sistema donde se realizó la operación
     * @param operacion Tipo de operación realizada
     * @param detalle Descripción detallada de la operación
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarAuditoria(int idUsuario, String modulo, String operacion, String detalle) {
        if (idUsuario > 0 && modulo != null && operacion != null && detalle != null
                && !modulo.trim().isEmpty() && !operacion.trim().isEmpty() && !detalle.trim().isEmpty()) {

            Auditoria auditoria = new Auditoria(idUsuario, modulo, operacion, detalle);
            DAL_Auditoria dal = new DAL_Auditoria();

            if (dal.insertar(auditoria)) {
                return 0;
            } else {
                showMessageDialog(null, "Error al registrar auditoría", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Busca un registro de auditoría por su identificador.
     *
     * @param idAuditoria Identificador del registro
     * @return Objeto Auditoria encontrado, o null si no existe
     */
    public Auditoria buscarAuditoria(long idAuditoria) {
        if (idAuditoria > 0) {
            DAL_Auditoria dal = new DAL_Auditoria();
            return dal.buscarPorId(idAuditoria);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista los registros de auditoría realizados por un usuario específico.
     *
     * @param idUsuario Identificador del usuario
     * @return Lista de registros de auditoría del usuario
     */
    public ArrayList<Auditoria> listarAuditoriaPorUsuario(int idUsuario) {
        if (idUsuario > 0) {
            DAL_Auditoria dal = new DAL_Auditoria();
            return new ArrayList<>(dal.listarPorUsuario(idUsuario));
        }
        return new ArrayList<>();
    }

    /**
     * Lista los registros de auditoría pertenecientes a un módulo específico.
     *
     * @param modulo Nombre del módulo del sistema
     * @return Lista de registros de auditoría del módulo
     */
    public ArrayList<Auditoria> listarAuditoriaPorModulo(String modulo) {
        if (modulo != null && !modulo.trim().isEmpty()) {
            DAL_Auditoria dal = new DAL_Auditoria();
            return new ArrayList<>(dal.listarPorModulo(modulo));
        }
        return new ArrayList<>();
    }

    /**
     * Lista todos los registros de auditoría almacenados.
     *
     * @return Lista de objetos Auditoria
     */
    public ArrayList<Auditoria> listarAuditoria() {
        DAL_Auditoria dal = new DAL_Auditoria();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza la información de un registro de auditoría.
     *
     * @param idAuditoria Identificador del registro a actualizar
     * @param idUsuario Identificador del usuario
     * @param modulo Módulo del sistema
     * @param operacion Tipo de operación realizada
     * @param detalle Descripción detallada de la operación
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarAuditoria(long idAuditoria, int idUsuario, String modulo, String operacion, String detalle) {
        if (idAuditoria > 0 && idUsuario > 0 && modulo != null && operacion != null && detalle != null
                && !modulo.trim().isEmpty() && !operacion.trim().isEmpty() && !detalle.trim().isEmpty()) {

            Auditoria auditoria = new Auditoria(idUsuario, modulo, operacion, detalle);
            auditoria.setIdAuditoria(idAuditoria);
            DAL_Auditoria dal = new DAL_Auditoria();

            if (dal.actualizar(auditoria)) {
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
     * Elimina un registro de auditoría por su identificador.
     *
     * @param idAuditoria Identificador del registro a eliminar
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarAuditoria(int idAuditoria) {
        if (idAuditoria > 0) {
            DAL_Auditoria dal = new DAL_Auditoria();
            if (dal.eliminarLogico(idAuditoria)) {
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

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
 * Capa de lógica de negocio para la entidad Especialidad.
 *
 * Gestiona el registro, consulta y actualización de las especialidades médicas
 * disponibles en la clínica.
 *
 * @author Fernandog
 */
public class BLLEspecialidad {

    /**
     * Registra una nueva especialidad médica.
     *
     * @param nombre Nombre de la especialidad
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarEspecialidad(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            Especialidad especialidad = new Especialidad(0, nombre);
            DAL_Especialidad dal = new DAL_Especialidad();

            if (dal.insertar(especialidad)) {
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
     * Busca una especialidad por su identificador.
     *
     * @param idEspecialidad Identificador de la especialidad
     * @return Objeto Especialidad encontrado, o null si no existe
     */
    public Especialidad buscarEspecialidad(int idEspecialidad) {
        if (idEspecialidad > 0) {
            DAL_Especialidad dal = new DAL_Especialidad();
            return dal.buscarPorId(idEspecialidad);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca una especialidad por su nombre exacto.
     *
     * @param nombre Nombre de la especialidad
     * @return Objeto Especialidad encontrado, o null si no existe
     */
    public Especialidad buscarEspecialidadPorNombre(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            DAL_Especialidad dal = new DAL_Especialidad();
            return dal.buscarPorNombre(nombre);
        }
        return null;
    }

    /**
     * Lista todas las especialidades registradas.
     *
     * @return Lista de objetos Especialidad
     */
    public ArrayList<Especialidad> listarEspecialidad() {
        DAL_Especialidad dal = new DAL_Especialidad();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza la información de una especialidad existente.
     *
     * @param idEspecialidad Identificador de la especialidad
     * @param nombre Nuevo nombre de la especialidad
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarEspecialidad(int idEspecialidad, String nombre) {
        if (idEspecialidad > 0 && nombre != null && !nombre.trim().isEmpty()) {
            Especialidad especialidad = new Especialidad(idEspecialidad, nombre);
            DAL_Especialidad dal = new DAL_Especialidad();

            if (dal.actualizar(especialidad)) {
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
     * Elimina una especialidad por su identificador.
     *
     * @param idEspecialidad Identificador de la especialidad
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarEspecialidad(int idEspecialidad) {
        if (idEspecialidad > 0) {
            DAL_Especialidad dal = new DAL_Especialidad();
            if (dal.eliminarLogico(idEspecialidad)) {
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

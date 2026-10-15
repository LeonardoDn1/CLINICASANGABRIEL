/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package datos;

import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD en la capa de acceso a datos.
 *
 * Define el contrato estándar que deben implementar todas las clases de acceso
 * a datos del sistema, garantizando operaciones básicas de persistencia para
 * cualquier tipo de entidad.
 *
 * @author Grupo04
 * @param <T> Tipo de entidad que gestiona el DAO
 */
public interface DAL_iCRUD<T> {

    /**
     * Inserta un nuevo registro en la base de datos.
     *
     * @param obj Objeto con los datos a insertar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean insertar(T obj);

    /**
     * Actualiza un registro existente en la base de datos.
     *
     * @param obj Objeto con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean actualizar(T obj);

    /**
     * Elimina lógicamente un registro (cambia su estado a inactivo).
     *
     * @param id Identificador del registro a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean eliminarLogico(int id);

    /**
     * Busca un registro por su identificador.
     *
     * @param id Identificador del registro
     * @return Objeto encontrado, o null si no existe
     */
    T buscarPorId(int id);

    /**
     * Lista todos los registros de la entidad.
     *
     * @return Lista de objetos de la entidad
     */
    List<T> listarTodos();
}

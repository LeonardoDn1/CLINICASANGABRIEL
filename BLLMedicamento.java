/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import entidades.*;
import datos.*;
import java.util.*;
import java.math.BigDecimal;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Capa de lógica de negocio para la entidad Medicamento.
 *
 * Gestiona el inventario de medicamentos, incluyendo el registro, consulta,
 * actualización de stock y control de niveles mínimos.
 *
 * @author Grupo04
 */
public class BLLMedicamento {

    /**
     * Registra un nuevo medicamento en el inventario.
     *
     * @param nombre Nombre comercial del medicamento
     * @param descripcion Descripción del medicamento
     * @param unidadMedida Unidad de medida (tabletas, ml, etc.)
     * @param stockActual Cantidad disponible en inventario
     * @param stockMinimo Nivel mínimo de stock para alerta
     * @param precioUnitario Precio por unidad
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarMedicamento(String nombre, String descripcion, String unidadMedida,
            int stockActual, int stockMinimo, BigDecimal precioUnitario) {
        if (nombre != null && !nombre.trim().isEmpty()
                && unidadMedida != null && !unidadMedida.trim().isEmpty()
                && stockActual >= 0 && stockMinimo >= 0
                && precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) >= 0) {

            Medicamento medicamento = new Medicamento(0, nombre, descripcion, unidadMedida,
                    stockActual, stockMinimo, precioUnitario);
            DAL_Medicamento dal = new DAL_Medicamento();

            if (dal.insertar(medicamento)) {
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
     * Busca un medicamento por su identificador.
     *
     * @param idMedicamento Identificador del medicamento
     * @return Objeto Medicamento encontrado, o null si no existe
     */
    public Medicamento buscarMedicamento(int idMedicamento) {
        if (idMedicamento > 0) {
            DAL_Medicamento dal = new DAL_Medicamento();
            return dal.buscarPorId(idMedicamento);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca medicamentos por nombre o descripción.
     *
     * @param criterio Texto a buscar en nombre o descripción
     * @return Lista de medicamentos que coinciden con el criterio
     */
    public ArrayList<Medicamento> buscarPorNombre(String criterio) {
        DAL_Medicamento dal = new DAL_Medicamento();
        return new ArrayList<>(dal.buscarPorNombre(criterio));
    }

    /**
     * Lista todos los medicamentos registrados.
     *
     * @return Lista de objetos Medicamento
     */
    public ArrayList<Medicamento> listarMedicamentos() {
        DAL_Medicamento dal = new DAL_Medicamento();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza la información de un medicamento existente.
     *
     * @param idMedicamento Identificador del medicamento
     * @param nombre Nuevo nombre del medicamento
     * @param descripcion Nueva descripción
     * @param unidadMedida Nueva unidad de medida
     * @param stockActual Nueva cantidad disponible
     * @param stockMinimo Nuevo nivel mínimo de stock
     * @param precioUnitario Nuevo precio por unidad
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarMedicamento(int idMedicamento, String nombre, String descripcion,
            String unidadMedida, int stockActual, int stockMinimo,
            BigDecimal precioUnitario) {
        if (idMedicamento > 0 && nombre != null && !nombre.trim().isEmpty()
                && unidadMedida != null && !unidadMedida.trim().isEmpty()
                && stockActual >= 0 && stockMinimo >= 0
                && precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) >= 0) {

            Medicamento medicamento = new Medicamento(idMedicamento, nombre, descripcion,
                    unidadMedida, stockActual, stockMinimo,
                    precioUnitario);
            DAL_Medicamento dal = new DAL_Medicamento();

            if (dal.actualizar(medicamento)) {
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
     * Actualiza la cantidad disponible de un medicamento.
     *
     * @param idMedicamento Identificador del medicamento
     * @param nuevoStock Nueva cantidad de stock
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarStock(int idMedicamento, int nuevoStock) {
        if (idMedicamento > 0 && nuevoStock >= 0) {
            DAL_Medicamento dal = new DAL_Medicamento();
            if (dal.actualizarStock(idMedicamento, nuevoStock)) {
                showMessageDialog(null, "Stock actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo actualizar el stock", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Lista los medicamentos con stock en nivel crítico.
     *
     * @return Lista de medicamentos con stock bajo
     */
    public ArrayList<Medicamento> listarStockBajo() {
        DAL_Medicamento dal = new DAL_Medicamento();
        return new ArrayList<>(dal.listarConStockBajo());
    }

    /**
     * Verifica si hay stock suficiente de un medicamento.
     *
     * @param idMedicamento Identificador del medicamento
     * @param cantidad Cantidad solicitada
     * @return true si hay stock suficiente, false en caso contrario
     */
    public boolean verificarStock(int idMedicamento, int cantidad) {
        if (idMedicamento > 0 && cantidad > 0) {
            DAL_Medicamento dal = new DAL_Medicamento();
            Medicamento medicamento = dal.buscarPorId(idMedicamento);
            if (medicamento != null) {
                return medicamento.getStockActual() >= cantidad;
            }
        }
        return false;
    }

    /**
     * Elimina un medicamento por su identificador.
     *
     * @param idMedicamento Identificador del medicamento
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarMedicamento(int idMedicamento) {
        if (idMedicamento > 0) {
            DAL_Medicamento dal = new DAL_Medicamento();
            if (dal.eliminarLogico(idMedicamento)) {
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

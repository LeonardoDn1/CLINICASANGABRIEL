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
 * Capa de lógica de negocio para la entidad RecetaDetalle.
 *
 * Gestiona el registro, consulta y actualización de los medicamentos prescritos
 * en las recetas médicas.
 *
 * @author Grupo04
 */
public class BLLRecetaDetalle {

    /**
     * Registra un medicamento dentro de una receta médica.
     *
     * @param idReceta Identificador de la receta
     * @param idMedicamento Identificador del medicamento prescrito
     * @param cantidad Cantidad prescrita (debe ser mayor que cero)
     * @param indicaciones Instrucciones de uso o dosificación
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarRecetaDetalle(int idReceta, int idMedicamento, int cantidad, String indicaciones) {
        if (idReceta > 0 && idMedicamento > 0 && cantidad > 0) {
            RecetaDetalle detalle = new RecetaDetalle(idReceta, idMedicamento, cantidad, indicaciones);
            DAL_RecetaDetalle dal = new DAL_RecetaDetalle();

            if (dal.insertar(detalle)) {
                showMessageDialog(null, "Medicamento agregado a la receta", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al agregar medicamento", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Busca un detalle de receta por su identificador.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @return Objeto RecetaDetalle encontrado, o null si no existe
     */
    public RecetaDetalle buscarRecetaDetalle(int idRecetaDetalle) {
        if (idRecetaDetalle > 0) {
            DAL_RecetaDetalle dal = new DAL_RecetaDetalle();
            return dal.buscarPorId(idRecetaDetalle);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todos los medicamentos asociados a una receta específica.
     *
     * @param idReceta Identificador de la receta
     * @return Lista de detalles de la receta
     */
    public ArrayList<RecetaDetalle> listarPorReceta(int idReceta) {
        if (idReceta > 0) {
            DAL_RecetaDetalle dal = new DAL_RecetaDetalle();
            return new ArrayList<>(dal.listarPorReceta(idReceta));
        } else {
            showMessageDialog(null, "Receta no valida", "Error", 0);
            return new ArrayList<>();
        }
    }

    /**
     * Lista todos los detalles de receta registrados.
     *
     * @return Lista de objetos RecetaDetalle
     */
    public ArrayList<RecetaDetalle> listarRecetaDetalles() {
        DAL_RecetaDetalle dal = new DAL_RecetaDetalle();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza un medicamento dentro del detalle de receta.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @param idMedicamento Nuevo identificador del medicamento
     * @param cantidad Nueva cantidad prescrita
     * @param indicaciones Nuevas indicaciones de uso
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarRecetaDetalle(int idRecetaDetalle, int idMedicamento, int cantidad, String indicaciones) {
        if (idRecetaDetalle > 0 && idMedicamento > 0 && cantidad > 0) {
            RecetaDetalle detalle = new RecetaDetalle(0, idMedicamento, cantidad, indicaciones);
            detalle.setIdRecetaDetalle(idRecetaDetalle);
            DAL_RecetaDetalle dal = new DAL_RecetaDetalle();

            if (dal.actualizar(detalle)) {
                showMessageDialog(null, "Detalle actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar detalle", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Elimina un detalle de receta por su identificador.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarRecetaDetalle(int idRecetaDetalle) {
        if (idRecetaDetalle > 0) {
            DAL_RecetaDetalle dal = new DAL_RecetaDetalle();
            if (dal.eliminarLogico(idRecetaDetalle)) {
                showMessageDialog(null, "Detalle eliminado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo eliminar detalle", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return 2;
        }
    }
}

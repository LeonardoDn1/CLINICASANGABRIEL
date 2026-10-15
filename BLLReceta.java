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
 * Capa de lógica de negocio para la entidad Receta.
 *
 * Gestiona el registro, consulta y actualización de recetas médicas
 * electrónicas asociadas a atenciones.
 *
 * @author Grupo04
 */
public class BLLReceta {

    /**
     * Registra una nueva receta electrónica asociada a una atención médica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarReceta(int idAtencion) {
        if (idAtencion > 0) {
            Receta receta = new Receta(idAtencion);
            DAL_Receta dal = new DAL_Receta();

            if (dal.insertar(receta)) {
                showMessageDialog(null, "Receta registrada", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al registrar receta", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Atención no valida", "Error", 0);
            return 2;
        }
    }

    /**
     * Busca una receta por su identificador.
     *
     * @param idReceta Identificador de la receta
     * @return Objeto Receta encontrado, o null si no existe
     */
    public Receta buscarReceta(int idReceta) {
        if (idReceta > 0) {
            DAL_Receta dal = new DAL_Receta();
            return dal.buscarPorId(idReceta);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca la receta asociada a una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Objeto Receta encontrado, o null si no existe
     */
    public Receta buscarPorAtencion(int idAtencion) {
        if (idAtencion > 0) {
            DAL_Receta dal = new DAL_Receta();
            return dal.buscarPorAtencion(idAtencion);
        } else {
            showMessageDialog(null, "Atención no valida", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todas las recetas registradas.
     *
     * @return Lista de objetos Receta
     */
    public ArrayList<Receta> listarRecetas() {
        DAL_Receta dal = new DAL_Receta();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza una receta existente.
     *
     * @param idReceta Identificador de la receta
     * @param idAtencion Nuevo identificador de la atención médica
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarReceta(int idReceta, int idAtencion) {
        if (idReceta > 0 && idAtencion > 0) {
            Receta receta = new Receta(idReceta, idAtencion, null);
            DAL_Receta dal = new DAL_Receta();

            if (dal.actualizar(receta)) {
                showMessageDialog(null, "Receta actualizada", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar receta", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Elimina una receta por su identificador.
     *
     * @param idReceta Identificador de la receta
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarReceta(int idReceta) {
        if (idReceta > 0) {
            DAL_Receta dal = new DAL_Receta();
            if (dal.eliminarLogico(idReceta)) {
                showMessageDialog(null, "Receta eliminada", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo eliminar la receta", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return 2;
        }
    }
}

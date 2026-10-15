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
 * Capa de lógica de negocio para la entidad SignosVitales.
 *
 * Gestiona el registro, consulta y actualización de los signos vitales tomados
 * durante las atenciones médicas.
 *
 * @author Grupo04
 */
public class BLLSignosVitales {

    /**
     * Registra los signos vitales asociados a una atención médica.
     *
     * @param signos Objeto SignosVitales con los datos a registrar
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarSignosVitales(SignosVitales signos) {
        if (signos != null && signos.getIdAtencion() > 0
                && signos.getPresionArterial() != null && !signos.getPresionArterial().trim().isEmpty()) {

            DAL_SignosVitales dal = new DAL_SignosVitales();

            if (dal.insertar(signos)) {
                showMessageDialog(null, "Signos vitales registrados correctamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al registrar signos vitales", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos de signos vitales incompletos", "Error", 0);
            return 2;
        }
    }

    /**
     * Actualiza los valores registrados de signos vitales.
     *
     * @param signos Objeto SignosVitales con los datos actualizados
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarSignosVitales(SignosVitales signos) {
        if (signos != null && signos.getIdSignos() > 0
                && signos.getPresionArterial() != null && !signos.getPresionArterial().trim().isEmpty()) {

            DAL_SignosVitales dal = new DAL_SignosVitales();

            if (dal.actualizar(signos)) {
                showMessageDialog(null, "Signos vitales actualizados", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar signos vitales", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos inválidos para actualizar", "Error", 0);
            return 2;
        }
    }

    /**
     * Busca signos vitales por su identificador.
     *
     * @param idSignos Identificador de los signos vitales
     * @return Objeto SignosVitales encontrado, o null si no existe
     */
    public SignosVitales buscarPorId(int idSignos) {
        if (idSignos > 0) {
            DAL_SignosVitales dal = new DAL_SignosVitales();
            return dal.buscarPorId(idSignos);
        } else {
            showMessageDialog(null, "Código de signos inválido", "Error", 0);
            return null;
        }
    }

    /**
     * Obtiene los signos vitales registrados en una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Objeto SignosVitales encontrado, o null si no existe
     */
    public SignosVitales buscarPorAtencion(int idAtencion) {
        if (idAtencion > 0) {
            DAL_SignosVitales dal = new DAL_SignosVitales();
            return dal.buscarPorAtencion(idAtencion);
        } else {
            showMessageDialog(null, "Atención médica inválida", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todos los registros de signos vitales.
     *
     * @return Lista de objetos SignosVitales
     */
    public ArrayList<SignosVitales> listarTodos() {
        DAL_SignosVitales dal = new DAL_SignosVitales();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Elimina un registro de signos vitales por su identificador.
     *
     * @param idSignos Identificador de los signos vitales
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarSignosVitales(int idSignos) {
        if (idSignos > 0) {
            DAL_SignosVitales dal = new DAL_SignosVitales();
            if (dal.eliminarLogico(idSignos)) {
                showMessageDialog(null, "Registro eliminado correctamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo eliminar registro", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código inválido", "Error", 0);
            return 2;
        }
    }
}

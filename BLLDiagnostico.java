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
 * Capa de lógica de negocio para la entidad Diagnostico.
 *
 * Gestiona el registro, consulta y actualización de diagnósticos asociados a
 * las atenciones médicas.
 *
 * @author Grupo04
 */
public class BLLDiagnostico {

    /**
     * Registra un nuevo diagnóstico asociado a una atención médica.
     *
     * @param idAtencion Identificador de la atención médica
     * @param codigoCie10 Código estándar CIE-10 del diagnóstico
     * @param descripcion Descripción textual del diagnóstico
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarDiagnostico(int idAtencion, String codigoCie10, String descripcion) {
        if (idAtencion > 0 && codigoCie10 != null && descripcion != null
                && !codigoCie10.trim().isEmpty() && !descripcion.trim().isEmpty()) {

            Diagnostico diagnostico = new Diagnostico(idAtencion, codigoCie10, descripcion);
            DAL_Diagnostico dal = new DAL_Diagnostico();

            if (dal.insertar(diagnostico)) {
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
     * Busca un diagnóstico por su identificador.
     *
     * @param idDiagnostico Identificador del diagnóstico
     * @return Objeto Diagnostico encontrado, o null si no existe
     */
    public Diagnostico buscarDiagnostico(int idDiagnostico) {
        if (idDiagnostico > 0) {
            DAL_Diagnostico dal = new DAL_Diagnostico();
            return dal.buscarPorId(idDiagnostico);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista los diagnósticos registrados en una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Lista de diagnósticos de la atención
     */
    public ArrayList<Diagnostico> listarDiagnosticosPorAtencion(int idAtencion) {
        if (idAtencion > 0) {
            DAL_Diagnostico dal = new DAL_Diagnostico();
            return new ArrayList<>(dal.listarPorAtencion(idAtencion));
        }
        return new ArrayList<>();
    }

    /**
     * Lista todos los diagnósticos registrados.
     *
     * @return Lista de objetos Diagnostico
     */
    public ArrayList<Diagnostico> listarDiagnostico() {
        DAL_Diagnostico dal = new DAL_Diagnostico();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza la información de un diagnóstico existente.
     *
     * @param idDiagnostico Identificador del diagnóstico
     * @param codigoCie10 Nuevo código CIE-10
     * @param descripcion Nueva descripción del diagnóstico
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarDiagnostico(int idDiagnostico, String codigoCie10, String descripcion) {
        if (idDiagnostico > 0 && codigoCie10 != null && descripcion != null
                && !codigoCie10.trim().isEmpty() && !descripcion.trim().isEmpty()) {

            Diagnostico diagnostico = new Diagnostico(0, codigoCie10, descripcion);
            diagnostico.setIdDiagnostico(idDiagnostico);
            DAL_Diagnostico dal = new DAL_Diagnostico();

            if (dal.actualizar(diagnostico)) {
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
     * Elimina un diagnóstico por su identificador.
     *
     * @param idDiagnostico Identificador del diagnóstico
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarDiagnostico(int idDiagnostico) {
        if (idDiagnostico > 0) {
            DAL_Diagnostico dal = new DAL_Diagnostico();
            if (dal.eliminarLogico(idDiagnostico)) {
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

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
 * Capa de lógica de negocio para la entidad ResultadoLaboratorio.
 *
 * Gestiona el registro, consulta y actualización de resultados de exámenes de
 * laboratorio.
 *
 * @author Grupo04
 */
public class BLLResultadoLaboratorio {

    /**
     * Registra un nuevo resultado de laboratorio.
     *
     * @param idExamen Identificador del examen asociado
     * @param idLaboratorista Identificador del laboratorista que registra el
     * resultado
     * @param resultadoTexto Descripción textual del resultado
     * @param rutaArchivo Ruta del archivo adjunto (opcional)
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarResultado(int idExamen, int idLaboratorista, String resultadoTexto, String rutaArchivo) {
        if (idExamen > 0 && idLaboratorista > 0 && resultadoTexto != null && !resultadoTexto.trim().isEmpty()) {
            ResultadoLaboratorio resultado = new ResultadoLaboratorio(idExamen, idLaboratorista, resultadoTexto, rutaArchivo);
            DAL_ResultadoLaboratorio dal = new DAL_ResultadoLaboratorio();

            if (dal.insertar(resultado)) {
                return 0;
            } else {
                showMessageDialog(null, "Error al registrar resultado", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Busca un resultado por su identificador.
     *
     * @param idResultado Identificador del resultado
     * @return Objeto ResultadoLaboratorio encontrado, o null si no existe
     */
    public ResultadoLaboratorio buscarResultado(int idResultado) {
        if (idResultado > 0) {
            DAL_ResultadoLaboratorio dal = new DAL_ResultadoLaboratorio();
            return dal.buscarPorId(idResultado);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca el resultado asociado a un examen específico.
     *
     * @param idExamen Identificador del examen
     * @return Objeto ResultadoLaboratorio encontrado, o null si no existe
     */
    public ResultadoLaboratorio buscarPorExamen(int idExamen) {
        if (idExamen > 0) {
            DAL_ResultadoLaboratorio dal = new DAL_ResultadoLaboratorio();
            return dal.buscarPorExamen(idExamen);
        } else {
            showMessageDialog(null, "Código de examen no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todos los resultados de laboratorio registrados.
     *
     * @return Lista de objetos ResultadoLaboratorio
     */
    public ArrayList<ResultadoLaboratorio> listarResultados() {
        DAL_ResultadoLaboratorio dal = new DAL_ResultadoLaboratorio();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza un resultado de laboratorio existente.
     *
     * @param idResultado Identificador del resultado
     * @param idLaboratorista Nuevo identificador del laboratorista
     * @param resultadoTexto Nueva descripción del resultado
     * @param rutaArchivo Nueva ruta del archivo adjunto
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarResultado(int idResultado, int idLaboratorista, String resultadoTexto, String rutaArchivo) {
        if (idResultado > 0 && idLaboratorista > 0 && resultadoTexto != null && !resultadoTexto.trim().isEmpty()) {
            ResultadoLaboratorio resultado = new ResultadoLaboratorio(0, idLaboratorista, resultadoTexto, rutaArchivo);
            resultado.setIdResultado(idResultado);
            DAL_ResultadoLaboratorio dal = new DAL_ResultadoLaboratorio();

            if (dal.actualizar(resultado)) {
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar resultado", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Elimina un resultado por su identificador.
     *
     * @param idResultado Identificador del resultado
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarResultado(int idResultado) {
        if (idResultado > 0) {
            DAL_ResultadoLaboratorio dal = new DAL_ResultadoLaboratorio();
            if (dal.eliminarLogico(idResultado)) {
                showMessageDialog(null, "Resultado eliminado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo eliminar resultado", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return 2;
        }
    }
}

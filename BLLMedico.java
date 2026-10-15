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
 * Capa de lógica de negocio para la entidad Medico.
 *
 * Gestiona el registro, consulta y actualización de los médicos, incluyendo la
 * validación de colegiatura y su relación con usuarios.
 *
 * @author Grupo04
 */
public class BLLMedico {

    /**
     * Registra un nuevo médico en el sistema.
     *
     * @param idUsuario Identificador del usuario asociado
     * @param numeroColegiatura Número de colegiatura profesional
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos o la colegiatura ya existe
     */
    public int insertarMedico(int idUsuario, String numeroColegiatura) {
        if (idUsuario > 0 && numeroColegiatura != null && !numeroColegiatura.trim().isEmpty()) {
            DAL_Medico dal = new DAL_Medico();

            if (dal.buscarPorColegiatura(numeroColegiatura) != null) {
                showMessageDialog(null, "La colegiatura ya se encuentra registrada", "Error", 0);
                return 2;
            }

            Medico medico = new Medico(0, idUsuario, numeroColegiatura);

            if (dal.insertar(medico)) {
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
     * Busca un médico por su identificador.
     *
     * @param idMedico Identificador del médico
     * @return Objeto Medico encontrado, o null si no existe
     */
    public Medico buscarMedico(int idMedico) {
        if (idMedico > 0) {
            DAL_Medico dal = new DAL_Medico();
            return dal.buscarPorId(idMedico);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca un médico por su número de colegiatura.
     *
     * @param numeroColegiatura Número de colegiatura del médico
     * @return Objeto Medico encontrado, o null si no existe
     */
    public Medico buscarPorColegiatura(String numeroColegiatura) {
        DAL_Medico dal = new DAL_Medico();
        return dal.buscarPorColegiatura(numeroColegiatura);
    }

    /**
     * Busca un médico asociado a un usuario específico.
     *
     * @param idUsuario Identificador del usuario
     * @return Objeto Medico encontrado, o null si no existe
     */
    public Medico buscarPorUsuario(int idUsuario) {
        if (idUsuario > 0) {
            DAL_Medico dal = new DAL_Medico();
            return dal.buscarPorUsuario(idUsuario);
        }
        return null;
    }

    /**
     * Lista todos los médicos registrados.
     *
     * @return Lista de objetos Medico
     */
    public ArrayList<Medico> listarMedicos() {

        DAL_Medico dal = new DAL_Medico();
        ArrayList<Medico> lista = new ArrayList<>(dal.listarTodos());

        BLLUsuario bllUsuario = new BLLUsuario();

        for(Medico medico : lista){

            Usuario usuario = bllUsuario.buscarUsuario(medico.getIdUsuario());

            if(usuario != null){

                medico.setNombreCompleto(
                    usuario.getNombres() + " " + usuario.getApellidos()
                );

            }
        }

    return lista;
    }

    /**
     * Actualiza la información de un médico existente.
     *
     * @param idMedico Identificador del médico
     * @param numeroColegiatura Nuevo número de colegiatura
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarMedico(int idMedico, String numeroColegiatura) {
        if (idMedico > 0 && numeroColegiatura != null && !numeroColegiatura.trim().isEmpty()) {
            Medico medico = new Medico(idMedico, 0, numeroColegiatura);
            DAL_Medico dal = new DAL_Medico();

            if (dal.actualizar(medico)) {
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
     * Elimina un médico por su identificador.
     *
     * @param idMedico Identificador del médico
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarMedico(int idMedico) {
        if (idMedico > 0) {
            DAL_Medico dal = new DAL_Medico();
            if (dal.eliminarLogico(idMedico)) {
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

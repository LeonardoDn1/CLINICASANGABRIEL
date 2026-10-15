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
 * Capa de lógica de negocio para la entidad SeguroMedico.
 *
 * Gestiona el registro, consulta y actualización de seguros médicos asociados a
 * los pacientes.
 *
 * @author Grupo04
 */
public class BLLSeguroMedico {

    /**
    * Valida los datos de un seguro médico.
    *
    * @param seguro Objeto SeguroMedico a validar
    * @return true si los datos son válidos, false en caso contrario
    */
   public boolean validarSeguro(SeguroMedico seguro) {

       if (seguro == null) {
           showMessageDialog(null,
                   "No se han ingresado los datos del seguro médico",
                   "Error", 0);
           return false;
       }

       String aseguradora = seguro.getAseguradora() != null
               ? seguro.getAseguradora().trim().replaceAll("\\s+", " ")
               : "";

       String numeroPoliza = seguro.getNumeroPoliza() != null
               ? seguro.getNumeroPoliza().trim().replaceAll("\\s+", "")
               : "";

       // Guardar los datos normalizados
       seguro.setAseguradora(aseguradora);
       seguro.setNumeroPoliza(numeroPoliza);

       if (aseguradora.isEmpty()
               || aseguradora.length() < 2
               || aseguradora.length() > 100
               || !aseguradora.matches(
                       "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .-]+")
               || numeroPoliza.isEmpty()
               || numeroPoliza.length() < 5
               || numeroPoliza.length() > 50
               || !numeroPoliza.matches("[A-Za-z0-9/-]+")
               || !numeroPoliza.matches(".*[0-9].*")
               || seguro.getVigenciaInicio() == null
               || seguro.getVigenciaFin() == null
               || seguro.getVigenciaFin().before(
                       seguro.getVigenciaInicio())) {

           showMessageDialog(null,
                   "Datos del seguro incompletos o incorrectos.\n"
                   + "- Aseguradora: entre 2 y 100 caracteres.\n"
                   + "- Póliza: entre 5 y 50 caracteres.\n"
                   + "- La póliza debe contener al menos un número.\n"
                   + "- Debe seleccionar las fechas de vigencia.\n"
                   + "- La fecha final no puede ser anterior a la inicial.",
                   "Error", 0);
           return false;
       }

       return true;
   }

    /**
    * Registra un nuevo seguro médico.
    *
    * @param seguro Objeto SeguroMedico con los datos a registrar
    * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
    * inválidos
    */
   public int insertarSeguro(SeguroMedico seguro) {

       if (seguro == null || seguro.getIdPaciente() <= 0) {
           showMessageDialog(null,
                   "El paciente asociado al seguro no es válido",
                   "Error", 0);
           return 2;
       }

       if (!validarSeguro(seguro)) {
           return 2;
       }

       DAL_SeguroMedico dal = new DAL_SeguroMedico();

       if (dal.insertar(seguro)) {
           showMessageDialog(null,
                   "Seguro médico registrado correctamente",
                   "Resultado", 1);
           return 0;
       } else {
           showMessageDialog(null,
                   "Error al registrar seguro médico",
                   "Error", 0);
           return 1;
       }
   }

    /**
    * Actualiza los datos de un seguro médico existente.
    *
    * @param seguro Objeto SeguroMedico con los datos actualizados
    * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
    * inválidos
    */
   public int actualizarSeguro(SeguroMedico seguro) {

       if (seguro == null
               || seguro.getIdSeguro() <= 0
               || seguro.getIdPaciente() <= 0) {

           showMessageDialog(null,
                   "El seguro médico seleccionado no es válido",
                   "Error", 0);
           return 2;
       }

       if (!validarSeguro(seguro)) {
           return 2;
       }

       DAL_SeguroMedico dal = new DAL_SeguroMedico();

       if (dal.actualizar(seguro)) {
           showMessageDialog(null,
                   "Seguro médico actualizado correctamente",
                   "Resultado", 1);
           return 0;
       } else {
           showMessageDialog(null,
                   "No se pudo actualizar el seguro médico",
                   "Error", 0);
           return 1;
       }
   }

    /**
     * Busca un seguro médico por su identificador.
     *
     * @param idSeguro Identificador del seguro
     * @return Objeto SeguroMedico encontrado, o null si no existe
     */
    public SeguroMedico buscarSeguro(int idSeguro) {
        if (idSeguro > 0) {
            DAL_SeguroMedico dal = new DAL_SeguroMedico();
            return dal.buscarPorId(idSeguro);
        } else {
            showMessageDialog(null, "Código de seguro inválido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista los seguros médicos asociados a un paciente específico.
     *
     * @param idPaciente Identificador del paciente
     * @return Lista de seguros del paciente
     */
    public ArrayList<SeguroMedico> listarPorPaciente(int idPaciente) {
        if (idPaciente > 0) {
            DAL_SeguroMedico dal = new DAL_SeguroMedico();
            return new ArrayList<>(dal.listarPorPaciente(idPaciente));
        } else {
            showMessageDialog(null, "Paciente no válido", "Error", 0);
            return new ArrayList<>();
        }
    }

    /**
     * Lista todos los seguros médicos registrados.
     *
     * @return Lista de objetos SeguroMedico
     */
    public ArrayList<SeguroMedico> listarTodos() {
        DAL_SeguroMedico dal = new DAL_SeguroMedico();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Elimina un seguro médico por su identificador.
     *
     * @param idSeguro Identificador del seguro
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarSeguro(int idSeguro) {
        if (idSeguro > 0) {
            DAL_SeguroMedico dal = new DAL_SeguroMedico();
            if (dal.eliminarLogico(idSeguro)) {
                showMessageDialog(null, "Seguro eliminado correctamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo eliminar el seguro", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código inválido", "Error", 0);
            return 2;
        }
    }
}

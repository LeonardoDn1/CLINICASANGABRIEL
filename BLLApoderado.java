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
 * Capa de lógica de negocio para la entidad Apoderado.
 *
 * Gestiona el registro, validación y consulta de apoderados asociados a
 * pacientes menores de edad.
 *
 * @author Grupo04
 */
public class BLLApoderado {

    /**
    * Registra un nuevo apoderado para un paciente menor de edad.
    *
    * @param apoderado Objeto Apoderado con los datos a registrar
    * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
    * inválidos
    */
   public int insertarApoderado(Apoderado apoderado) {

       if (apoderado == null || apoderado.getIdPaciente() <= 0) {
           showMessageDialog(null,
                   "El paciente asociado al apoderado no es válido",
                   "Error", 0);
           return 2;
       }

       if (!validarApoderado(apoderado)) {
           return 2;
       }

       DAL_Apoderado dal = new DAL_Apoderado();

       if (dal.insertar(apoderado)) {
           showMessageDialog(null,
                   "Apoderado registrado correctamente",
                   "Resultado", 1);
           return 0;
       } else {
           showMessageDialog(null,
                   "Error al registrar apoderado",
                   "Error", 0);
           return 1;
       }
   }

    /*
    * Valida la información del apoderado antes de asociarlo al paciente.
    *
    * @param apoderado Objeto Apoderado a validar
    * @return true si los datos son válidos, false en caso contrario
    */
   public boolean validarApoderado(Apoderado apoderado) {

       if (apoderado == null) {
           showMessageDialog(null,
                   "No se han ingresado los datos del apoderado",
                   "Error", 0);
           return false;
       }

       String dni = apoderado.getDni() != null
               ? apoderado.getDni().trim()
               : "";

       String nombres = apoderado.getNombres() != null
               ? apoderado.getNombres().trim().replaceAll("\\s+", " ")
               : "";

       String apellidos = apoderado.getApellidos() != null
               ? apoderado.getApellidos().trim().replaceAll("\\s+", " ")
               : "";

       String telefono = apoderado.getTelefono() != null
               ? apoderado.getTelefono().trim()
               : "";

       String parentesco = apoderado.getParentesco() != null
               ? apoderado.getParentesco().trim().replaceAll("\\s+", " ")
               : "";

       // Actualizar los valores normalizados en el objeto
       apoderado.setDni(dni);
       apoderado.setNombres(nombres);
       apoderado.setApellidos(apellidos);
       apoderado.setTelefono(telefono);
       apoderado.setParentesco(parentesco);

       // Validar campos obligatorios
       if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()
               || telefono.isEmpty() || parentesco.isEmpty()) {

           showMessageDialog(null,
                   "Debe completar todos los datos del apoderado",
                   "Error", 0);
           return false;
       }

       // Validar DNI
       if (!dni.matches("\\d{8}")) {
           showMessageDialog(null,
                   "El DNI del apoderado debe contener exactamente 8 números",
                   "Error", 0);
           return false;
       }

       // Evitar DNI formados por el mismo número repetido
       if (dni.matches("(\\d)\\1{7}")) {
           showMessageDialog(null,
                   "El DNI del apoderado no es válido",
                   "Error", 0);
           return false;
       }

       // Validar nombres
       if (nombres.length() < 2 || nombres.length() > 100) {
           showMessageDialog(null,
                   "Los nombres del apoderado deben contener entre 2 y 100 caracteres",
                   "Error", 0);
           return false;
       }

       if (!nombres.matches(
               "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?:[ '-][A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*")) {

           showMessageDialog(null,
                   "Los nombres del apoderado solo deben contener letras",
                   "Error", 0);
           return false;
       }

       // Validar apellidos
       if (apellidos.length() < 2 || apellidos.length() > 100) {
           showMessageDialog(null,
                   "Los apellidos del apoderado deben contener entre 2 y 100 caracteres",
                   "Error", 0);
           return false;
       }

       if (!apellidos.matches(
               "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?:[ '-][A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*")) {

           showMessageDialog(null,
                   "Los apellidos del apoderado solo deben contener letras",
                   "Error", 0);
           return false;
       }

       // Validar teléfono celular peruano
       if (!telefono.matches("9\\d{8}")) {
           showMessageDialog(null,
                   "El teléfono del apoderado debe contener 9 números y comenzar con 9",
                   "Error", 0);
           return false;
       }

       // Validar parentesco
       if (parentesco.length() < 1 || parentesco.length() > 50) {
           showMessageDialog(null,
                   "El parentesco debe contener entre 1 y 50 caracteres",
                   "Error", 0);
           return false;
       }

       if (!parentesco.matches(
               "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?:[ '-][A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*")) {

           showMessageDialog(null,
                   "El parentesco solo debe contener letras",
                   "Error", 0);
           return false;
       }

       return true;
   }

   /**
    * Actualiza los datos de un apoderado existente.
    *
    * @param apoderado Objeto Apoderado con los datos actualizados
    * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
    * inválidos
    */
   public int actualizarApoderado(Apoderado apoderado) {

       if (apoderado == null
               || apoderado.getIdApoderado() <= 0
               || apoderado.getIdPaciente() <= 0) {

           showMessageDialog(null,
                   "El apoderado seleccionado no es válido",
                   "Error", 0);
           return 2;
       }

       // Validar los datos personales del apoderado
       if (!validarApoderado(apoderado)) {
           return 2;
       }

       // Buscar al paciente asociado al apoderado
       DAL_Paciente dalPaciente = new DAL_Paciente();
       Paciente paciente = dalPaciente.buscarPorId(
               apoderado.getIdPaciente()
       );

       if (paciente == null) {
           showMessageDialog(null,
                   "No se encontró al paciente asociado al apoderado",
                   "Error", 0);
           return 2;
       }

       // El DNI del apoderado debe ser diferente al DNI del paciente
       if (paciente.getDni().equals(
               apoderado.getDni().trim())) {

           showMessageDialog(null,
                   "El DNI del apoderado no puede ser igual al DNI del paciente",
                   "Error", 0);
           return 2;
       }

       DAL_Apoderado dal = new DAL_Apoderado();

       if (dal.actualizar(apoderado)) {
           showMessageDialog(null,
                   "Apoderado actualizado correctamente",
                   "Resultado", 1);
           return 0;
       } else {
           showMessageDialog(null,
                   "No se pudo actualizar apoderado",
                   "Error", 0);
           return 1;
       }
   }
    /**
     * Busca un apoderado por su identificador.
     *
     * @param idApoderado Identificador del apoderado
     * @return Objeto Apoderado encontrado, o null si no existe
     */
    public Apoderado buscarPorId(int idApoderado) {
        if (idApoderado > 0) {
            DAL_Apoderado dal = new DAL_Apoderado();
            return dal.buscarPorId(idApoderado);
        } else {
            showMessageDialog(null, "Código de apoderado inválido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca el apoderado asociado a un paciente específico.
     *
     * @param idPaciente Identificador del paciente
     * @return Objeto Apoderado encontrado, o null si no existe
     */
    public Apoderado buscarPorPaciente(int idPaciente) {
        if (idPaciente > 0) {
            DAL_Apoderado dal = new DAL_Apoderado();
            return dal.buscarPorPaciente(idPaciente);
        } else {
            showMessageDialog(null, "Paciente inválido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todos los apoderados registrados.
     *
     * @return Lista de objetos Apoderado
     */
    public ArrayList<Apoderado> listarTodos() {
        DAL_Apoderado dal = new DAL_Apoderado();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Bloquea la eliminación de apoderados. Los apoderados no pueden ser
     * eliminados, solo modificados.
     *
     * @param idApoderado Identificador del apoderado
     * @return 2 (operación no permitida)
     */
    public int eliminarApoderado(int idApoderado) {
        if (idApoderado <= 0) {
            showMessageDialog(null, "Código inválido", "Error", 0);
            return 2;
        }

        showMessageDialog(null, "No se permite eliminar el apoderado.\nUse la opción Modificar Apoderado para actualizar sus datos.", "Advertencia", 2);
        return 2;
    }
}

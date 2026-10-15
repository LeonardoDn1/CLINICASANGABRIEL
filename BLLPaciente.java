/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import entidades.*;
import datos.*;
import java.sql.Date;
import java.util.*;
import static javax.swing.JOptionPane.showMessageDialog;
import conexion.Conexion;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Capa de lógica de negocio para la entidad Paciente.
 *
 * Gestiona el registro, consulta, actualización y eliminación lógica de
 * pacientes, incluyendo validaciones de datos y transacciones para el registro
 * de apoderados y seguros médicos.
 *
 * @author Grupo04
 */
public class BLLPaciente {

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * @param numeroHistoriaClinica Número único de historia clínica
     * @param dni Documento nacional de identidad
     * @param nombres Nombres del paciente
     * @param apellidos Apellidos del paciente
     * @param fechaNacimiento Fecha de nacimiento
     * @param sexo Sexo (M o F)
     * @param telefono Número de teléfono de contacto
     * @param direccion Dirección de residencia
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarPaciente(String numeroHistoriaClinica, String dni, String nombres, String apellidos,
            Date fechaNacimiento, String sexo, String telefono, String direccion) {
        return insertarPacienteConApoderado(numeroHistoriaClinica,dni,nombres,apellidos,fechaNacimiento,sexo,telefono,direccion,null,null);
    }

    /**
     * Busca un paciente por su identificador.
     *
     * @param idPaciente Identificador del paciente
     * @return Objeto Paciente encontrado, o null si no existe
     */
    public Paciente buscarPaciente(int idPaciente) {
        if (idPaciente > 0) {
            DAL_Paciente dal = new DAL_Paciente();
            return dal.buscarPorId(idPaciente);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca un paciente por su número de DNI.
     *
     * @param dni Documento nacional de identidad
     * @return Objeto Paciente encontrado, o null si no existe
     */
    public Paciente buscarPorDni(String dni) {
        if (dni != null && dni.trim().length() == 8) {
            DAL_Paciente dal = new DAL_Paciente();
            return dal.buscarPorDni(dni);
        } else {
            showMessageDialog(null, "DNI no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca un paciente por su número de historia clínica.
     *
     * @param historiaClinica Número de historia clínica
     * @return Objeto Paciente encontrado, o null si no existe
     */
    public Paciente buscarPorHistoriaClinica(String historiaClinica) {
        if (historiaClinica != null && !historiaClinica.trim().isEmpty()) {
            DAL_Paciente dal = new DAL_Paciente();
            return dal.buscarPorHistoriaClinica(historiaClinica);
        } else {
            showMessageDialog(null, "Historia clínica no valida", "Error", 0);
            return null;
        }
    }

    /**
     * Busca pacientes por coincidencia en nombres o apellidos.
     *
     * @param criterio Texto a buscar en nombres o apellidos
     * @return Lista de pacientes que coinciden con el criterio
     */
    public ArrayList<Paciente> buscarPorNombreOApellido(String criterio) {
        DAL_Paciente dal = new DAL_Paciente();
        return new ArrayList<>(dal.buscarPorNombreOApellido(criterio));
    }

    /**
     * Lista todos los pacientes activos registrados.
     *
     * @return Lista de objetos Paciente
     */
    public ArrayList<Paciente> listarPacientes() {
        DAL_Paciente dal = new DAL_Paciente();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
    * Actualiza la información personal del paciente.
    *
    * @param idPaciente Identificador del paciente
    * @param nombres Nuevos nombres del paciente
    * @param apellidos Nuevos apellidos del paciente
    * @param fechaNacimiento Nueva fecha de nacimiento
    * @param sexo Nuevo sexo (M o F)
    * @param telefono Nuevo número de teléfono
    * @param direccion Nueva dirección
    * @param estado Nuevo estado (ACTIVO o INACTIVO)
    * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
    * inválidos
    */
   public int actualizarPaciente(int idPaciente, String nombres, String apellidos,
           Date fechaNacimiento, String sexo, String telefono,
           String direccion, String estado) {

       nombres = nombres != null
               ? nombres.trim().replaceAll("\\s+", " ")
               : "";

       apellidos = apellidos != null
               ? apellidos.trim().replaceAll("\\s+", " ")
               : "";

       sexo = sexo != null
               ? sexo.trim().toUpperCase()
               : "";

       telefono = telefono != null
               ? telefono.trim()
               : "";

       direccion = direccion != null
               ? direccion.trim().replaceAll("\\s+", " ")
               : "";

       estado = estado != null
               ? estado.trim().toUpperCase()
               : "";

       // Validación del identificador del paciente
       if (idPaciente <= 0) {
           showMessageDialog(null,
                   "Código de paciente no válido",
                   "Error", 0);
           return 2;
       }

       // Validar nombres, apellidos, fecha, sexo, teléfono y dirección
       if (!validarDatosPaciente(nombres, apellidos, fechaNacimiento,
               sexo, telefono, direccion)) {
           return 2;
       }

       // Validación del estado
       if (estado.isEmpty()) {
           showMessageDialog(null,
                   "Debe seleccionar el estado del paciente",
                   "Error", 0);
           return 2;
       }

       if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO")) {
           showMessageDialog(null,
                   "El estado seleccionado no es válido",
                   "Error", 0);
           return 2;
       }

       // Verificar que un paciente menor tenga apoderado registrado
       if (esMenorDeEdad(fechaNacimiento)) {

           BLLApoderado bllApoderado = new BLLApoderado();
           Apoderado apoderado = bllApoderado.buscarPorPaciente(idPaciente);

           if (apoderado == null) {
               showMessageDialog(null,
                       "El paciente menor de edad debe contar con un apoderado",
                       "Error", 0);
               return 2;
           }
       }

       Paciente paciente = new Paciente();
       paciente.setIdPaciente(idPaciente);
       paciente.setNombres(nombres);
       paciente.setApellidos(apellidos);
       paciente.setFechaNacimiento(fechaNacimiento);
       paciente.setSexo(sexo);
       paciente.setTelefono(telefono);
       paciente.setDireccion(direccion);
       paciente.setEstado(estado);

       DAL_Paciente dal = new DAL_Paciente();

       if (dal.actualizar(paciente)) {
           showMessageDialog(null,
                   "Registro actualizado correctamente",
                   "Resultado", 1);
           return 0;
       } else {
           showMessageDialog(null,
                   "Error al actualizar registro",
                   "Error", 0);
           return 1;
       }
   }

    /**
     * Realiza eliminación lógica del paciente (cambia estado a INACTIVO).
     *
     * @param idPaciente Identificador del paciente
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarPaciente(int idPaciente) {
        if (idPaciente > 0) {
            DAL_Paciente dal = new DAL_Paciente();
            if (dal.eliminarLogico(idPaciente)) {
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

    /**
     * Genera un nuevo número de historia clínica.
     *
     * @return Nuevo número de historia clínica con formato HC-XXXXX
     */
    public String generarNuevaHistoriaClinica() {
        DAL_Paciente dal = new DAL_Paciente();
        return dal.generarNuevaHistoriaClinica();
    }

    /**
     * Verifica si el paciente es menor de edad.
     *
     * @param fechaNacimiento Fecha de nacimiento del paciente
     * @return true si es menor de edad, false en caso contrario
     */
    
        /**
     * Valida los datos personales de un paciente.
     *
     * @param nombres Nombres del paciente
     * @param apellidos Apellidos del paciente
     * @param fechaNacimiento Fecha de nacimiento
     * @param sexo Sexo del paciente
     * @param telefono Teléfono del paciente
     * @param direccion Dirección del paciente
     * @return true si los datos son válidos, false en caso contrario
     */
    private boolean validarDatosPaciente(String nombres, String apellidos,
            Date fechaNacimiento, String sexo, String telefono,
            String direccion) {

        // Validar campos obligatorios
        if (nombres == null || nombres.trim().isEmpty()
                || apellidos == null || apellidos.trim().isEmpty()
                || fechaNacimiento == null
                || sexo == null || sexo.trim().isEmpty()
                || telefono == null || telefono.trim().isEmpty()
                || direccion == null || direccion.trim().isEmpty()) {

            showMessageDialog(null,
                    "Debe completar todos los datos obligatorios del paciente",
                    "Error", 0);
            return false;
        }

        nombres = nombres.trim();
        apellidos = apellidos.trim();
        sexo = sexo.trim().toUpperCase();
        telefono = telefono.trim();
        direccion = direccion.trim();

        // Validar longitud de los nombres
        if (nombres.length() < 2 || nombres.length() > 100) {
            showMessageDialog(null,
                    "Los nombres deben contener entre 2 y 100 caracteres",
                    "Error", 0);
            return false;
        }

        // Validar formato de los nombres
        if (!nombres.matches(
                "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?:[ '-][A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*")) {

            showMessageDialog(null,
                    "Los nombres solo deben contener letras, espacios, guiones o apóstrofes",
                    "Error", 0);
            return false;
        }

        // Validar longitud de los apellidos
        if (apellidos.length() < 2 || apellidos.length() > 100) {
            showMessageDialog(null,
                    "Los apellidos deben contener entre 2 y 100 caracteres",
                    "Error", 0);
            return false;
        }

        // Validar formato de los apellidos
        if (!apellidos.matches(
                "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?:[ '-][A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*")) {

            showMessageDialog(null,
                    "Los apellidos solo deben contener letras, espacios, guiones o apóstrofes",
                    "Error", 0);
            return false;
        }

        // Validar que la fecha no sea futura
        Date fechaActual = new Date(System.currentTimeMillis());

        if (fechaNacimiento.after(fechaActual)) {
            showMessageDialog(null,
                    "La fecha de nacimiento no puede ser posterior a la fecha actual",
                    "Error", 0);
            return false;
        }

        // Validar que la edad no sea mayor a 120 años
        Calendar fechaMinima = Calendar.getInstance();
        fechaMinima.add(Calendar.YEAR, -120);

        Date fechaLimite = new Date(fechaMinima.getTimeInMillis());

        if (fechaNacimiento.before(fechaLimite)) {
            showMessageDialog(null,
                    "La fecha de nacimiento no puede superar los 120 años de antigüedad",
                    "Error", 0);
            return false;
        }

        // Validar sexo
        if (!sexo.equals("M") && !sexo.equals("F")) {
            showMessageDialog(null,
                    "El sexo seleccionado no es válido",
                    "Error", 0);
            return false;
        }

        // Validar teléfono celular peruano
        if (!telefono.matches("9\\d{8}")) {
            showMessageDialog(null,
                    "El teléfono debe contener 9 números y comenzar con 9",
                    "Error", 0);
            return false;
        }

        // Validar longitud de la dirección
        if (direccion.length() < 5 || direccion.length() > 200) {
            showMessageDialog(null,
                    "La dirección debe contener entre 5 y 200 caracteres",
                    "Error", 0);
            return false;
        }

        // Evitar una dirección formada solamente por símbolos
        if (!direccion.matches(".*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9].*")) {
            showMessageDialog(null,
                    "La dirección debe contener letras o números",
                    "Error", 0);
            return false;
        }

        return true;
    }
    
    private boolean esMenorDeEdad(Date fechaNacimiento) {
        Calendar nacimiento = Calendar.getInstance();
        nacimiento.setTime(fechaNacimiento);

        Calendar hoy = Calendar.getInstance();
        int edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);

        if (hoy.get(Calendar.MONTH) < nacimiento.get(Calendar.MONTH)
                || (hoy.get(Calendar.MONTH) == nacimiento.get(Calendar.MONTH)
                && hoy.get(Calendar.DAY_OF_MONTH) < nacimiento.get(Calendar.DAY_OF_MONTH))) {
            edad--;
        }
        return edad < 18;
    }

    /**
     * Registra paciente, apoderado y seguro médico en una transacción. Si falla
     * una operación, se revierte todo el registro.
     *
     * @param numeroHistoriaClinica Número único de historia clínica
     * @param dni Documento nacional de identidad
     * @param nombres Nombres del paciente
     * @param apellidos Apellidos del paciente
     * @param fechaNacimiento Fecha de nacimiento
     * @param sexo Sexo (M o F)
     * @param telefono Número de teléfono
     * @param direccion Dirección de residencia
     * @param apoderado Objeto Apoderado (requerido si es menor de edad)
     * @param seguro Objeto SeguroMedico (opcional)
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarPacienteConApoderado(String numeroHistoriaClinica, String dni, String nombres, String apellidos,
            Date fechaNacimiento, String sexo, String telefono, String direccion,
            Apoderado apoderado, SeguroMedico seguro) {

        numeroHistoriaClinica = numeroHistoriaClinica != null ? numeroHistoriaClinica.trim().toUpperCase() : "";
        dni = dni != null ? dni.trim() : "";
        nombres = nombres != null
        ? nombres.trim().replaceAll("\\s+", " ")
        : "";
        apellidos = apellidos != null
                ? apellidos.trim().replaceAll("\\s+", " ")
                : "";
        sexo = sexo != null ? sexo.trim().toUpperCase() : "";
        telefono = telefono != null ? telefono.trim() : "";
        direccion = direccion != null
        ? direccion.trim().replaceAll("\\s+", " ")
        : "";
 
        // Validación de historia clínica
        if (numeroHistoriaClinica.isEmpty()) {
            showMessageDialog(null, "Debe ingresar el número de historia clínica", "Error", 0);
            return 2;
        }

        if (!numeroHistoriaClinica.matches("HC-\\d{5}")) {
            showMessageDialog(null, "La historia clínica debe tener el formato HC-00001", "Error", 0);
            return 2;
        }

        // Validación de DNI
        if (dni.isEmpty()) {
            showMessageDialog(null, "Debe ingresar el DNI del paciente", "Error", 0);
            return 2;
        }

        if (!dni.matches("\\d{8}")) {
            showMessageDialog(null, "El DNI debe contener exactamente 8 números", "Error", 0);
            return 2;
        }

        // Evita DNI formados por el mismo número repetido
        if (dni.matches("(\\d)\\1{7}")) {
            showMessageDialog(null, "El número de DNI ingresado no es válido", "Error", 0);
            return 2;
        }

        // Validar nombres, apellidos, fecha, sexo, teléfono y dirección
        if (!validarDatosPaciente(nombres, apellidos, fechaNacimiento,
                sexo, telefono, direccion)) {
            return 2;
        }

        DAL_Paciente dalPaciente = new DAL_Paciente();

        // Verificar DNI único
        if (dalPaciente.buscarPorDni(dni) != null) {
            showMessageDialog(null, "El DNI ya se encuentra registrado", "Error", 0);
            return 1;
        }

        // Verificar historia clínica única
        if (dalPaciente.buscarPorHistoriaClinica(numeroHistoriaClinica) != null) {
            showMessageDialog(null, "El número de historia clínica ya se encuentra registrado", "Error", 0);
            return 1;
        }

        boolean menor = esMenorDeEdad(fechaNacimiento);

        // Validación de apoderado para menores de edad
        if (menor && apoderado == null) {
            showMessageDialog(null, "El paciente menor de edad debe contar con un apoderado", "Error", 0);
            return 2;
        }

        BLLApoderado bllApoderado = new BLLApoderado();

        if (menor && !bllApoderado.validarApoderado(apoderado)) {
            showMessageDialog(null, "Los datos del apoderado son incorrectos", "Error", 0);
            return 2;
        }
        
        // El DNI del apoderado no puede ser igual al DNI del paciente
        if (menor && apoderado != null
                && dni.equals(apoderado.getDni().trim())) {

            showMessageDialog(null,
                    "El DNI del apoderado no puede ser igual al DNI del paciente",
                    "Error", 0);
            return 2;
        }

        BLLSeguroMedico bllSeguro = new BLLSeguroMedico();
        if (seguro != null && !bllSeguro.validarSeguro(seguro)) {
            showMessageDialog(null, "Los datos del seguro médico son incorrectos", "Error", 0);
            return 2;
        }

        Paciente paciente = new Paciente.Builder(dni, nombres, apellidos, fechaNacimiento, sexo, telefono, direccion)
                .numeroHistoriaClinica(numeroHistoriaClinica)
                .estado("ACTIVO")
                .build();

        DAL_Apoderado dalApoderado = new DAL_Apoderado();
        DAL_SeguroMedico dalSeguro = new DAL_SeguroMedico();
        Connection con = null;
        boolean autoCommitAnterior = true;

        try {
            con = Conexion.getInstancia().getConexion();
            autoCommitAnterior = con.getAutoCommit();
            con.setAutoCommit(false);

            // Registrar paciente
            int idPaciente = dalPaciente.insertarPaciente(paciente, con);

            if (idPaciente <= 0) {
                throw new SQLException("No se pudo registrar el paciente");
            }

            // Registrar apoderado si es menor de edad
            if (menor) {
                apoderado.setIdPaciente(idPaciente);
                if (!dalApoderado.insertar(apoderado, con)) {
                    throw new SQLException("No se pudo registrar el apoderado");
                }
            }

            // Registrar seguro médico si existe
            if (seguro != null) {
                seguro.setIdPaciente(idPaciente);
                if (!dalSeguro.insertar(seguro, con)) {
                    throw new SQLException("No se pudo registrar el seguro médico");
                }
            }

            con.commit();

            // Mensaje de éxito según lo registrado
            if (menor && seguro != null) {
                showMessageDialog(null, "Paciente, apoderado y seguro registrados correctamente", "Resultado", 1);
            } else if (menor) {
                showMessageDialog(null, "Paciente y apoderado registrados correctamente", "Resultado", 1);
            } else if (seguro != null) {
                showMessageDialog(null, "Paciente y seguro registrados correctamente", "Resultado", 1);
            } else {
                showMessageDialog(null, "Paciente registrado correctamente", "Resultado", 1);
            }
            return 0;

        } catch (SQLException e) {
            // Rollback en caso de error
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al ejecutar rollback: " + ex.getMessage());
                }
            }

            System.err.println("Error en transacción paciente-apoderado: " + e.getMessage());
            showMessageDialog(null, "No se pudo completar el registro. No se guardó ningún dato", "Error", 0);
            return 1;

        } finally {
            // Restaurar configuración y cerrar conexión
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommitAnterior);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
}

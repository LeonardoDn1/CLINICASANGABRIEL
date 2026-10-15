/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 * Entidad que representa a un apoderado de paciente menor de edad.
 * 
 * Esta clase encapsula la información del responsable legal o familiar
 * que acompaña a un paciente que no ha alcanzado la mayoría de edad.
 * La información registrada incluye datos de identificación, contacto
 * y el vínculo de parentesco con el paciente.
 * 
 * La relación con la entidad Paciente se establece mediante el campo
 * idPaciente, lo que permite mantener la integridad referencial entre
 * ambas entidades.
 * 
 * @author Grupo04
 */
public class Apoderado {
    
    private int idApoderado;
    private int idPaciente;
    private String dni;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String parentesco;

    /**
     * Constructor por defecto.
     * Inicializa una instancia vacía de la entidad Apoderado.
     */
    public Apoderado() {}

    /**
     * Constructor con parámetros obligatorios.
     * 
     * @param idPaciente   Identificador del paciente al que representa
     * @param dni          Número de documento de identidad del apoderado
     * @param nombres      Nombres del apoderado
     * @param apellidos    Apellidos del apoderado
     * @param telefono     Número de teléfono de contacto del apoderado
     * @param parentesco   Relación del apoderado con el paciente (padre, madre, tutor, etc.)
     */
    public Apoderado(int idPaciente, String dni, String nombres,
                     String apellidos, String telefono, String parentesco) {
        this.idPaciente = idPaciente;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.parentesco = parentesco;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdApoderado() {
        return idApoderado;
    }

    public void setIdApoderado(int idApoderado) {
        this.idApoderado = idApoderado;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    /**
     * Devuelve una representación textual del apoderado.
     * 
     * @return Cadena que contiene el nombre completo y el parentesco del apoderado
     */
    @Override
    public String toString() {
        return nombres + " " + apellidos + " (" + parentesco + ")";
    }
}
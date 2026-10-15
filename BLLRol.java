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
 * Capa de lógica de negocio para la entidad Rol.
 *
 * Gestiona la consulta y validación de los roles institucionales disponibles en
 * el sistema para el control de acceso.
 *
 * @author Grupo04
 */
public class BLLRol {

    /**
     * Lista todos los roles disponibles en el sistema.
     *
     * @return Lista de objetos Rol
     */
    public ArrayList<Rol> listarRoles() {
        DAL_Rol dal = new DAL_Rol();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Busca un rol por su identificador.
     *
     * @param idRol Identificador del rol
     * @return Objeto Rol encontrado, o null si no existe
     */
    public Rol buscarRol(int idRol) {
        if (idRol > 0) {
            DAL_Rol dal = new DAL_Rol();
            return dal.buscarPorId(idRol);
        } else {
            showMessageDialog(null, "Código de rol no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Verifica si un nombre de rol pertenece a los roles institucionales
     * permitidos.
     *
     * @param nombreRol Nombre del rol a validar
     * @return true si el rol es válido, false en caso contrario
     */
    public boolean validarRolPermitido(String nombreRol) {
        if (nombreRol == null || nombreRol.trim().isEmpty()) {
            return false;
        }

        String rol = nombreRol.trim().toUpperCase();

        return rol.equals("ADMINISTRADOR")
                || rol.equals("RECEPCIONISTA")
                || rol.equals("MÉDICO")
                || rol.equals("MEDICO")
                || rol.equals("ENFERMERA")
                || rol.equals("LABORATORISTA")
                || rol.equals("FARMACÉUTICO")
                || rol.equals("FARMACEUTICO")
                || rol.equals("CAJERO")
                || rol.equals("DIRECTOR MÉDICO")
                || rol.equals("DIRECTOR MEDICO");
    }
}

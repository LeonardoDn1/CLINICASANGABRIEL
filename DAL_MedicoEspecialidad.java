/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.MedicoEspecialidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad MedicoEspecialidad.
 *
 * Gestiona las operaciones de asignación y consulta de especialidades asociadas
 * a los médicos, estableciendo la relación muchos-a-muchos entre ambas
 * entidades.
 *
 * @author Grupo04
 */
public class DAL_MedicoEspecialidad {

    /**
     * Asigna una especialidad a un médico.
     *
     * @param me Objeto MedicoEspecialidad con los identificadores
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean insertar(MedicoEspecialidad me) {
        String sql = "INSERT INTO medico_especialidad (id_medico, id_especialidad) VALUES (?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, me.getIdMedico());
            ps.setInt(2, me.getIdEspecialidad());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_MedicoEspecialidad.insertar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una especialidad específica de un médico.
     *
     * @param idMedico Identificador del médico
     * @param idEspecialidad Identificador de la especialidad a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean eliminar(int idMedico, int idEspecialidad) {
        String sql = "DELETE FROM medico_especialidad WHERE id_medico = ? AND id_especialidad = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idMedico);
            ps.setInt(2, idEspecialidad);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_MedicoEspecialidad.eliminar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina todas las especialidades asignadas a un médico.
     *
     * @param idMedico Identificador del médico
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean eliminarPorMedico(int idMedico) {
        String sql = "DELETE FROM medico_especialidad WHERE id_medico = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idMedico);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_MedicoEspecialidad.eliminarPorMedico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las especialidades asignadas a un médico específico.
     *
     * @param idMedico Identificador del médico
     * @return Lista de objetos MedicoEspecialidad del médico
     */
    public List<MedicoEspecialidad> listarPorMedico(int idMedico) {
        List<MedicoEspecialidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM medico_especialidad WHERE id_medico = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new MedicoEspecialidad(
                            rs.getInt("id_medico"),
                            rs.getInt("id_especialidad")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_MedicoEspecialidad.listarPorMedico: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todas las asignaciones médico-especialidad registradas.
     *
     * @return Lista de objetos MedicoEspecialidad
     */
    public List<MedicoEspecialidad> listarTodos() {
        List<MedicoEspecialidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM medico_especialidad";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new MedicoEspecialidad(
                        rs.getInt("id_medico"),
                        rs.getInt("id_especialidad")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_MedicoEspecialidad.listarTodos: " + e.getMessage());
        }
        return lista;
    }
}

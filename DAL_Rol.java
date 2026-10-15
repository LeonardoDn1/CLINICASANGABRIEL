/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Rol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Rol.
 *
 * Gestiona las consultas relacionadas con los roles institucionales disponibles
 * en el sistema para la asignación de permisos.
 *
 * @author Grupo04
 */
public class DAL_Rol {

    /**
     * Lista todos los roles registrados en el sistema.
     *
     * @return Lista de objetos Rol
     */
    public List<Rol> listarTodos() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM roles";

        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol r = new Rol(
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol"),
                        rs.getString("descripcion")
                );
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Rol.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca un rol por su identificador.
     *
     * @param idRol Identificador del rol
     * @return Objeto Rol encontrado, o null si no existe
     */
    public Rol buscarPorId(int idRol) {
        String sql = "SELECT * FROM roles WHERE id_rol = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Rol(
                            rs.getInt("id_rol"),
                            rs.getString("nombre_rol"),
                            rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Rol.buscarPorId: " + e.getMessage());
        }
        return null;
    }
}

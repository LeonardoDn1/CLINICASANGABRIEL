/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Especialidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Especialidad.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con las
 * especialidades médicas disponibles en la clínica.
 *
 * @author Fernandog
 */
public class DAL_Especialidad implements DAL_iCRUD<Especialidad> {

    /**
     * Registra una nueva especialidad médica.
     *
     * @param e Objeto Especialidad con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Especialidad e) {
        String sql = "INSERT INTO especialidades (nombre) VALUES (?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, e.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error en DAL_Especialidad.insertar: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Actualiza una especialidad existente.
     *
     * @param e Objeto Especialidad con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Especialidad e) {
        String sql = "UPDATE especialidades SET nombre = ? WHERE id_especialidad = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, e.getNombre());
            ps.setInt(2, e.getIdEspecialidad());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error en DAL_Especialidad.actualizar: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Elimina una especialidad por su identificador.
     *
     * @param idEspecialidad Identificador de la especialidad
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idEspecialidad) {
        String sql = "DELETE FROM especialidades WHERE id_especialidad = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idEspecialidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error en DAL_Especialidad.eliminarLogico: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Busca una especialidad por su identificador.
     *
     * @param idEspecialidad Identificador de la especialidad
     * @return Objeto Especialidad encontrado, o null si no existe
     */
    @Override
    public Especialidad buscarPorId(int idEspecialidad) {
        String sql = "SELECT * FROM especialidades WHERE id_especialidad = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEspecialidad(rs);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error en DAL_Especialidad.buscarPorId: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Busca una especialidad por su nombre exacto.
     *
     * @param nombre Nombre de la especialidad
     * @return Objeto Especialidad encontrado, o null si no existe
     */
    public Especialidad buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM especialidades WHERE nombre = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEspecialidad(rs);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error en DAL_Especialidad.buscarPorNombre: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Lista todas las especialidades registradas en orden alfabético.
     *
     * @return Lista de objetos Especialidad
     */
    @Override
    public List<Especialidad> listarTodos() {
        List<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM especialidades ORDER BY nombre";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearEspecialidad(rs));
            }
        } catch (SQLException ex) {
            System.err.println("Error en DAL_Especialidad.listarTodos: " + ex.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Especialidad.
     *
     * @param rs ResultSet con los datos de la especialidad
     * @return Objeto Especialidad mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Especialidad mapearEspecialidad(ResultSet rs) throws SQLException {
        return new Especialidad(
                rs.getInt("id_especialidad"),
                rs.getString("nombre")
        );
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Apoderado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Apoderado.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * apoderados de pacientes menores de edad en la base de datos.
 *
 * @author Grupo04
 */
public class DAL_Apoderado implements DAL_iCRUD<Apoderado> {

    /**
     * Inserta un nuevo apoderado en la base de datos.
     *
     * @param a Objeto Apoderado con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Apoderado a) {
        String sql = "INSERT INTO apoderados (id_paciente, dni, nombres, apellidos, telefono, parentesco) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, a.getIdPaciente());
            ps.setString(2, a.getDni());
            ps.setString(3, a.getNombres());
            ps.setString(4, a.getApellidos());
            ps.setString(5, a.getTelefono());
            ps.setString(6, a.getParentesco());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Apoderado.insertar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserta un apoderado utilizando una conexión existente (para
     * transacciones).
     *
     * @param a Objeto Apoderado con los datos a registrar
     * @param con Conexión activa a la base de datos
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean insertar(Apoderado a, Connection con) {
        String sql = "INSERT INTO apoderados (id_paciente, dni, nombres, apellidos, telefono, parentesco) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, a.getIdPaciente());
            ps.setString(2, a.getDni());
            ps.setString(3, a.getNombres());
            ps.setString(4, a.getApellidos());
            ps.setString(5, a.getTelefono());
            ps.setString(6, a.getParentesco());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Apoderado.insertar transacción: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un apoderado existente.
     *
     * @param a Objeto Apoderado con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Apoderado a) {
        String sql = "UPDATE apoderados SET dni = ?, nombres = ?, apellidos = ?, "
                + "telefono = ?, parentesco = ? WHERE id_apoderado = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, a.getDni());
            ps.setString(2, a.getNombres());
            ps.setString(3, a.getApellidos());
            ps.setString(4, a.getTelefono());
            ps.setString(5, a.getParentesco());
            ps.setInt(6, a.getIdApoderado());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Apoderado.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina lógicamente un apoderado (borrado físico).
     *
     * @param idApoderado Identificador del apoderado a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idApoderado) {
        String sql = "DELETE FROM apoderados WHERE id_apoderado = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idApoderado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Apoderado.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un apoderado por su identificador.
     *
     * @param idApoderado Identificador del apoderado
     * @return Objeto Apoderado encontrado, o null si no existe
     */
    @Override
    public Apoderado buscarPorId(int idApoderado) {
        String sql = "SELECT * FROM apoderados WHERE id_apoderado = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idApoderado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearApoderado(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Apoderado.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca el apoderado asociado a un paciente específico.
     *
     * @param idPaciente Identificador del paciente
     * @return Objeto Apoderado encontrado, o null si no existe
     */
    public Apoderado buscarPorPaciente(int idPaciente) {
        String sql = "SELECT * FROM apoderados WHERE id_paciente = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearApoderado(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Apoderado.buscarPorPaciente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los apoderados registrados.
     *
     * @return Lista de objetos Apoderado
     */
    @Override
    public List<Apoderado> listarTodos() {
        List<Apoderado> lista = new ArrayList<>();
        String sql = "SELECT * FROM apoderados";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearApoderado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Apoderado.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Apoderado.
     *
     * @param rs ResultSet con los datos del apoderado
     * @return Objeto Apoderado mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Apoderado mapearApoderado(ResultSet rs) throws SQLException {
        Apoderado a = new Apoderado(
                rs.getInt("id_paciente"),
                rs.getString("dni"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("telefono"),
                rs.getString("parentesco")
        );
        a.setIdApoderado(rs.getInt("id_apoderado"));
        return a;
    }
}

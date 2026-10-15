/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Diagnostico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Diagnostico.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * diagnósticos médicos registrados en las atenciones.
 *
 * @author Grupo04
 */
public class DAL_Diagnostico implements DAL_iCRUD<Diagnostico> {

    /**
     * Registra un nuevo diagnóstico asociado a una atención médica.
     *
     * @param d Objeto Diagnostico con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Diagnostico d) {
        String sql = "INSERT INTO diagnosticos (id_atencion, codigo_cie10, descripcion) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, d.getIdAtencion());
            ps.setString(2, d.getCodigoCie10());
            ps.setString(3, d.getDescripcion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        d.setIdDiagnostico(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Diagnostico.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza un diagnóstico existente.
     *
     * @param d Objeto Diagnostico con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Diagnostico d) {
        String sql = "UPDATE diagnosticos SET codigo_cie10 = ?, descripcion = ? WHERE id_diagnostico = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, d.getCodigoCie10());
            ps.setString(2, d.getDescripcion());
            ps.setInt(3, d.getIdDiagnostico());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Diagnostico.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un diagnóstico por su identificador.
     *
     * @param idDiagnostico Identificador del diagnóstico
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idDiagnostico) {
        String sql = "DELETE FROM diagnosticos WHERE id_diagnostico = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idDiagnostico);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Diagnostico.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un diagnóstico por su identificador.
     *
     * @param idDiagnostico Identificador del diagnóstico
     * @return Objeto Diagnostico encontrado, o null si no existe
     */
    @Override
    public Diagnostico buscarPorId(int idDiagnostico) {
        String sql = "SELECT * FROM diagnosticos WHERE id_diagnostico = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDiagnostico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearDiagnostico(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Diagnostico.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista los diagnósticos asociados a una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Lista de diagnósticos de la atención
     */
    public List<Diagnostico> listarPorAtencion(int idAtencion) {
        List<Diagnostico> lista = new ArrayList<>();
        String sql = "SELECT * FROM diagnosticos WHERE id_atencion = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAtencion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDiagnostico(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Diagnostico.listarPorAtencion: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los diagnósticos registrados.
     *
     * @return Lista de objetos Diagnostico
     */
    @Override
    public List<Diagnostico> listarTodos() {
        List<Diagnostico> lista = new ArrayList<>();
        String sql = "SELECT * FROM diagnosticos";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDiagnostico(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Diagnostico.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Diagnostico.
     *
     * @param rs ResultSet con los datos del diagnóstico
     * @return Objeto Diagnostico mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Diagnostico mapearDiagnostico(ResultSet rs) throws SQLException {
        Diagnostico d = new Diagnostico(
                rs.getInt("id_atencion"),
                rs.getString("codigo_cie10"),
                rs.getString("descripcion")
        );
        d.setIdDiagnostico(rs.getInt("id_diagnostico"));
        return d;
    }
}

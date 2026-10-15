/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Comprobante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Comprobante.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * comprobantes de pago emitidos en la clínica.
 *
 * @author Grupo04
 */
public class DAL_Comprobante implements DAL_iCRUD<Comprobante> {

    /**
     * Registra un nuevo comprobante de pago.
     *
     * @param c Objeto Comprobante con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Comprobante c) {
        String sql = "INSERT INTO comprobantes (id_pago, numero_comprobante, tipo_comprobante) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, c.getIdPago());
            ps.setString(2, c.getNumeroComprobante());
            ps.setString(3, c.getTipoComprobante());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdComprobante(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Comprobante.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza un comprobante existente.
     *
     * @param c Objeto Comprobante con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Comprobante c) {
        String sql = "UPDATE comprobantes SET id_pago = ?, numero_comprobante = ?, tipo_comprobante = ? WHERE id_comprobante = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, c.getIdPago());
            ps.setString(2, c.getNumeroComprobante());
            ps.setString(3, c.getTipoComprobante());
            ps.setInt(4, c.getIdComprobante());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Comprobante.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un comprobante por su identificador.
     *
     * @param idComprobante Identificador del comprobante
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idComprobante) {
        String sql = "DELETE FROM comprobantes WHERE id_comprobante = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idComprobante);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Comprobante.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un comprobante por su identificador.
     *
     * @param idComprobante Identificador del comprobante
     * @return Objeto Comprobante encontrado, o null si no existe
     */
    @Override
    public Comprobante buscarPorId(int idComprobante) {
        String sql = "SELECT * FROM comprobantes WHERE id_comprobante = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComprobante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearComprobante(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Comprobante.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca el comprobante asociado a un pago específico.
     *
     * @param idPago Identificador del pago
     * @return Objeto Comprobante encontrado, o null si no existe
     */
    public Comprobante buscarPorPago(int idPago) {
        String sql = "SELECT * FROM comprobantes WHERE id_pago = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearComprobante(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Comprobante.buscarPorPago: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un comprobante por su número correlativo.
     *
     * @param numeroComprobante Número único del comprobante
     * @return Objeto Comprobante encontrado, o null si no existe
     */
    public Comprobante buscarPorNumero(String numeroComprobante) {
        String sql = "SELECT * FROM comprobantes WHERE numero_comprobante = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numeroComprobante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearComprobante(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Comprobante.buscarPorNumero: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los comprobantes registrados.
     *
     * @return Lista de objetos Comprobante
     */
    @Override
    public List<Comprobante> listarTodos() {
        List<Comprobante> lista = new ArrayList<>();
        String sql = "SELECT * FROM comprobantes ORDER BY fecha_emision DESC";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearComprobante(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Comprobante.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Comprobante.
     *
     * @param rs ResultSet con los datos del comprobante
     * @return Objeto Comprobante mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Comprobante mapearComprobante(ResultSet rs) throws SQLException {
        Comprobante c = new Comprobante(
                rs.getInt("id_pago"),
                rs.getString("numero_comprobante"),
                rs.getString("tipo_comprobante")
        );
        c.setIdComprobante(rs.getInt("id_comprobante"));
        c.setFechaEmision(rs.getTimestamp("fecha_emision"));
        return c;
    }
}

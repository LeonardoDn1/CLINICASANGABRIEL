/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Medicamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Medicamento.
 * 
 * Gestiona las operaciones CRUD y consultas específicas relacionadas
 * con el inventario de medicamentos de la clínica.
 * 
 * @author Grupo04
 */
public class DAL_Medicamento implements DAL_iCRUD<Medicamento> {
    
    /**
     * Registra un nuevo medicamento en el inventario.
     * 
     * @param m Objeto Medicamento con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Medicamento m) {
        String sql = "INSERT INTO medicamentos (nombre, descripcion, unidad_medida, stock_actual, stock_minimo, precio_unitario) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());
            
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getDescripcion());
            ps.setString(3, m.getUnidadMedida());
            ps.setInt(4, m.getStockActual());
            ps.setInt(5, m.getStockMinimo());
            ps.setBigDecimal(6, m.getPrecioUnitario());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        m.setIdMedicamento(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medicamento.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza los datos de un medicamento existente.
     * 
     * @param m Objeto Medicamento con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Medicamento m) {
        String sql = "UPDATE medicamentos SET nombre = ?, descripcion = ?, unidad_medida = ?, " +
                     "stock_actual = ?, stock_minimo = ?, precio_unitario = ? WHERE id_medicamento = ?";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());
            
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getDescripcion());
            ps.setString(3, m.getUnidadMedida());
            ps.setInt(4, m.getStockActual());
            ps.setInt(5, m.getStockMinimo());
            ps.setBigDecimal(6, m.getPrecioUnitario());
            ps.setInt(7, m.getIdMedicamento());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medicamento.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el stock de un medicamento.
     * 
     * @param idMedicamento Identificador del medicamento
     * @param nuevoStock    Nueva cantidad de stock
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean actualizarStock(int idMedicamento, int nuevoStock) {
        String sql = "UPDATE medicamentos SET stock_actual = ? WHERE id_medicamento = ?";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());
            
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idMedicamento);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medicamento.actualizarStock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un medicamento por su identificador.
     * 
     * @param idMedicamento Identificador del medicamento
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idMedicamento) {
        String sql = "DELETE FROM medicamentos WHERE id_medicamento = ?";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());
            
            ps.setInt(1, idMedicamento);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medicamento.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un medicamento por su identificador.
     * 
     * @param idMedicamento Identificador del medicamento
     * @return Objeto Medicamento encontrado, o null si no existe
     */
    @Override
    public Medicamento buscarPorId(int idMedicamento) {
        String sql = "SELECT * FROM medicamentos WHERE id_medicamento = ?";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMedicamento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMedicamento(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medicamento.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca medicamentos por nombre o descripción.
     * 
     * @param criterio Texto a buscar en nombre o descripción
     * @return Lista de medicamentos que coinciden con el criterio
     */
    public List<Medicamento> buscarPorNombre(String criterio) {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicamentos WHERE nombre LIKE ? OR descripcion LIKE ? ORDER BY nombre";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtro = "%" + criterio + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearMedicamento(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medicamento.buscarPorNombre: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista los medicamentos con stock en nivel crítico.
     * 
     * @return Lista de medicamentos con stock bajo
     */
    public List<Medicamento> listarConStockBajo() {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicamentos WHERE stock_actual <= stock_minimo ORDER BY nombre";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMedicamento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medicamento.listarConStockBajo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los medicamentos registrados.
     * 
     * @return Lista de objetos Medicamento
     */
    @Override
    public List<Medicamento> listarTodos() {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicamentos ORDER BY nombre";
        try (Connection con = Conexion.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMedicamento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medicamento.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Medicamento.
     * 
     * @param rs ResultSet con los datos del medicamento
     * @return Objeto Medicamento mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Medicamento mapearMedicamento(ResultSet rs) throws SQLException {
        return new Medicamento(
                rs.getInt("id_medicamento"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getString("unidad_medida"),
                rs.getInt("stock_actual"),
                rs.getInt("stock_minimo"),
                rs.getBigDecimal("precio_unitario")
        );
    }
}
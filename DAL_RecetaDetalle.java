/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.RecetaDetalle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad RecetaDetalle.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * medicamentos prescritos en las recetas médicas.
 *
 * @author Grupo04
 */
public class DAL_RecetaDetalle implements DAL_iCRUD<RecetaDetalle> {

    /**
     * Registra un nuevo detalle de receta (medicamento prescrito).
     *
     * @param rd Objeto RecetaDetalle con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(RecetaDetalle rd) {
        String sql = "INSERT INTO receta_detalle (id_receta, id_medicamento, cantidad, indicaciones) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, rd.getIdReceta());
            ps.setInt(2, rd.getIdMedicamento());
            ps.setInt(3, rd.getCantidad());
            ps.setString(4, rd.getIndicaciones());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        rd.setIdRecetaDetalle(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_RecetaDetalle.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza un detalle de receta existente.
     *
     * @param rd Objeto RecetaDetalle con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(RecetaDetalle rd) {
        String sql = "UPDATE receta_detalle SET id_medicamento = ?, cantidad = ?, indicaciones = ? WHERE id_receta_detalle = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, rd.getIdMedicamento());
            ps.setInt(2, rd.getCantidad());
            ps.setString(3, rd.getIndicaciones());
            ps.setInt(4, rd.getIdRecetaDetalle());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_RecetaDetalle.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un detalle de receta por su identificador.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idRecetaDetalle) {
        String sql = "DELETE FROM receta_detalle WHERE id_receta_detalle = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idRecetaDetalle);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_RecetaDetalle.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un detalle de receta por su identificador.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @return Objeto RecetaDetalle encontrado, o null si no existe
     */
    @Override
    public RecetaDetalle buscarPorId(int idRecetaDetalle) {
        String sql = "SELECT * FROM receta_detalle WHERE id_receta_detalle = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRecetaDetalle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearRecetaDetalle(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_RecetaDetalle.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los medicamentos prescritos en una receta específica.
     *
     * @param idReceta Identificador de la receta
     * @return Lista de detalles de la receta
     */
    public List<RecetaDetalle> listarPorReceta(int idReceta) {
        List<RecetaDetalle> lista = new ArrayList<>();
        String sql = "SELECT * FROM receta_detalle WHERE id_receta = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReceta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearRecetaDetalle(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_RecetaDetalle.listarPorReceta: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los detalles de receta registrados.
     *
     * @return Lista de objetos RecetaDetalle
     */
    @Override
    public List<RecetaDetalle> listarTodos() {
        List<RecetaDetalle> lista = new ArrayList<>();
        String sql = "SELECT * FROM receta_detalle";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearRecetaDetalle(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_RecetaDetalle.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto RecetaDetalle.
     *
     * @param rs ResultSet con los datos del detalle de receta
     * @return Objeto RecetaDetalle mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private RecetaDetalle mapearRecetaDetalle(ResultSet rs) throws SQLException {
        RecetaDetalle rd = new RecetaDetalle(
                rs.getInt("id_receta"),
                rs.getInt("id_medicamento"),
                rs.getInt("cantidad"),
                rs.getString("indicaciones")
        );
        rd.setIdRecetaDetalle(rs.getInt("id_receta_detalle"));
        return rd;
    }
}

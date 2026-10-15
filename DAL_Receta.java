/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Receta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Receta.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con las
 * recetas médicas electrónicas emitidas durante las atenciones.
 *
 * @author Grupo04
 */
public class DAL_Receta implements DAL_iCRUD<Receta> {

    /**
     * Registra una nueva receta médica.
     *
     * @param r Objeto Receta con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Receta r) {
        String sql = "INSERT INTO recetas (id_atencion) VALUES (?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, r.getIdAtencion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        r.setIdReceta(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Receta.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza una receta existente.
     *
     * @param r Objeto Receta con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Receta r) {
        String sql = "UPDATE recetas SET id_atencion = ? WHERE id_receta = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, r.getIdAtencion());
            ps.setInt(2, r.getIdReceta());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Receta.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una receta por su identificador.
     *
     * @param idReceta Identificador de la receta
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idReceta) {
        String sql = "DELETE FROM recetas WHERE id_receta = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idReceta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Receta.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca una receta por su identificador.
     *
     * @param idReceta Identificador de la receta
     * @return Objeto Receta encontrado, o null si no existe
     */
    @Override
    public Receta buscarPorId(int idReceta) {
        String sql = "SELECT * FROM recetas WHERE id_receta = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReceta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearReceta(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Receta.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca la receta asociada a una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Objeto Receta encontrado, o null si no existe
     */
    public Receta buscarPorAtencion(int idAtencion) {
        String sql = "SELECT * FROM recetas WHERE id_atencion = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAtencion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearReceta(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Receta.buscarPorAtencion: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todas las recetas registradas en orden descendente por fecha.
     *
     * @return Lista de objetos Receta
     */
    @Override
    public List<Receta> listarTodos() {
        List<Receta> lista = new ArrayList<>();
        String sql = "SELECT * FROM recetas ORDER BY fecha_emision DESC";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearReceta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Receta.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Receta.
     *
     * @param rs ResultSet con los datos de la receta
     * @return Objeto Receta mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Receta mapearReceta(ResultSet rs) throws SQLException {
        return new Receta(
                rs.getInt("id_receta"),
                rs.getInt("id_atencion"),
                rs.getTimestamp("fecha_emision")
        );
    }
}

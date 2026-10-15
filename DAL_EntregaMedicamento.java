/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.EntregaMedicamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad EntregaMedicamento.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con la
 * dispensación de medicamentos a pacientes, utilizando procedimientos
 * almacenados para las operaciones principales.
 *
 * @author Grupo04
 */
public class DAL_EntregaMedicamento implements DAL_iCRUD<EntregaMedicamento> {

    /**
     * Registra una nueva entrega de medicamento mediante procedimiento
     * almacenado.
     *
     * @param em Objeto EntregaMedicamento con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(EntregaMedicamento em) {
        String sql = "{call SP_EntregarMedicamentoConStock(?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, em.getIdRecetaDetalle());
            ps.setInt(2, em.getIdFarmaceutico());
            ps.setInt(3, em.getCantidadEntregada());

            boolean tieneResultSet = ps.execute();

            if (tieneResultSet) {
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        em.setIdEntrega(rs.getInt(1));
                    }
                }
                return true;
            } else {
                return ps.getUpdateCount() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error en DAL_EntregaMedicamento.insertar: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza una entrega de medicamento mediante procedimiento almacenado.
     *
     * @param em Objeto EntregaMedicamento con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(EntregaMedicamento em) {
        String sql = "{call sp_actualizar_entrega_medicamento(?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, em.getIdRecetaDetalle());
            ps.setInt(2, em.getIdFarmaceutico());
            ps.setInt(3, em.getCantidadEntregada());
            ps.setInt(4, em.getIdEntrega());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_EntregaMedicamento.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una entrega de medicamento mediante procedimiento almacenado.
     *
     * @param idEntrega Identificador de la entrega
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idEntrega) {
        String sql = "{call sp_eliminar_entrega_medicamento(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idEntrega);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_EntregaMedicamento.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca una entrega por su identificador mediante procedimiento almacenado.
     *
     * @param idEntrega Identificador de la entrega
     * @return Objeto EntregaMedicamento encontrado, o null si no existe
     */
    @Override
    public EntregaMedicamento buscarPorId(int idEntrega) {
        String sql = "{call sp_buscar_entrega_medicamento_por_id(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idEntrega);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEntrega(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_EntregaMedicamento.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista el historial de entregas de un ítem de receta.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @return Lista de entregas asociadas al ítem de receta
     */
    public List<EntregaMedicamento> listarPorRecetaDetalle(int idRecetaDetalle) {
        List<EntregaMedicamento> lista = new ArrayList<>();
        String sql = "{call sp_listar_entregas_por_receta_detalle(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idRecetaDetalle);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearEntrega(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_EntregaMedicamento.listarPorRecetaDetalle: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista las entregas realizadas por un farmacéutico específico.
     *
     * @param idFarmaceutico Identificador del farmacéutico
     * @return Lista de entregas realizadas por el farmacéutico
     */
    public List<EntregaMedicamento> listarPorFarmaceutico(int idFarmaceutico) {
        List<EntregaMedicamento> lista = new ArrayList<>();
        String sql = "{call sp_listar_entregas_por_farmaceutico(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idFarmaceutico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearEntrega(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_EntregaMedicamento.listarPorFarmaceutico: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todas las entregas de medicamentos registradas.
     *
     * @return Lista de objetos EntregaMedicamento
     */
    @Override
    public List<EntregaMedicamento> listarTodos() {
        List<EntregaMedicamento> lista = new ArrayList<>();
        String sql = "{call sp_listar_entregas_medicamentos()}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearEntrega(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_EntregaMedicamento.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto EntregaMedicamento.
     *
     * @param rs ResultSet con los datos de la entrega
     * @return Objeto EntregaMedicamento mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private EntregaMedicamento mapearEntrega(ResultSet rs) throws SQLException {
        EntregaMedicamento em = new EntregaMedicamento(
                rs.getInt("id_receta_detalle"),
                rs.getInt("id_farmaceutico"),
                rs.getInt("cantidad_entregada")
        );
        em.setIdEntrega(rs.getInt("id_entrega"));
        em.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
        return em;
    }
}

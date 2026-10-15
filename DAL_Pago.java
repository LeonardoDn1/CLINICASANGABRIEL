/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Pago;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Pago.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * pagos realizados por los pacientes, utilizando procedimientos almacenados
 * para las operaciones principales.
 *
 * @author Grupo04
 */
public class DAL_Pago implements DAL_iCRUD<Pago> {

    /**
     * Registra un nuevo pago (por defecto con comprobante tipo BOLETA).
     *
     * @param p Objeto Pago con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Pago p) {
        return insertar(p, "BOLETA");
    }

    /**
     * Registra un nuevo pago con un tipo de comprobante específico.
     *
     * @param p Objeto Pago con los datos a registrar
     * @param tipoComprobante Tipo de comprobante a generar (BOLETA o FACTURA)
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean insertar(Pago p, String tipoComprobante) {
        String sql = "{call SP_RegistrarPagoConComprobante(?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, p.getIdAtencion());
            ps.setInt(2, p.getIdCajero());
            ps.setBigDecimal(3, p.getMonto());
            ps.setString(4, p.getMetodoPago());
            ps.setString(5, tipoComprobante);

            boolean tieneResultSet = ps.execute();

            if (tieneResultSet) {
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        p.setIdPago(rs.getInt(1));
                    }
                }
                return true;
            } else {
                return ps.getUpdateCount() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error en DAL_Pago.insertar: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza un pago existente mediante procedimiento almacenado.
     *
     * @param p Objeto Pago con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Pago p) {
        String sql = "{call sp_actualizar_pago(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, p.getIdAtencion());
            ps.setInt(2, p.getIdCajero());
            ps.setBigDecimal(3, p.getMonto());
            ps.setString(4, p.getMetodoPago());
            ps.setInt(5, p.getIdPago());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Pago.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un pago mediante procedimiento almacenado.
     *
     * @param idPago Identificador del pago
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idPago) {
        String sql = "{call sp_eliminar_pago(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Pago.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un pago por su identificador mediante procedimiento almacenado.
     *
     * @param idPago Identificador del pago
     * @return Objeto Pago encontrado, o null si no existe
     */
    @Override
    public Pago buscarPorId(int idPago) {
        String sql = "{call sp_buscar_pago_por_id(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPago(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Pago.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca el pago asociado a una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Objeto Pago encontrado, o null si no existe
     */
    public Pago buscarPorAtencion(int idAtencion) {
        String sql = "{call sp_buscar_pago_por_atencion(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idAtencion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPago(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Pago.buscarPorAtencion: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista los pagos procesados por un cajero específico.
     *
     * @param idCajero Identificador del cajero
     * @return Lista de pagos realizados por el cajero
     */
    public List<Pago> listarPorCajero(int idCajero) {
        List<Pago> lista = new ArrayList<>();
        String sql = "{call sp_listar_pagos_por_cajero(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idCajero);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPago(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Pago.listarPorCajero: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los pagos registrados.
     *
     * @return Lista de objetos Pago
     */
    @Override
    public List<Pago> listarTodos() {
        List<Pago> lista = new ArrayList<>();
        String sql = "{call sp_listar_pagos()}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPago(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Pago.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Pago.
     *
     * @param rs ResultSet con los datos del pago
     * @return Objeto Pago mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Pago mapearPago(ResultSet rs) throws SQLException {
        Pago p = new Pago(
                rs.getInt("id_atencion"),
                rs.getInt("id_cajero"),
                rs.getBigDecimal("monto"),
                rs.getString("metodo_pago")
        );
        p.setIdPago(rs.getInt("id_pago"));
        p.setFechaPago(rs.getTimestamp("fecha_pago"));
        return p;
    }
}

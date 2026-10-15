/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.AtencionMedica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad AtencionMedica.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con las
 * atenciones médicas, utilizando procedimientos almacenados para las
 * operaciones principales.
 *
 * @author Grupo04
 */
public class DAL_AtencionMedica implements DAL_iCRUD<AtencionMedica> {

    /**
     * Registra una nueva atención médica utilizando un procedimiento
     * almacenado.
     *
     * @param a Objeto AtencionMedica con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(AtencionMedica a) {
        String sql = "{call SP_RegistrarAtencionCompleta(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, a.getIdCita());
            ps.setString(2, a.getMotivoConsulta());
            ps.setString(3, a.getAntecedentes());
            ps.setString(4, a.getTratamiento());
            ps.setString(5, a.getObservaciones());
            ps.setBoolean(6, a.isCerrada());

            boolean tieneResultSet = ps.execute();

            if (tieneResultSet) {
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        a.setIdAtencion(rs.getInt(1));
                    }
                }
                return true;
            } else {
                return ps.getUpdateCount() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error en DAL_AtencionMedica.insertar: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza una atención médica existente mediante procedimiento
     * almacenado.
     *
     * @param a Objeto AtencionMedica con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(AtencionMedica a) {
        String sql = "{call sp_actualizar_atencion_medica(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, a.getMotivoConsulta());
            ps.setString(2, a.getAntecedentes());
            ps.setString(3, a.getTratamiento());
            ps.setString(4, a.getObservaciones());
            ps.setBoolean(5, a.isCerrada());
            ps.setInt(6, a.getIdAtencion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_AtencionMedica.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra definitivamente una atención médica mediante procedimiento
     * almacenado.
     *
     * @param idAtencion Identificador de la atención a cerrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean cerrarAtencion(int idAtencion) {
        String sql = "{call sp_cerrar_atencion_medica(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idAtencion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_AtencionMedica.cerrarAtencion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina lógicamente una atención (equivale a cerrarla).
     *
     * @param idAtencion Identificador de la atención
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idAtencion) {
        return cerrarAtencion(idAtencion);
    }

    /**
     * Busca una atención médica por su identificador mediante procedimiento
     * almacenado.
     *
     * @param idAtencion Identificador de la atención
     * @return Objeto AtencionMedica encontrado, o null si no existe
     */
    @Override
    public AtencionMedica buscarPorId(int idAtencion) {
        String sql = "{call sp_buscar_atencion_medica_por_id(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idAtencion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAtencion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_AtencionMedica.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca la atención asociada a una cita específica.
     *
     * @param idCita Identificador de la cita
     * @return Objeto AtencionMedica encontrado, o null si no existe
     */
    public AtencionMedica buscarPorCita(int idCita) {
        String sql = "SELECT * FROM atenciones_medicas WHERE id_cita = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAtencion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_AtencionMedica.buscarPorCita: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todas las atenciones médicas registradas mediante procedimiento
     * almacenado.
     *
     * @return Lista de objetos AtencionMedica
     */
    @Override
    public List<AtencionMedica> listarTodos() {
        List<AtencionMedica> lista = new ArrayList<>();
        String sql = "{call sp_listar_atenciones_medicas()}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAtencion(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_AtencionMedica.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto AtencionMedica.
     *
     * @param rs ResultSet con los datos de la atención
     * @return Objeto AtencionMedica mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private AtencionMedica mapearAtencion(ResultSet rs) throws SQLException {
        return new AtencionMedica.Builder(
                rs.getInt("id_cita"),
                rs.getString("motivo_consulta")
        )
                .idAtencion(rs.getInt("id_atencion"))
                .antecedentes(rs.getString("antecedentes"))
                .tratamiento(rs.getString("tratamiento"))
                .observaciones(rs.getString("observaciones"))
                .cerrada(rs.getBoolean("cerrada"))
                .fechaAtencion(rs.getTimestamp("fecha_atencion"))
                .build();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.ResultadoLaboratorio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad ResultadoLaboratorio.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * resultados de exámenes de laboratorio, utilizando procedimientos almacenados
 * para las operaciones principales.
 *
 * @author Grupo04
 */
public class DAL_ResultadoLaboratorio implements DAL_iCRUD<ResultadoLaboratorio> {

    /**
     * Registra un nuevo resultado de examen mediante procedimiento almacenado.
     *
     * @param r Objeto ResultadoLaboratorio con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(ResultadoLaboratorio r) {
        String sql = "{call SP_RegistrarResultadoLaboratorio(?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, r.getIdExamen());
            ps.setInt(2, r.getIdLaboratorista());
            ps.setString(3, r.getResultadoTexto());
            ps.setString(4, r.getRutaArchivo());

            boolean tieneResultado = ps.execute();

            while (true) {
                if (tieneResultado) {
                    try (ResultSet rs = ps.getResultSet()) {
                        if (rs != null && rs.next()) {
                            r.setIdResultado(rs.getInt(1));
                        }
                    }
                } else {
                    int filasAfectadas = ps.getUpdateCount();
                    if (filasAfectadas == -1) {
                        break;
                    }
                }
                tieneResultado = ps.getMoreResults();
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error en DAL_ResultadoLaboratorio.insertar: " + e.getMessage()
                    + " | SQLState: " + e.getSQLState()
                    + " | Código: " + e.getErrorCode());
            return false;
        }
    }

    /**
     * Actualiza un resultado de examen existente mediante procedimiento
     * almacenado.
     *
     * @param r Objeto ResultadoLaboratorio con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(ResultadoLaboratorio r) {
        String sql = "{call sp_actualizar_resultado_laboratorio(?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, r.getIdLaboratorista());
            ps.setString(2, r.getResultadoTexto());
            ps.setString(3, r.getRutaArchivo());
            ps.setInt(4, r.getIdResultado());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_ResultadoLaboratorio.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un resultado de examen mediante procedimiento almacenado.
     *
     * @param idResultado Identificador del resultado
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idResultado) {
        String sql = "{call sp_eliminar_resultado_laboratorio(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idResultado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_ResultadoLaboratorio.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un resultado por su identificador mediante procedimiento
     * almacenado.
     *
     * @param idResultado Identificador del resultado
     * @return Objeto ResultadoLaboratorio encontrado, o null si no existe
     */
    @Override
    public ResultadoLaboratorio buscarPorId(int idResultado) {
        String sql = "{call sp_buscar_resultado_laboratorio_por_id(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idResultado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultado(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_ResultadoLaboratorio.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca el resultado asociado a un examen específico.
     *
     * @param idExamen Identificador del examen
     * @return Objeto ResultadoLaboratorio encontrado, o null si no existe
     */
    public ResultadoLaboratorio buscarPorExamen(int idExamen) {
        String sql = "{call sp_buscar_resultado_laboratorio_por_examen(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idExamen);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultado(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_ResultadoLaboratorio.buscarPorExamen: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los resultados de laboratorio registrados.
     *
     * @return Lista de objetos ResultadoLaboratorio
     */
    @Override
    public List<ResultadoLaboratorio> listarTodos() {
        List<ResultadoLaboratorio> lista = new ArrayList<>();
        String sql = "{call sp_listar_resultados_laboratorio()}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_ResultadoLaboratorio.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto ResultadoLaboratorio.
     *
     * @param rs ResultSet con los datos del resultado
     * @return Objeto ResultadoLaboratorio mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private ResultadoLaboratorio mapearResultado(ResultSet rs) throws SQLException {
        ResultadoLaboratorio r = new ResultadoLaboratorio(
                rs.getInt("id_examen"),
                rs.getInt("id_laboratorista"),
                rs.getString("resultado_texto"),
                rs.getString("ruta_archivo")
        );
        r.setIdResultado(rs.getInt("id_resultado"));
        r.setFechaResultado(rs.getTimestamp("fecha_resultado"));
        return r;
    }
}

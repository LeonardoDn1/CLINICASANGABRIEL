/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Auditoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Auditoria.
 *
 * Gestiona el registro y consulta de eventos del sistema, utilizando
 * procedimientos almacenados para las operaciones principales y manteniendo el
 * historial de acciones de los usuarios.
 *
 * @author Grupo04
 */
public class DAL_Auditoria implements DAL_iCRUD<Auditoria> {

    /**
     * Registra un nuevo evento de auditoría en el sistema.
     *
     * @param a Objeto Auditoria con los datos del evento
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Auditoria a) {
        String sql = "INSERT INTO auditoria (id_usuario, modulo, operacion, detalle) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, a.getIdUsuario());
            ps.setString(2, a.getModulo());
            ps.setString(3, a.getOperacion());
            ps.setString(4, a.getDetalle());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        a.setIdAuditoria(rs.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Auditoria.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza un registro de auditoría existente mediante procedimiento
     * almacenado.
     *
     * @param a Objeto Auditoria con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Auditoria a) {
        String sql = "{call sp_actualizar_auditoria(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, a.getIdUsuario());
            ps.setString(2, a.getModulo());
            ps.setString(3, a.getOperacion());
            ps.setString(4, a.getDetalle());
            ps.setLong(5, a.getIdAuditoria());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Auditoria.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de auditoría mediante procedimiento almacenado.
     *
     * @param idAuditoria Identificador del registro a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idAuditoria) {
        String sql = "{call sp_eliminar_auditoria(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idAuditoria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Auditoria.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un registro de auditoría por su identificador (tipo long).
     *
     * @param idAuditoria Identificador del registro
     * @return Objeto Auditoria encontrado, o null si no existe
     */
    public Auditoria buscarPorId(long idAuditoria) {
        String sql = "{call sp_buscar_auditoria_por_id(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setLong(1, idAuditoria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAuditoria(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Auditoria.buscarPorId(long): " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un registro de auditoría por su identificador (tipo int).
     *
     * @param idAuditoria Identificador del registro
     * @return Objeto Auditoria encontrado, o null si no existe
     */
    @Override
    public Auditoria buscarPorId(int idAuditoria) {
        return buscarPorId((long) idAuditoria);
    }

    /**
     * Lista los registros de auditoría filtrados por usuario.
     *
     * @param idUsuario Identificador del usuario
     * @return Lista de objetos Auditoria del usuario especificado
     */
    public List<Auditoria> listarPorUsuario(int idUsuario) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "{call SP_ListarAuditoriaPorFiltros(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAuditoria(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Auditoria.listarPorUsuario: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista los registros de auditoría filtrados por módulo.
     *
     * @param modulo Nombre del módulo del sistema
     * @return Lista de objetos Auditoria del módulo especificado
     */
    public List<Auditoria> listarPorModulo(String modulo) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "{call sp_listar_auditorias_por_modulo(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setString(1, modulo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAuditoria(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Auditoria.listarPorModulo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los registros de auditoría mediante procedimiento almacenado.
     *
     * @return Lista de todos los objetos Auditoria
     */
    @Override
    public List<Auditoria> listarTodos() {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "{call sp_listar_auditorias()}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAuditoria(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Auditoria.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Auditoria.
     *
     * @param rs ResultSet con los datos de auditoría
     * @return Objeto Auditoria mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Auditoria mapearAuditoria(ResultSet rs) throws SQLException {
        Auditoria a = new Auditoria(
                rs.getInt("id_usuario"),
                rs.getString("modulo"),
                rs.getString("operacion"),
                rs.getString("detalle")
        );
        a.setIdAuditoria(rs.getLong("id_auditoria"));
        a.setFechaHora(rs.getTimestamp("fecha_hora"));
        return a;
    }

    /**
     * Establece el identificador del usuario en sesión para auditoría.
     *
     * @param con Conexión activa a la base de datos
     * @param idUsuario Identificador del usuario logueado
     * @throws SQLException Si ocurre un error al ejecutar la sentencia
     */
    public static void establecerUsuarioSesion(Connection con, int idUsuario) throws SQLException {
        String sql = "SET @usuario_actual_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }
}

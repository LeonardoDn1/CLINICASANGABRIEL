/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Capa de acceso a datos para la entidad Usuario.
 *
 * Gestiona las operaciones CRUD, autenticación y control de acceso de los
 * usuarios del sistema, utilizando procedimientos almacenados y encriptación
 * BCrypt para la seguridad de contraseñas.
 *
 * @author Grupo04
 */
public class DAL_Usuario implements DAL_iCRUD<Usuario> {

    /**
     * Autentica a un usuario verificando sus credenciales.
     *
     * @param username Nombre de usuario
     * @param passwordPlana Contraseña en texto plano
     * @return Objeto Usuario autenticado, o null si falla
     */
    public Usuario autenticar(String username, String passwordPlana) {
        String sql = "{call SP_AutenticarUsuario(?)}";

        try (Connection cn = Conexion.getInstancia().getConexion(); CallableStatement ps = cn.prepareCall(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String estado = rs.getString("estado");
                    if ("BLOQUEADO".equalsIgnoreCase(estado) || "INACTIVO".equalsIgnoreCase(estado)) {
                        return null;
                    }

                    String hashEnBD = rs.getString("password_hash");
                    boolean valido = BCrypt.checkpw(passwordPlana, hashEnBD);

                    if (valido) {
                        Usuario user = mapearUsuario(rs);
                        SesionUsuario.setUsuarioLogueado(user.getIdUsuario());
                        actualizarUltimoAcceso(user.getIdUsuario());
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Usuario.autenticar: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza la fecha y hora del último acceso del usuario.
     *
     * @param idUsuario Identificador del usuario
     */
    private void actualizarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE usuarios SET ultimo_acceso = NOW() WHERE id_usuario = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar ultimo acceso: " + e.getMessage());
        }
    }

    /**
     * Registra un nuevo usuario en el sistema mediante procedimiento
     * almacenado.
     *
     * @param u Objeto Usuario con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Usuario u) {
        String sql = "{call sp_insertar_usuario(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setInt(3, u.getIdRol());
            ps.setString(4, u.getNombres());
            ps.setString(5, u.getApellidos());
            ps.setString(6, u.getEmail());
            ps.setString(7, (u.getEstado() != null && !u.getEstado().trim().isEmpty()) ? u.getEstado() : "ACTIVO");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error en DAL_Usuario.insertar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza los datos de un usuario existente mediante procedimiento
     * almacenado.
     *
     * @param u Objeto Usuario con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Usuario u) {
        String sql = "{call sp_actualizar_usuario(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, u.getIdRol());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getEmail());
            ps.setInt(5, u.getIdUsuario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Usuario.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bloquea un usuario (eliminación lógica) mediante procedimiento
     * almacenado.
     *
     * @param idUsuario Identificador del usuario a bloquear
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idUsuario) {
        String sql = "{call sp_bloquear_usuario(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Usuario.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un usuario por su identificador mediante procedimiento almacenado.
     *
     * @param idUsuario Identificador del usuario
     * @return Objeto Usuario encontrado, o null si no existe
     */
    @Override
    public Usuario buscarPorId(int idUsuario) {
        String sql = "{call sp_buscar_usuario_por_id(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Usuario.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los usuarios registrados mediante procedimiento almacenado.
     *
     * @return Lista de objetos Usuario
     */
    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "{call sp_listar_usuarios()}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Usuario.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Desbloquea un usuario (restaura acceso) mediante procedimiento
     * almacenado.
     *
     * @param idUsuario Identificador del usuario a desbloquear
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean activarUsuario(int idUsuario) {
        String sql = "{call sp_activar_usuario(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Usuario.activarUsuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Convierte un ResultSet en un objeto Usuario.
     *
     * @param rs ResultSet con los datos del usuario
     * @return Objeto Usuario mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario.Builder(
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getInt("id_rol"),
                rs.getString("nombres"),
                rs.getString("apellidos")
        )
                .idUsuario(rs.getInt("id_usuario"))
                .email(rs.getString("email"))
                .estado(rs.getString("estado"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion"))
                .ultimoAcceso(rs.getTimestamp("ultimo_acceso"))
                .build();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Medico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Medico.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * médicos registrados en el sistema, utilizando procedimientos almacenados para
 * las operaciones principales.
 *
 * @author Grupo04
 */
public class DAL_Medico implements DAL_iCRUD<Medico> {

    /**
     * Registra un nuevo médico mediante procedimiento almacenado.
     *
     * @param m Objeto Medico con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Medico m) {
        String sql = "{call SP_RegistrarMedicoConEspecialidades(?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, m.getIdUsuario());
            ps.setString(2, m.getNumeroColegiatura());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    m.setIdMedico(rs.getInt("id"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medico.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza los datos de un médico mediante procedimiento almacenado.
     *
     * @param m Objeto Medico con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Medico m) {
        String sql = "{call sp_actualizar_medico(?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, m.getNumeroColegiatura());
            ps.setInt(2, m.getIdMedico());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medico.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un médico mediante procedimiento almacenado.
     *
     * @param idMedico Identificador del médico
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idMedico) {
        String sql = "{call sp_eliminar_medico(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idMedico);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medico.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un médico por su identificador mediante procedimiento almacenado.
     *
     * @param idMedico Identificador del médico
     * @return Objeto Medico encontrado, o null si no existe
     */
    @Override
    public Medico buscarPorId(int idMedico) {
        String sql = "{call sp_buscar_medico_por_id(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMedico(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medico.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un médico por su número de colegiatura.
     *
     * @param colegiatura Número de colegiatura del médico
     * @return Objeto Medico encontrado, o null si no existe
     */
    public Medico buscarPorColegiatura(String colegiatura) {
        String sql = "{call sp_buscar_medico_por_colegiatura(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setString(1, colegiatura);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMedico(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medico.buscarPorColegiatura: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un médico por el identificador de usuario asociado.
     *
     * @param idUsuario Identificador del usuario
     * @return Objeto Medico encontrado, o null si no existe
     */
    public Medico buscarPorUsuario(int idUsuario) {
        String sql = "{call sp_buscar_medico_por_usuario(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMedico(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medico.buscarPorUsuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los médicos registrados mediante procedimiento almacenado.
     *
     * @return Lista de objetos Medico
     */
    @Override
    public List<Medico> listarTodos() {
        List<Medico> lista = new ArrayList<>();
        String sql = "{call sp_listar_medicos()}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMedico(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Medico.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Medico.
     *
     * @param rs ResultSet con los datos del médico
     * @return Objeto Medico mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Medico mapearMedico(ResultSet rs) throws SQLException {
        Medico medico = new Medico();
        medico.setIdMedico(rs.getInt("id_medico"));
        medico.setIdUsuario(rs.getInt("id_usuario"));
        medico.setNumeroColegiatura(rs.getString("numero_colegiatura"));
        return medico;
    }
}

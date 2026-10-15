/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.SeguroMedico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad SeguroMedico.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * seguros médicos de los pacientes.
 *
 * @author Grupo04
 */
public class DAL_SeguroMedico implements DAL_iCRUD<SeguroMedico> {

    /**
     * Registra un nuevo seguro médico para un paciente.
     *
     * @param sm Objeto SeguroMedico con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(SeguroMedico sm) {
        String sql = "INSERT INTO seguros_medicos (id_paciente, aseguradora, numero_poliza, vigencia_inicio, vigencia_fin) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, sm.getIdPaciente());
            ps.setString(2, sm.getAseguradora());
            ps.setString(3, sm.getNumeroPoliza());

            if (sm.getVigenciaInicio() != null) {
                ps.setDate(4, new java.sql.Date(sm.getVigenciaInicio().getTime()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (sm.getVigenciaFin() != null) {
                ps.setDate(5, new java.sql.Date(sm.getVigenciaFin().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_SeguroMedico.insertar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra un seguro médico utilizando una conexión existente (para
     * transacciones).
     *
     * @param sm Objeto SeguroMedico con los datos a registrar
     * @param con Conexión activa a la base de datos
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean insertar(SeguroMedico sm, Connection con) {
        String sql = "INSERT INTO seguros_medicos (id_paciente, aseguradora, numero_poliza, vigencia_inicio, vigencia_fin) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, sm.getIdPaciente());
            ps.setString(2, sm.getAseguradora());
            ps.setString(3, sm.getNumeroPoliza());

            if (sm.getVigenciaInicio() != null) {
                ps.setDate(4, sm.getVigenciaInicio());
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (sm.getVigenciaFin() != null) {
                ps.setDate(5, sm.getVigenciaFin());
            } else {
                ps.setNull(5, Types.DATE);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_SeguroMedico.insertar transacción: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un seguro médico existente.
     *
     * @param sm Objeto SeguroMedico con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(SeguroMedico sm) {
        String sql = "UPDATE seguros_medicos SET aseguradora = ?, numero_poliza = ?, "
                + "vigencia_inicio = ?, vigencia_fin = ? WHERE id_seguro = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, sm.getAseguradora());
            ps.setString(2, sm.getNumeroPoliza());

            if (sm.getVigenciaInicio() != null) {
                ps.setDate(3, new java.sql.Date(sm.getVigenciaInicio().getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (sm.getVigenciaFin() != null) {
                ps.setDate(4, new java.sql.Date(sm.getVigenciaFin().getTime()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setInt(5, sm.getIdSeguro());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_SeguroMedico.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un seguro médico por su identificador.
     *
     * @param idSeguro Identificador del seguro
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idSeguro) {
        String sql = "DELETE FROM seguros_medicos WHERE id_seguro = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idSeguro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_SeguroMedico.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un seguro médico por su identificador.
     *
     * @param idSeguro Identificador del seguro
     * @return Objeto SeguroMedico encontrado, o null si no existe
     */
    @Override
    public SeguroMedico buscarPorId(int idSeguro) {
        String sql = "SELECT * FROM seguros_medicos WHERE id_seguro = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSeguro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearSeguroMedico(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_SeguroMedico.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista los seguros médicos asociados a un paciente específico.
     *
     * @param idPaciente Identificador del paciente
     * @return Lista de seguros del paciente
     */
    public List<SeguroMedico> listarPorPaciente(int idPaciente) {
        List<SeguroMedico> lista = new ArrayList<>();
        String sql = "SELECT * FROM seguros_medicos WHERE id_paciente = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearSeguroMedico(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_SeguroMedico.listarPorPaciente: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los seguros médicos registrados.
     *
     * @return Lista de objetos SeguroMedico
     */
    @Override
    public List<SeguroMedico> listarTodos() {
        List<SeguroMedico> lista = new ArrayList<>();
        String sql = "SELECT * FROM seguros_medicos";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearSeguroMedico(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_SeguroMedico.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto SeguroMedico.
     *
     * @param rs ResultSet con los datos del seguro
     * @return Objeto SeguroMedico mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private SeguroMedico mapearSeguroMedico(ResultSet rs) throws SQLException {
        SeguroMedico sm = new SeguroMedico(
                rs.getInt("id_paciente"),
                rs.getString("aseguradora"),
                rs.getString("numero_poliza"),
                rs.getDate("vigencia_inicio"),
                rs.getDate("vigencia_fin")
        );
        sm.setIdSeguro(rs.getInt("id_seguro"));
        return sm;
    }
}

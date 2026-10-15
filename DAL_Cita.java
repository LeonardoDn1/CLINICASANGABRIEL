/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Cita.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con la
 * programación de citas médicas, utilizando procedimientos almacenados para las
 * operaciones principales.
 *
 * @author Grupo04
 */
public class DAL_Cita implements DAL_iCRUD<Cita> {

    /**
     * Registra una nueva cita médica mediante procedimiento almacenado.
     *
     * @param c Objeto Cita con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Cita c) {
        String sql = "{call SP_RegistrarCitaConValidacion(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, c.getIdPaciente());
            ps.setInt(2, c.getIdMedico());

            if (c.getFecha() != null) {
                ps.setDate(3, new java.sql.Date(c.getFecha().getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setTime(4, c.getHoraInicio());
            ps.setTime(5, c.getHoraFin());
            ps.setString(6, c.getEstado() != null ? c.getEstado() : "PROGRAMADA");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c.setIdCita(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Cita.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza una cita existente mediante procedimiento almacenado.
     *
     * @param c Objeto Cita con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Cita c) {
        String sql = "{call sp_actualizar_cita(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            if (c.getFecha() != null) {
                ps.setDate(1, new java.sql.Date(c.getFecha().getTime()));
            } else {
                ps.setNull(1, Types.DATE);
            }

            ps.setTime(2, c.getHoraInicio());
            ps.setTime(3, c.getHoraFin());
            ps.setString(4, c.getEstado());
            ps.setInt(5, c.getIdCita());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Cita.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancela una cita (eliminación lógica cambiando estado a CANCELADA).
     *
     * @param idCita Identificador de la cita a cancelar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idCita) {
        return cambiarEstado(idCita, "CANCELADA");
    }

    /**
     * Cambia el estado de una cita mediante procedimiento almacenado.
     *
     * @param idCita Identificador de la cita
     * @param nuevoEstado Nuevo estado a asignar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean cambiarEstado(int idCita, String nuevoEstado) {
        String sql = "{call SP_CancelarCita(?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCita);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Cita.cambiarEstado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca una cita por su identificador.
     *
     * @param idCita Identificador de la cita
     * @return Objeto Cita encontrado, o null si no existe
     */
    @Override
    public Cita buscarPorId(int idCita) {
        String sql = "SELECT * FROM citas WHERE id_cita = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCita(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Cita.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista las citas de un médico en una fecha específica.
     *
     * @param idMedico Identificador del médico
     * @param fecha Fecha a consultar
     * @return Lista de citas del médico en la fecha indicada
     */
    public List<Cita> listarPorMedicoYFecha(int idMedico, java.util.Date fecha) {
        List<Cita> lista = new ArrayList<>();
        String sql = "{call sp_listar_citas_por_medico_y_fecha(?, ?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idMedico);
            ps.setDate(2, new java.sql.Date(fecha.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Cita.listarPorMedicoYFecha: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista el historial de citas de un paciente.
     *
     * @param idPaciente Identificador del paciente
     * @return Lista de citas del paciente
     */
    public List<Cita> listarPorPaciente(int idPaciente) {
        List<Cita> lista = new ArrayList<>();
        String sql = "{call sp_listar_citas_por_paciente(?)}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Cita.listarPorPaciente: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todas las citas registradas.
     *
     * @return Lista de todas las citas
     */
    @Override
    public List<Cita> listarTodos() {
        List<Cita> lista = new ArrayList<>();
        String sql = "{call sp_listar_citas()}";
        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCita(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Cita.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto Cita.
     *
     * @param rs ResultSet con los datos de la cita
     * @return Objeto Cita mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Cita mapearCita(ResultSet rs) throws SQLException {
        return new Cita.Builder(
                rs.getInt("id_paciente"),
                rs.getInt("id_medico"),
                rs.getDate("fecha"),
                rs.getTime("hora_inicio")
        )
                .idCita(rs.getInt("id_cita"))
                .horaFin(rs.getTime("hora_fin"))
                .estado(rs.getString("estado"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion"))
                .build();
    }
}

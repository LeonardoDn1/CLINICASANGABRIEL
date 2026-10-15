/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.HorarioMedico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad HorarioMedico.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * horarios de atención de los médicos.
 *
 * @author Grupo04
 */
public class DAL_HorarioMedico implements DAL_iCRUD<HorarioMedico> {

    /**
     * Registra un nuevo horario de atención para un médico.
     *
     * @param hm Objeto HorarioMedico con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(HorarioMedico hm) {
        String sql = "INSERT INTO horarios_medico (id_medico, dia_semana, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, hm.getIdMedico());
            ps.setString(2, hm.getDiaSemana());
            ps.setTime(3, hm.getHoraInicio());
            ps.setTime(4, hm.getHoraFin());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        hm.setIdHorario(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_HorarioMedico.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza un horario de atención existente.
     *
     * @param hm Objeto HorarioMedico con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(HorarioMedico hm) {
        String sql = "UPDATE horarios_medico SET dia_semana = ?, hora_inicio = ?, hora_fin = ? WHERE id_horario = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, hm.getDiaSemana());
            ps.setTime(2, hm.getHoraInicio());
            ps.setTime(3, hm.getHoraFin());
            ps.setInt(4, hm.getIdHorario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_HorarioMedico.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un horario por su identificador.
     *
     * @param idHorario Identificador del horario
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idHorario) {
        String sql = "DELETE FROM horarios_medico WHERE id_horario = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idHorario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_HorarioMedico.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un horario por su identificador.
     *
     * @param idHorario Identificador del horario
     * @return Objeto HorarioMedico encontrado, o null si no existe
     */
    @Override
    public HorarioMedico buscarPorId(int idHorario) {
        String sql = "SELECT * FROM horarios_medico WHERE id_horario = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idHorario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearHorario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_HorarioMedico.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los horarios de un médico específico.
     *
     * @param idMedico Identificador del médico
     * @return Lista de horarios del médico
     */
    public List<HorarioMedico> listarPorMedico(int idMedico) {
        List<HorarioMedico> lista = new ArrayList<>();
        String sql = "SELECT * FROM horarios_medico WHERE id_medico = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearHorario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_HorarioMedico.listarPorMedico: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca horarios de un médico por día de la semana específico.
     *
     * @param idMedico Identificador del médico
     * @param diaSemana Día de la semana a consultar
     * @return Lista de horarios del médico en el día especificado
     */
    public List<HorarioMedico> buscarPorMedicoYDia(int idMedico, String diaSemana) {
        List<HorarioMedico> lista = new ArrayList<>();
        String sql = "SELECT * FROM horarios_medico WHERE id_medico = ? AND dia_semana = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMedico);
            ps.setString(2, diaSemana);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearHorario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_HorarioMedico.buscarPorMedicoYDia: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los horarios registrados.
     *
     * @return Lista de objetos HorarioMedico
     */
    @Override
    public List<HorarioMedico> listarTodos() {
        List<HorarioMedico> lista = new ArrayList<>();
        String sql = "SELECT * FROM horarios_medico";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearHorario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_HorarioMedico.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto HorarioMedico.
     *
     * @param rs ResultSet con los datos del horario
     * @return Objeto HorarioMedico mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private HorarioMedico mapearHorario(ResultSet rs) throws SQLException {
        HorarioMedico hm = new HorarioMedico(
                rs.getInt("id_medico"),
                rs.getString("dia_semana"),
                rs.getTime("hora_inicio"),
                rs.getTime("hora_fin")
        );
        hm.setIdHorario(rs.getInt("id_horario"));
        return hm;
    }
}

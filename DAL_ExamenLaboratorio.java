/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.ExamenLaboratorio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad ExamenLaboratorio.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con las
 * solicitudes de exámenes de laboratorio y diagnóstico.
 *
 * @author Grupo04
 */
public class DAL_ExamenLaboratorio implements DAL_iCRUD<ExamenLaboratorio> {

    /**
     * Registra una nueva solicitud de examen de laboratorio.
     *
     * @param ex Objeto ExamenLaboratorio con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(ExamenLaboratorio ex) {
        String sql = "INSERT INTO examenes_laboratorio (id_atencion, tipo_examen, estado) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, ex.getIdAtencion());
            ps.setString(2, ex.getTipoExamen());
            ps.setString(3, ex.getEstado() != null ? ex.getEstado() : "PENDIENTE");

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ex.setIdExamen(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_ExamenLaboratorio.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza una solicitud de examen existente.
     *
     * @param ex Objeto ExamenLaboratorio con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(ExamenLaboratorio ex) {
        String sql = "UPDATE examenes_laboratorio SET tipo_examen = ?, estado = ? WHERE id_examen = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ex.getTipoExamen());
            ps.setString(2, ex.getEstado());
            ps.setInt(3, ex.getIdExamen());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_ExamenLaboratorio.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cambia el estado de una solicitud de examen.
     *
     * @param idExamen Identificador del examen
     * @param nuevoEstado Nuevo estado a asignar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean cambiarEstado(int idExamen, String nuevoEstado) {
        String sql = "UPDATE examenes_laboratorio SET estado = ? WHERE id_examen = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idExamen);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_ExamenLaboratorio.cambiarEstado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una solicitud de examen por su identificador.
     *
     * @param idExamen Identificador del examen
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idExamen) {
        String sql = "DELETE FROM examenes_laboratorio WHERE id_examen = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idExamen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_ExamenLaboratorio.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca una solicitud de examen por su identificador.
     *
     * @param idExamen Identificador del examen
     * @return Objeto ExamenLaboratorio encontrado, o null si no existe
     */
    @Override
    public ExamenLaboratorio buscarPorId(int idExamen) {
        String sql = "SELECT * FROM examenes_laboratorio WHERE id_examen = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idExamen);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearExamen(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_ExamenLaboratorio.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista las solicitudes de examen asociadas a una atención médica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Lista de exámenes de la atención
     */
    public List<ExamenLaboratorio> listarPorAtencion(int idAtencion) {
        List<ExamenLaboratorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM examenes_laboratorio WHERE id_atencion = ? ORDER BY fecha_solicitud DESC";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAtencion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearExamen(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_ExamenLaboratorio.listarPorAtencion: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista las solicitudes de examen filtradas por estado.
     *
     * @param estado Estado de las solicitudes (PENDIENTE, EN_PROCESO,
     * FINALIZADO, ENTREGADO)
     * @return Lista de exámenes con el estado especificado
     */
    public List<ExamenLaboratorio> listarPorEstado(String estado) {
        List<ExamenLaboratorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM examenes_laboratorio WHERE estado = ? ORDER BY fecha_solicitud ASC";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearExamen(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_ExamenLaboratorio.listarPorEstado: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todas las solicitudes de examen registradas.
     *
     * @return Lista de objetos ExamenLaboratorio
     */
    @Override
    public List<ExamenLaboratorio> listarTodos() {
        List<ExamenLaboratorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM examenes_laboratorio ORDER BY fecha_solicitud DESC";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearExamen(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_ExamenLaboratorio.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto ExamenLaboratorio.
     *
     * @param rs ResultSet con los datos del examen
     * @return Objeto ExamenLaboratorio mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private ExamenLaboratorio mapearExamen(ResultSet rs) throws SQLException {
        ExamenLaboratorio ex = new ExamenLaboratorio(
                rs.getInt("id_atencion"),
                rs.getString("tipo_examen"),
                rs.getString("estado")
        );
        ex.setIdExamen(rs.getInt("id_examen"));
        ex.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
        return ex;
    }
}

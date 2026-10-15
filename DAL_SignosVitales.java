/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.SignosVitales;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad SignosVitales.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con las
 * mediciones clínicas tomadas durante las atenciones médicas.
 *
 * @author Grupo04
 */
public class DAL_SignosVitales implements DAL_iCRUD<SignosVitales> {

    /**
     * Registra los signos vitales de una atención médica.
     *
     * @param s Objeto SignosVitales con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(SignosVitales s) {
        String sql = "INSERT INTO signos_vitales (id_atencion, presion_arterial, temperatura, "
                + "frecuencia_cardiaca, frecuencia_respiratoria, peso_kg, talla_cm, saturacion_oxigeno) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, s.getIdAtencion());
            ps.setString(2, s.getPresionArterial());

            if (s.getTemperatura() != null) {
                ps.setBigDecimal(3, s.getTemperatura());
            } else {
                ps.setNull(3, Types.DECIMAL);
            }

            if (s.getFrecuenciaCardiaca() != null) {
                ps.setInt(4, s.getFrecuenciaCardiaca());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (s.getFrecuenciaRespiratoria() != null) {
                ps.setInt(5, s.getFrecuenciaRespiratoria());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (s.getPesoKg() != null) {
                ps.setBigDecimal(6, s.getPesoKg());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }

            if (s.getTallaCm() != null) {
                ps.setBigDecimal(7, s.getTallaCm());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }

            if (s.getSaturacionOxigeno() != null) {
                ps.setInt(8, s.getSaturacionOxigeno());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        s.setIdSignos(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_SignosVitales.insertar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza los signos vitales de una atención médica.
     *
     * @param s Objeto SignosVitales con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(SignosVitales s) {
        String sql = "UPDATE signos_vitales SET presion_arterial = ?, temperatura = ?, "
                + "frecuencia_cardiaca = ?, frecuencia_respiratoria = ?, peso_kg = ?, "
                + "talla_cm = ?, saturacion_oxigeno = ? WHERE id_signos = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, s.getPresionArterial());

            if (s.getTemperatura() != null) {
                ps.setBigDecimal(2, s.getTemperatura());
            } else {
                ps.setNull(2, Types.DECIMAL);
            }

            if (s.getFrecuenciaCardiaca() != null) {
                ps.setInt(3, s.getFrecuenciaCardiaca());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (s.getFrecuenciaRespiratoria() != null) {
                ps.setInt(4, s.getFrecuenciaRespiratoria());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (s.getPesoKg() != null) {
                ps.setBigDecimal(5, s.getPesoKg());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }

            if (s.getTallaCm() != null) {
                ps.setBigDecimal(6, s.getTallaCm());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }

            if (s.getSaturacionOxigeno() != null) {
                ps.setInt(7, s.getSaturacionOxigeno());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setInt(8, s.getIdSignos());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_SignosVitales.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina los signos vitales por su identificador.
     *
     * @param idSignos Identificador de los signos vitales
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idSignos) {
        String sql = "DELETE FROM signos_vitales WHERE id_signos = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idSignos);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_SignosVitales.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca los signos vitales por su identificador.
     *
     * @param idSignos Identificador de los signos vitales
     * @return Objeto SignosVitales encontrado, o null si no existe
     */
    @Override
    public SignosVitales buscarPorId(int idSignos) {
        String sql = "SELECT * FROM signos_vitales WHERE id_signos = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSignos);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearSignosVitales(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_SignosVitales.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca los signos vitales asociados a una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Objeto SignosVitales encontrado, o null si no existe
     */
    public SignosVitales buscarPorAtencion(int idAtencion) {
        String sql = "SELECT * FROM signos_vitales WHERE id_atencion = ?";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAtencion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearSignosVitales(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_SignosVitales.buscarPorAtencion: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los registros de signos vitales.
     *
     * @return Lista de objetos SignosVitales
     */
    @Override
    public List<SignosVitales> listarTodos() {
        List<SignosVitales> lista = new ArrayList<>();
        String sql = "SELECT * FROM signos_vitales";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearSignosVitales(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_SignosVitales.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Convierte un ResultSet en un objeto SignosVitales.
     *
     * @param rs ResultSet con los datos de los signos vitales
     * @return Objeto SignosVitales mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private SignosVitales mapearSignosVitales(ResultSet rs) throws SQLException {
        SignosVitales s = new SignosVitales(
                rs.getInt("id_atencion"),
                rs.getString("presion_arterial"),
                rs.getBigDecimal("temperatura"),
                (Integer) rs.getObject("frecuencia_cardiaca"),
                (Integer) rs.getObject("frecuencia_respiratoria"),
                rs.getBigDecimal("peso_kg"),
                rs.getBigDecimal("talla_cm"),
                (Integer) rs.getObject("saturacion_oxigeno")
        );
        s.setIdSignos(rs.getInt("id_signos"));
        return s;
    }
}

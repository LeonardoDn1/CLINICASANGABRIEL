/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Reporte;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

/**
 * Capa de acceso a datos para la generación de reportes.
 *
 * Proporciona métodos para consultar información consolidada de la clínica,
 * incluyendo estadísticas de pacientes, ingresos, especialidades y
 * medicamentos.
 *
 * @author Grupo04
 */
public class DAL_Reporte {

    /**
     * Genera un reporte estadístico consolidado de la clínica.
     *
     * @param usuarioGenero Nombre del usuario que genera el reporte
     * @return Objeto Reporte con la información consolidada
     */
    public Reporte generarReporteGeneral(String usuarioGenero) {
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());

        Reporte.Builder builder = new Reporte.Builder(
                "Reporte Estadístico Consolidado",
                fechaActual,
                usuarioGenero
        );

        int totalPacientes = obtenerTotalPacientes();
        builder.totalPacientes(totalPacientes);

        BigDecimal ingresosDiarios = obtenerIngresosDiarios();
        builder.ingresosDiarios(ingresosDiarios);

        obtenerEspecialidadMasDemandada(builder);
        obtenerMedicamentoMasUtilizado(builder);

        return builder.build();
    }

    /**
     * Obtiene el total de pacientes activos registrados.
     *
     * @return Número total de pacientes activos
     */
    private int obtenerTotalPacientes() {
        String sql = "SELECT COUNT(*) FROM pacientes WHERE estado = 'ACTIVO'";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Reporte.obtenerTotalPacientes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Obtiene el total de ingresos del día actual.
     *
     * @return Suma total de pagos registrados hoy
     */
    private BigDecimal obtenerIngresosDiarios() {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM pagos WHERE DATE(fecha_pago) = CURDATE()";
        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Reporte.obtenerIngresosDiarios: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Obtiene la especialidad médica con mayor número de citas.
     *
     * @param builder Builder del reporte para asignar el resultado
     */
    private void obtenerEspecialidadMasDemandada(Reporte.Builder builder) {
        String sql = "SELECT e.nombre, COUNT(c.id_cita) AS total "
                + "FROM citas c "
                + "JOIN medicos m ON c.id_medico = m.id_medico "
                + "JOIN medico_especialidad me ON m.id_medico = me.id_medico "
                + "JOIN especialidades e ON me.id_especialidad = e.id_especialidad "
                + "GROUP BY e.id_especialidad, e.nombre "
                + "ORDER BY total DESC LIMIT 1";

        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String especialidad = rs.getString("nombre");
                int cantidad = rs.getInt("total");
                builder.especialidadMasDemandada(especialidad, cantidad);
            } else {
                builder.especialidadMasDemandada("N/A", 0);
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Reporte.obtenerEspecialidadMasDemandada: " + e.getMessage());
            builder.especialidadMasDemandada("N/A", 0);
        }
    }

    /**
     * Obtiene el medicamento más utilizado en recetas médicas.
     *
     * @param builder Builder del reporte para asignar el resultado
     */
    private void obtenerMedicamentoMasUtilizado(Reporte.Builder builder) {
        String sql = "SELECT m.nombre, SUM(rd.cantidad) AS total "
                + "FROM receta_detalle rd "
                + "JOIN medicamentos m ON rd.id_medicamento = m.id_medicamento "
                + "GROUP BY m.id_medicamento, m.nombre "
                + "ORDER BY total DESC LIMIT 1";

        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String medicamento = rs.getString("nombre");
                int cantidad = rs.getInt("total");
                builder.medicamentoMasUtilizado(medicamento, cantidad);
            } else {
                builder.medicamentoMasUtilizado("N/A", 0);
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Reporte.obtenerMedicamentoMasUtilizado: " + e.getMessage());
            builder.medicamentoMasUtilizado("N/A", 0);
        }
    }

    /**
     * Genera un reporte filtrado por tipo y rango de fechas para la vista
     * previa.
     *
     * @param tipoReporte Tipo de reporte a generar
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @param usuario Nombre del usuario que genera el reporte
     * @return Objeto Reporte con los datos filtrados
     */
    public Reporte generarReportePorFiltro(String tipoReporte, Timestamp fechaInicio, Timestamp fechaFin, String usuario) {
        ArrayList<String[]> listaDatos = new ArrayList<>();
        String sql = "";

        switch (tipoReporte) {
            case "Pacientes por especialidad":
                sql = "SELECT e.nombre AS Especialidad, COUNT(DISTINCT c.id_paciente) AS Pacientes "
                        + "FROM especialidades e "
                        + "JOIN medico_especialidad me ON e.id_especialidad = me.id_especialidad "
                        + "JOIN medicos m ON me.id_medico = m.id_medico "
                        + "JOIN citas c ON m.id_medico = c.id_medico "
                        + "WHERE c.fecha BETWEEN ? AND ? "
                        + "GROUP BY e.id_especialidad, e.nombre";
                break;

            case "Ingresos diarios de caja":
                sql = "SELECT DATE_FORMAT(p.fecha_pago, '%Y-%m-%d') AS Fecha, COUNT(p.id_pago) AS Transacciones, SUM(p.monto) AS Total "
                        + "FROM pagos p "
                        + "WHERE p.fecha_pago BETWEEN ? AND ? "
                        + "GROUP BY DATE_FORMAT(p.fecha_pago, '%Y-%m-%d')";
                break;

            case "Productividad médica":
                sql = "SELECT CONCAT(u.nombres, ' ', u.apellidos) AS Medico, IFNULL(GROUP_CONCAT(DISTINCT e.nombre), 'General') AS Especialidad, COUNT(a.id_atencion) AS Atenciones "
                        + "FROM medicos m "
                        + "JOIN usuarios u ON m.id_usuario = u.id_usuario "
                        + "JOIN citas c ON m.id_medico = c.id_medico "
                        + "JOIN atenciones_medicas a ON c.id_cita = a.id_cita "
                        + "LEFT JOIN medico_especialidad me ON m.id_medico = me.id_medico "
                        + "LEFT JOIN especialidades e ON me.id_especialidad = e.id_especialidad "
                        + "WHERE a.fecha_atencion BETWEEN ? AND ? "
                        + "GROUP BY m.id_medico, u.nombres, u.apellidos";
                break;

            case "Medicamentos más utilizados":
                sql = "SELECT m.nombre AS Medicamento, SUM(rd.cantidad) AS Cantidad "
                        + "FROM receta_detalle rd "
                        + "JOIN medicamentos m ON rd.id_medicamento = m.id_medicamento "
                        + "JOIN recetas r ON rd.id_receta = r.id_receta "
                        + "WHERE r.fecha_emision BETWEEN ? AND ? "
                        + "GROUP BY m.id_medicamento, m.nombre "
                        + "ORDER BY SUM(rd.cantidad) DESC";
                break;
        }

        try (Connection con = Conexion.getInstancia().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, fechaInicio);
            ps.setTimestamp(2, fechaFin);

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    String[] fila = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        fila[i - 1] = rs.getString(i);
                    }
                    listaDatos.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Reporte.generarReportePorFiltro: " + e.getMessage());
        }

        return new Reporte.Builder(tipoReporte, new Timestamp(System.currentTimeMillis()), usuario)
                .datos(listaDatos)
                .build();
    }
}

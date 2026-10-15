package logica;

import entidades.*;
import datos.*;
import java.sql.Timestamp;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Capa de lógica de negocio para la generación de reportes.
 *
 * Gestiona la obtención de información consolidada para reportes gerenciales,
 * incluyendo estadísticas de pacientes, ingresos, especialidades y
 * medicamentos.
 *
 * @author Grupo04
 */
public class BLLReporte {

    /**
     * Genera un reporte estadístico consolidado de la clínica.
     *
     * @param usuarioGenero Nombre del usuario que genera el reporte
     * @return Objeto Reporte con la información consolidada
     */
    public Reporte generarReporteGeneral(String usuarioGenero) {
        if (usuarioGenero != null && !usuarioGenero.trim().isEmpty()) {
            DAL_Reporte dal = new DAL_Reporte();
            return dal.generarReporteGeneral(usuarioGenero);
        } else {
            showMessageDialog(null, "Usuario no válido", "Error", 0);
            return null;
        }
    }

    /**
     * Obtiene el total de pacientes activos registrados.
     *
     * @return Número total de pacientes activos
     */
    public int obtenerTotalPacientes() {
        Reporte reporte = generarReporteGeneral("Sistema");
        if (reporte != null) {
            return reporte.getTotalPacientes();
        }
        return 0;
    }

    /**
     * Obtiene el resumen de ingresos diarios.
     *
     * @return Cadena con el total de ingresos del día
     */
    public String obtenerResumenIngresos() {
        Reporte reporte = generarReporteGeneral("Sistema");
        if (reporte != null) {
            return "Ingresos diarios: S/ " + reporte.getIngresosDiarios();
        }
        return "No existe información";
    }

    /**
     * Obtiene la especialidad médica con mayor demanda.
     *
     * @return Nombre de la especialidad más demandada
     */
    public String obtenerEspecialidadDemandada() {
        Reporte reporte = generarReporteGeneral("Sistema");
        if (reporte != null) {
            return reporte.getEspecialidadMasDemandada();
        }
        return "N/A";
    }

    /**
     * Obtiene el medicamento más utilizado en la clínica.
     *
     * @return Nombre del medicamento más utilizado
     */
    public String obtenerMedicamentoUtilizado() {
        Reporte reporte = generarReporteGeneral("Sistema");
        if (reporte != null) {
            return reporte.getMedicamentoMasUtilizado();
        }
        return "N/A";
    }

    /**
     * Genera un reporte con formato específico para exportación.
     *
     * @param usuario Nombre del usuario que genera el reporte
     * @param tipoReporte Tipo de formato (PDF o EXCEL)
     * @return Objeto Reporte con el tipo de formato configurado
     */
    public Reporte generarReportePorTipo(String usuario, String tipoReporte) {
        if (usuario != null && !usuario.trim().isEmpty()
                && tipoReporte != null && (tipoReporte.equals("PDF") || tipoReporte.equals("EXCEL"))) {

            Reporte reporte = generarReporteGeneral(usuario);
            if (reporte != null) {
                reporte.setTipoReporte(tipoReporte);
                return reporte;
            }
        } else {
            showMessageDialog(null, "Datos no válidos", "Error", 0);
        }
        return null;
    }

    /**
     * Genera un reporte filtrado por tipo y rango de fechas.
     *
     * @param tipoReporte Tipo de reporte a generar
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @param usuario Nombre del usuario que genera el reporte
     * @return Objeto Reporte con los datos filtrados
     */
    public Reporte generarReportePorFiltro(String tipoReporte, Timestamp fechaInicio,
            Timestamp fechaFin, String usuario) {
        if (usuario != null && !usuario.trim().isEmpty()
                && tipoReporte != null && !tipoReporte.trim().isEmpty()
                && fechaInicio != null && fechaFin != null) {

            DAL_Reporte dal = new DAL_Reporte();
            return dal.generarReportePorFiltro(tipoReporte, fechaInicio, fechaFin, usuario);
        } else {
            showMessageDialog(null, "Parámetros de consulta no válidos", "Error", 0);
            return null;
        }
    }
}

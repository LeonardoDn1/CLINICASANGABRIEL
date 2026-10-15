package entidades;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Entidad que representa un reporte administrativo.
 *
 * Contiene información consolidada para la gestión gerencial, incluyendo
 * estadísticas de pacientes, ingresos y uso de medicamentos.
 *
 * @author Grupo04
 */
public class Reporte {

    private String titulo;
    private Timestamp fechaGeneracion;
    private String usuarioGenero;
    private String tipoReporte;
    private int totalPacientes;
    private BigDecimal ingresosDiarios;
    private String especialidadMasDemandada;
    private int pacientesPorEspecialidad;
    private String medicamentoMasUtilizado;
    private int cantidadMedicamento;
    private byte[] archivoPDF;
    private ArrayList<String[]> datos;

    /**
     * Constructor por defecto.
     */
    public Reporte() {
    }

    /**
     * Constructor privado utilizado por el Builder.
     *
     * @param builder Instancia del Builder con los atributos configurados
     */
    private Reporte(Builder builder) {
        this.titulo = builder.titulo;
        this.fechaGeneracion = builder.fechaGeneracion;
        this.usuarioGenero = builder.usuarioGenero;
        this.tipoReporte = builder.tipoReporte;
        this.totalPacientes = builder.totalPacientes;
        this.ingresosDiarios = builder.ingresosDiarios;
        this.especialidadMasDemandada = builder.especialidadMasDemandada;
        this.pacientesPorEspecialidad = builder.pacientesPorEspecialidad;
        this.medicamentoMasUtilizado = builder.medicamentoMasUtilizado;
        this.cantidadMedicamento = builder.cantidadMedicamento;
        this.archivoPDF = builder.archivoPDF;
        this.datos = builder.datos;
    }

    // MÉTODOS GETTER Y SETTER
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Timestamp getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Timestamp fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getUsuarioGenero() {
        return usuarioGenero;
    }

    public void setUsuarioGenero(String usuarioGenero) {
        this.usuarioGenero = usuarioGenero;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public int getTotalPacientes() {
        return totalPacientes;
    }

    public void setTotalPacientes(int totalPacientes) {
        this.totalPacientes = totalPacientes;
    }

    public BigDecimal getIngresosDiarios() {
        return ingresosDiarios;
    }

    public void setIngresosDiarios(BigDecimal ingresosDiarios) {
        this.ingresosDiarios = ingresosDiarios;
    }

    public String getEspecialidadMasDemandada() {
        return especialidadMasDemandada;
    }

    public void setEspecialidadMasDemandada(String especialidadMasDemandada) {
        this.especialidadMasDemandada = especialidadMasDemandada;
    }

    public int getPacientesPorEspecialidad() {
        return pacientesPorEspecialidad;
    }

    public void setPacientesPorEspecialidad(int pacientesPorEspecialidad) {
        this.pacientesPorEspecialidad = pacientesPorEspecialidad;
    }

    public String getMedicamentoMasUtilizado() {
        return medicamentoMasUtilizado;
    }

    public void setMedicamentoMasUtilizado(String medicamentoMasUtilizado) {
        this.medicamentoMasUtilizado = medicamentoMasUtilizado;
    }

    public int getCantidadMedicamento() {
        return cantidadMedicamento;
    }

    public void setCantidadMedicamento(int cantidadMedicamento) {
        this.cantidadMedicamento = cantidadMedicamento;
    }

    public byte[] getArchivoPDF() {
        return archivoPDF;
    }

    public void setArchivoPDF(byte[] archivoPDF) {
        this.archivoPDF = archivoPDF;
    }

    public ArrayList<String[]> getDatos() {
        return datos;
    }

    public void setDatos(ArrayList<String[]> datos) {
        this.datos = datos;
    }

    // PATRÓN BUILDER
    /**
     * Clase interna para construcción fluida de objetos Reporte.
     */
    public static class Builder {

        private String titulo;
        private Timestamp fechaGeneracion;
        private String usuarioGenero;
        private String tipoReporte = "PDF";
        private int totalPacientes;
        private BigDecimal ingresosDiarios;
        private String especialidadMasDemandada;
        private int pacientesPorEspecialidad;
        private String medicamentoMasUtilizado;
        private int cantidadMedicamento;
        private byte[] archivoPDF;
        private ArrayList<String[]> datos = new ArrayList<>();

        /**
         * Constructor del Builder con parámetros obligatorios.
         *
         * @param titulo Título del reporte
         * @param fechaGeneracion Fecha y hora de generación
         * @param usuarioGenero Usuario que generó el reporte
         */
        public Builder(String titulo, Timestamp fechaGeneracion, String usuarioGenero) {
            this.titulo = titulo;
            this.fechaGeneracion = fechaGeneracion;
            this.usuarioGenero = usuarioGenero;
        }

        public Builder tipoReporte(String tipoReporte) {
            this.tipoReporte = tipoReporte;
            return this;
        }

        public Builder totalPacientes(int totalPacientes) {
            this.totalPacientes = totalPacientes;
            return this;
        }

        public Builder ingresosDiarios(BigDecimal ingresosDiarios) {
            this.ingresosDiarios = ingresosDiarios;
            return this;
        }

        public Builder especialidadMasDemandada(String especialidad, int cantidad) {
            this.especialidadMasDemandada = especialidad;
            this.pacientesPorEspecialidad = cantidad;
            return this;
        }

        public Builder medicamentoMasUtilizado(String medicamento, int cantidad) {
            this.medicamentoMasUtilizado = medicamento;
            this.cantidadMedicamento = cantidad;
            return this;
        }

        public Builder archivoPDF(byte[] archivoPDF) {
            this.archivoPDF = archivoPDF;
            return this;
        }

        public Builder datos(ArrayList<String[]> datos) {
            this.datos = datos;
            return this;
        }

        /**
         * Construye la instancia final de Reporte.
         *
         * @return Instancia de Reporte con los atributos configurados
         */
        public Reporte build() {
            return new Reporte(this);
        }
    }
}

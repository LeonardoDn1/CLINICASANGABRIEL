/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.sql.Timestamp;

/**
 * Entidad que representa un registro de auditoría del sistema.
 *
 * Almacena el historial de operaciones críticas realizadas por los usuarios,
 * incluyendo la fecha, hora, módulo afectado y el detalle de la acción
 * ejecutada.
 *
 * @author Grupo04
 */
public class Auditoria {

    private long idAuditoria;
    private int idUsuario;
    private Timestamp fechaHora;
    private String modulo;
    private String operacion;
    private String detalle;

    /**
     * Constructor por defecto.
     */
    public Auditoria() {
    }

    /**
     * Constructor con los datos básicos de auditoría.
     *
     * @param idUsuario Identificador del usuario que realizó la operación
     * @param modulo Módulo del sistema donde se ejecutó la acción
     * @param operacion Tipo de operación realizada (CREACION, MODIFICACION,
     * ELIMINACION, LOGIN, etc.)
     * @param detalle Descripción detallada de la operación
     */
    public Auditoria(int idUsuario, String modulo, String operacion, String detalle) {
        this.idUsuario = idUsuario;
        this.modulo = modulo;
        this.operacion = operacion;
        this.detalle = detalle;
    }

    // MÉTODOS GETTER Y SETTER
    public long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    /**
     * Devuelve una representación textual del registro de auditoría.
     *
     * @return Cadena con los datos principales de la auditoría
     */
    @Override
    public String toString() {
        return "Auditoria{"
                + "idAuditoria=" + idAuditoria
                + ", idUsuario=" + idUsuario
                + ", fechaHora=" + fechaHora
                + ", modulo='" + modulo + '\''
                + ", operacion='" + operacion + '\''
                + '}';
    }
}

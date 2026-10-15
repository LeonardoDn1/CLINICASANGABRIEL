/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.math.BigDecimal;

/**
 * Entidad que representa los signos vitales de un paciente en una atención.
 *
 * Registra las mediciones clínicas fundamentales tomadas durante una consulta
 * médica, como presión arterial, temperatura, frecuencia cardíaca, peso y
 * talla.
 *
 * @author Grupo04
 */
public class SignosVitales {

    private int idSignos;
    private int idAtencion;
    private String presionArterial;
    private BigDecimal temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private BigDecimal pesoKg;
    private BigDecimal tallaCm;
    private Integer saturacionOxigeno;

    /**
     * Constructor por defecto.
     */
    public SignosVitales() {
    }

    /**
     * Constructor con todas las mediciones de signos vitales.
     *
     * @param idAtencion Identificador de la atención médica
     * @param presionArterial Presión arterial (ej. 120/80)
     * @param temperatura Temperatura corporal en grados Celsius
     * @param frecuenciaCardiaca Frecuencia cardíaca (latidos por minuto)
     * @param frecuenciaRespiratoria Frecuencia respiratoria (respiraciones por
     * minuto)
     * @param pesoKg Peso del paciente en kilogramos
     * @param tallaCm Talla del paciente en centímetros
     * @param saturacionOxigeno Saturación de oxígeno en sangre (%)
     */
    public SignosVitales(int idAtencion, String presionArterial, BigDecimal temperatura,
            Integer frecuenciaCardiaca, Integer frecuenciaRespiratoria,
            BigDecimal pesoKg, BigDecimal tallaCm, Integer saturacionOxigeno) {
        this.idAtencion = idAtencion;
        this.presionArterial = presionArterial;
        this.temperatura = temperatura;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.pesoKg = pesoKg;
        this.tallaCm = tallaCm;
        this.saturacionOxigeno = saturacionOxigeno;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdSignos() {
        return idSignos;
    }

    public void setIdSignos(int idSignos) {
        this.idSignos = idSignos;
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public String getPresionArterial() {
        return presionArterial;
    }

    public void setPresionArterial(String presionArterial) {
        this.presionArterial = presionArterial;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public Integer getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getTallaCm() {
        return tallaCm;
    }

    public void setTallaCm(BigDecimal tallaCm) {
        this.tallaCm = tallaCm;
    }

    public Integer getSaturacionOxigeno() {
        return saturacionOxigeno;
    }

    public void setSaturacionOxigeno(Integer saturacionOxigeno) {
        this.saturacionOxigeno = saturacionOxigeno;
    }

    /**
     * Devuelve una representación textual de los signos vitales.
     *
     * @return Cadena con las principales mediciones clínicas
     */
    @Override
    public String toString() {
        return "Signos Vitales - Presión: " + presionArterial
                + ", Temp: " + temperatura + "°C, Peso: " + pesoKg + "kg";
    }
}

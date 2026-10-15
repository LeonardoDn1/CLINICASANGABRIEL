/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.math.BigDecimal;

/**
 * Entidad que representa un medicamento en el inventario.
 *
 * Administra el catálogo de medicamentos disponibles en la clínica, incluyendo
 * su stock, precio y control de niveles mínimos para generar alertas de
 * reposición.
 *
 * @author Grupo04
 */
public class Medicamento {

    private int idMedicamento;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private int stockActual;
    private int stockMinimo;
    private BigDecimal precioUnitario;

    /**
     * Constructor por defecto.
     */
    public Medicamento() {
    }

    /**
     * Constructor con todos los parámetros del medicamento.
     *
     * @param idMedicamento Identificador único del medicamento
     * @param nombre Nombre comercial del medicamento
     * @param descripcion Descripción del medicamento
     * @param unidadMedida Unidad de medida (tabletas, ml, etc.)
     * @param stockActual Cantidad disponible en inventario
     * @param stockMinimo Nivel mínimo de stock para alerta
     * @param precioUnitario Precio por unidad
     */
    public Medicamento(int idMedicamento, String nombre, String descripcion,
            String unidadMedida, int stockActual, int stockMinimo,
            BigDecimal precioUnitario) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.precioUnitario = precioUnitario;
    }

    // MÉTODOS GETTER Y SETTER
    public int getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Verifica si el stock actual se encuentra en nivel crítico.
     *
     * @return true si el stock actual es menor o igual al stock mínimo
     */
    public boolean isStockBajo() {
        return stockActual <= stockMinimo;
    }

    /**
     * Devuelve una representación textual del medicamento.
     *
     * @return Cadena con el nombre y cantidad disponible
     */
    @Override
    public String toString() {
        return nombre + " (Stock: " + stockActual + " " + unidadMedida + ")";
    }
}

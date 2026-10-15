/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import entidades.*;
import datos.*;
import java.math.BigDecimal;
import java.util.*;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Capa de lógica de negocio para la entidad Pago.
 *
 * Gestiona el registro, consulta y actualización de pagos asociados a
 * atenciones médicas, incluyendo validaciones de métodos de pago y prevención
 * de pagos duplicados.
 *
 * @author Grupo04
 */
public class BLLPago {

    /**
     * Registra un nuevo pago asociado a una atención médica.
     *
     * @param idAtencion Identificador de la atención médica
     * @param idCajero Identificador del cajero que registra el pago
     * @param monto Monto total de la transacción
     * @param metodoPago Método de pago (EFECTIVO, TARJETA, TRANSFERENCIA,
     * BILLETERA_ELECTRONICA)
     * @param tipoComprobante Tipo de comprobante (BOLETA o FACTURA)
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos, 3 si ya existe un pago para la atención
     */
    public int insertarPago(int idAtencion, int idCajero, BigDecimal monto, String metodoPago, String tipoComprobante) {

        // Verificar que la atención no tenga ya un pago registrado
        Pago pagoExistente = buscarPorAtencion(idAtencion);
        if (pagoExistente != null) {
            showMessageDialog(null, "La atención ya cuenta con un pago registrado.", "Pago duplicado", 2);
            return 3;
        }

        if (idAtencion > 0 && idCajero > 0 && monto != null && monto.compareTo(BigDecimal.ZERO) > 0
                && metodoPago != null && tipoComprobante != null
                && (metodoPago.equals("EFECTIVO") || metodoPago.equals("TARJETA")
                || metodoPago.equals("TRANSFERENCIA") || metodoPago.equals("BILLETERA_ELECTRONICA"))
                && (tipoComprobante.equals("BOLETA") || tipoComprobante.equals("FACTURA"))) {

            Pago pago = new Pago(idAtencion, idCajero, monto, metodoPago);
            DAL_Pago dal = new DAL_Pago();

            if (dal.insertar(pago, tipoComprobante)) {
                showMessageDialog(null, "Registro insertado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al insertar registro", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Busca un pago por su identificador.
     *
     * @param idPago Identificador del pago
     * @return Objeto Pago encontrado, o null si no existe
     */
    public Pago buscarPago(int idPago) {
        if (idPago > 0) {
            DAL_Pago dal = new DAL_Pago();
            return dal.buscarPorId(idPago);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca el pago asociado a una atención médica específica.
     *
     * @param idAtencion Identificador de la atención médica
     * @return Objeto Pago encontrado, o null si no existe
     */
    public Pago buscarPorAtencion(int idAtencion) {
        if (idAtencion > 0) {
            DAL_Pago dal = new DAL_Pago();
            return dal.buscarPorAtencion(idAtencion);
        } else {
            showMessageDialog(null, "Código de atención no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todos los pagos registrados.
     *
     * @return Lista de objetos Pago
     */
    public ArrayList<Pago> listarPagos() {
        DAL_Pago dal = new DAL_Pago();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Lista los pagos realizados por un cajero específico.
     *
     * @param idCajero Identificador del cajero
     * @return Lista de pagos realizados por el cajero
     */
    public ArrayList<Pago> listarPorCajero(int idCajero) {
        DAL_Pago dal = new DAL_Pago();
        return new ArrayList<>(dal.listarPorCajero(idCajero));
    }

    /**
     * Actualiza la información de un pago existente.
     *
     * @param idPago Identificador del pago
     * @param idAtencion Identificador de la atención médica
     * @param idCajero Identificador del cajero
     * @param monto Nuevo monto del pago
     * @param metodoPago Nuevo método de pago
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarPago(int idPago, int idAtencion, int idCajero, BigDecimal monto, String metodoPago) {
        if (idPago > 0 && idAtencion > 0 && idCajero > 0 && monto != null && monto.compareTo(BigDecimal.ZERO) > 0
                && metodoPago != null
                && (metodoPago.equals("EFECTIVO") || metodoPago.equals("TARJETA")
                || metodoPago.equals("TRANSFERENCIA") || metodoPago.equals("BILLETERA_ELECTRONICA"))) {

            Pago pago = new Pago(idAtencion, idCajero, monto, metodoPago);
            pago.setIdPago(idPago);
            DAL_Pago dal = new DAL_Pago();

            if (dal.actualizar(pago)) {
                showMessageDialog(null, "Registro actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar registro", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 2;
        }
    }

    /**
     * Elimina un pago por su identificador.
     *
     * @param idPago Identificador del pago
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarPago(int idPago) {
        if (idPago > 0) {
            DAL_Pago dal = new DAL_Pago();
            if (dal.eliminarLogico(idPago)) {
                showMessageDialog(null, "Registro eliminado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo eliminar el registro", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return 2;
        }
    }
}

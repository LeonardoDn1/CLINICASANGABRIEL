/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import entidades.*;
import datos.*;
import java.util.*;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Capa de lógica de negocio para la entidad Comprobante.
 *
 * Gestiona la emisión, consulta y actualización de comprobantes de pago,
 * garantizando la unicidad de su numeración.
 *
 * @author Grupo04
 */
public class BLLComprobante {

    /**
     * Registra un nuevo comprobante asociado a un pago.
     *
     * @param idPago Identificador del pago asociado
     * @param numeroComprobante Número único del comprobante
     * @param tipoComprobante Tipo de comprobante (BOLETA o FACTURA)
     * @return 0 si fue exitoso, 1-3 si ocurrió un error
     */
    public int insertarComprobante(int idPago, String numeroComprobante, String tipoComprobante) {
        if (idPago > 0 && numeroComprobante != null && !numeroComprobante.trim().isEmpty() && tipoComprobante != null) {
            DAL_Comprobante dal = new DAL_Comprobante();

            if (dal.buscarPorNumero(numeroComprobante) == null) {
                Comprobante comprobante = new Comprobante(idPago, numeroComprobante, tipoComprobante);
                if (dal.insertar(comprobante)) {
                    showMessageDialog(null, "Registro insertado", "Resultado", 1);
                    return 0;
                } else {
                    showMessageDialog(null, "Error al insertar registro", "Error", 0);
                    return 1;
                }
            } else {
                showMessageDialog(null, "El número de comprobante ya existe", "Error", 0);
                return 2;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 3;
        }
    }

    /**
     * Busca un comprobante por su identificador.
     *
     * @param idComprobante Identificador del comprobante
     * @return Objeto Comprobante encontrado, o null si no existe
     */
    public Comprobante buscarComprobante(int idComprobante) {
        if (idComprobante > 0) {
            DAL_Comprobante dal = new DAL_Comprobante();
            return dal.buscarPorId(idComprobante);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Busca el comprobante asociado a un pago específico.
     *
     * @param idPago Identificador del pago
     * @return Objeto Comprobante encontrado, o null si no existe
     */
    public Comprobante buscarComprobantePorPago(int idPago) {
        if (idPago > 0) {
            DAL_Comprobante dal = new DAL_Comprobante();
            return dal.buscarPorPago(idPago);
        }
        return null;
    }

    /**
     * Busca un comprobante por su número.
     *
     * @param numeroComprobante Número del comprobante
     * @return Objeto Comprobante encontrado, o null si no existe
     */
    public Comprobante buscarComprobantePorNumero(String numeroComprobante) {
        if (numeroComprobante != null && !numeroComprobante.trim().isEmpty()) {
            DAL_Comprobante dal = new DAL_Comprobante();
            return dal.buscarPorNumero(numeroComprobante);
        }
        return null;
    }

    /**
     * Lista todos los comprobantes registrados.
     *
     * @return Lista de objetos Comprobante
     */
    public ArrayList<Comprobante> listarComprobante() {
        DAL_Comprobante dal = new DAL_Comprobante();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza la información de un comprobante existente.
     *
     * @param idComprobante Identificador del comprobante
     * @param idPago Identificador del pago asociado
     * @param numeroComprobante Nuevo número del comprobante
     * @param tipoComprobante Nuevo tipo de comprobante
     * @return 0 si fue exitoso, 1-3 si ocurrió un error
     */
    public int actualizarComprobante(int idComprobante, int idPago, String numeroComprobante, String tipoComprobante) {
        if (idComprobante > 0 && idPago > 0 && numeroComprobante != null && !numeroComprobante.trim().isEmpty() && tipoComprobante != null) {
            DAL_Comprobante dal = new DAL_Comprobante();
            Comprobante comprobanteExistente = dal.buscarPorNumero(numeroComprobante);

            if (comprobanteExistente != null && comprobanteExistente.getIdComprobante() != idComprobante) {
                showMessageDialog(null, "El número de comprobante ya pertenece a otro registro", "Error", 0);
                return 1;
            }

            Comprobante comprobante = new Comprobante(idPago, numeroComprobante, tipoComprobante);
            comprobante.setIdComprobante(idComprobante);

            if (dal.actualizar(comprobante)) {
                showMessageDialog(null, "Registro actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar registro", "Error", 0);
                return 2;
            }
        } else {
            showMessageDialog(null, "Datos no validos", "Error", 0);
            return 3;
        }
    }

    /**
     * Elimina un comprobante por su identificador.
     *
     * @param idComprobante Identificador del comprobante
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarComprobante(int idComprobante) {
        if (idComprobante > 0) {
            DAL_Comprobante dal = new DAL_Comprobante();
            if (dal.eliminarLogico(idComprobante)) {
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

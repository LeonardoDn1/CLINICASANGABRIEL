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
 * Capa de lógica de negocio para la entidad EntregaMedicamento.
 *
 * Gestiona el registro, consulta y actualización de entregas de medicamentos
 * dispensados a pacientes.
 *
 * @author Grupo04
 */
public class BLLEntregaMedicamento {

    /**
     * Registra una nueva entrega de medicamento.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @param idFarmaceutico Identificador del farmacéutico que realiza la
     * entrega
     * @param cantidadEntregada Cantidad de medicamento dispensada
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarEntregaMedicamento(int idRecetaDetalle, int idFarmaceutico, int cantidadEntregada) {
        if (idRecetaDetalle > 0 && idFarmaceutico > 0 && cantidadEntregada > 0) {
            EntregaMedicamento entrega = new EntregaMedicamento(idRecetaDetalle, idFarmaceutico, cantidadEntregada);
            DAL_EntregaMedicamento dal = new DAL_EntregaMedicamento();

            if (dal.insertar(entrega)) {
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
     * Busca una entrega por su identificador.
     *
     * @param idEntrega Identificador de la entrega
     * @return Objeto EntregaMedicamento encontrado, o null si no existe
     */
    public EntregaMedicamento buscarEntregaMedicamento(int idEntrega) {
        if (idEntrega > 0) {
            DAL_EntregaMedicamento dal = new DAL_EntregaMedicamento();
            return dal.buscarPorId(idEntrega);
        } else {
            showMessageDialog(null, "Código no valido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista las entregas realizadas para un detalle de receta específico.
     *
     * @param idRecetaDetalle Identificador del detalle de receta
     * @return Lista de entregas asociadas al detalle de receta
     */
    public ArrayList<EntregaMedicamento> listarPorRecetaDetalle(int idRecetaDetalle) {
        if (idRecetaDetalle > 0) {
            DAL_EntregaMedicamento dal = new DAL_EntregaMedicamento();
            return new ArrayList<>(dal.listarPorRecetaDetalle(idRecetaDetalle));
        }
        return new ArrayList<>();
    }

    /**
     * Lista las entregas realizadas por un farmacéutico específico.
     *
     * @param idFarmaceutico Identificador del farmacéutico
     * @return Lista de entregas realizadas por el farmacéutico
     */
    public ArrayList<EntregaMedicamento> listarPorFarmaceutico(int idFarmaceutico) {
        if (idFarmaceutico > 0) {
            DAL_EntregaMedicamento dal = new DAL_EntregaMedicamento();
            return new ArrayList<>(dal.listarPorFarmaceutico(idFarmaceutico));
        }
        return new ArrayList<>();
    }

    /**
     * Lista todas las entregas de medicamentos registradas.
     *
     * @return Lista de objetos EntregaMedicamento
     */
    public ArrayList<EntregaMedicamento> listarEntregaMedicamento() {
        DAL_EntregaMedicamento dal = new DAL_EntregaMedicamento();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza la información de una entrega existente.
     *
     * @param idEntrega Identificador de la entrega
     * @param idRecetaDetalle Nuevo identificador del detalle de receta
     * @param idFarmaceutico Nuevo identificador del farmacéutico
     * @param cantidadEntregada Nueva cantidad entregada
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarEntregaMedicamento(int idEntrega, int idRecetaDetalle, int idFarmaceutico, int cantidadEntregada) {
        if (idEntrega > 0 && idRecetaDetalle > 0 && idFarmaceutico > 0 && cantidadEntregada > 0) {
            EntregaMedicamento entrega = new EntregaMedicamento(idRecetaDetalle, idFarmaceutico, cantidadEntregada);
            entrega.setIdEntrega(idEntrega);
            DAL_EntregaMedicamento dal = new DAL_EntregaMedicamento();

            if (dal.actualizar(entrega)) {
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
     * Elimina una entrega por su identificador.
     *
     * @param idEntrega Identificador de la entrega
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int eliminarEntregaMedicamento(int idEntrega) {
        if (idEntrega > 0) {
            DAL_EntregaMedicamento dal = new DAL_EntregaMedicamento();
            if (dal.eliminarLogico(idEntrega)) {
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

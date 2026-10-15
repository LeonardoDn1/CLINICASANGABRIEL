/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;

import entidades.ExamenLaboratorio;
import entidades.ResultadoLaboratorio;
import logica.BLLExamenLaboratorio;
import logica.BLLResultadoLaboratorio;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import entidades.Usuario;

/**
 *
 * @author Grupo04
 */
public class ifrmExamenes extends javax.swing.JInternalFrame {

    private final BLLExamenLaboratorio bllExamen;
    private final BLLResultadoLaboratorio bllResultado;
    private final Usuario usuarioActual;
    private ExamenLaboratorio examenSeleccionado;

    public ifrmExamenes() {
        this(null);
    }

    public ifrmExamenes(Usuario usuario) {
        initComponents();
        this.usuarioActual = usuario;
        this.bllExamen = new BLLExamenLaboratorio();
        this.bllResultado = new BLLResultadoLaboratorio();
        inicializarCombos();
        configurarEventos();
        listarSolicitudesExamen();
        limpiarSeleccion();
    }

    private void inicializarCombos() {
        DefaultComboBoxModel<String> modelEstados = new DefaultComboBoxModel<>(new String[]{
            "Pendiente", "En proceso", "Finalizado", "Entregado"
        });
        cmbEstadoExamen.setModel(modelEstados);
    }

    private void configurarEventos() {
        tblSolicitudesPendientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblSolicitudesPendientes.getSelectedRow() != -1) {
                cargarExamenSeleccionado();
            }
        });
    }

    private void listarSolicitudesExamen() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID Examen", "ID Atención", "Tipo de Examen", "Fecha de Solicitud", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String estado = obtenerEstadoBD();
        
        
        ArrayList<ExamenLaboratorio> lista = bllExamen.listarPorEstado(estado);

        for (ExamenLaboratorio ex : lista) {
            model.addRow(new Object[]{
                ex.getIdExamen(),
                ex.getIdAtencion(),
                ex.getTipoExamen(),
                ex.getFechaSolicitud(),
                ex.getEstado()
            });
        }

        tblSolicitudesPendientes.setModel(model);
    }
    
    
    private String obtenerTextoEstado(String estadoBD) {

        switch (estadoBD) {

            case "PENDIENTE":
                return "Pendiente";

            case "EN_PROCESO":
                return "En proceso";

            case "FINALIZADO":
                return "Finalizado";

            case "ENTREGADO":
                return "Entregado";

            default:
                return "Pendiente";
        }
    }
    
    
    
    
    
    private void cargarExamenSeleccionado() {
        int fila = tblSolicitudesPendientes.getSelectedRow();

        if (fila != -1) {
            int idExamen = (int) tblSolicitudesPendientes.getValueAt(fila, 0);
            this.examenSeleccionado = bllExamen.buscarExamen(idExamen);

            if (examenSeleccionado != null) {
                ResultadoLaboratorio resultado = bllResultado.buscarPorExamen(idExamen);

                if (resultado != null) {
                    txaResultado.setText(resultado.getResultadoTexto());
                } else {
                    txaResultado.setText("");
                }

                configurarControlesPorEstado(examenSeleccionado.getEstado());
            }
        }
    }

    private void configurarControlesPorEstado(String estado) {
        if ("PENDIENTE".equals(estado)) {
            txaResultado.setEditable(true);
            btnRegistrarResultado.setText("Registrar Resultado");
            btnRegistrarResultado.setEnabled(true);
            btnFinalizarExamen.setText("Finalizar Examen");
            btnFinalizarExamen.setEnabled(false);
        } else if ("EN_PROCESO".equals(estado)) {
            txaResultado.setEditable(true);
            btnRegistrarResultado.setText("Actualizar Resultado");
            btnRegistrarResultado.setEnabled(true);
            btnFinalizarExamen.setText("Finalizar Examen");
            btnFinalizarExamen.setEnabled(true);
        } else if ("FINALIZADO".equals(estado)) {
            txaResultado.setEditable(false);
            btnRegistrarResultado.setText("Actualizar Resultado");
            btnRegistrarResultado.setEnabled(false);
            btnFinalizarExamen.setText("Marcar como Entregado");
            btnFinalizarExamen.setEnabled(true);
        } else {
            txaResultado.setEditable(false);
            btnRegistrarResultado.setText("Actualizar Resultado");
            btnRegistrarResultado.setEnabled(false);
            btnFinalizarExamen.setText("Marcar como Entregado");
            btnFinalizarExamen.setEnabled(false);
        }
    }

    private void configurarControlesSinSeleccion() {
        txaResultado.setEditable(false);
        btnRegistrarResultado.setEnabled(false);
        btnFinalizarExamen.setEnabled(false);

        String estado = obtenerEstadoBD();

        if ("PENDIENTE".equals(estado)) {
            btnRegistrarResultado.setText("Registrar Resultado");
            btnFinalizarExamen.setText("Finalizar Examen");
        } else if ("EN_PROCESO".equals(estado)) {
            btnRegistrarResultado.setText("Actualizar Resultado");
            btnFinalizarExamen.setText("Finalizar Examen");
        } else if ("FINALIZADO".equals(estado)) {
            btnRegistrarResultado.setText("Actualizar Resultado");
            btnFinalizarExamen.setText("Marcar como Entregado");
        } else {
            btnRegistrarResultado.setText("Actualizar Resultado");
            btnFinalizarExamen.setText("Marcar como Entregado");
        }
    }

    private String obtenerEstadoBD() {
        String estado = String.valueOf(cmbEstadoExamen.getSelectedItem());

        if ("Pendiente".equals(estado)) {
            return "PENDIENTE";
        }
        if ("En proceso".equals(estado)) {
            return "EN_PROCESO";
        }
        if ("Finalizado".equals(estado)) {
            return "FINALIZADO";
        }
        return "ENTREGADO";
    }

    private void limpiarSeleccion() {
        txaResultado.setText("");
        tblSolicitudesPendientes.clearSelection();
        this.examenSeleccionado = null;
        configurarControlesSinSeleccion();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panExamenes = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        scpSolicitudesPendientes = new javax.swing.JScrollPane();
        tblSolicitudesPendientes = new javax.swing.JTable();
        lblResultado = new javax.swing.JLabel();
        scpResultado = new javax.swing.JScrollPane();
        txaResultado = new javax.swing.JTextArea();
        btnRegistrarResultado = new javax.swing.JButton();
        btnFinalizarExamen = new javax.swing.JButton();
        lblEstadoExamen = new javax.swing.JLabel();
        cmbEstadoExamen = new javax.swing.JComboBox<>();

        setClosable(true);
        setResizable(true);

        lblTitulo.setFont(new java.awt.Font("Baskerville Old Face", 1, 24)); // NOI18N
        lblTitulo.setText("EXÁMENES - CLÍNICA SAN GABRIEL");

        tblSolicitudesPendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Examen", "ID Atención", "Tipo de Examen", "Fecha de Solicitud", "Estado"
            }
        ));
        tblSolicitudesPendientes.setFillsViewportHeight(true);
        scpSolicitudesPendientes.setViewportView(tblSolicitudesPendientes);

        lblResultado.setText("Detalle / Resultado");

        txaResultado.setColumns(20);
        txaResultado.setLineWrap(true);
        txaResultado.setRows(5);
        txaResultado.setWrapStyleWord(true);
        scpResultado.setViewportView(txaResultado);

        btnRegistrarResultado.setText("Registrar Resultado");
        btnRegistrarResultado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarResultadoActionPerformed(evt);
            }
        });

        btnFinalizarExamen.setText("Finalizar Examen");
        btnFinalizarExamen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarExamenActionPerformed(evt);
            }
        });

        lblEstadoExamen.setText("Filtrar por estado :");

        cmbEstadoExamen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente", "En proceso", "Finalizado", "Entregado" }));
        cmbEstadoExamen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEstadoExamenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panExamenesLayout = new javax.swing.GroupLayout(panExamenes);
        panExamenes.setLayout(panExamenesLayout);
        panExamenesLayout.setHorizontalGroup(
            panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panExamenesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panExamenesLayout.createSequentialGroup()
                        .addComponent(lblEstadoExamen)
                        .addGap(18, 18, 18)
                        .addComponent(cmbEstadoExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panExamenesLayout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(lblTitulo))
                    .addGroup(panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panExamenesLayout.createSequentialGroup()
                            .addComponent(btnRegistrarResultado)
                            .addGap(60, 60, 60)
                            .addComponent(btnFinalizarExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(152, 152, 152))
                        .addGroup(panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblResultado)
                            .addComponent(scpSolicitudesPendientes, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scpResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        panExamenesLayout.setVerticalGroup(
            panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panExamenesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstadoExamen)
                    .addComponent(cmbEstadoExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scpSolicitudesPendientes, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblResultado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scpResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrarResultado)
                    .addComponent(btnFinalizarExamen))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panExamenes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panExamenes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Evento: Cambio de estado en el combo - Filtra los exámenes.
     */
    private void cmbEstadoExamenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoExamenActionPerformed
        if (bllExamen != null) {
            limpiarSeleccion();
            listarSolicitudesExamen();
        }
    }//GEN-LAST:event_cmbEstadoExamenActionPerformed

    /**
     * Evento: Registrar o actualizar el resultado del examen seleccionado.
     */
    private void btnRegistrarResultadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarResultadoActionPerformed
        if (examenSeleccionado == null) {
        JOptionPane.showMessageDialog(this,
                "Seleccione una solicitud de examen de la tabla.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Obtener datos reales desde BD
    examenSeleccionado = bllExamen.buscarExamen(examenSeleccionado.getIdExamen());

    if (examenSeleccionado == null) {
        JOptionPane.showMessageDialog(this,
                "No se encontró el examen seleccionado.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }


    String estadoActual = examenSeleccionado.getEstado();

    System.out.println("ID Examen: " + examenSeleccionado.getIdExamen());
    System.out.println("Estado actual: " + estadoActual);


    // No permitir modificar entregados
    if ("ENTREGADO".equals(estadoActual)) {

        JOptionPane.showMessageDialog(this,
                "El examen ya fue entregado y no puede modificarse.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);

        return;
    }


    if (usuarioActual == null) {

        JOptionPane.showMessageDialog(this,
                "No se pudo identificar al laboratorista.",
                "Error",
                JOptionPane.ERROR_MESSAGE);

        return;
    }


    String resultadoTexto = txaResultado.getText().trim();


    if (resultadoTexto.isEmpty()) {

        JOptionPane.showMessageDialog(this,
                "Ingrese el detalle o resultado del examen.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);

        return;
    }


    try {

        int idLaboratorista = usuarioActual.getIdUsuario();


        ResultadoLaboratorio resultadoExistente =
                bllResultado.buscarPorExamen(
                        examenSeleccionado.getIdExamen()
                );


        int respuesta;


        // Si ya existe resultado, actualiza
        if (resultadoExistente != null) {

            respuesta = bllResultado.actualizarResultado(
                    resultadoExistente.getIdResultado(),
                    idLaboratorista,
                    resultadoTexto,
                    ""
            );


        } else {

            // Nuevo resultado
            respuesta = bllResultado.insertarResultado(
                    examenSeleccionado.getIdExamen(),
                    idLaboratorista,
                    resultadoTexto,
                    ""
            );

        }



        if (respuesta != 0) {

            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el resultado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }



        JOptionPane.showMessageDialog(this,
                "Resultado registrado correctamente.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);



        // Actualizar información en pantalla
        examenSeleccionado =
                bllExamen.buscarExamen(
                        examenSeleccionado.getIdExamen()
                );


        listarSolicitudesExamen();


        limpiarSeleccion();



    } catch (Exception e) {

        JOptionPane.showMessageDialog(this,
                "Error al registrar resultado: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);

        e.printStackTrace();
    }
    }//GEN-LAST:event_btnRegistrarResultadoActionPerformed

    /**
     * Evento: Finalizar examen o marcar como entregado.
     */
    private void btnFinalizarExamenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarExamenActionPerformed
        // TODO add your handling code here:
        if (examenSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un examen de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String estado = examenSeleccionado.getEstado();

        if ("EN_PROCESO".equals(estado)) {
            ResultadoLaboratorio resultado = bllResultado.buscarPorExamen(examenSeleccionado.getIdExamen());

            if (resultado == null) {
                JOptionPane.showMessageDialog(this,
                        "Primero debe registrar el resultado del examen.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea finalizar el Examen N.° " + examenSeleccionado.getIdExamen() + "?",
                    "Confirmar finalización",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar == JOptionPane.YES_OPTION) {
                int respuesta = bllExamen.cambiarEstadoExamen(
                        examenSeleccionado.getIdExamen(),
                        "FINALIZADO");

                if (respuesta == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Examen finalizado correctamente.",
                            "Resultado",
                            JOptionPane.INFORMATION_MESSAGE);

                    limpiarSeleccion();
                    cmbEstadoExamen.setSelectedItem("Finalizado");
                }
            }
        } else if ("FINALIZADO".equals(estado)) {
            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Confirma que el resultado del Examen N.° "
                    + examenSeleccionado.getIdExamen() + " fue entregado?",
                    "Confirmar entrega",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar == JOptionPane.YES_OPTION) {
                int respuesta = bllExamen.cambiarEstadoExamen(
                        examenSeleccionado.getIdExamen(),
                        "ENTREGADO");

                if (respuesta == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Resultado marcado como entregado.",
                            "Resultado",
                            JOptionPane.INFORMATION_MESSAGE);

                    limpiarSeleccion();
                    cmbEstadoExamen.setSelectedItem("Entregado");
                }
            }
        }
    }//GEN-LAST:event_btnFinalizarExamenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFinalizarExamen;
    private javax.swing.JButton btnRegistrarResultado;
    private javax.swing.JComboBox<String> cmbEstadoExamen;
    private javax.swing.JLabel lblEstadoExamen;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panExamenes;
    private javax.swing.JScrollPane scpResultado;
    private javax.swing.JScrollPane scpSolicitudesPendientes;
    private javax.swing.JTable tblSolicitudesPendientes;
    private javax.swing.JTextArea txaResultado;
    // End of variables declaration//GEN-END:variables
}

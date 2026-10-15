/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;

import entidades.AtencionMedica;
import entidades.Cita;
import entidades.Diagnostico;
import entidades.ExamenLaboratorio;
import entidades.Medicamento;
import entidades.Paciente;
import entidades.Receta;
import entidades.RecetaDetalle;
import entidades.SignosVitales;
import logica.BLLAtencionMedica;
import logica.BLLCita;
import logica.BLLDiagnostico;
import logica.BLLExamenLaboratorio;
import logica.BLLMedicamento;
import logica.BLLPaciente;
import logica.BLLReceta;
import logica.BLLRecetaDetalle;
import logica.BLLSignosVitales;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 *
 * @author Grupo04
 */
public class IfrmHistorialClinico extends javax.swing.JInternalFrame {

    private final BLLPaciente bllPaciente;
    private final BLLAtencionMedica bllAtencion;
    private final BLLCita bllCita;
    private final BLLSignosVitales bllSignos;
    private final BLLDiagnostico bllDiagnostico;
    private final BLLExamenLaboratorio bllExamen;
    private final BLLReceta bllReceta;
    private final BLLRecetaDetalle bllRecetaDetalle;
    private final BLLMedicamento bllMedicamento;

    private Paciente pacienteActual;
    private ArrayList<AtencionMedica> listaAtencionesPaciente;

    public IfrmHistorialClinico() {
        initComponents();

        this.bllPaciente = new BLLPaciente();
        this.bllAtencion = new BLLAtencionMedica();
        this.bllCita = new BLLCita();
        this.bllSignos = new BLLSignosVitales();
        this.bllDiagnostico = new BLLDiagnostico();
        this.bllExamen = new BLLExamenLaboratorio();
        this.bllReceta = new BLLReceta();
        this.bllRecetaDetalle = new BLLRecetaDetalle();
        this.bllMedicamento = new BLLMedicamento();

        bloquearCamposPaciente();
    }

    /**
     * Bloquea los campos para que sean de solo lectura.
     */
    private void bloquearCamposPaciente() {
        txtNombrePaciente.setEditable(false);
        txtDniPaciente.setEditable(false);
        txtNumeroHistoria.setEditable(false);
        txtEdadSexo.setEditable(false);
        txaMotivoConsulta.setEditable(false);
        txaAntecedentes.setEditable(false);
        txaTratamiento.setEditable(false);
        txaObservaciones.setEditable(false);
        txtPresion.setEditable(false);
        txtFC.setEditable(false);
        txtFR.setEditable(false);
        txtTemp.setEditable(false);
        txtPeso.setEditable(false);
        txtTalla.setEditable(false);
        txtSaturacion.setEditable(false);
    }

    /**
     * Carga automáticamente el historial usando el ID del paciente.
     */
    public void cargarHistorialAutomatico(int idPaciente) {
        this.pacienteActual = bllPaciente.buscarPaciente(idPaciente);

        if (this.pacienteActual != null) {
            txtNombrePaciente.setText(pacienteActual.getNombres() + " " + pacienteActual.getApellidos());
            txtDniPaciente.setText(pacienteActual.getDni());
            txtNumeroHistoria.setText(pacienteActual.getNumeroHistoriaClinica());
            txtEdadSexo.setText(pacienteActual.getSexo().equalsIgnoreCase("M") ? "Masculino" : "Femenino");

            cargarTablaAtencionesAnteriores(idPaciente);
            txtBuscarPaciente.setText(pacienteActual.getDni());
        } else {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del paciente para el historial.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga la tabla de atenciones anteriores del paciente.
     */
    private void cargarTablaAtencionesAnteriores(int idPaciente) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID Atención", "Fecha y Hora", "Médico ID", "Motivo Consulta", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.listaAtencionesPaciente = new ArrayList<>();
        ArrayList<Cita> citasPaciente = bllCita.listarCitasPorPaciente(idPaciente);

        if (citasPaciente != null) {
            for (Cita c : citasPaciente) {
                AtencionMedica at = bllAtencion.buscarPorCita(c.getIdCita());
                if (at != null) {
                    listaAtencionesPaciente.add(at);
                    model.addRow(new Object[]{
                        at.getIdAtencion(),
                        at.getFechaAtencion() != null ? at.getFechaAtencion() : c.getFecha(),
                        c.getIdMedico(),
                        at.getMotivoConsulta(),
                        at.isCerrada() ? "CERRADA" : "ABIERTA"
                    });
                }
            }
        }

        tblAtencionesAnteriores.setModel(model);

        if (tblAtencionesAnteriores.getRowCount() > 0) {
            tblAtencionesAnteriores.setRowSelectionInterval(0, 0);
        } else {
            JOptionPane.showMessageDialog(this, "El paciente no tiene atenciones médicas finalizadas.", "Historial", JOptionPane.INFORMATION_MESSAGE);
            limpiarDetalles();
        }
    }

    /**
     * Carga la tabla de diagnósticos.
     */
    private void cargarTablaDiagnosticos(int idAtencion) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Código CIE-10", "Descripción del Diagnóstico"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ArrayList<Diagnostico> lista = bllDiagnostico.listarDiagnosticosPorAtencion(idAtencion);
        if (lista != null) {
            for (Diagnostico d : lista) {
                model.addRow(new Object[]{d.getCodigoCie10(), d.getDescripcion()});
            }
        }
        tblDiagnosticos.setModel(model);
    }

    /**
     * Carga la tabla de exámenes de laboratorio.
     */
    private void cargarTablaExamenes(int idAtencion) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Código", "Examen Solicitado", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ArrayList<ExamenLaboratorio> lista = bllExamen.listarPorAtencion(idAtencion);
        if (lista != null) {
            for (ExamenLaboratorio ex : lista) {
                model.addRow(new Object[]{ex.getIdExamen(), ex.getTipoExamen(), ex.getEstado()});
            }
        }
        tblExamenesLaboratorio.setModel(model);
    }

    /**
     * Carga la tabla de receta médica.
     */
    private void cargarTablaReceta(int idAtencion) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Código", "Medicamento", "Cantidad", "Indicaciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Receta r = bllReceta.buscarPorAtencion(idAtencion);
        if (r != null) {
            ArrayList<RecetaDetalle> detalles = bllRecetaDetalle.listarPorReceta(r.getIdReceta());
            if (detalles != null) {
                for (RecetaDetalle rd : detalles) {
                    Medicamento med = bllMedicamento.buscarMedicamento(rd.getIdMedicamento());
                    String nombreMed = (med != null) ? med.getNombre() : "ID " + rd.getIdMedicamento();
                    model.addRow(new Object[]{rd.getIdRecetaDetalle(), nombreMed, rd.getCantidad(), rd.getIndicaciones()});
                }
            }
        }
        tblRecetaMedica.setModel(model);
    }

    /**
     * Limpia los campos de signos vitales.
     */
    private void limpiarCamposSignosVitales() {
        txtPresion.setText("");
        txtFC.setText("");
        txtFR.setText("");
        txtTemp.setText("");
        txtPeso.setText("");
        txtTalla.setText("");
        txtSaturacion.setText("");
    }

    /**
     * Limpia los detalles de la atención.
     */
    private void limpiarDetalles() {
        txaMotivoConsulta.setText("");
        txaAntecedentes.setText("");
        txaTratamiento.setText("");
        txaObservaciones.setText("");
        limpiarCamposSignosVitales();

        ((DefaultTableModel) tblDiagnosticos.getModel()).setRowCount(0);
        ((DefaultTableModel) tblExamenesLaboratorio.getModel()).setRowCount(0);
        ((DefaultTableModel) tblRecetaMedica.getModel()).setRowCount(0);
    }

    /**
     * Limpia todos los controles.
     */
    private void limpiarControles() {
        txtNombrePaciente.setText("");
        txtDniPaciente.setText("");
        txtNumeroHistoria.setText("");
        txtEdadSexo.setText("");
        ((DefaultTableModel) tblAtencionesAnteriores.getModel()).setRowCount(0);
        limpiarDetalles();
        this.pacienteActual = null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panPrincipal = new javax.swing.JPanel();
        panBusqueda = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        btnBuscarHistorial = new javax.swing.JButton();
        panDatosPaciente = new javax.swing.JPanel();
        lblPaciente = new javax.swing.JLabel();
        lblDni = new javax.swing.JLabel();
        txtNombrePaciente = new javax.swing.JTextField();
        txtDniPaciente = new javax.swing.JTextField();
        lblNumeroHistoria = new javax.swing.JLabel();
        lblEdadSexo = new javax.swing.JLabel();
        txtEdadSexo = new javax.swing.JTextField();
        txtNumeroHistoria = new javax.swing.JTextField();
        txtBuscarPaciente = new javax.swing.JTextField();
        panAtenciones = new javax.swing.JPanel();
        scpAtenciones = new javax.swing.JScrollPane();
        tblAtencionesAnteriores = new javax.swing.JTable();
        tpnDetalleAtencion = new javax.swing.JTabbedPane();
        panSignosVitales = new javax.swing.JPanel();
        lblPresion = new javax.swing.JLabel();
        lblFC = new javax.swing.JLabel();
        lblFR = new javax.swing.JLabel();
        lblTemperatura = new javax.swing.JLabel();
        txtPresion = new javax.swing.JTextField();
        txtFC = new javax.swing.JTextField();
        txtFR = new javax.swing.JTextField();
        txtTemp = new javax.swing.JTextField();
        lblPeso = new javax.swing.JLabel();
        lblTalla = new javax.swing.JLabel();
        lblSaturacion = new javax.swing.JLabel();
        txtPeso = new javax.swing.JTextField();
        txtTalla = new javax.swing.JTextField();
        txtSaturacion = new javax.swing.JTextField();
        lblMotivoConsulta = new javax.swing.JLabel();
        scpMotivo = new javax.swing.JScrollPane();
        txaMotivoConsulta = new javax.swing.JTextArea();
        lblAntecedentes = new javax.swing.JLabel();
        scpAntecedentes = new javax.swing.JScrollPane();
        txaAntecedentes = new javax.swing.JTextArea();
        panTratamiento = new javax.swing.JPanel();
        scpDiagnosticos = new javax.swing.JScrollPane();
        tblDiagnosticos = new javax.swing.JTable();
        lblTratamiento = new javax.swing.JLabel();
        lblObservaciones = new javax.swing.JLabel();
        scpTratamiento = new javax.swing.JScrollPane();
        txaTratamiento = new javax.swing.JTextArea();
        scpObservaciones = new javax.swing.JScrollPane();
        txaObservaciones = new javax.swing.JTextArea();
        panReceta = new javax.swing.JPanel();
        scpReceta = new javax.swing.JScrollPane();
        tblExamenesLaboratorio = new javax.swing.JTable();
        panExamenes = new javax.swing.JPanel();
        scpExamenes = new javax.swing.JScrollPane();
        tblRecetaMedica = new javax.swing.JTable();

        setClosable(true);
        setResizable(true);

        panBusqueda.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "BÚSQUEDA Y DATOS DEL PACIENTE", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblBuscar.setText("Buscar Paciente (DNI / N° Historia):");

        btnBuscarHistorial.setText("🔍 Buscar Historial");
        btnBuscarHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarHistorialActionPerformed(evt);
            }
        });

        panDatosPaciente.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información del Paciente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblPaciente.setText("Paciente:");

        lblDni.setText("DNI:");

        lblNumeroHistoria.setText("N° Historia:");

        lblEdadSexo.setText("Edad / Sexo:");

        javax.swing.GroupLayout panDatosPacienteLayout = new javax.swing.GroupLayout(panDatosPaciente);
        panDatosPaciente.setLayout(panDatosPacienteLayout);
        panDatosPacienteLayout.setHorizontalGroup(
            panDatosPacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDatosPacienteLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panDatosPacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPaciente)
                    .addComponent(lblDni))
                .addGap(10, 10, 10)
                .addGroup(panDatosPacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNombrePaciente, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                    .addComponent(txtDniPaciente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panDatosPacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEdadSexo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNumeroHistoria, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(panDatosPacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEdadSexo, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                    .addComponent(txtNumeroHistoria))
                .addGap(15, 15, 15))
        );
        panDatosPacienteLayout.setVerticalGroup(
            panDatosPacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDatosPacienteLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panDatosPacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPaciente)
                    .addComponent(txtNombrePaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumeroHistoria)
                    .addComponent(txtNumeroHistoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(panDatosPacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDni)
                    .addComponent(txtDniPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEdadSexo)
                    .addComponent(txtEdadSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        txtBuscarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarPacienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panBusquedaLayout = new javax.swing.GroupLayout(panBusqueda);
        panBusqueda.setLayout(panBusquedaLayout);
        panBusquedaLayout.setHorizontalGroup(
            panBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBusquedaLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panDatosPaciente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panBusquedaLayout.createSequentialGroup()
                        .addComponent(lblBuscar)
                        .addGap(48, 48, 48)
                        .addComponent(txtBuscarPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(btnBuscarHistorial)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
        );
        panBusquedaLayout.setVerticalGroup(
            panBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBusquedaLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(btnBuscarHistorial)
                    .addComponent(txtBuscarPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(panDatosPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        panAtenciones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ATENCIONES MÉDICAS ANTERIORES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        tblAtencionesAnteriores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha y Hora", "Médico", "Especialidad", "Diagnóstico Principal"
            }
        ));
        tblAtencionesAnteriores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAtencionesAnterioresMouseClicked(evt);
            }
        });
        scpAtenciones.setViewportView(tblAtencionesAnteriores);

        javax.swing.GroupLayout panAtencionesLayout = new javax.swing.GroupLayout(panAtenciones);
        panAtenciones.setLayout(panAtencionesLayout);
        panAtencionesLayout.setHorizontalGroup(
            panAtencionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAtencionesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(scpAtenciones)
                .addGap(15, 15, 15))
        );
        panAtencionesLayout.setVerticalGroup(
            panAtencionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAtencionesLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(scpAtenciones, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        tpnDetalleAtencion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DETALLE COMPLETO DE LA ATENCIÓN SELECCIONADA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblPresion.setText("Presión Arterial (PA):");

        lblFC.setText("Frecuencia Cardíaca (FC):");

        lblFR.setText("Frecuencia Respiratoria (FR):");

        lblTemperatura.setText("Temperatura:");

        lblPeso.setText("Peso:");

        lblTalla.setText("Talla:");

        lblSaturacion.setText("Saturación O2:");

        lblMotivoConsulta.setText("Motivo de Consulta:");

        txaMotivoConsulta.setColumns(20);
        txaMotivoConsulta.setRows(5);
        scpMotivo.setViewportView(txaMotivoConsulta);

        lblAntecedentes.setText("Antecedentes:");

        txaAntecedentes.setColumns(20);
        txaAntecedentes.setRows(5);
        scpAntecedentes.setViewportView(txaAntecedentes);

        javax.swing.GroupLayout panSignosVitalesLayout = new javax.swing.GroupLayout(panSignosVitales);
        panSignosVitales.setLayout(panSignosVitalesLayout);
        panSignosVitalesLayout.setHorizontalGroup(
            panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSignosVitalesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblFR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTemperatura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblPresion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblMotivoConsulta)
                    .addComponent(lblAntecedentes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scpAntecedentes)
                    .addGroup(panSignosVitalesLayout.createSequentialGroup()
                        .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPresion)
                            .addComponent(txtFC)
                            .addComponent(txtFR)
                            .addComponent(txtTemp, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                        .addGap(62, 62, 62)
                        .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTalla, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSaturacion))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTalla, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSaturacion, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(scpMotivo))
                .addContainerGap(164, Short.MAX_VALUE))
        );
        panSignosVitalesLayout.setVerticalGroup(
            panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSignosVitalesLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPresion)
                    .addComponent(txtPresion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPeso)
                    .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFC)
                    .addComponent(txtFC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTalla)
                    .addComponent(txtTalla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFR)
                    .addComponent(txtFR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSaturacion)
                    .addComponent(txtSaturacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTemperatura)
                    .addComponent(txtTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMotivoConsulta)
                    .addComponent(scpMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panSignosVitalesLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(lblAntecedentes))
                    .addGroup(panSignosVitalesLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scpAntecedentes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(62, Short.MAX_VALUE))
        );

        tpnDetalleAtencion.addTab("Signos Vitales", panSignosVitales);

        tblDiagnosticos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Código", "Descripción del Diagnóstico", "Tipo"
            }
        ));
        scpDiagnosticos.setViewportView(tblDiagnosticos);

        lblTratamiento.setText("Tratamiento:");

        lblObservaciones.setText("Observaciones:");

        txaTratamiento.setColumns(20);
        txaTratamiento.setRows(5);
        scpTratamiento.setViewportView(txaTratamiento);

        txaObservaciones.setColumns(20);
        txaObservaciones.setRows(5);
        scpObservaciones.setViewportView(txaObservaciones);

        javax.swing.GroupLayout panTratamientoLayout = new javax.swing.GroupLayout(panTratamiento);
        panTratamiento.setLayout(panTratamientoLayout);
        panTratamientoLayout.setHorizontalGroup(
            panTratamientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTratamientoLayout.createSequentialGroup()
                .addGroup(panTratamientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panTratamientoLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(panTratamientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblTratamiento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblObservaciones, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panTratamientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scpObservaciones, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                            .addComponent(scpTratamiento)))
                    .addGroup(panTratamientoLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(scpDiagnosticos, javax.swing.GroupLayout.PREFERRED_SIZE, 708, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        panTratamientoLayout.setVerticalGroup(
            panTratamientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTratamientoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(scpDiagnosticos, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panTratamientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panTratamientoLayout.createSequentialGroup()
                        .addComponent(lblTratamiento)
                        .addGap(24, 24, 24))
                    .addGroup(panTratamientoLayout.createSequentialGroup()
                        .addComponent(scpTratamiento, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(panTratamientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblObservaciones)
                    .addComponent(scpObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        tpnDetalleAtencion.addTab("Tratamiento / Diagnóstico", panTratamiento);

        tblExamenesLaboratorio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Código", "Examen Solicitado", "Indicaciones para el Laboratorio", "Estado"
            }
        ));
        scpReceta.setViewportView(tblExamenesLaboratorio);

        javax.swing.GroupLayout panRecetaLayout = new javax.swing.GroupLayout(panReceta);
        panReceta.setLayout(panRecetaLayout);
        panRecetaLayout.setHorizontalGroup(
            panRecetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRecetaLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(scpReceta, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );
        panRecetaLayout.setVerticalGroup(
            panRecetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRecetaLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(scpReceta, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        tpnDetalleAtencion.addTab("Exámenes de Laboratorio", panReceta);

        tblRecetaMedica.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Código", "Medicamento", "Cantidad", "Indicaciones / Dosis / Frecuencia"
            }
        ));
        scpExamenes.setViewportView(tblRecetaMedica);

        javax.swing.GroupLayout panExamenesLayout = new javax.swing.GroupLayout(panExamenes);
        panExamenes.setLayout(panExamenesLayout);
        panExamenesLayout.setHorizontalGroup(
            panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panExamenesLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(scpExamenes, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        panExamenesLayout.setVerticalGroup(
            panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panExamenesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scpExamenes, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        tpnDetalleAtencion.addTab("Receta Médica", panExamenes);

        javax.swing.GroupLayout panPrincipalLayout = new javax.swing.GroupLayout(panPrincipal);
        panPrincipal.setLayout(panPrincipalLayout);
        panPrincipalLayout.setHorizontalGroup(
            panPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panPrincipalLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tpnDetalleAtencion)
                    .addComponent(panAtenciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        panPrincipalLayout.setVerticalGroup(
            panPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panPrincipalLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(panBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(panAtenciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tpnDetalleAtencion, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Evento: Buscar paciente por DNI o N° de Historia Clínica.
     */
    private void btnBuscarHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarHistorialActionPerformed
        // TODO add your handling code here:
        String criterio = txtBuscarPaciente.getText().trim();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el DNI o N° de Historia Clínica del paciente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.pacienteActual = bllPaciente.buscarPorDni(criterio);
        if (pacienteActual == null) {
            this.pacienteActual = bllPaciente.buscarPorHistoriaClinica(criterio);
        }

        if (pacienteActual == null) {
            JOptionPane.showMessageDialog(this, "No se encontró ningún paciente con el criterio ingresado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarControles();
            return;
        }

        txtNombrePaciente.setText(pacienteActual.getNombres() + " " + pacienteActual.getApellidos());
        txtDniPaciente.setText(pacienteActual.getDni());
        txtNumeroHistoria.setText(pacienteActual.getNumeroHistoriaClinica());
        txtEdadSexo.setText(pacienteActual.getSexo().equalsIgnoreCase("M") ? "Masculino" : "Femenino");

        cargarTablaAtencionesAnteriores(pacienteActual.getIdPaciente());
    }//GEN-LAST:event_btnBuscarHistorialActionPerformed

    /**
     * Carga el detalle de la atención seleccionada.
     */
    private void tblAtencionesAnterioresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAtencionesAnterioresMouseClicked
        // TODO add your handling code here:
        int fila = tblAtencionesAnteriores.getSelectedRow();
        if (fila == -1) {
            return;
        }

        int idAtencion = (int) tblAtencionesAnteriores.getValueAt(fila, 0);
        AtencionMedica at = bllAtencion.buscarAtencion(idAtencion);

        if (at == null) {
            return;
        }

        txaMotivoConsulta.setText(at.getMotivoConsulta());
        txaAntecedentes.setText(at.getAntecedentes());

        SignosVitales sv = bllSignos.buscarPorAtencion(idAtencion);
        if (sv != null) {
            txtPresion.setText(sv.getPresionArterial());
            txtFC.setText(String.valueOf(sv.getFrecuenciaCardiaca()));
            txtFR.setText(String.valueOf(sv.getFrecuenciaRespiratoria()));
            txtTemp.setText(String.valueOf(sv.getTemperatura()));
            txtPeso.setText(String.valueOf(sv.getPesoKg()));
            txtTalla.setText(String.valueOf(sv.getTallaCm()));
            txtSaturacion.setText(String.valueOf(sv.getSaturacionOxigeno()));
        } else {
            limpiarCamposSignosVitales();
        }

        txaTratamiento.setText(at.getTratamiento());
        txaObservaciones.setText(at.getObservaciones());
        cargarTablaDiagnosticos(idAtencion);
        cargarTablaExamenes(idAtencion);
        cargarTablaReceta(idAtencion);
    }//GEN-LAST:event_tblAtencionesAnterioresMouseClicked

    private void txtBuscarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarPacienteActionPerformed
        // TODO add your handling code here:
        btnBuscarHistorialActionPerformed(evt);
    }//GEN-LAST:event_txtBuscarPacienteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarHistorial;
    private javax.swing.JLabel lblAntecedentes;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblEdadSexo;
    private javax.swing.JLabel lblFC;
    private javax.swing.JLabel lblFR;
    private javax.swing.JLabel lblMotivoConsulta;
    private javax.swing.JLabel lblNumeroHistoria;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblPaciente;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblPresion;
    private javax.swing.JLabel lblSaturacion;
    private javax.swing.JLabel lblTalla;
    private javax.swing.JLabel lblTemperatura;
    private javax.swing.JLabel lblTratamiento;
    private javax.swing.JPanel panAtenciones;
    private javax.swing.JPanel panBusqueda;
    private javax.swing.JPanel panDatosPaciente;
    private javax.swing.JPanel panExamenes;
    private javax.swing.JPanel panPrincipal;
    private javax.swing.JPanel panReceta;
    private javax.swing.JPanel panSignosVitales;
    private javax.swing.JPanel panTratamiento;
    private javax.swing.JScrollPane scpAntecedentes;
    private javax.swing.JScrollPane scpAtenciones;
    private javax.swing.JScrollPane scpDiagnosticos;
    private javax.swing.JScrollPane scpExamenes;
    private javax.swing.JScrollPane scpMotivo;
    private javax.swing.JScrollPane scpObservaciones;
    private javax.swing.JScrollPane scpReceta;
    private javax.swing.JScrollPane scpTratamiento;
    private javax.swing.JTable tblAtencionesAnteriores;
    private javax.swing.JTable tblDiagnosticos;
    private javax.swing.JTable tblExamenesLaboratorio;
    private javax.swing.JTable tblRecetaMedica;
    private javax.swing.JTabbedPane tpnDetalleAtencion;
    private javax.swing.JTextArea txaAntecedentes;
    private javax.swing.JTextArea txaMotivoConsulta;
    private javax.swing.JTextArea txaObservaciones;
    private javax.swing.JTextArea txaTratamiento;
    private javax.swing.JTextField txtBuscarPaciente;
    private javax.swing.JTextField txtDniPaciente;
    private javax.swing.JTextField txtEdadSexo;
    private javax.swing.JTextField txtFC;
    private javax.swing.JTextField txtFR;
    private javax.swing.JTextField txtNombrePaciente;
    private javax.swing.JTextField txtNumeroHistoria;
    private javax.swing.JTextField txtPeso;
    private javax.swing.JTextField txtPresion;
    private javax.swing.JTextField txtSaturacion;
    private javax.swing.JTextField txtTalla;
    private javax.swing.JTextField txtTemp;
    // End of variables declaration//GEN-END:variables
}

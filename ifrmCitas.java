/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;

import entidades.Cita;
import entidades.Especialidad;
import entidades.Medico;
import entidades.MedicoEspecialidad;
import entidades.Paciente;
import entidades.Usuario;
import logica.BLLCita;
import logica.BLLEspecialidad;
import logica.BLLMedico;
import logica.BLLMedicoEspecialidad;
import logica.BLLPaciente;
import logica.BLLUsuario;
import logica.BLLHorarioMedico;
import entidades.HorarioMedico;


import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Armando
 */
public class ifrmCitas extends javax.swing.JInternalFrame {

    private final BLLCita bllCita;
    private final BLLPaciente bllPaciente;
    private final BLLEspecialidad bllEspecialidad;
    private final BLLMedico bllMedico;
    private final BLLMedicoEspecialidad bllMedicoEspecialidad;
    private final BLLUsuario bllUsuario;
    private final BLLHorarioMedico bllHorarioMedico;

    private Paciente pacienteSeleccionado;
    private Cita citaSeleccionada;

    public ifrmCitas() {
        initComponents();

        this.bllCita = new BLLCita();
        this.bllPaciente = new BLLPaciente();
        this.bllEspecialidad = new BLLEspecialidad();
        this.bllMedico = new BLLMedico();
        this.bllMedicoEspecialidad = new BLLMedicoEspecialidad();
        bllUsuario = new BLLUsuario();
        bllHorarioMedico = new BLLHorarioMedico();

        cargarEspecialidades();
        cargarHorariosStandard();
        cargarEstadosCita();
        configurarEventos();
        listarCitasTabla();
    }

    private void configurarEventos() {
        // Evento al presionar Enter/escribir en DNI del Paciente
        txtDniPaciente.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || txtDniPaciente.getText().trim().length() == 8) {
                    buscarPacientePorDni();
                }
            }
        });

        // Evento de selección en la tabla de citas
        tblCitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCitas.getSelectedRow() != -1) {
                cargarCitaSeleccionadaDesdeTabla();
            }
        });
    }
    
    /**
     * Carga las especialidades en el combo correspondiente.
     */
    private void cargarEspecialidades() {
        ArrayList<Especialidad> lista = bllEspecialidad.listarEspecialidad();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("-- Seleccionar Especialidad --");
        for (Especialidad e : lista) {
            model.addElement(e);
        }
        cmbEspecialidad.setModel((javax.swing.ComboBoxModel) model);
    }

    /**
     * Carga los médicos que pertenecen a la especialidad seleccionada.
     */
    private void cargarMedicosPorEspecialidad(int idEspecialidad) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("-- Seleccionar Médico --");

        if (idEspecialidad > 0) {
            ArrayList<Medico> medicos = bllMedico.listarMedicos();
            for (Medico m : medicos) {
                ArrayList<MedicoEspecialidad> espMed = bllMedicoEspecialidad.listarPorMedico(m.getIdMedico());
                for (MedicoEspecialidad me : espMed) {
                    if (me.getIdEspecialidad() == idEspecialidad) {
                        model.addElement(m);
                        break;
                    }
                }
            }
        }
        cmbMedico.setModel((javax.swing.ComboBoxModel) model);
    }

    /**
     * Carga los horarios disponibles en bloques de 30 minutos.
     */
    private void cargarHorariosStandard() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("-- Seleccionar Hora --");
        for (int hora = 8; hora < 18; hora++) {
            model.addElement(String.format("%02d:00:00", hora));
            model.addElement(String.format("%02d:30:00", hora));
        }
        cmbHoraCita.setModel(model);
    }

    /**
     * Carga los estados permitidos para una cita.
     */
    private void cargarEstadosCita() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(new String[]{
            "PROGRAMADA", "CONFIRMADA", "ATENDIDA", "CANCELADA", "NO_ASISTIO"
        });
        cmbEstadoCita.setModel(model);
    }

    /**
     * Limpia el formulario de citas.
     */
    private void limpiarFormulario() {
        txtDniPaciente.setText("");
        txtNombrePaciente.setText("");
        jdcFechaCita.setDate(null);
        if (cmbEspecialidad.getItemCount() > 0) {
            cmbEspecialidad.setSelectedIndex(0);
        }
        cmbHoraCita.setSelectedIndex(0);
        cmbEstadoCita.setSelectedIndex(0);
        this.pacienteSeleccionado = null;
        this.citaSeleccionada = null;
        tblCitas.clearSelection();
    }

    /**
     * Busca un paciente por DNI.
     */
    private void buscarPacientePorDni() {
        String dni = txtDniPaciente.getText().trim();
        if (dni.isEmpty()) {
            return;
        }

        Paciente p = bllPaciente.buscarPorDni(dni);
        if (p != null) {
            this.pacienteSeleccionado = p;
            txtNombrePaciente.setText(p.getNombres() + " " + p.getApellidos());
        } else {
            this.pacienteSeleccionado = null;
            txtNombrePaciente.setText("PACIENTE NO ENCONTRADO");
        }
    }

    /**
     * Carga los datos de la cita seleccionada en la tabla.
     */
    private void cargarCitaSeleccionadaDesdeTabla() {
        int fila = tblCitas.getSelectedRow();
        if (fila != -1) {
            int idCita = (int) tblCitas.getValueAt(fila, 0);
            this.citaSeleccionada = bllCita.buscarCita(idCita);

            if (citaSeleccionada != null) {
                this.pacienteSeleccionado = bllPaciente.buscarPaciente(citaSeleccionada.getIdPaciente());
                if (pacienteSeleccionado != null) {
                    txtDniPaciente.setText(pacienteSeleccionado.getDni());
                    txtNombrePaciente.setText(pacienteSeleccionado.getNombres() + " " + pacienteSeleccionado.getApellidos());
                }

                Medico medicoSel = bllMedico.buscarMedico(citaSeleccionada.getIdMedico());
                if (medicoSel != null) {
                    ArrayList<MedicoEspecialidad> espList = bllMedicoEspecialidad.listarPorMedico(medicoSel.getIdMedico());
                    if (!espList.isEmpty()) {
                        int idEsp = espList.get(0).getIdEspecialidad();
                        for (int i = 1; i < cmbEspecialidad.getItemCount(); i++) {
                            Object item = cmbEspecialidad.getModel().getElementAt(i);
                            if (item instanceof Especialidad) {
                                Especialidad e = (Especialidad) item;
                                if (e.getIdEspecialidad() == idEsp) {
                                    cmbEspecialidad.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                        for (int i = 1; i < cmbMedico.getItemCount(); i++) {
                            Object item = cmbMedico.getModel().getElementAt(i);
                            if (item instanceof Medico) {
                                Medico m = (Medico) item;
                                if (m.getIdMedico() == medicoSel.getIdMedico()) {
                                    cmbMedico.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                    }
                }

                jdcFechaCita.setDate(citaSeleccionada.getFecha());
                String horaInicioStr = citaSeleccionada.getHoraInicio().toString();
                if (horaInicioStr.length() == 5) {
                    horaInicioStr += ":00";
                }
                cmbHoraCita.setSelectedItem(horaInicioStr);
                cmbEstadoCita.setSelectedItem(citaSeleccionada.getEstado());
            }
        }
    }

    /**
     * Lista todas las citas en la tabla.
     */
    private void listarCitasTabla() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                    "ID Cita",
                    "Paciente",
                    "Médico",
                    "Especialidad",
                    "Fecha",
                    "Hora Inicio",
                    "Hora Fin",
                    "Estado"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ArrayList<Cita> lista = bllCita.listarCita();

        for (Cita c : lista) {
            Paciente paciente = bllPaciente.buscarPaciente(c.getIdPaciente());
            String nombrePaciente = (paciente != null) ? paciente.getNombres() + " " + paciente.getApellidos() : "Sin paciente";

            Medico medico = bllMedico.buscarMedico(c.getIdMedico());
            String nombreMedico = "Sin médico";
            if (medico != null) {
                Usuario usuario = bllUsuario.buscarUsuario(medico.getIdUsuario());
                if (usuario != null) {
                    nombreMedico = usuario.getNombres() + " " + usuario.getApellidos();
                }
            }

            String nombreEspecialidad = "Sin especialidad";
            ArrayList<MedicoEspecialidad> relaciones = bllMedicoEspecialidad.listarPorMedico(c.getIdMedico());
            if (!relaciones.isEmpty()) {
                MedicoEspecialidad relacion = relaciones.get(0);
                Especialidad especialidad = bllEspecialidad.buscarEspecialidad(relacion.getIdEspecialidad());
                if (especialidad != null) {
                    nombreEspecialidad = especialidad.getNombre();
                }
            }

            model.addRow(new Object[]{
                c.getIdCita(),
                nombrePaciente,
                nombreMedico,
                nombreEspecialidad,
                c.getFecha(),
                c.getHoraInicio(),
                c.getHoraFin(),
                c.getEstado()
            });
        }

        tblCitas.setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblPaciente = new javax.swing.JLabel();
        lblEspecialidad = new javax.swing.JLabel();
        lblMedico = new javax.swing.JLabel();
        lblFechaCita = new javax.swing.JLabel();
        lblHoraCita = new javax.swing.JLabel();
        lblEstadoCita = new javax.swing.JLabel();
        txtDniPaciente = new javax.swing.JTextField();
        txtNombrePaciente = new javax.swing.JTextField();
        cmbEspecialidad = new javax.swing.JComboBox<>();
        cmbMedico = new javax.swing.JComboBox<>();
        cmbEstadoCita = new javax.swing.JComboBox<>();
        cmbHoraCita = new javax.swing.JComboBox<>();
        jdcFechaCita = new com.toedter.calendar.JDateChooser();
        btnVerDisponibilidad = new javax.swing.JButton();
        btnReservar = new javax.swing.JButton();
        btnReprogramar = new javax.swing.JButton();
        btnCancelarCita = new javax.swing.JButton();
        scpTablaCitasRegistradas = new javax.swing.JScrollPane();
        tblCitas = new javax.swing.JTable();
        lblTituloPaciente = new javax.swing.JLabel();
        lblTituloEspecialidad = new javax.swing.JLabel();
        lblTituloFecha = new javax.swing.JLabel();
        lblTituloEstado = new javax.swing.JLabel();
        lblListaCitasRegistradas = new javax.swing.JLabel();

        setClosable(true);
        setResizable(true);
        setTitle("Citas");

        lblPaciente.setText("Paciente (DNI/Nombre)");

        lblEspecialidad.setText("Especialidad");

        lblMedico.setText("Médico");

        lblFechaCita.setText("Fecha Cita");

        lblHoraCita.setText("Hora Cita (30 min)");

        lblEstadoCita.setText("Estado Cita");

        txtDniPaciente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDniPacienteKeyReleased(evt);
            }
        });

        txtNombrePaciente.setEditable(false);

        cmbEspecialidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar Especialidad" }));
        cmbEspecialidad.addActionListener(this::cmbEspecialidadActionPerformed);

        cmbMedico.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar Médico" }));

        cmbEstadoCita.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Programada", " Confirmada", "Atendida", "Cancelada", "No asistió" }));

        cmbHoraCita.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar Hora", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "14:00", "14:30", "15:00", "15:30", "16:00" }));

        btnVerDisponibilidad.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVerDisponibilidad.setForeground(new java.awt.Color(51, 51, 255));
        btnVerDisponibilidad.setText("Ver Disponibilidad");
        btnVerDisponibilidad.addActionListener(this::btnVerDisponibilidadActionPerformed);

        btnReservar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnReservar.setForeground(new java.awt.Color(51, 204, 0));
        btnReservar.setText("Reservar Cita");
        btnReservar.addActionListener(this::btnReservarActionPerformed);

        btnReprogramar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnReprogramar.setForeground(new java.awt.Color(51, 102, 255));
        btnReprogramar.setText("Reprogramar Cita");
        btnReprogramar.addActionListener(this::btnReprogramarActionPerformed);

        btnCancelarCita.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelarCita.setForeground(new java.awt.Color(255, 51, 51));
        btnCancelarCita.setText("Cancelar Cita");
        btnCancelarCita.addActionListener(this::btnCancelarCitaActionPerformed);

        tblCitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Cita", "Paciente", "Médico", "Especialidad", "Fecha", "Hora Inicio", "Hora Fin", "Estado"
            }
        ));
        scpTablaCitasRegistradas.setViewportView(tblCitas);

        lblTituloPaciente.setText("1. Paciente");

        lblTituloEspecialidad.setText("2. Especialidad y Médico");

        lblTituloFecha.setText("3. Fecha y Hora");

        lblTituloEstado.setText("4. Estado de la Cita");

        lblListaCitasRegistradas.setText("5. Lista de Citas Registradas");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addComponent(txtDniPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblMedico, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbMedico, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblHoraCita, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbHoraCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtNombrePaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblListaCitasRegistradas)
                            .addComponent(scpTablaCitasRegistradas, javax.swing.GroupLayout.PREFERRED_SIZE, 701, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(44, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(40, 40, 40)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblTituloPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPaciente)
                                .addComponent(lblTituloEspecialidad)
                                .addComponent(lblTituloFecha)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblEspecialidad)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(44, 44, 44)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblTituloEstado)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblEstadoCita, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblFechaCita))
                                    .addGap(45, 45, 45)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jdcFechaCita, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cmbEstadoCita, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVerDisponibilidad)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnReservar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addComponent(btnReprogramar)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelarCita)
                .addGap(189, 189, 189))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblTituloPaciente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPaciente)
                    .addComponent(txtDniPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombrePaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(lblTituloEspecialidad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEspecialidad)
                    .addComponent(lblMedico)
                    .addComponent(cmbMedico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(lblTituloFecha)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHoraCita)
                        .addComponent(cmbHoraCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblFechaCita)
                    .addComponent(jdcFechaCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(btnVerDisponibilidad, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblTituloEstado)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstadoCita)
                    .addComponent(cmbEstadoCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReservar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReprogramar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarCita, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(lblListaCitasRegistradas)
                .addGap(18, 18, 18)
                .addComponent(scpTablaCitasRegistradas, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Evento: Cambio de especialidad - Filtra médicos según la especialidad
     * seleccionada.
     */
    private void cmbEspecialidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEspecialidadActionPerformed
        // TODO add your handling code here:
        Object selected = cmbEspecialidad.getSelectedItem();
        if (selected instanceof Especialidad) {
            cargarMedicosPorEspecialidad(((Especialidad) selected).getIdEspecialidad());
        } else {
            cmbMedico.setModel(new DefaultComboBoxModel(new String[]{"-- Seleccionar Médico --"}));
        }
    }//GEN-LAST:event_cmbEspecialidadActionPerformed

    /**
     * Evento: Cancelar una cita.
     */
    private void btnCancelarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarCitaActionPerformed
        // TODO add your handling code here:
        if (citaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla para cancelar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea cancelar la cita N° " + citaSeleccionada.getIdCita() + "?",
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int res = bllCita.cancelarCita(citaSeleccionada.getIdCita());
            if (res == 0) {
                limpiarFormulario();
                listarCitasTabla();
            }
        }
    }//GEN-LAST:event_btnCancelarCitaActionPerformed

    /**
     * Evento: Ver disponibilidad del médico en la fecha y hora seleccionada.
     */
    private void btnVerDisponibilidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDisponibilidadActionPerformed
    Object medicoObj = cmbMedico.getSelectedItem();
    java.util.Date fechaDate = jdcFechaCita.getDate();
    String horaStr = (String) cmbHoraCita.getSelectedItem();


    if (!(medicoObj instanceof Medico) 
            || fechaDate == null 
            || horaStr == null 
            || horaStr.startsWith("--")) {


        JOptionPane.showMessageDialog(
                this,
                "Seleccione un Médico, una Fecha y una Hora válida.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }


    Medico medico = (Medico) medicoObj;

    Date fechaSql = new Date(fechaDate.getTime());

    Time horaInicio = Time.valueOf(horaStr);



    // ======================================
    // 1. VALIDAR HORARIO DEL MÉDICO
    // ======================================


    Calendar calendario = Calendar.getInstance();
    calendario.setTime(fechaDate);


    String diaSemana = "";

    switch(calendario.get(Calendar.DAY_OF_WEEK)){

        case Calendar.MONDAY:
            diaSemana = "LUNES";
            break;

        case Calendar.TUESDAY:
            diaSemana = "MARTES";
            break;

        case Calendar.WEDNESDAY:
            diaSemana = "MIERCOLES";
            break;

        case Calendar.THURSDAY:
            diaSemana = "JUEVES";
            break;

        case Calendar.FRIDAY:
            diaSemana = "VIERNES";
            break;

        case Calendar.SATURDAY:
            diaSemana = "SABADO";
            break;

        case Calendar.SUNDAY:
            diaSemana = "DOMINGO";
            break;
    }



    ArrayList<HorarioMedico> horarios =
        bllHorarioMedico.buscarPorMedicoYDia(
                medico.getIdMedico(),
                diaSemana
        );



    boolean dentroHorario = false;



    for(HorarioMedico h : horarios){

        if(horaInicio.compareTo(h.getHoraInicio()) >= 0
                &&
           horaInicio.compareTo(h.getHoraFin()) < 0){

            dentroHorario = true;
            break;
        }
    }



    if(!dentroHorario){

        JOptionPane.showMessageDialog(
                this,
                "El médico no atiende ese día o fuera de su horario laboral.",
                "Disponibilidad",
                JOptionPane.ERROR_MESSAGE
        );

        return;
    }



    // ======================================
    // 2. VALIDAR CITAS EXISTENTES
    // ======================================


    ArrayList<Cita> citasDia =
            bllCita.listarCitasPorMedicoFecha(
                    medico.getIdMedico(),
                    fechaSql
            );


    boolean ocupado = false;



    for(Cita c : citasDia){

        if(!c.getEstado().equalsIgnoreCase("CANCELADA")
                &&
           !c.getEstado().equalsIgnoreCase("NO_ASISTIO")){


            if(horaInicio.compareTo(c.getHoraInicio()) >= 0
                    &&
               horaInicio.compareTo(c.getHoraFin()) < 0){

                ocupado = true;
                break;
            }
        }
    }



    if(ocupado){

        JOptionPane.showMessageDialog(
                this,
                "El médico NO está disponible porque ya tiene una cita registrada.",
                "Disponibilidad",
                JOptionPane.ERROR_MESSAGE
        );

    }else{

        JOptionPane.showMessageDialog(
                this,
                "El médico se encuentra DISPONIBLE para esa fecha y hora.",
                "Disponibilidad",
                JOptionPane.INFORMATION_MESSAGE
        );

    }
    }//GEN-LAST:event_btnVerDisponibilidadActionPerformed

    /**
     * Evento: Reservar una nueva cita.
     */
    private void btnReservarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReservarActionPerformed
        // TODO add your handling code here:
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe buscar y seleccionar un Paciente registrado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object medicoObj = cmbMedico.getSelectedItem();
        if (!(medicoObj instanceof Medico)) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Médico válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.util.Date fechaDate = jdcFechaCita.getDate();
        String horaStr = (String) cmbHoraCita.getSelectedItem();

        if (fechaDate == null || horaStr == null || horaStr.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Seleccione la Fecha y Hora de la cita.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Medico medico = (Medico) medicoObj;
        Date fechaSql = new Date(fechaDate.getTime());
        Time horaInicio = Time.valueOf(horaStr);
        Time horaFin = new Time(horaInicio.getTime() + 1800000);
        String estado = cmbEstadoCita.getSelectedItem().toString();

        int res = bllCita.insertarCita(
                pacienteSeleccionado.getIdPaciente(),
                medico.getIdMedico(),
                fechaSql,
                horaInicio,
                horaFin,
                estado
        );

        if (res == 0) {
            limpiarFormulario();
            listarCitasTabla();
        }
    }//GEN-LAST:event_btnReservarActionPerformed

    /**
     * Evento: Reprogramar una cita existente.
     */
    private void btnReprogramarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReprogramarActionPerformed
        // TODO add your handling code here:
        if (citaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla para reprogramar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("ATENDIDA".equalsIgnoreCase(citaSeleccionada.getEstado())) {
            JOptionPane.showMessageDialog(this, "No se puede reprogramar una cita que ya fue ATENDIDA.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.util.Date fechaDate = jdcFechaCita.getDate();
        String horaStr = (String) cmbHoraCita.getSelectedItem();

        if (fechaDate == null || horaStr == null || horaStr.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Seleccione la nueva Fecha y Hora.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date fechaSql = new Date(fechaDate.getTime());
        Time horaInicio = Time.valueOf(horaStr);
        Time horaFin = new Time(horaInicio.getTime() + 1800000);
        String nuevoEstado = cmbEstadoCita.getSelectedItem().toString();

        int res = bllCita.actualizarCita(
                citaSeleccionada.getIdCita(),
                fechaSql,
                horaInicio,
                horaFin,
                nuevoEstado
        );

        if (res == 0) {
            limpiarFormulario();
            listarCitasTabla();
        }
    }//GEN-LAST:event_btnReprogramarActionPerformed

    // Si presiona Enter o ya tiene 8 dígitos (DNI completo)
    private void txtDniPacienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniPacienteKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER || txtDniPaciente.getText().trim().length() == 8) {
            buscarPacientePorDni();
        }
    }//GEN-LAST:event_txtDniPacienteKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarCita;
    private javax.swing.JButton btnReprogramar;
    private javax.swing.JButton btnReservar;
    private javax.swing.JButton btnVerDisponibilidad;
    private javax.swing.JComboBox<String> cmbEspecialidad;
    private javax.swing.JComboBox<String> cmbEstadoCita;
    private javax.swing.JComboBox<String> cmbHoraCita;
    private javax.swing.JComboBox<String> cmbMedico;
    private com.toedter.calendar.JDateChooser jdcFechaCita;
    private javax.swing.JLabel lblEspecialidad;
    private javax.swing.JLabel lblEstadoCita;
    private javax.swing.JLabel lblFechaCita;
    private javax.swing.JLabel lblHoraCita;
    private javax.swing.JLabel lblListaCitasRegistradas;
    private javax.swing.JLabel lblMedico;
    private javax.swing.JLabel lblPaciente;
    private javax.swing.JLabel lblTituloEspecialidad;
    private javax.swing.JLabel lblTituloEstado;
    private javax.swing.JLabel lblTituloFecha;
    private javax.swing.JLabel lblTituloPaciente;
    private javax.swing.JScrollPane scpTablaCitasRegistradas;
    private javax.swing.JTable tblCitas;
    private javax.swing.JTextField txtDniPaciente;
    private javax.swing.JTextField txtNombrePaciente;
    // End of variables declaration//GEN-END:variables
}

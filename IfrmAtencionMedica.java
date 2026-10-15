/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package presentacion;

import entidades.AtencionMedica;
import entidades.Cita;
import entidades.Medicamento;
import entidades.Receta;
import entidades.SignosVitales;
import logica.BLLAtencionMedica;
import logica.BLLCita;
import logica.BLLDiagnostico;
import logica.BLLExamenLaboratorio;
import logica.BLLMedicamento;
import logica.BLLReceta;
import logica.BLLRecetaDetalle;
import logica.BLLSignosVitales;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Ventana para el registro de atención médica. Permite buscar una cita,
 * registrar signos vitales, diagnósticos, recetas médicas y solicitar exámenes
 * de laboratorio.
 *
 * @author Grupo04
 */
public class IfrmAtencionMedica extends javax.swing.JInternalFrame {

    private final BLLAtencionMedica bllAtencion;
    private final BLLCita bllCita;
    private final BLLSignosVitales bllSignos;
    private final BLLDiagnostico bllDiagnostico;
    private final BLLMedicamento bllMedicamento;
    private final BLLReceta bllReceta;
    private final BLLRecetaDetalle bllRecetaDetalle;
    private final BLLExamenLaboratorio bllExamen;

    // Objetos de sesión de la atención activa
    private Cita citaActual;
    private AtencionMedica atencionActual;
    private Receta recetaActual;
    private final DefaultTableModel modeloTablaReceta;

    // 1. Constructor vacio
    public IfrmAtencionMedica() {
        this(null);
    }

    public IfrmAtencionMedica(entidades.Usuario usuario) {
        initComponents();
        aplicarPermisosAtencion(usuario); // Desactiva las pestañas si idRol es 4

        this.bllAtencion = new BLLAtencionMedica();
        this.bllCita = new BLLCita();
        this.bllSignos = new BLLSignosVitales();
        this.bllDiagnostico = new BLLDiagnostico();
        this.bllMedicamento = new BLLMedicamento();
        this.bllReceta = new BLLReceta();
        this.bllRecetaDetalle = new BLLRecetaDetalle();
        this.bllExamen = new BLLExamenLaboratorio();

        this.modeloTablaReceta = new DefaultTableModel(
                new String[]{"ID Medicamento", "Medicamento", "Cantidad", "Indicaciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblReceta.setModel(modeloTablaReceta);

        inicializarCatalogos();
        configurarEventos();
    }

    private void inicializarCatalogos() {
        // Cargar combo de medicamentos disponibles desde inventario
        ArrayList<Medicamento> medicamentos = bllMedicamento.listarMedicamentos();
        DefaultComboBoxModel modelMed = new DefaultComboBoxModel();
        modelMed.addElement("-- Seleccionar Medicamento --");
        for (Medicamento m : medicamentos) {
            modelMed.addElement(m);
        }
        cmbMedicamento.setModel((javax.swing.ComboBoxModel) modelMed);

        // Cargar tipos de exámenes de laboratorio
        DefaultComboBoxModel<String> modelExamenes = new DefaultComboBoxModel<>(new String[]{
            "-- Seleccionar Examen --",
            "Hemograma Completo",
            "Perfil Lipídico",
            "Glucosa en Ayunas",
            "Examen Completo de Orina",
            "Radiografía de Tórax",
            "Ecografía Abdominal"
        });
        cmbTipoExamen.setModel(modelExamenes);
    }

    private void configurarEventos() {
        // Al presionar ENTER en el campo de texto, busca la cita
        txtCitaId.addActionListener(e -> buscarCitaYPrepararAtencion());

        // El botón ahora abre el historial clínico del paciente actual
        btnVerHistorial.addActionListener(e -> abrirHistorialClinico());

        btnCerrarAtencion.addActionListener(e -> guardarYCerrarAtencion());
        btnAgregarMedicamento.addActionListener(e -> agregarMedicamentoAReceta());
        btnSolicitarExamen.addActionListener(e -> solicitarExamenLaboratorio());
    }

    /**
     * Instancia y abre la ventana del historial clínico, pasándole el ID del
     * paciente de la cita actual.
     */
    private void abrirHistorialClinico() {
        if (citaActual == null) {
            JOptionPane.showMessageDialog(this, "Primero ingrese el N° de Cita y presione ENTER para cargar los datos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear la instancia del historial clínico
        IfrmHistorialClinico frmHistorial = new IfrmHistorialClinico();

        // Agregarlo al JDesktopPane principal
        if (this.getDesktopPane() != null) {
            this.getDesktopPane().add(frmHistorial);
            frmHistorial.toFront();
            frmHistorial.setVisible(true);
        }

        // Llamar al método público en IfrmHistorialClinico
        frmHistorial.cargarHistorialAutomatico(citaActual.getIdPaciente());
    }

    /**
     * Verifica que exista una cita válida y confirmada antes de atender.
     */
    private void buscarCitaYPrepararAtencion() {
        String idCitaStr = txtCitaId.getText().trim();
        if (idCitaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el N° de Cita Confirmada.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idCita = Integer.parseInt(idCitaStr);
            Cita cita = bllCita.buscarCita(idCita);

            if (cita == null) {
                JOptionPane.showMessageDialog(this, "La cita ingresada no existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar estado de la cita
            if ("CANCELADA".equalsIgnoreCase(cita.getEstado())) {
                JOptionPane.showMessageDialog(this, "No se puede atender una cita CANCELADA.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            this.citaActual = cita;

            // Verificar si ya existe una atención registrada para esta cita
            AtencionMedica atencionExistente = bllAtencion.buscarPorCita(idCita);
            if (atencionExistente != null) {
                this.atencionActual = atencionExistente;
                cargarDatosAtencionExistente();
            } else {
                JOptionPane.showMessageDialog(this, "Cita N° " + idCita + " lista para registrar atención.", "Cita Encontrada", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código de cita debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Registra los signos vitales, diagnósticos y cierra
     * la atención.
     */
    private void guardarYCerrarAtencion() {
         if (citaActual == null) {
            JOptionPane.showMessageDialog(this, "Primero debe buscar una Cita Confirmada.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Si es ENFERMERA (el botón cambió su texto a Guardar Signos Vitales)
        if ("Guardar Signos Vitales".equals(btnCerrarAtencion.getText())) {
            AtencionMedica atencionExistente = bllAtencion.buscarPorCita(citaActual.getIdCita());
            int idAtencion = (atencionExistente != null) ? atencionExistente.getIdAtencion() : citaActual.getIdCita();

            guardarSignosVitales(idAtencion);
            JOptionPane.showMessageDialog(this, "Signos vitales registrados con éxito.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            limpiarPantalla();
            return; // Termina aquí para no ejecutar la validación médica de diagnósticos
        }

        // A partir de aquí continúa la lógica normal para el MÉDICO
        if (atencionActual != null && atencionActual.isCerrada()) {
            JOptionPane.showMessageDialog(this, "La atención médica ya se encuentra CERRADA y no puede modificarse.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String motivo = txaMotivo.getText().trim();
        String diagnosticoTexto = txaDiagnostico.getText().trim();
        String tratamiento = txaTratamiento.getText().trim();
        String antecedentes = txaAntecedentes.getText().trim();
        String observaciones = txaObservaciones.getText().trim();

        // Validaciones obligatorias
        if (motivo.isEmpty() || diagnosticoTexto.isEmpty() || tratamiento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Motivo, Diagnóstico y Tratamiento son campos obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resAtencion;

        if (atencionActual == null) {
            // No se había solicitado examen antes: se crea la atención (siempre abierta)
            AtencionMedica nueva = new AtencionMedica.Builder(citaActual.getIdCita(), motivo)
                    .antecedentes(antecedentes)
                    .tratamiento(tratamiento)
                    .observaciones(observaciones)
                    .cerrada(false) // el SP de insert rechaza cerrada = true
                    .build();

            resAtencion = bllAtencion.insertarAtencion(nueva);

            if (resAtencion == 0) {
                this.atencionActual = bllAtencion.buscarPorCita(citaActual.getIdCita());
            }
        } else {
            // Ya existe (se creó al solicitar un examen): se actualiza, no se inserta
            AtencionMedica actualizada = new AtencionMedica.Builder(citaActual.getIdCita(), motivo)
                    .idAtencion(atencionActual.getIdAtencion())
                    .antecedentes(antecedentes)
                    .tratamiento(tratamiento)
                    .observaciones(observaciones)
                    .cerrada(false)
                    .build();

            resAtencion = bllAtencion.actualizarAtencion(actualizada);
        }

        if (resAtencion == 0 && atencionActual != null) {

            // 2. Guardar Signos Vitales
            guardarSignosVitales(atencionActual.getIdAtencion());

            // 3. Guardar Diagnóstico
            bllDiagnostico.insertarDiagnostico(atencionActual.getIdAtencion(), "CIE10-GEN", diagnosticoTexto);

            if (modeloTablaReceta.getRowCount() > 0) {

                int estadoReceta = bllReceta.insertarReceta(atencionActual.getIdAtencion());

                if (estadoReceta == 0) {
                    Receta recetaGenerada = bllReceta.buscarPorAtencion(atencionActual.getIdAtencion());

                    if (recetaGenerada != null) {
                        int idReceta = recetaGenerada.getIdReceta();

                        for (int i = 0; i < modeloTablaReceta.getRowCount(); i++) {
                            int idMedicamento = (int) modeloTablaReceta.getValueAt(i, 0);
                            int cantidad = (int) modeloTablaReceta.getValueAt(i, 2);
                            String indicaciones = modeloTablaReceta.getValueAt(i, 3).toString();

                            bllRecetaDetalle.insertarRecetaDetalle(idReceta, idMedicamento, cantidad, indicaciones);
                        }
                    }
                }
            }

            // 4. Cambiar estado de la cita a ATENDIDA
            bllCita.cambiarEstadoCita(citaActual.getIdCita(), "ATENDIDA");

            // 5. Cierre de atención
            bllAtencion.cerrarAtencion(atencionActual.getIdAtencion());

            JOptionPane.showMessageDialog(this, "Atención médica registrada y cerrada con éxito.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            limpiarPantalla();
        }
    }

    private void guardarSignosVitales(int idAtencion) {
        try {
            String pa = txtPresion.getText().trim();
            BigDecimal temp = txtTemperatura.getText().trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtTemperatura.getText().trim());
            Integer fc = txtFrecuenciaCardiaca.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtFrecuenciaCardiaca.getText().trim());
            Integer fr = txtFrecuenciaRespiratoria.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtFrecuenciaRespiratoria.getText().trim());
            BigDecimal peso = txtPeso.getText().trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtPeso.getText().trim());
            BigDecimal talla = txtTalla.getText().trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtTalla.getText().trim());
            Integer satO2 = txtSaturacionO2.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtSaturacionO2.getText().trim());

            SignosVitales sv = new SignosVitales(idAtencion, pa, temp, fc, fr, peso, talla, satO2);
            bllSignos.insertarSignosVitales(sv);
        } catch (Exception e) {
            System.err.println("Error al parsear signos vitales: " + e.getMessage());
        }
    }

    /**
     * Agrega medicamentos al detalle de la receta
     * electrónica.
     */
    private void agregarMedicamentoAReceta() {
        Object medObj = cmbMedicamento.getSelectedItem();
        String cantidadStr = txtCantidad.getText().trim();
        String indicaciones = txtIndicaciones.getText().trim();

        if (!(medObj instanceof Medicamento) || cantidadStr.isEmpty() || indicaciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un medicamento, cantidad e indicaciones.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) { // RN-29: Cantidad válida > 0
                JOptionPane.showMessageDialog(this, "La cantidad prescribir debe ser mayor a 0.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Medicamento m = (Medicamento) medObj;

            // Agregar fila a la tabla visual de recetas
            modeloTablaReceta.addRow(new Object[]{
                m.getIdMedicamento(),
                m.getNombre(),
                cantidad,
                indicaciones
            });

            // Limpiar controles de detalle de receta
            cmbMedicamento.setSelectedIndex(0);
            txtCantidad.setText("");
            txtIndicaciones.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Genera la solicitud de examen de laboratorio asociada a la
     * atención.
     */
    private void solicitarExamenLaboratorio() {

        String tipoExamen = (String) cmbTipoExamen.getSelectedItem();

        if (tipoExamen == null || tipoExamen.startsWith("--")) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un tipo de examen de la lista.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        // Si todavía no existe una atención, se crea una atención activa
        if (atencionActual == null && citaActual != null) {

            AtencionMedica nueva = new AtencionMedica.Builder(
                    citaActual.getIdCita(),
                    txaMotivo.getText().trim()
            )
            .antecedentes(txaAntecedentes.getText())
            .tratamiento(txaTratamiento.getText())
            .observaciones(txaObservaciones.getText())
            .cerrada(false)
            .build();


            int resultado = bllAtencion.insertarAtencion(nueva);

            if(resultado == 0){

                atencionActual = bllAtencion.buscarPorCita(citaActual.getIdCita());

            }else{

                JOptionPane.showMessageDialog(this,
                        "No se pudo crear la atención médica.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }


        if (atencionActual == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe haber una atención activa para solicitar exámenes.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        // Registrar examen con estado inicial PENDIENTE
        int res = bllExamen.insertarExamen(
                atencionActual.getIdAtencion(),
                tipoExamen,
                "PENDIENTE"
        );


        if(res == 0){

            JOptionPane.showMessageDialog(this,
                    "Examen solicitado correctamente.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);

            cmbTipoExamen.setSelectedIndex(0);
        }
    }

    private void cargarDatosAtencionExistente() {
        if (atencionActual != null) {
            txaMotivo.setText(atencionActual.getMotivoConsulta());
            txaTratamiento.setText(atencionActual.getTratamiento());
            txaAntecedentes.setText(atencionActual.getAntecedentes());
            txaObservaciones.setText(atencionActual.getObservaciones());

            // Cargar signos vitales si existen
            SignosVitales sv = bllSignos.buscarPorAtencion(atencionActual.getIdAtencion());
            if (sv != null) {
                txtPresion.setText(sv.getPresionArterial());
                txtTemperatura.setText(String.valueOf(sv.getTemperatura()));
                txtFrecuenciaCardiaca.setText(String.valueOf(sv.getFrecuenciaCardiaca()));
                txtFrecuenciaRespiratoria.setText(String.valueOf(sv.getFrecuenciaRespiratoria()));
                txtPeso.setText(String.valueOf(sv.getPesoKg()));
                txtTalla.setText(String.valueOf(sv.getTallaCm()));
                txtSaturacionO2.setText(String.valueOf(sv.getSaturacionOxigeno()));
            }
        }
    }

    private void limpiarPantalla() {
        txtCitaId.setText("");
        txtPresion.setText("");
        txtTemperatura.setText("");
        txtFrecuenciaCardiaca.setText("");
        txtFrecuenciaRespiratoria.setText("");
        txtPeso.setText("");
        txtTalla.setText("");
        txtSaturacionO2.setText("");
        txaMotivo.setText("");
        txaDiagnostico.setText("");
        txaTratamiento.setText("");
        txaAntecedentes.setText("");
        txaObservaciones.setText("");
        modeloTablaReceta.setRowCount(0);
        this.citaActual = null;
        this.atencionActual = null;
    }

    /**
     * Desactiva las pestañas y controles médicos cuando el usuario logueado es
     * Enfermera. Respeta la estructura gráfica y no altera la jerarquía del
     * Composite.
     */
    public void aplicarPermisosAtencion(entidades.Usuario usuario) {
        if (usuario == null) {
            return;
        }

        // Evaluamos si el idRol pertenece a Enfermera (idRol == 4)
        if (usuario.getIdRol() == 4) {

            tpnExamenes.setEnabledAt(0, false); 
            tpnExamenes.setEnabledAt(1, false); 
            tpnExamenes.setEnabledAt(2, false); 

            txaMotivo.setEditable(false);
            txaDiagnostico.setEditable(false);
            txaObservaciones.setEditable(false);
            txaAntecedentes.setEditable(false);
            txaTratamiento.setEditable(false);
            cmbMedicamento.setEnabled(false);
            txtCantidad.setEditable(false);
            txtIndicaciones.setEditable(false);
            btnAgregarMedicamento.setEnabled(false);
            cmbTipoExamen.setEnabled(false);
            btnSolicitarExamen.setEnabled(false);

            btnCerrarAtencion.setText("Guardar Signos Vitales");
        }
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
        panCita = new javax.swing.JPanel();
        lblCita = new javax.swing.JLabel();
        txtCitaId = new javax.swing.JTextField();
        btnVerHistorial = new javax.swing.JButton();
        panSignosVitales = new javax.swing.JPanel();
        lblPresion = new javax.swing.JLabel();
        lblFC = new javax.swing.JLabel();
        lblFR = new javax.swing.JLabel();
        lblTemperatura = new javax.swing.JLabel();
        txtPresion = new javax.swing.JTextField();
        txtFrecuenciaCardiaca = new javax.swing.JTextField();
        txtTemperatura = new javax.swing.JTextField();
        lblPeso = new javax.swing.JLabel();
        lblTalla = new javax.swing.JLabel();
        lblSaturacion = new javax.swing.JLabel();
        txtPeso = new javax.swing.JTextField();
        txtTalla = new javax.swing.JTextField();
        txtSaturacionO2 = new javax.swing.JTextField();
        txtFrecuenciaRespiratoria = new javax.swing.JTextField();
        btnCerrarAtencion = new javax.swing.JButton();
        tpnExamenes = new javax.swing.JTabbedPane();
        panDiagnostico = new javax.swing.JPanel();
        lblMotivo = new javax.swing.JLabel();
        lblDiagnostico = new javax.swing.JLabel();
        lblObservaciones = new javax.swing.JLabel();
        lblAntecedentes = new javax.swing.JLabel();
        lblTratamiento = new javax.swing.JLabel();
        scpDiagnostico = new javax.swing.JScrollPane();
        txaDiagnostico = new javax.swing.JTextArea();
        scpObservaciones = new javax.swing.JScrollPane();
        txaObservaciones = new javax.swing.JTextArea();
        scpMotivo = new javax.swing.JScrollPane();
        txaMotivo = new javax.swing.JTextArea();
        scpAntecedentes = new javax.swing.JScrollPane();
        txaAntecedentes = new javax.swing.JTextArea();
        scpTratamiento = new javax.swing.JScrollPane();
        txaTratamiento = new javax.swing.JTextArea();
        panReceta = new javax.swing.JPanel();
        cmbMedicamento = new javax.swing.JComboBox<>();
        lblMedicamento = new javax.swing.JLabel();
        lblCantidad = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        lblIndicaciones = new javax.swing.JLabel();
        txtIndicaciones = new javax.swing.JTextField();
        btnAgregarMedicamento = new javax.swing.JButton();
        scpReceta = new javax.swing.JScrollPane();
        tblReceta = new javax.swing.JTable();
        panExamenes = new javax.swing.JPanel();
        lblTipoExamen = new javax.swing.JLabel();
        cmbTipoExamen = new javax.swing.JComboBox<>();
        btnSolicitarExamen = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);

        panPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REGISTRO DE ATENCIÓN MÉDICA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        panCita.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CITA MÉDICA Y HISTORIAL PREVIO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblCita.setText("N° Cita Confirmada:");

        txtCitaId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCitaIdActionPerformed(evt);
            }
        });

        btnVerHistorial.setText("Ver Historial Previo del Paciente");
        btnVerHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerHistorialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panCitaLayout = new javax.swing.GroupLayout(panCita);
        panCita.setLayout(panCitaLayout);
        panCitaLayout.setHorizontalGroup(
            panCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCitaLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblCita)
                .addGap(18, 18, 18)
                .addComponent(txtCitaId, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(btnVerHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panCitaLayout.setVerticalGroup(
            panCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panCitaLayout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(panCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCita)
                    .addComponent(txtCitaId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerHistorial))
                .addGap(13, 13, 13))
        );

        panSignosVitales.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SIGNOS VITALES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblPresion.setText("Presión Arterial (PA):");

        lblFC.setText("Frecuencia Cardíaca (FC):");

        lblFR.setText("Frecuencia Respiratoria (FR):");

        lblTemperatura.setText("Temperatura (C°):");

        lblPeso.setText("Peso (kg):");

        lblTalla.setText("Talla (cm):");

        lblSaturacion.setText("Saturación O2:");

        javax.swing.GroupLayout panSignosVitalesLayout = new javax.swing.GroupLayout(panSignosVitales);
        panSignosVitales.setLayout(panSignosVitalesLayout);
        panSignosVitalesLayout.setHorizontalGroup(
            panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSignosVitalesLayout.createSequentialGroup()
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panSignosVitalesLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTemperatura)
                            .addComponent(lblFC)
                            .addComponent(lblPresion))
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panSignosVitalesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblFR)
                        .addGap(18, 18, 18)))
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panSignosVitalesLayout.createSequentialGroup()
                        .addComponent(txtTemperatura)
                        .addGap(554, 554, 554))
                    .addGroup(panSignosVitalesLayout.createSequentialGroup()
                        .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPresion, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(txtFrecuenciaCardiaca, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(txtFrecuenciaRespiratoria))
                        .addGap(33, 33, 33)
                        .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblSaturacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTalla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPeso)
                            .addComponent(txtTalla)
                            .addComponent(txtSaturacionO2, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        panSignosVitalesLayout.setVerticalGroup(
            panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSignosVitalesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPresion)
                    .addComponent(txtPresion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPeso)
                    .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFrecuenciaCardiaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFC)
                    .addComponent(lblTalla)
                    .addComponent(txtTalla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSaturacion)
                        .addComponent(txtSaturacionO2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFrecuenciaRespiratoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblFR))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSignosVitalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTemperatura)
                    .addComponent(txtTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        btnCerrarAtencion.setText("Guardar y Cerrar Atención");
        btnCerrarAtencion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarAtencionActionPerformed(evt);
            }
        });

        lblMotivo.setText("Motivo Consulta:");

        lblDiagnostico.setText("Diagnóstico (*):");

        lblObservaciones.setText("Observaciones:");

        lblAntecedentes.setText("Antecedentes:");

        lblTratamiento.setText("Tratamiento:");

        txaDiagnostico.setColumns(20);
        txaDiagnostico.setRows(5);
        scpDiagnostico.setViewportView(txaDiagnostico);

        txaObservaciones.setColumns(20);
        txaObservaciones.setRows(5);
        scpObservaciones.setViewportView(txaObservaciones);

        txaMotivo.setColumns(20);
        txaMotivo.setRows(5);
        scpMotivo.setViewportView(txaMotivo);

        txaAntecedentes.setColumns(20);
        txaAntecedentes.setRows(5);
        scpAntecedentes.setViewportView(txaAntecedentes);

        txaTratamiento.setColumns(20);
        txaTratamiento.setRows(5);
        scpTratamiento.setViewportView(txaTratamiento);

        javax.swing.GroupLayout panDiagnosticoLayout = new javax.swing.GroupLayout(panDiagnostico);
        panDiagnostico.setLayout(panDiagnosticoLayout);
        panDiagnosticoLayout.setHorizontalGroup(
            panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDiagnosticoLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTratamiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblAntecedentes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMotivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDiagnostico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblObservaciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scpDiagnostico, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                    .addComponent(scpObservaciones)
                    .addComponent(scpMotivo)
                    .addComponent(scpAntecedentes)
                    .addComponent(scpTratamiento))
                .addContainerGap(198, Short.MAX_VALUE))
        );
        panDiagnosticoLayout.setVerticalGroup(
            panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDiagnosticoLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scpMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMotivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scpDiagnostico, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDiagnostico))
                .addGap(12, 12, 12)
                .addGroup(panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblObservaciones)
                    .addComponent(scpObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAntecedentes)
                    .addComponent(scpAntecedentes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(panDiagnosticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTratamiento)
                    .addComponent(scpTratamiento, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        tpnExamenes.addTab("DIAGNÓSTICO", panDiagnostico);

        cmbMedicamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblMedicamento.setText("Medicamento:");

        lblCantidad.setText("Cantidad:");

        lblIndicaciones.setText("Indicaciones:");

        btnAgregarMedicamento.setText("Agregar Medicamento");
        btnAgregarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMedicamentoActionPerformed(evt);
            }
        });

        tblReceta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Medicamento", "Cantidad", "Indicaciones"
            }
        ));
        scpReceta.setViewportView(tblReceta);

        javax.swing.GroupLayout panRecetaLayout = new javax.swing.GroupLayout(panReceta);
        panReceta.setLayout(panRecetaLayout);
        panRecetaLayout.setHorizontalGroup(
            panRecetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRecetaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblMedicamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbMedicamento, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCantidad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblIndicaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIndicaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAgregarMedicamento)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panRecetaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scpReceta, javax.swing.GroupLayout.PREFERRED_SIZE, 833, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        panRecetaLayout.setVerticalGroup(
            panRecetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRecetaLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(panRecetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbMedicamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMedicamento)
                    .addComponent(lblCantidad)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIndicaciones)
                    .addComponent(txtIndicaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarMedicamento))
                .addGap(18, 18, 18)
                .addComponent(scpReceta, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        tpnExamenes.addTab("PRESCRIPCIÓN DE RECETA ELECTRÓNICA", panReceta);

        lblTipoExamen.setText("Tipo de Examen:");

        cmbTipoExamen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnSolicitarExamen.setText("Solicitar Examen");
        btnSolicitarExamen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolicitarExamenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panExamenesLayout = new javax.swing.GroupLayout(panExamenes);
        panExamenes.setLayout(panExamenesLayout);
        panExamenesLayout.setHorizontalGroup(
            panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panExamenesLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(lblTipoExamen)
                .addGap(33, 33, 33)
                .addComponent(cmbTipoExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(btnSolicitarExamen)
                .addContainerGap(295, Short.MAX_VALUE))
        );
        panExamenesLayout.setVerticalGroup(
            panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panExamenesLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(panExamenesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipoExamen)
                    .addComponent(cmbTipoExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSolicitarExamen))
                .addContainerGap(158, Short.MAX_VALUE))
        );

        tpnExamenes.addTab("SOLICITUD DE EXÁMENES", panExamenes);

        javax.swing.GroupLayout panPrincipalLayout = new javax.swing.GroupLayout(panPrincipal);
        panPrincipal.setLayout(panPrincipalLayout);
        panPrincipalLayout.setHorizontalGroup(
            panPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panPrincipalLayout.createSequentialGroup()
                .addGroup(panPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panPrincipalLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panSignosVitales, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panCita, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tpnExamenes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 888, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panPrincipalLayout.createSequentialGroup()
                        .addGap(371, 371, 371)
                        .addComponent(btnCerrarAtencion)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panPrincipalLayout.setVerticalGroup(
            panPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panPrincipalLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(panCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panSignosVitales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tpnExamenes, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCerrarAtencion)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(panPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Evento: Solicitar un examen de laboratorio.
     */
    private void btnSolicitarExamenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolicitarExamenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSolicitarExamenActionPerformed

    /**
     * Evento: Guardar la atencion medica y cerrar el registro.
     */
    private void btnCerrarAtencionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarAtencionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCerrarAtencionActionPerformed

    /**
     * Evento: Abrir el historial clinico del paciente.
     */
    private void btnVerHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerHistorialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnVerHistorialActionPerformed

    /**
     * Evento: Agregar un medicamento a la receta electronica.
     */
    private void btnAgregarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarMedicamentoActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnAgregarMedicamentoActionPerformed

    /**
     * Evento: Buscar cita al presionar Enter en el campo de texto.
     */
    private void txtCitaIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCitaIdActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txtCitaIdActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarMedicamento;
    private javax.swing.JButton btnCerrarAtencion;
    private javax.swing.JButton btnSolicitarExamen;
    private javax.swing.JButton btnVerHistorial;
    private javax.swing.JComboBox<String> cmbMedicamento;
    private javax.swing.JComboBox<String> cmbTipoExamen;
    private javax.swing.JLabel lblAntecedentes;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCita;
    private javax.swing.JLabel lblDiagnostico;
    private javax.swing.JLabel lblFC;
    private javax.swing.JLabel lblFR;
    private javax.swing.JLabel lblIndicaciones;
    private javax.swing.JLabel lblMedicamento;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblPresion;
    private javax.swing.JLabel lblSaturacion;
    private javax.swing.JLabel lblTalla;
    private javax.swing.JLabel lblTemperatura;
    private javax.swing.JLabel lblTipoExamen;
    private javax.swing.JLabel lblTratamiento;
    private javax.swing.JPanel panCita;
    private javax.swing.JPanel panDiagnostico;
    private javax.swing.JPanel panExamenes;
    private javax.swing.JPanel panPrincipal;
    private javax.swing.JPanel panReceta;
    private javax.swing.JPanel panSignosVitales;
    private javax.swing.JScrollPane scpAntecedentes;
    private javax.swing.JScrollPane scpDiagnostico;
    private javax.swing.JScrollPane scpMotivo;
    private javax.swing.JScrollPane scpObservaciones;
    private javax.swing.JScrollPane scpReceta;
    private javax.swing.JScrollPane scpTratamiento;
    private javax.swing.JTable tblReceta;
    private javax.swing.JTabbedPane tpnExamenes;
    private javax.swing.JTextArea txaAntecedentes;
    private javax.swing.JTextArea txaDiagnostico;
    private javax.swing.JTextArea txaMotivo;
    private javax.swing.JTextArea txaObservaciones;
    private javax.swing.JTextArea txaTratamiento;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCitaId;
    private javax.swing.JTextField txtFrecuenciaCardiaca;
    private javax.swing.JTextField txtFrecuenciaRespiratoria;
    private javax.swing.JTextField txtIndicaciones;
    private javax.swing.JTextField txtPeso;
    private javax.swing.JTextField txtPresion;
    private javax.swing.JTextField txtSaturacionO2;
    private javax.swing.JTextField txtTalla;
    private javax.swing.JTextField txtTemperatura;
    // End of variables declaration//GEN-END:variables
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package presentacion;

import entidades.Usuario;
import entidades.Rol;
import logica.BLLRol;
import logica.BLLAuditoria;
import presentacion.menu.ModuloMenu;
import presentacion.menu.OpcionMenu;

/**
 *
 * @author Grupo04
 */
public class frmPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(frmPrincipal.class.getName());

    private Usuario usuarioActual;
    private String rolNombre;

    private javax.swing.Timer timerInactividad;
    private final int TIEMPO_INACTIVIDAD = 5 * 60 * 1000; // 5 minutos

    public frmPrincipal() {
        initComponents();

        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
            this.dispose();
        });
    }

    public frmPrincipal(Usuario usuario) {
        initComponents();
        this.usuarioActual = usuario;

        cargarInformacionUsuario();
        ejecutarSeguridadComposite();
        iniciarTimerInactividad();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scpTexto = new javax.swing.JScrollPane();
        txpMensaje = new javax.swing.JTextPane();
        dppEscritorio = new javax.swing.JDesktopPane();
        mnbPrincipal = new javax.swing.JMenuBar();
        mnuSeguridad = new javax.swing.JMenu();
        mniAuditoria = new javax.swing.JMenuItem();
        mniUsuarios = new javax.swing.JMenuItem();
        mnuAdmision = new javax.swing.JMenu();
        mniCitas = new javax.swing.JMenuItem();
        mniPacientes = new javax.swing.JMenuItem();
        mniHistorialClinico = new javax.swing.JMenuItem();
        mnuCuerpoMedico = new javax.swing.JMenu();
        mniEspecialidades = new javax.swing.JMenuItem();
        mniAtencionMedica = new javax.swing.JMenuItem();
        mniMedicos = new javax.swing.JMenuItem();
        mnuServiciosAuxiliares = new javax.swing.JMenu();
        mniExamenes = new javax.swing.JMenuItem();
        mniMedicamentos = new javax.swing.JMenuItem();
        mniFarmacia = new javax.swing.JMenuItem();
        mnuFinanciera = new javax.swing.JMenu();
        mniCaja = new javax.swing.JMenuItem();
        mnuGerencia = new javax.swing.JMenu();
        mniReportes = new javax.swing.JMenuItem();
        mnuSesion = new javax.swing.JMenu();
        mniCerrarSesion = new javax.swing.JMenuItem();
        mniSalir = new javax.swing.JMenuItem();

        scpTexto.setViewportView(txpMensaje);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gestion Principal");

        javax.swing.GroupLayout dppEscritorioLayout = new javax.swing.GroupLayout(dppEscritorio);
        dppEscritorio.setLayout(dppEscritorioLayout);
        dppEscritorioLayout.setHorizontalGroup(
            dppEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 797, Short.MAX_VALUE)
        );
        dppEscritorioLayout.setVerticalGroup(
            dppEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 437, Short.MAX_VALUE)
        );

        mnuSeguridad.setText("Seguridad y Sistema");

        mniAuditoria.setText("Auditoria");
        mniAuditoria.addActionListener(this::mniAuditoriaActionPerformed);
        mnuSeguridad.add(mniAuditoria);

        mniUsuarios.setText("Usuarios");
        mniUsuarios.addActionListener(this::mniUsuariosActionPerformed);
        mnuSeguridad.add(mniUsuarios);

        mnbPrincipal.add(mnuSeguridad);

        mnuAdmision.setText("Admision y Pacientes");

        mniCitas.setText("Citas");
        mniCitas.addActionListener(this::mniCitasActionPerformed);
        mnuAdmision.add(mniCitas);

        mniPacientes.setText("Pacientes");
        mniPacientes.addActionListener(this::mniPacientesActionPerformed);
        mnuAdmision.add(mniPacientes);

        mniHistorialClinico.setText("Historial Clinico");
        mniHistorialClinico.addActionListener(this::mniHistorialClinicoActionPerformed);
        mnuAdmision.add(mniHistorialClinico);

        mnbPrincipal.add(mnuAdmision);

        mnuCuerpoMedico.setText("Cuerpo Medico");

        mniEspecialidades.setText("Especialidades");
        mniEspecialidades.addActionListener(this::mniEspecialidadesActionPerformed);
        mnuCuerpoMedico.add(mniEspecialidades);

        mniAtencionMedica.setText("Atencion Medica");
        mniAtencionMedica.addActionListener(this::mniAtencionMedicaActionPerformed);
        mnuCuerpoMedico.add(mniAtencionMedica);

        mniMedicos.setText("Medicos");
        mniMedicos.addActionListener(this::mniMedicosActionPerformed);
        mnuCuerpoMedico.add(mniMedicos);

        mnbPrincipal.add(mnuCuerpoMedico);

        mnuServiciosAuxiliares.setText("Servicios Auxiliares");

        mniExamenes.setText("Laboratorio clinico");
        mniExamenes.addActionListener(this::mniExamenesActionPerformed);
        mnuServiciosAuxiliares.add(mniExamenes);

        mniMedicamentos.setText("Medicamentos");
        mniMedicamentos.addActionListener(this::mniMedicamentosActionPerformed);
        mnuServiciosAuxiliares.add(mniMedicamentos);

        mniFarmacia.setText("Farmacia");
        mniFarmacia.addActionListener(this::mniFarmaciaActionPerformed);
        mnuServiciosAuxiliares.add(mniFarmacia);

        mnbPrincipal.add(mnuServiciosAuxiliares);

        mnuFinanciera.setText("Gestion Financiera");

        mniCaja.setText("Caja");
        mniCaja.addActionListener(this::mniCajaActionPerformed);
        mnuFinanciera.add(mniCaja);

        mnbPrincipal.add(mnuFinanciera);

        mnuGerencia.setText("Gerencia");

        mniReportes.setText("Reporte");
        mniReportes.addActionListener(this::mniReportesActionPerformed);
        mnuGerencia.add(mniReportes);

        mnbPrincipal.add(mnuGerencia);

        mnuSesion.setText("Sesìon");

        mniCerrarSesion.setText("Cerrar Sesion");
        mniCerrarSesion.addActionListener(this::mniCerrarSesionActionPerformed);
        mnuSesion.add(mniCerrarSesion);

        mniSalir.setText("Salir");
        mniSalir.addActionListener(this::mniSalirActionPerformed);
        mnuSesion.add(mniSalir);

        mnbPrincipal.add(mnuSesion);

        setJMenuBar(mnbPrincipal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dppEscritorio)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dppEscritorio)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mniAuditoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniAuditoriaActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmAuditoria.class, ifrmAuditoria::new);

    }//GEN-LAST:event_mniAuditoriaActionPerformed

    private void mniUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniUsuariosActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmUsuarios.class, ifrmUsuarios::new);

    }//GEN-LAST:event_mniUsuariosActionPerformed

    private void mniCitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCitasActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmCitas.class, ifrmCitas::new);

    }//GEN-LAST:event_mniCitasActionPerformed

    private void mniPacientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPacientesActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmPacientes.class, ifrmPacientes::new);

    }//GEN-LAST:event_mniPacientesActionPerformed

    private void mniHistorialClinicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniHistorialClinicoActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(IfrmHistorialClinico.class, IfrmHistorialClinico::new);
    }//GEN-LAST:event_mniHistorialClinicoActionPerformed

    private void mniEspecialidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniEspecialidadesActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmEspecialidades.class, ifrmEspecialidades::new);

    }//GEN-LAST:event_mniEspecialidadesActionPerformed

    private void mniAtencionMedicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniAtencionMedicaActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(IfrmAtencionMedica.class, () -> new IfrmAtencionMedica(this.usuarioActual));

    }//GEN-LAST:event_mniAtencionMedicaActionPerformed

    private void mniMedicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniMedicosActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmMedicos.class, ifrmMedicos::new);

    }//GEN-LAST:event_mniMedicosActionPerformed

    private void mniExamenesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExamenesActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmExamenes.class, () -> new ifrmExamenes(this.usuarioActual));

    }//GEN-LAST:event_mniExamenesActionPerformed

    private void mniMedicamentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniMedicamentosActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmMedicamentos.class, ifrmMedicamentos::new);

    }//GEN-LAST:event_mniMedicamentosActionPerformed

    private void mniFarmaciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniFarmaciaActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmFarmacia.class, () -> new ifrmFarmacia(this.usuarioActual));

    }//GEN-LAST:event_mniFarmaciaActionPerformed

    private void mniCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCajaActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmCaja.class, () -> new ifrmCaja(this.usuarioActual));

    }//GEN-LAST:event_mniCajaActionPerformed

    private void mniReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniReportesActionPerformed
        // TODO add your handling code here:
        abrirVentanaInterna(ifrmReportes.class, ifrmReportes::new);

    }//GEN-LAST:event_mniReportesActionPerformed

    private void mniCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCerrarSesionActionPerformed
        // TODO add your handling code here:
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirmacion != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }

        BLLAuditoria bllAuditoria = new BLLAuditoria();
        bllAuditoria.insertarAuditoria(usuarioActual.getIdUsuario(), "SEGURIDAD", "LOGOUT",
                "Cierre de sesión de " + usuarioActual.getUsername());

        this.dispose();
        new Login().setVisible(true);
    }//GEN-LAST:event_mniCerrarSesionActionPerformed

    private void mniSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSalirActionPerformed
        // TODO add your handling code here:
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(this,
                "¿Desea salir del sistema?", "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_mniSalirActionPerformed

    // APERTURA DE VENTANAS INTERNAS
    private void abrirVentanaInterna(Class<? extends javax.swing.JInternalFrame> claseVentana,
            java.util.function.Supplier<javax.swing.JInternalFrame> creador) {
        for (javax.swing.JInternalFrame ventana : dppEscritorio.getAllFrames()) {
            if (claseVentana.isInstance(ventana)) {
                try {
                    ventana.setIcon(false);
                    ventana.setSelected(true);
                    ventana.toFront();
                } catch (java.beans.PropertyVetoException e) {
                    System.err.println("No se pudo seleccionar la ventana: " + e.getMessage());
                }
                return;
            }
        }

        javax.swing.JInternalFrame nueva = creador.get();
        dppEscritorio.add(nueva);
        nueva.setVisible(true);

        int x = (dppEscritorio.getWidth() - nueva.getWidth()) / 2;
        int y = (dppEscritorio.getHeight() - nueva.getHeight()) / 2;
        nueva.setLocation(Math.max(x, 0), Math.max(y, 0));

        try {
            nueva.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "No se pudo abrir la ventana solicitada.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* 
         * Si el usario ejecuta esta clase directamente (Shift + F6), 
         * sera enviado al Login de inmediato.
         */
        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }

    /**
     * Aplicamos el patrón Composite para habilitar/mostrar únicamente los
     * módulos permitidos según el rol del usuario autenticado.
     */
    private void ejecutarSeguridadComposite() {
        if (usuarioActual == null) {
            return;
        }

        // 1. Obtenemos el objeto Rol desde la base de datos
        BLLRol bllRol = new BLLRol();
        Rol rolObjeto = bllRol.buscarRol(usuarioActual.getIdRol());

        if (rolObjeto == null) {
            return;
        }

        // Nombres exactos de Roles
        final String ADMIN = "ADMINISTRADOR";
        final String RECEP = "RECEPCIONISTA";
        final String MEDICO = "MEDICO";
        final String ENFERMERA = "ENFERMERA";
        final String LAB = "LABORATORISTA";
        final String FARMACIA = "FARMACEUTICO";
        final String CAJERO = "CAJERO";
        final String DIRECTOR = "DIRECTOR_MEDICO";

        // CONSTRUCCIÓN DEL ÁRBOL COMPOSITE 
        // Seguridad y Sistema -> Exclusivo Administrador
        ModuloMenu modSeguridad = new ModuloMenu(mnuSeguridad)
                .agregar(new OpcionMenu(mniAuditoria, ADMIN))
                .agregar(new OpcionMenu(mniUsuarios, ADMIN));

        // Admisión y Pacientes -> Exclusivo Recepcionista 
        ModuloMenu modAdmision = new ModuloMenu(mnuAdmision)
                .agregar(new OpcionMenu(mniCitas, RECEP))
                .agregar(new OpcionMenu(mniPacientes, RECEP))
                .agregar(new OpcionMenu(mniHistorialClinico, MEDICO, DIRECTOR));

        // Cuerpo Médico y Atención 
        // - Especialidades y Médicos/Horarios: Administrador 
        // - Atención Médica: Médico y Enfermera
        ModuloMenu modMedico = new ModuloMenu(mnuCuerpoMedico)
                .agregar(new OpcionMenu(mniEspecialidades, ADMIN))
                .agregar(new OpcionMenu(mniMedicos, ADMIN))
                .agregar(new OpcionMenu(mniAtencionMedica, MEDICO, ENFERMERA));

        // Servicios Auxiliares
        // - Exámenes/Laboratorio: Laboratorista 
        // - Medicamentos/Farmacia: Farmacéutico 
        ModuloMenu modAuxiliares = new ModuloMenu(mnuServiciosAuxiliares)
                .agregar(new OpcionMenu(mniExamenes, LAB))
                .agregar(new OpcionMenu(mniMedicamentos, FARMACIA))
                .agregar(new OpcionMenu(mniFarmacia, FARMACIA));

        // Gestión Financiera / Caja -> Exclusivo Cajero 
        ModuloMenu modCaja = new ModuloMenu(mnuFinanciera)
                .agregar(new OpcionMenu(mniCaja, CAJERO));

        // Gerencia y Reportes 
        // - Reportes Operativos/Estratégicos: Director Médico
        // - Reportes Institucionales PDF/Excel: Administrador
        ModuloMenu modGerencia = new ModuloMenu(mnuGerencia)
                .agregar(new OpcionMenu(mniReportes, DIRECTOR, ADMIN));

        // Raíz del Composite
        ModuloMenu arbolRaiz = new ModuloMenu(null);
        arbolRaiz.agregar(modSeguridad)
                .agregar(modAdmision)
                .agregar(modMedico)
                .agregar(modAuxiliares)
                .agregar(modCaja)
                .agregar(modGerencia);

        // Dispara la verificación recursiva del Composite
        arbolRaiz.aplicarPermisos(rolObjeto);
    }

    // LÓGICA DE USUARIO Y SESIÓN
    /**
     * Muestra el saludo y el rol del usuario en la pantalla principal.
     */
    private void cargarInformacionUsuario() {
        if (usuarioActual == null) {
            return;
        }

        BLLRol bllRol = new BLLRol();
        Rol rol = bllRol.buscarRol(usuarioActual.getIdRol());
        this.rolNombre = (rol != null) ? rol.getNombreRol() : "";

        setTitle("Clinica San Gabriel - " + usuarioActual.getNombres() + " " + usuarioActual.getApellidos() + " (" + rolNombre + ")");
        txpMensaje.setText(
                "Bienvenido(a), " + usuarioActual.getNombres() + " " + usuarioActual.getApellidos() + ".\n\n"
                + "Rol: " + rolNombre + "\n\n"
                + "Seleccione un módulo del menú superior para comenzar.");
    }

    /**
     * Inicia el temporizador de cierre automático por inactividad. Escucha la
     * actividad global del sistema.
     */
    private void iniciarTimerInactividad() {
        timerInactividad = new javax.swing.Timer(TIEMPO_INACTIVIDAD, (e) -> {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Sesión cerrada por inactividad.",
                    "Seguridad",
                    javax.swing.JOptionPane.WARNING_MESSAGE);

            this.dispose();
            new Login().setVisible(true);
        });

        timerInactividad.setRepeats(false);
        timerInactividad.start();

        // Escuchador Global de Eventos de la AWT (Captura mouse y teclado en toda la ventana)
        long eventMask = java.awt.AWTEvent.MOUSE_EVENT_MASK
                | java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK
                | java.awt.AWTEvent.KEY_EVENT_MASK;

        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener((java.awt.event.AWTEventListener) event -> {
            // Cada vez que el usuario mueve el ratón, hace clic o presiona una tecla
            if (timerInactividad != null && timerInactividad.isRunning()) {
                timerInactividad.restart(); // Reinicia la cuenta regresiva de 5 min
            }
        }, eventMask);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane dppEscritorio;
    private javax.swing.JMenuBar mnbPrincipal;
    private javax.swing.JMenuItem mniAtencionMedica;
    private javax.swing.JMenuItem mniAuditoria;
    private javax.swing.JMenuItem mniCaja;
    private javax.swing.JMenuItem mniCerrarSesion;
    private javax.swing.JMenuItem mniCitas;
    private javax.swing.JMenuItem mniEspecialidades;
    private javax.swing.JMenuItem mniExamenes;
    private javax.swing.JMenuItem mniFarmacia;
    private javax.swing.JMenuItem mniHistorialClinico;
    private javax.swing.JMenuItem mniMedicamentos;
    private javax.swing.JMenuItem mniMedicos;
    private javax.swing.JMenuItem mniPacientes;
    private javax.swing.JMenuItem mniReportes;
    private javax.swing.JMenuItem mniSalir;
    private javax.swing.JMenuItem mniUsuarios;
    private javax.swing.JMenu mnuAdmision;
    private javax.swing.JMenu mnuCuerpoMedico;
    private javax.swing.JMenu mnuFinanciera;
    private javax.swing.JMenu mnuGerencia;
    private javax.swing.JMenu mnuSeguridad;
    private javax.swing.JMenu mnuServiciosAuxiliares;
    private javax.swing.JMenu mnuSesion;
    private javax.swing.JScrollPane scpTexto;
    private javax.swing.JTextPane txpMensaje;
    // End of variables declaration//GEN-END:variables
}

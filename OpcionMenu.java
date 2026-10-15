package presentacion.menu;

import entidades.Rol;
import javax.swing.JMenuItem;
import java.util.Arrays;
import java.util.List;

public class OpcionMenu extends ComponenteMenu {

    private final JMenuItem item;
    private final List<String> rolesPermitidos;

    public OpcionMenu(JMenuItem item, String... roles) {
        this.item = item;
        this.rolesPermitidos = Arrays.asList(roles);
    }

    @Override
    public boolean aplicarPermisos(Rol rolActual) {
        if (rolActual == null || rolActual.getNombreRol() == null) {
            item.setVisible(false);
            item.setEnabled(false);
            return false;
        }

        boolean tieneAcceso = rolesPermitidos.stream()
                .anyMatch(r -> r.equalsIgnoreCase(rolActual.getNombreRol().trim()));

        item.setVisible(tieneAcceso);
        item.setEnabled(tieneAcceso);

        return tieneAcceso;
    }
}
